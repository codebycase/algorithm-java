/******************************************************************************
 *  Compilation:  javac Quick3string.java
 *  Execution:    java Quick3string < input.txt
 *  Dependencies: StdIn.java StdOut.java 
 *  Data files:   https://algs4.cs.princeton.edu/51radix/words3.txt
 *                https://algs4.cs.princeton.edu/51radix/shells.txt
 *
 *  Reads string from standard input and 3-way string quicksort them.
 *
 *  % java Quick3string < shell.txt
 *  are
 *  by
 *  sea
 *  seashells
 *  seashells
 *  sells
 *  sells
 *  she
 *  she
 *  shells
 *  shore
 *  surely
 *  the
 *  the
 *
 *
 ******************************************************************************/

package a06_sorting_searching;

/**
 * The {@code Quick3string} class provides static methods for sorting an array of strings using
 * 3-way radix quicksort.
 * <p>
 * For additional documentation, see <a href="https://algs4.cs.princeton.edu/51radix">Section
 * 5.1</a> of <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public class Quick3string {
	private static final int CUTOFF = 15; // cutoff to insertion sort

	// do not instantiate
	private Quick3string() {
	}

	/**
	 * Rearranges the array of strings in ascending order.
	 *
	 * @param a
	 *            the array to be sorted
	 */
	public static void sort(String[] a) {
		sort(a, 0, a.length - 1, 0);
		assert isSorted(a);
	}

	// return the dth character of s, -1 if d = length of s
	private static int charAt(String s, int d) {
		assert d >= 0 && d <= s.length();
		if (d == s.length())
			return -1;
		return s.charAt(d);
	}

	// 3-way string quicksort a[lo..hi] starting at dth character
	private static void sort(String[] a, int lo, int hi, int d) {

		// cutoff to insertion sort for small subarrays
		if (hi <= lo + CUTOFF) {
			insertion(a, lo, hi, d);
			return;
		}

		int lt = lo, gt = hi;
		int v = charAt(a[lo], d);
		int i = lo + 1;
		while (i <= gt) {
			int t = charAt(a[i], d);
			if (t < v)
				exch(a, lt++, i++);
			else if (t > v)
				exch(a, i, gt--);
			else
				i++;
		}

		// a[lo..lt-1] < v = a[lt..gt] < a[gt+1..hi].
		sort(a, lo, lt - 1, d);
		if (v >= 0)
			sort(a, lt, gt, d + 1);
		sort(a, gt + 1, hi, d);
	}

	// sort from a[lo] to a[hi], starting at the dth character
	private static void insertion(String[] a, int lo, int hi, int d) {
		for (int i = lo; i <= hi; i++)
			for (int j = i; j > lo && less(a[j], a[j - 1], d); j--)
				exch(a, j, j - 1);
	}

	// exchange a[i] and a[j]
	private static void exch(String[] a, int i, int j) {
		String temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}

	private static boolean less(String v, String w, int d) {
		assert v.substring(0, d).equals(w.substring(0, d));
		for (int i = d; i < Math.min(v.length(), w.length()); i++) {
			if (v.charAt(i) < w.charAt(i))
				return true;
			if (v.charAt(i) > w.charAt(i))
				return false;
		}
		return v.length() < w.length();
	}

	// is the array sorted
	private static boolean isSorted(String[] a) {
		for (int i = 1; i < a.length; i++)
			if (a[i].compareTo(a[i - 1]) < 0)
				return false;
		return true;
	}

	
}