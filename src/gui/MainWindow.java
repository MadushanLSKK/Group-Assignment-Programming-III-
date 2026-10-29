package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import dataset.CsvHandler;
import performance.PerformanceEvaluator;

/**
 * The user interface (GUI).
 *
 * Screen layout:
 *   TOP    : "Browse CSV" button + file name
 *   LEFT   : list of columns          RIGHT : preview of the data
 *   BOTTOM : "Run Sorting" button, results table, best algorithm
 */
public class MainWindow extends JFrame {

    private final CsvHandler csv = new CsvHandler();     

    // ---- screen components ----
    private final JButton browseButton = new JButton("Browse CSV...");
    private final JLabel fileLabel = new JLabel("No file loaded");

    private final DefaultListModel<String> columnModel = new DefaultListModel<String>();
    private final JList<String> columnList = new JList<String>(columnModel);

    private final DefaultTableModel previewModel = new ReadOnlyTableModel();
    private final JTable previewTable = new JTable(previewModel);

    private final JButton runButton = new JButton("Run Sorting");
    private final DefaultTableModel resultsModel = new ReadOnlyTableModel();
    private final JTable resultsTable = new JTable(resultsModel);
    private final JLabel bestLabel = new JLabel("Best algorithm: -");
    private final JLabel statusLabel = new JLabel("Step 1: click Browse CSV and choose a file.");

    public MainWindow() {
        super("Sorting Algorithm Performance Evaluator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        buildScreen();
        addListeners();
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }

    // ------------------------------------------------------------------
    // BUILD THE SCREEN
    // ------------------------------------------------------------------
    private void buildScreen() {
        // top bar
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(browseButton);
        top.add(fileLabel);

        // left: columns
        columnList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane columnScroll = new JScrollPane(columnList);
        columnScroll.setBorder(BorderFactory.createTitledBorder("Step 2: select a numeric column"));

        // right: data preview
        previewTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane previewScroll = new JScrollPane(previewTable);
        previewScroll.setBorder(BorderFactory.createTitledBorder("Data preview (first 100 rows)"));

        JSplitPane middle = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, columnScroll, previewScroll);
        middle.setDividerLocation(250);

        // bottom: run button, results, best algorithm
        resultsModel.setColumnIdentifiers(new String[] {"Rank", "Algorithm", "Median time (ms)", "Runs", "Sorted correctly?"});
        JScrollPane resultsScroll = new JScrollPane(resultsTable);
        resultsScroll.setPreferredSize(new Dimension(100, 160));
        resultsScroll.setBorder(BorderFactory.createTitledBorder("Results (median of several runs, fastest first)"));

        bestLabel.setFont(bestLabel.getFont().deriveFont(Font.BOLD, 16f));
        JPanel messages = new JPanel(new BorderLayout());
        messages.add(bestLabel, BorderLayout.NORTH);
        messages.add(statusLabel, BorderLayout.SOUTH);

        JPanel runPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        runPanel.add(runButton);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(runPanel, BorderLayout.NORTH);
        bottom.add(resultsScroll, BorderLayout.CENTER);
        bottom.add(messages, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(middle, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void addListeners() {
        browseButton.addActionListener(e -> browseFile());
        runButton.addActionListener(e -> runSorting());
        columnList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                checkSelectedColumn();
            }
        });
    }

