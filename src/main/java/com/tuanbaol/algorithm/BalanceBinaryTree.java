package com.tuanbaol.algorithm;

import com.tuanbaol.CollectionUtil;

import java.util.LinkedList;
import java.util.function.Supplier;

public class BalanceBinaryTree {
    public BinaryNode tree;

    public BinaryNode build(LinkedList<Integer> vals) {
        Supplier<Integer> valSupplier = () -> vals.poll();
        BinaryNode root = BinaryNode.of(valSupplier.get());
        this.tree = root;
        return buildNotBalanceTree(root, valSupplier);
    }

    private BinaryNode buildNotBalanceTree(BinaryNode parent, Supplier<Integer> valSupplier) {
        Integer parentVal = parent.getIntVl();
        Integer value = valSupplier.get();
        if (value != null && value < parentVal) {
            parent.setLeft(BinaryNode.of(value));
            parent.getLeft().setParent(parent);
            buildNotBalanceTree(parent.getLeft(), valSupplier);
        } else if (value != null && value >= parentVal) {
            parent.setRight(BinaryNode.of(value));
            parent.getRight().setParent(parent);
            buildNotBalanceTree(parent.getRight(), valSupplier);
        }
        return parent;
    }

    public static void main(String[] args) {
        LinkedList<Integer> vals = CollectionUtil.ofLinkedList(12, 43, 123, 545, 1, 3, 565, 234, 4, 688);
        PrintTree.print(new BalanceBinaryTree().build(vals));
    }
}
