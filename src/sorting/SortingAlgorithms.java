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

}