    // ------------------------------------------------------------------
    // STEP 1: UPLOAD A CSV FILE
    // ------------------------------------------------------------------
    private void browseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("CSV files (*.csv)", "csv"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            loadCsvFile(chooser.getSelectedFile());
        }
    }

    private void loadCsvFile(File file) {
        try {
            csv.loadFile(file);                      
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Cannot load file", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Load failed: " + e.getMessage());
            return;
        }

        fileLabel.setText(file.getName() + "  (" + csv.getRowCount() + " rows, "
                + csv.getHeaders().size() + " columns)");
        showColumns();
        showPreview();
        resultsModel.setRowCount(0);
        bestLabel.setText("Best algorithm: -");
        bestLabel.setForeground(Color.BLACK);
        statusLabel.setText("Step 2: select a numeric column, then click Run Sorting.");
    }

    /** Fills the column list; numeric columns are marked so the user knows what can be sorted. */
    private void showColumns() {
        columnModel.clear();
        for (int i = 0; i < csv.getHeaders().size(); i++) {
            String tag = csv.isNumericColumn(i) ? "   (numeric)" : "   (text - cannot sort)";
            columnModel.addElement(csv.getHeaders().get(i) + tag);
        }
    }

    /** Shows the first 100 rows in the preview table. */
    private void showPreview() {
        int columns = csv.getHeaders().size();
        int shown = Math.min(100, csv.getRowCount());
        Object[][] data = new Object[shown][columns];
        for (int r = 0; r < shown; r++) {
            String[] row = csv.getRows().get(r);
            for (int c = 0; c < columns; c++) {
                data[r][c] = c < row.length ? row[c] : "";
            }
        }
        previewModel.setDataVector(data, csv.getHeaders().toArray());
    }

    /** If the user clicks a text column, tell them and unselect it. */
    private void checkSelectedColumn() {
        int index = columnList.getSelectedIndex();
        if (index >= 0 && !csv.isNumericColumn(index)) {
            statusLabel.setText("That column is not numeric. Please select a numeric column.");
            columnList.clearSelection();
        }
    }

    // ------------------------------------------------------------------
    // STEP 3: RUN THE SORTING ALGORITHMS
    // ------------------------------------------------------------------
    private void runSorting() {
        // 1. Get the values of the selected column. Errors are shown to the user.
        final double[] values;
        try {
            values = csv.getColumnValues(columnList.getSelectedIndex());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Cannot sort", JOptionPane.WARNING_MESSAGE);
            return;
        }
        final String columnName = csv.getHeaders().get(columnList.getSelectedIndex());
        final int skipped = csv.getSkippedCells();

        // 2. Insertion Sort is very slow on big data, so let the user decide
        boolean includeInsertion = true;
        if (values.length > 50000) {
            int answer = JOptionPane.showConfirmDialog(this,
                    "This column has " + values.length + " values.\n"
                    + "Insertion Sort is very slow on large data. Include it anyway?",
                    "Large dataset", JOptionPane.YES_NO_OPTION);
            includeInsertion = (answer == JOptionPane.YES_OPTION);
        }
        final boolean withInsertion = includeInsertion;

        // 3. Run in the background so the window does not freeze
        runButton.setEnabled(false);
        browseButton.setEnabled(false);
        resultsModel.setRowCount(0);
        bestLabel.setText("Best algorithm: ...");
        statusLabel.setText("Sorting " + values.length + " values, please wait...");

        new SwingWorker<List<PerformanceEvaluator.Result>, Void>() {
            @Override
            protected List<PerformanceEvaluator.Result> doInBackground() {
                List<PerformanceEvaluator.Result> results = new ArrayList<PerformanceEvaluator.Result>();
                for (String name : PerformanceEvaluator.ALGORITHMS) {
                    if (name.equals("Insertion Sort") && !withInsertion) continue;
                    results.add(PerformanceEvaluator.measure(name, values));
                }
                return results;
            }

            @Override
            protected void done() {
                try {
                    showResults(get(), columnName, values.length, skipped);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MainWindow.this, "Sorting failed: " + e,
                            "Error", JOptionPane.ERROR_MESSAGE);
                    statusLabel.setText("Sorting failed.");
                } finally {
                    runButton.setEnabled(true);
                    browseButton.setEnabled(true);
                }
            }
        }.execute();
    }

    /** Puts the results in the table (fastest first) and shows the best algorithm. */
    private void showResults(List<PerformanceEvaluator.Result> results, String columnName, int count, int skipped) {
        PerformanceEvaluator.sortByTime(results);
        for (int i = 0; i < results.size(); i++) {
            PerformanceEvaluator.Result r = results.get(i);
            resultsModel.addRow(new Object[] {
                i + 1,
                r.name,
                String.format(Locale.US, "%.3f", r.timeMs),
                r.runs,
                r.correct ? "Yes" : "NO"
            });
        }

        PerformanceEvaluator.Result best = PerformanceEvaluator.findBest(results);
        if (best == null) {
            bestLabel.setText("Best algorithm: none (all results were wrong)");
            bestLabel.setForeground(Color.RED);
        } else {
            bestLabel.setText(String.format(Locale.US, "Best algorithm: %s (%.3f ms)", best.name, best.timeMs));
            bestLabel.setForeground(new Color(0, 110, 40));
        }

        String message = "Done. Sorted " + count + " values from \"" + columnName + "\".";
        if (skipped > 0) {
            message += " (" + skipped + " empty/non-numeric cells were ignored)";
        }
        statusLabel.setText(message);
    }

    /** A table whose cells the user cannot edit. */
    private static class ReadOnlyTableModel extends DefaultTableModel {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
