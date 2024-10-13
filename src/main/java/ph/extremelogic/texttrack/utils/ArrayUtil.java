package ph.extremelogic.texttrack.utils;

import java.lang.reflect.Array;

import static java.lang.System.arraycopy;

/**
 * Utility class providing static methods to manipulate arrays.
 * <p>
 * This class contains methods for array operations that are not directly available
 * within the Java standard library. These methods include shifting elements within
 * an array and resizing arrays. The class is designed to be used statically and
 * is not intended to be instantiated.
 * </p>
 */
public class ArrayUtil {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * <p>
     * Since {@code ArrayUtil} is a utility class containing only static methods,
     * it does not make sense to instantiate it. Making the constructor private
     * ensures that the class usage is limited to static contexts.
     * </p>
     */
    private ArrayUtil() {
        // Prevent instantiation
    }

    /**
     * Shifts all elements of the given byte array one position to the left and shrinks the array size by one.
     * This method effectively removes the first element of the array and returns a new array with one less element,
     * containing the remaining elements. If the input array is empty, it immediately returns the same array without changes.
     *
     * @param array The byte array to shift and shrink. If the array is empty, it is returned as is.
     * @return A new byte array with the elements shifted left by one position, with the size decreased by one.
     *         If the input array is empty, the same array is returned.
     */
    public static byte[] shiftLeftAndShrink(byte[] array) {
        if (array.length == 0) return array; // Return if empty
        byte[] newArray = new byte[array.length - 1];
        System.arraycopy(array, 1, newArray, 0, newArray.length);
        return newArray;
    }

    /**
     * Swaps two elements in an array of any type.
     *
     * @param arr   the array in which elements will be swapped
     * @param ind1  the index of the first element to swap
     * @param ind2  the index of the second element to swap
     */
    public static <T> void swap(T[] arr, int ind1, int ind2) {
        if (ind1 != ind2) {
            T tmp = arr[ind1];
            arr[ind1] = arr[ind2];
            arr[ind2] = tmp;
        }
    }

    /**
     * A generic swap method that works with primitive arrays using reflection.
     *
     * @param arr   the array in which elements will be swapped
     * @param ind1  the index of the first element to swap
     * @param ind2  the index of the second element to swap
     */
    public static void swap(Object arr, int ind1, int ind2) {
        if (!arr.getClass().isArray()) {
            throw new IllegalArgumentException("Input must be an array.");
        }

        if (ind1 != ind2) {
            Object tmp = Array.get(arr, ind1);
            Array.set(arr, ind1, Array.get(arr, ind2));
            Array.set(arr, ind2, tmp);
        }
    }

    /**
     * Concatenates multiple arrays of integers into a single array.
     * This method takes any number of integer arrays as input and merges them into a single array.
     * The order of elements in the output array follows the order of the input arrays.
     *
     * @param arrays An array of int arrays to be concatenated. This can be a sequence of arrays or an array of arrays.
     * @return A new array containing all elements of the input arrays, concatenated in the order they appear.
     */
    public static int[] concatenate(int[]... arrays) {
        int totalLength = 0;
        for (int[] array : arrays) {
            totalLength += array.length;
        }
        int[] result = new int[totalLength];
        int offset = 0;
        for (int[] array : arrays) {
            arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
}
