package a05_graphs_trees_heaps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import util.Interval;
import util.ListNode;
import util.TreeNode;

public class BinarySearchTreeBootCamp {
	/**
	 * Given a binary search tree (BST), find the lowest common ancestor (LCA) of two given nodes in
	 * the BST.
	 */
	public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
		if (root.val > p.val && root.val > q.val) {
			return lowestCommonAncestor(root.left, p, q);
		} else if (root.val < p.val && root.val < q.val) {
			return lowestCommonAncestor(root.right, p, q);
		} else {
			return root;
		}
	}

	/**
	 * <pre>
	Given a binary tree, determine if it is a valid binary search tree (BST).
	
	Assume a BST is defined as follows:
	
	The left subtree of a node contains only nodes with keys less than the node's key.
	The right subtree of a node contains only nodes with keys greater than the node's key.
	Both the left and right subtrees must also be binary search trees.
	Example 1:
	    2
	   / \
	  1   3
	Binary tree [2,1,3], return true.
	Example 2:
	    1
	   / \
	  2   3
	Binary tree [1,2,3], return false.
	 * </pre>
	 * 
	 * @author lchen
	 *
	 */
	public boolean isValidBST(TreeNode root) {
		return isValidBST(root, Long.MIN_VALUE, Long.MAX_VALUE);
	}

	private boolean isValidBST(TreeNode root, long minVal, long maxVal) {
		if (root == null)
			return true;
		if (root.val >= maxVal || root.val <= minVal)
			return false;
		return isValidBST(root.left, minVal, root.val) && isValidBST(root.right, root.val, maxVal);
	}

	/**
	 * Given a binary search tree with non-negative values, find the minimum absolute difference
	 * between values of any two nodes.
	 */
	private TreeNode prev;

	public int minDifference(TreeNode node) {
		if (node == null)
			return Integer.MAX_VALUE;
		int minDiff = minDifference(node.left);
		if (prev != null)
			minDiff = Math.min(minDiff, node.val - prev.val);
		prev = node;
		minDiff = Math.min(minDiff, minDifference(node.right));
		return minDiff;
	}

	/**
	 * Write a program that takes as input a BST and an interval and returns the BST keys that lie
	 * in the interval.
	 */
	public List<Integer> rangeLookupInBST(TreeNode tree, Interval interval) {
		List<Integer> result = new ArrayList<>();
		rangeLookupInBST(tree, interval, result);
		return result;
	}

	private void rangeLookupInBST(TreeNode tree, Interval interval, List<Integer> result) {
		if (tree == null)
			return;
		if (interval.left <= tree.val && tree.val <= interval.right) {
			rangeLookupInBST(tree.left, interval, result);
			result.add(tree.val);
			rangeLookupInBST(tree.right, interval, result);
		} else if (interval.left > tree.val) {
			rangeLookupInBST(tree.right, interval, result);
		} else {
			rangeLookupInBST(tree.left, interval, result);
		}
	}

	/**
	 * Given a non-empty binary search tree and a target value, find the value in the BST that is
	 * closest to the target.
	 */
	public int closestValue(TreeNode root, double target) {
		int result = root.val;
		while (root != null) {
			if (Math.abs(target - root.val) < Math.abs(target - result))
				result = root.val;
			root = root.val > target ? root.left : root.right;
		}
		return result;
	}

	/**
	 * Given a non-empty binary search tree and a target value, find k values in the BST that are
	 * closest to the target.
	 */
	public List<Integer> closestKValues(TreeNode root, double target, int k) {
		LinkedList<Integer> list = new LinkedList<Integer>();
		closestKValues(list, root, target, k);
		return list;
	}

	// in-order traverse
	private boolean closestKValues(LinkedList<Integer> list, TreeNode node, double target, int k) {
		if (node == null)
			return false;

		if (closestKValues(list, node.left, target, k))
			return true;

		if (list.size() == k) {
			if (Math.abs(list.getFirst() - target) < Math.abs(node.val - target))
				return true;
			else
				list.removeFirst();
		}

		list.addLast(node.val);
		return closestKValues(list, node.right, target, k);
	}

	/**
	 * <pre>
	Given a binary tree, find the largest subtree which is a Binary Search Tree (BST), where largest means subtree with largest number of nodes in it.
	
	Note:
	A subtree must include all of its descendants.
	Here's an example:
	10
	/ \
	5  15
	/ \   \ 
	1   8   7
	The Largest BST Subtree in this case is the highlighted one. 
	The return value is the subtree's size, which is 3.
	 * </pre>
	 */
	public int largestBSTSubtree(TreeNode root) {
		if (root == null)
			return 0;
		if (root.left == null && root.right == null)
			return 1;
		if (isValidBST(root, Long.MIN_VALUE, Long.MAX_VALUE))
			return countTreeNode(root);
		return Math.max(largestBSTSubtree(root.left), largestBSTSubtree(root.right));
	}

	private int countTreeNode(TreeNode root) {
		if (root == null)
			return 0;
		if (root.left == null && root.right == null)
			return 1;
		return 1 + countTreeNode(root.left) + countTreeNode(root.right);
	}

	/**
	 * Given a singly linked list where elements are sorted in ascending order, convert it to a
	 * height balanced BST.
	 * 
	 */
	public TreeNode sortedListToBST(ListNode head) {
		if (head == null)
			return null;
		return convertToBST(head, null);
	}

	private TreeNode convertToBST(ListNode head, ListNode tail) {
		ListNode slow = head;
		ListNode fast = head;
		if (head == tail)
			return null;

		while (fast != tail && fast.next != tail) {
			fast = fast.next.next;
			slow = slow.next;
		}
		TreeNode thead = new TreeNode(slow.val);
		thead.left = convertToBST(head, slow);
		thead.right = convertToBST(slow.next, tail);
		return thead;
	}

	/**
	 * Suppose you are given the sequence in which keys are visited in an preorder traversal of a
	 * BST, and all keys are distinct. Can you reconstruct the BST from the sequence?
	 * 
	 * The complexity is O(n)
	 */
	private Integer rootIdx;

	public TreeNode rebuildBSTFromPreorder(List<Integer> preorderSequence) {
		rootIdx = 0;
		return rebuildBSTFromPreorderOnValueRange(preorderSequence, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	private TreeNode rebuildBSTFromPreorderOnValueRange(List<Integer> preorderSequence, Integer lowerBound,
			Integer upperBound) {
		if (rootIdx == preorderSequence.size())
			return null;
		Integer root = preorderSequence.get(rootIdx);
		if (root < lowerBound || root > upperBound)
			return null;
		rootIdx++;
		TreeNode leftSubtree = rebuildBSTFromPreorderOnValueRange(preorderSequence, lowerBound, root);
		TreeNode rightSubtree = rebuildBSTFromPreorderOnValueRange(preorderSequence, root, upperBound);
		return new TreeNode(root, leftSubtree, rightSubtree);
	}

	/**
	 * For example, if the three arrays are [5, 10, 15], [3, 6, 9, 12, 15] and [8, 16, 24], then 15,
	 * 15, 16 lie in the smallest possible interval which is 1.
	 * 
	 * <br>
	 * 
	 * Idea: We can begin with the first element of each arrays, [5, 3, 8]. The smallest interval
	 * whose left end point is 3 has length 8 - 3 = 5. The element after 3 is 6, so we continue with
	 * the triple (5, 6, 8). The smallest interval whose left end point is 5 has length 8 - 5 = 3.
	 * The element after 5 is 10, so we continue with the triple (10, 6, 8)...
	 */
	public int minDistanceInKSortedArrays(List<List<Integer>> sortedArrays) {
		// Indices into each of the arrays
		List<Integer> heads = new ArrayList<>(sortedArrays.size());
		for (int i = 0; i < sortedArrays.size(); i++)
			heads.add(0);

		int result = Integer.MAX_VALUE;
		NavigableSet<ArrayData> currentHeads = new TreeSet<>(
				(a, b) -> (a.val - b.val == 0 ? a.idx - b.idx : a.val - b.val));

		for (int i = 0; i < sortedArrays.size(); i++) {
			currentHeads.add(new ArrayData(i, sortedArrays.get(i).get(heads.get(i))));
		}

		while (true) {
			result = Math.min(result, currentHeads.last().val - currentHeads.first().val);
			int idxNextMin = currentHeads.first().idx;
			heads.set(idxNextMin, heads.get(idxNextMin) + 1);
			// Return if some array has no remaining elements.
			if (heads.get(idxNextMin) >= sortedArrays.get(idxNextMin).size())
				return result;
			currentHeads.pollFirst();
			currentHeads.add(new ArrayData(idxNextMin, sortedArrays.get(idxNextMin).get(heads.get(idxNextMin))));
		}
	}

	private class ArrayData {
		private int val;
		private int idx;

		public ArrayData(int idx, int val) {
			this.idx = idx;
			this.val = val;
		}
	}
	
	public static void main(String[] args) {
		BinarySearchTreeBootCamp bootCamp = new BinarySearchTreeBootCamp();
		List<Integer> preorder = Arrays.asList(3, 2, 1, 5, 4, 6);
		TreeNode tree = bootCamp.rebuildBSTFromPreorder(preorder);
		assert (3 == tree.val);
		assert (2 == tree.left.val);
		assert (1 == tree.left.left.val);
		assert (5 == tree.right.val);
		assert (4 == tree.right.left.val);
		assert (6 == tree.right.right.val);

		List<List<Integer>> sortedArrays = new ArrayList<>();
		sortedArrays.add(Arrays.asList(5, 10, 15));
		sortedArrays.add(Arrays.asList(3, 6, 9, 12, 15));
		sortedArrays.add(Arrays.asList(8, 16, 24));
		int result = bootCamp.minDistanceInKSortedArrays(sortedArrays);
		assert result == 1;
	}

}