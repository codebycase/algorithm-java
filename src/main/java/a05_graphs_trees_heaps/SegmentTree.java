package a05_graphs_trees_heaps;

import java.util.Arrays;

/**
 * The {@code SegmentTree} class is an structure for efficient search of cummulative data. It
 * performs Range Minimum Query and Range Sum Query in O(log(n)) time. It can be easily customizable
 * to support Range Max Query, Range Multiplication Query etc.
 * <p>
 * Also it has been develop with {@code LazyPropagation} for range updates, which means when you
 * perform update operations over a range, the update process affects the least nodes as possible so
 * that the bigger the range you want to update the less time it consumes to update it. Eventually
 * those changes will be propagated to the children and the whole array will be up to date.
 * <p>
 * Example:
 * <p>
 * SegmentTreeHeap st = new SegmentTreeHeap(new Integer[]{1, 3, 4, 2, 1, -2, 4}); st.update(0, 3, 1)
 * In the above case only the node that represents the range [0, 3] will be updated (and not their
 * children) so in this case the update task will be less than n*log(n)
 *
 * Memory usage: O(n)
 *
 */
public class SegmentTree {

	private Node[] heap;
	private int[] array;
	private int size;

	// Time-complexity: O(n*log(n))
	public SegmentTree(int[] array) {
		this.array = Arrays.copyOf(array, array.length);
		// The max size of this array is about 2 * 2 ^ log2(n) + 1
		size = (int) (2 * Math.pow(2.0, Math.floor((Math.log((double) array.length) / Math.log(2.0)) + 1)));
		heap = new Node[size];
		build(1, 0, array.length); // 1-based tree/heap
	}

	public int size() {
		return array.length;
	}

	// Initialize the Nodes of the Segment tree
	private void build(int v, int from, int size) {
		Node node = new Node();
		heap[v] = node;
		node.from = from;
		node.to = from + size - 1;
		if (size == 1) {
			node.sum = array[from];
			node.min = array[from];
		} else {
			int len = size / 2;
			build(2 * v, from, len);
			build(2 * v + 1, from + len, size - len);
			node.sum = heap[2 * v].sum + heap[2 * v + 1].sum;
			node.min = Math.min(heap[2 * v].min, heap[2 * v + 1].min);
		}
	}

	// Range sum query time-omplexity: O(log(n))
	public int rsq(int from, int to) {
		return rsq(1, from, to);
	}

	private int rsq(int v, int from, int to) {
		Node node = heap[v];

		// If you did a range update that contained this node, you can infer the Sum without going down the
		// tree
		if (node.pendingVal != null && contains(node.from, node.to, from, to)) {
			return (to - from + 1) * node.pendingVal;
		}

		if (contains(from, to, node.from, node.to)) {
			return node.sum;
		}

		if (intersects(from, to, node.from, node.to)) {
			propagate(v);
			int leftSum = rsq(2 * v, from, to);
			int rightSum = rsq(2 * v + 1, from, to);

			return leftSum + rightSum;
		}

		return 0;
	}

	/**
	 * Range Min Query Time-Complexity: O(log(n))
	 */
	public int rMinQ(int from, int to) {
		return rMinQ(1, from, to);
	}

	private int rMinQ(int v, int from, int to) {
		Node node = heap[v];

		// If you did a range update that contained this node, you can infer the Min value without going
		// down the tree
		if (node.pendingVal != null && contains(node.from, node.to, from, to)) {
			return node.pendingVal;
		}

		if (contains(from, to, node.from, node.to)) {
			return heap[v].min;
		}

		if (intersects(from, to, node.from, node.to)) {
			propagate(v);
			int leftMin = rMinQ(2 * v, from, to);
			int rightMin = rMinQ(2 * v + 1, from, to);

			return Math.min(leftMin, rightMin);
		}

		return Integer.MAX_VALUE;
	}

	/**
	 * Range Update Operation. With this operation you can update either one position or a range of
	 * positions with a given number. The update operations will update the less it can to update the
	 * whole range (Lazy Propagation). The values will be propagated lazily from top to bottom of the
	 * segment tree. This behavior is really useful for updates on portions of the array
	 * 
	 * Time-Complexity: O(log(n))
	 *
	 */
	public void update(int from, int to, int value) {
		update(1, from, to, value);
	}

	private void update(int v, int from, int to, int value) {
		// The Node of the heap tree represents a range of the array with bounds: [n.from, n.to]
		Node node = heap[v];

		/**
		 * If the updating-range contains the portion of the current Node We lazily update it. This means We
		 * do NOT update each position of the vector, but update only some temporal values into the Node;
		 * such values into the Node will be propagated down to its children only when they need to.
		 */
		if (contains(from, to, node.from, node.to)) {
			change(node, value);
		}

		if (node.size() == 1)
			return;

		if (intersects(from, to, node.from, node.to)) {
			/**
			 * Before keeping going down to the tree We need to propagate the the values that have been
			 * temporally/lazily saved into this Node to its children So that when We visit them the values are
			 * properly updated
			 */
			propagate(v);

			update(2 * v, from, to, value);
			update(2 * v + 1, from, to, value);

			node.sum = heap[2 * v].sum + heap[2 * v + 1].sum;
			node.min = Math.min(heap[2 * v].min, heap[2 * v + 1].min);
		}
	}

	// Propagate temporal values to children
	private void propagate(int v) {
		Node node = heap[v];
		if (node.pendingVal != null) {
			change(heap[2 * v], node.pendingVal);
			change(heap[2 * v + 1], node.pendingVal);
			node.pendingVal = null; // unset the pending propagation value
		}
	}

	// Save the temporal values that will be propagated lazily
	private void change(Node n, int value) {
		n.pendingVal = value;
		n.sum = n.size() * value;
		n.min = value;
		array[n.from] = value;

	}

	// Test if the range1 contains range2
	private boolean contains(int from1, int to1, int from2, int to2) {
		return from2 >= from1 && to2 <= to1;
	}

	// check inclusive intersection, test if range1[from1, to1] intersects range2[from2, to2]
	private boolean intersects(int from1, int to1, int from2, int to2) {
		return from1 <= from2 && to1 >= from2 // (.[..)..] or (.[...]..)
				|| from1 >= from2 && from1 <= to2; // [.(..]..) or [..(..)..
	}

	// The Node class represents a partition range of the array.
	static class Node {
		int sum, min;
		int from, to;
		// Store the value that will be propagated lazily
		Integer pendingVal = null;

		int size() {
			return to - from + 1;
		}
	}

	public static void main(String[] args) {
		int[] nums = { 1, 4, 3, 6, 7, 5, 2, 0, 9, 8 };
		SegmentTree solution = new SegmentTree(nums);
		assert solution.rsq(1, 7) == 27;
		solution.update(2, 2, 5);
		assert solution.rsq(1, 7) == 29;
	}
}
