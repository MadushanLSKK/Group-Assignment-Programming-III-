package dataset;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


// File upload and dataset handling.

public class CsvHandler {

    private List<String> headers = new ArrayList<String>();   // column names
    private List<String[]> rows = new ArrayList<String[]>();  // data rows (without the header)
    private boolean[] numericColumn = new boolean[0];         // numericColumn[i] = is column i numeric?
    private int skippedCells = 0;                             // empty/text cells ignored in the last column


    // 1. LOAD THE FILE

    public void loadFile(File file) throws Exception {

        // --- Validate the file first (invalid file uploads) ---
        if (file == null || !file.exists()) {
            throw new Exception("The file does not exist.");
        }
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            throw new Exception("Invalid file type. Please choose a .csv file.");
        }
        if (file.length() == 0) {
            throw new Exception("The file is empty.");
        }

        // --- Read all lines (try UTF-8 first, then Latin-1 for older files) ---
        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            try {
                lines = Files.readAllLines(file.toPath(), StandardCharsets.ISO_8859_1);
            } catch (IOException e2) {
                throw new Exception("The file could not be read.");
            }
        }

        // --- Split every non-blank line into cells ---
        List<String[]> table = new ArrayList<String[]>();
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                table.add(splitLine(line));
            }
        }
        if (table.size() < 2) {
            throw new Exception("The file needs a header row and at least one data row.");
        }

        // --- First row = headers (remove the invisible BOM character if present) ---
        String[] headerRow = table.get(0);
        headerRow[0] = headerRow[0].replace("\uFEFF", "");
        List<String> newHeaders = new ArrayList<String>();
        for (int i = 0; i < headerRow.length; i++) {
            String name = headerRow[i].trim();
            newHeaders.add(name.isEmpty() ? "Column " + (i + 1) : name);
        }

        // --- Remaining rows = data ---
        List<String[]> newRows = new ArrayList<String[]>(table.subList(1, table.size()));
        boolean[] newNumeric = findNumericColumns(newRows, newHeaders.size());

        boolean anyNumeric = false;
        for (boolean b : newNumeric) {
            if (b) anyNumeric = true;
        }
        if (!anyNumeric) {
            throw new Exception("This file has no numeric columns, so there is nothing to sort.");
        }


        headers = newHeaders;
        rows = newRows;
        numericColumn = newNumeric;
    }


    // 2. SPLIT ONE LINE INTO CELLS
    //    Example line:  1,"Perera, Nimal",85000
    //    Result:        3 cells ->  1  |  Perera, Nimal  |  85000

    private String[] splitLine(String line) {
        List<String> cells = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (insideQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');          // "" inside quotes means one quote character
                    i++;
                } else {
                    insideQuotes = !insideQuotes; // start or end of a quoted cell
                }
            } else if (c == ',' && !insideQuotes) {
                cells.add(current.toString());    // a comma outside quotes ends the cell
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        cells.add(current.toString());            // the last cell
        return cells.toArray(new String[0]);
    }


    // 3. FIND THE NUMERIC COLUMNS
    //    A column is numeric if at least 90% of its non-empty cells are numbers.

    private boolean[] findNumericColumns(List<String[]> data, int columnCount) {
        boolean[] result = new boolean[columnCount];
        for (int col = 0; col < columnCount; col++) {
            int filled = 0;
            int numbers = 0;
            for (String[] row : data) {
                String cell = getCell(row, col);
                if (cell.isEmpty()) continue;
                filled++;
                if (isNumber(cell)) numbers++;
            }
            result[col] = numbers > 0 && numbers >= 0.9 * filled;
        }
        return result;
    }
    // method isNumber tries to change the text into a number. If it fails, the text is not a number.
    private boolean isNumber(String text) {
        try {
            double d = Double.parseDouble(text);
            return !Double.isNaN(d) && !Double.isInfinite(d);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Returns the trimmed cell, or "" if this row is shorter than expected. */
    private String getCell(String[] row, int col) {
        return col < row.length ? row[col].trim() : "";
    }


    // 4. GET THE VALUES OF ONE COLUMN (used by the sorting step)

    public double[] getColumnValues(int col) throws Exception {
        if (rows.isEmpty()) {
            throw new Exception("Please upload a CSV file first.");
        }
        if (col < 0 || col >= headers.size()) {
            throw new Exception("Please select a column first.");
        }
        //copies the numbers into an array. This array goes to the sorting algorithms.
        String name = headers.get(col);

        if (!numericColumn[col]) {
            boolean allEmpty = true;
            for (String[] row : rows) {
                if (!getCell(row, col).isEmpty()) allEmpty = false;
            }
            if (allEmpty) {
                throw new Exception("Column \"" + name + "\" is empty.");
            }
            throw new Exception("Column \"" + name + "\" is not numeric. Please select a numeric column.");
        }

        double[] values = new double[rows.size()];
        int count = 0;
        skippedCells = 0;
        for (String[] row : rows) {
            String cell = getCell(row, col);
            if (isNumber(cell)) {
                values[count++] = Double.parseDouble(cell);
            } else {
                skippedCells++;     // empty or text cell inside a numeric column
            }
        }
        return Arrays.copyOf(values, count);
    }


    // Simple getters used by the GUI

    public List<String> getHeaders()      { return headers; }

    public List<String[]> getRows()       { return rows; }

    public int getRowCount()              { return rows.size(); }

    public int getSkippedCells()          { return skippedCells; }

    public boolean isNumericColumn(int col) {
        return col >= 0 && col < numericColumn.length && numericColumn[col];
    }

    public int getNumericColumnCount() {
        int count = 0;
        for (boolean b : numericColumn) {
            if (b) count++;
        }
        return count;
    }
}
