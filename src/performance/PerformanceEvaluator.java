package performance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sorting.SortingAlgorithms;

/**
 * Performance evaluation.
 *
 * Runs a sorting algorithm on COPIES of the data, measures how long it takes,
 * checks that the result is really sorted, and finds the fastest algorithm.
 */
public class PerformanceEvaluator {

    /** The five algorithms, in the order they are run. */
    public static final String[] ALGORITHMS = {
        "Insertion Sort", "Shell Sort", "Merge Sort", "Quick Sort", "Heap Sort"
    };

    private static final int MAX_RUNS = 11;            // repeat each algorithm up to this many times
    private static final double TIME_BUDGET_MS = 1000; // slow algorithms stop repeating after this much time

    /** The result of running one algorithm. */
    public static class Result {
        public final String name;
        public final double timeMs;      // median execution time in milliseconds
        public final boolean correct;    // true if the output was sorted every time
        public final int runs;           // how many runs the median is based on

        public Result(String name, double timeMs, boolean correct, int runs) {
            this.name = name;
            this.timeMs = timeMs;
            this.correct = correct;
            this.runs = runs;
        }
    }

    // ------------------------------------------------------------------
    // Measure one algorithm
    // ------------------------------------------------------------------
    public static Result measure(String algorithm, double[] original) {
        warmUp(algorithm, original);
        System.gc();                                     // clean memory before timing

        List<Double> times = new ArrayList<Double>();
        boolean correct = true;
        double total = 0;

        for (int run = 0; run < MAX_RUNS && total < TIME_BUDGET_MS; run++) {
            double[] copy = original.clone();            // every run gets the same unsorted data
            long start = System.nanoTime();
            runAlgorithm(algorithm, copy);               // only the sorting is timed
            long end = System.nanoTime();

            double timeMs = (end - start) / 1000000.0;   // nanoseconds -> milliseconds
            times.add(timeMs);
            total += timeMs;
            if (!isSorted(copy)) correct = false;
        }

        Collections.sort(times);
        double median = times.get(times.size() / 2);     // the middle value
        return new Result(algorithm, median, correct, times.size());
    }


    private static void warmUp(String algorithm, double[] original) {
        int size = Math.min(original.length, 1000);
        for (int i = 0; i < 3; i++) {
            double[] sample = new double[size];
            System.arraycopy(original, 0, sample, 0, size);
            runAlgorithm(algorithm, sample);
        }
    }

    /** Calls the right method from the SortingAlgorithms class. */
    private static void runAlgorithm(String algorithm, double[] data) {
        switch (algorithm) {
            case "Insertion Sort": SortingAlgorithms.insertionSort(data); break;
            case "Shell Sort":     SortingAlgorithms.shellSort(data);     break;
            case "Merge Sort":     SortingAlgorithms.mergeSort(data);     break;
            case "Quick Sort":     SortingAlgorithms.quickSort(data);     break;
            case "Heap Sort":      SortingAlgorithms.heapSort(data);      break;
            default: throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
    }

    private static boolean isSorted(double[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[i - 1] > a[i]) return false;
        }
        return true;
    }

    // ------------------------------------------------------------------
    // Rank the results and find the best one
    // ------------------------------------------------------------------
    /** Sorts the list so the fastest algorithm is first. */
    public static void sortByTime(List<Result> results) {
        Collections.sort(results, new Comparator<Result>() {
            @Override
            public int compare(Result a, Result b) {
                return Double.compare(a.timeMs, b.timeMs);
            }
        });
    }

    /** The best algorithm = the correct result with the shortest time (null if none). */
    public static Result findBest(List<Result> results) {
        Result best = null;
        for (Result r : results) {
            if (r.correct && (best == null || r.timeMs < best.timeMs)) {
                best = r;
            }
        }
        return best;
    }
}
