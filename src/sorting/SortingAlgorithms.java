package sorting;

/**
 * The five sorting algorithms.
 *
 * Every method sorts the given array IN PLACE, from smallest to largest.
 * Example:  double[] data = {5, 2, 9, 1};  SortingAlgorithms.mergeSort(data);  ->  {1, 2, 5, 9}
 */
public class SortingAlgorithms {

    // ==================================================================
    // 1. INSERTION SORT        Time: O(n^2)
    //    Take each element and slide it left until it is in the right place,
    //    like sorting playing cards in your hand.
    // ==================================================================
    public static void insertionSort(double[] a) {
        for (int i = 1; i < a.length; i++) {
            double key = a[i];          // the element we want to place
            int j = i - 1;
            while (j >= 0 && a[j] > key) {
                a[j + 1] = a[j];        // shift bigger elements one step right
                j--;
            }
            a[j + 1] = key;             // put the element in its place
        }
    }

    // ==================================================================
    // 2. SHELL SORT            Time: about O(n^1.5) with this gap sequence
    //    Insertion sort that first compares elements far apart (a big "gap"),
    //    then makes the gap smaller until it is 1.
    // ==================================================================
    public static void shellSort(double[] a) {
        for (int gap = a.length / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < a.length; i++) {
                double key = a[i];
                int j = i;
                while (j >= gap && a[j - gap] > key) {
                    a[j] = a[j - gap];  // shift by 'gap' positions instead of 1
                    j -= gap;
                }
                a[j] = key;
            }
        }
    }


    // ==================================================================
    // 3. MERGE SORT            Time: O(n log n)
    //    Split the array in half, sort each half, then merge the two sorted halves.
    // ==================================================================
    public static void mergeSort(double[] a) {
        if (a.length < 2) return;
        double[] temp = new double[a.length];     // helper array used while merging
        mergeSort(a, temp, 0, a.length - 1);
    }

    private static void mergeSort(double[] a, double[] temp, int left, int right) {
        if (left >= right) return;                // 0 or 1 element is already sorted
        int middle = left + (right - left) / 2;
        mergeSort(a, temp, left, middle);         // sort left half
        mergeSort(a, temp, middle + 1, right);    // sort right half
        merge(a, temp, left, middle, right);      // combine them
    }

    private static void merge(double[] a, double[] temp, int left, int middle, int right) {
        for (int k = left; k <= right; k++) {
            temp[k] = a[k];                       // copy the part we are merging
        }
        int i = left;                             // position in left half
        int j = middle + 1;                       // position in right half
        for (int k = left; k <= right; k++) {
            if (i > middle) {
                a[k] = temp[j++];                 // left half finished
            } else if (j > right) {
                a[k] = temp[i++];                 // right half finished
            } else if (temp[i] <= temp[j]) {
                a[k] = temp[i++];                 // smaller value comes first
            } else {
                a[k] = temp[j++];
            }
        }
    }

    // ==================================================================
    // 4. QUICK SORT            Time: O(n log n) on average
    //    Pick a pivot (the middle element). Move smaller values to its left and
    //    bigger values to its right. Then sort the left and right parts the same way.
    // ==================================================================
    public static void quickSort(double[] a) {
        quickSort(a, 0, a.length - 1);
    }

    private static void quickSort(double[] a, int low, int high) {
        if (low >= high) return;
        double pivot = a[low + (high - low) / 2]; // middle pivot (good for sorted data too)
        int i = low;
        int j = high;
        while (i <= j) {
            while (a[i] < pivot) i++;             // find a value that belongs on the right
            while (a[j] > pivot) j--;             // find a value that belongs on the left
            if (i <= j) {
                swap(a, i, j);
                i++;
                j--;
            }
        }
        quickSort(a, low, j);                     // sort the left part
        quickSort(a, i, high);                    // sort the right part
    }

    // ------------------------------------------------------------------
    private static void swap(double[] a, int i, int j) {
        double temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

}