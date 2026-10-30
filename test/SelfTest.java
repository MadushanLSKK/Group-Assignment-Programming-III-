import java.util.Arrays;
import java.util.Random;

import sorting.SortingAlgorithms;
import performance.PerformanceEvaluator;

/** Console checks for all three parts. Run with run_tests.sh / run_tests.bat */
public class SelfTest {
    static int passed = 0, failed = 0;

    public static void main(String[] args) throws Exception {
        testSorting();
        System.out.println("\nResult: " + passed + " passed, " + failed + " failed");
        System.exit(failed == 0 ? 0 : 1);
    }

    // ---------- MAIN TEST METHOD ----------
    static void testSorting() {
        Random rnd = new Random(1);
        for (String name : PerformanceEvaluator.ALGORITHMS) {
            check(name + ": empty / single / two", sorts(name, new double[0]) && sorts(name, new double[]{4}) && sorts(name, new double[]{2, 1}));
            check(name + ": random 2000", sorts(name, random(rnd, 2000, 100000)));
            check(name + ": many duplicates", sorts(name, random(rnd, 2000, 4)));
            check(name + ": all equal", sorts(name, filled(500, 3)));
            check(name + ": negatives and decimals", sorts(name, new double[]{3.5, -1, 0, -7.25, 2, 2}));
            double[] up = new double[3000], down = new double[3000];
            for (int i = 0; i < 3000; i++) { up[i] = i; down[i] = 3000 - i; }
            check(name + ": already sorted / reverse sorted", sorts(name, up) && sorts(name, down));
            boolean many = true;
            for (int t = 0; t < 100; t++) many &= sorts(name, random(rnd, rnd.nextInt(150), 30));
            check(name + ": 100 random small arrays", many);
        }
        double[] big = new double[1_000_000];
        for (int i = 0; i < big.length; i++) big[i] = i % 1000;
        for (String name : PerformanceEvaluator.ALGORITHMS) {
            if (name.startsWith("Insertion") || name.startsWith("Shell")) continue;
            check(name + ": 1 million values", sorts(name, big));
        }
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