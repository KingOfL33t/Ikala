package com.ikalagaming.entity;

import com.ikalagaming.util.BinaryTree;
import com.ikalagaming.util.BinaryTreeNode;

/**
 * A {@link BinaryTree} subclass for storing integers. Allows the finding of the
 * smallest int not stored in the tree.
 *
 * @author Ches Burks
 *
 */
public class IntegerTree extends BinaryTree<Integer> {

	/**
	 * Returns the smallest integer not stored in the tree.
	 *
	 * @return the smallest available int not in the tree.
	 */
	public int getSmallestUnusedInt() {
		if (this.treeRoot == null) {
			return 0;
		}
		BinaryTreeNode<Integer> node = this.getSmallestSubnode(this.treeRoot);
		int smallest = 0;
		boolean exitNextLoop = false;

		while (true) {
			if ((this.find(smallest, node) != null)) {
				++smallest;
				exitNextLoop = false;
			}
			else if (this.find(smallest, node.getParent()) != null) {
				++smallest;
				exitNextLoop = false;
			}
			else if (node.getParent() != null) {
				node = node.getParent();
				exitNextLoop = false;
			}
			else {
				if (exitNextLoop) {
					break;
				}
				exitNextLoop = true;
			}
		}
		return smallest;
	}
}
