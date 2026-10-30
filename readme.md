# Sorting Algorithm Performance Evaluator
IC4302 - Programming III | Group Assignment

A Java Swing application: upload a `.csv` file, select a numeric column, and compare how fast
Insertion, Shell, Merge, Quick and Heap Sort are. The fastest one is shown as the best algorithm.

## Project structure
```
Group-Assignment-Programming-III-/
  src/
    app/          Main.java                    starts the application
    dataset/      CsvHandler.java              file upload, CSV reading, numeric column detection
    sorting/      SortingAlgorithms.java       the five sorting algorithms
    performance/  PerformanceEvaluator.java    timing (median of several runs), best algorithm
    gui/          MainWindow.java              Swing user interface
  test/           SelfTest.java                57 automatic checks
  sample_data/    small_demo.csv, employees_10k.csv, sales_100k.csv
  SortingApp.jar  ready-to-run jar
```

## Team responsibilities
| Member | Package | Responsibility |
|---|---|---|
| Yasith | `dataset` | File upload and dataset handling |
| Kavindu | `sorting`, `test` | Sorting algorithms and automatic tests |
| Ashen | `performance`, `gui`, `app` | Performance evaluation and user interface |

## How the parts connect
```
gui.MainWindow --> dataset.CsvHandler                  load the file, get a column's values
gui.MainWindow --> performance.PerformanceEvaluator --> sorting.SortingAlgorithms
```

## Run it (JDK 8 or newer)
| What | Command |
|---|---|
| Run the app (Linux/macOS) | `./run.sh` |
| Run the app (Windows) | `run.bat` |
| Run from the jar | `java -jar SortingApp.jar` |
| Run the tests | `./run_tests.sh` or `run_tests.bat` |

More datasets: https://www.kaggle.com/datasets

## Using the app
1. Click **Browse CSV...** and choose a file. All columns are listed; numeric ones are marked.
2. Select a numeric column (text columns are rejected).
3. Click **Run Sorting**. The table shows each algorithm's median time, fastest first, and the **Best algorithm**.

## About the timing
Each algorithm is run several times on fresh copies of the same data (after a warm-up) and the median time
is reported, so results are stable. On small files the faster algorithms finish within a few milliseconds of
each other, so their order can still change slightly; on larger files the order is stable.

## Edge cases handled
Wrong file type, missing file, empty file, header-only file, no numeric columns, quoted commas, blank lines,
selecting a text column, selecting nothing, empty column, empty/text cells inside a numeric column (skipped and
reported), sorting before uploading, very large data (asks before running Insertion Sort).