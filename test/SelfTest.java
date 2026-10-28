import java.util.Arrays;
import java.util.Random;

import sorting.SortingAlgorithms;
import performance.PerformanceEvaluator;

/** Console checks for all three parts. Run with run_tests.sh / run_tests.bat */
public class SelfTest {
    static int passed = 0, failed = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("\nResult: " + passed + " passed, " + failed + " failed");
        System.exit(failed == 0 ? 0 : 1);
    }

    // ---------- COMPARES  ALGORITHM WITH Arrays.sort ----------
    static boolean sorts(String name, double[] input) {
        double[] expected = input.clone();
        Arrays.sort(expected);                 // trusted answer from Java
        double[] actual = input.clone();
        switch (name) {
            case "Insertion Sort": SortingAlgorithms.insertionSort(actual); break;
            case "Shell Sort":     SortingAlgorithms.shellSort(actual);     break;
            case "Merge Sort":     SortingAlgorithms.mergeSort(actual);     break;
            case "Quick Sort":     SortingAlgorithms.quickSort(actual);     break;
            default:               SortingAlgorithms.heapSort(actual);
        }
        return Arrays.equals(expected, actual);   // true if your result matches
    }

    // ---------- HELPERS USED BY THE SORTING TESTS ----------
    static double[] random(Random r, int n, int bound) {
        double[] a = new double[n];
        for (int i = 0; i < n; i++) a[i] = r.nextInt(bound) - bound / 2 + (r.nextBoolean() ? 0.5 : 0);
        return a;
    }

    static double[] filled(int n, double v) { double[] a = new double[n]; Arrays.fill(a, v); return a; }

    static void check(String name, boolean ok) {
        if (ok) passed++; else failed++;
        System.out.println((ok ? "[PASS] " : "[FAIL] ") + name);
    }
}