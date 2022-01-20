package com.tuanbaol.algorithm;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class BinaryNode {
    protected String name;
    protected String value;
    protected Integer intVl = 0;
    protected BinaryNode left;
    protected BinaryNode right;
    protected BinaryNode parent;

    protected BinaryNode widthPriority(Consumer<BinaryNode> consumer, Consumer<BinaryNode> afterLayerConsumer) {
        List<BinaryNode> layerNodes = new ArrayList<>();
        layerNodes.add(this);
        while (layerNodes.size() > 0) {
            ArrayList<BinaryNode> nextLayerNodes = new ArrayList<>();
            layerNodes.forEach(layerNode -> {
                consumer.accept(layerNode);
                if (layerNode.left != null) {
                    nextLayerNodes.add(layerNode.left);
                }
                if (layerNode.right != null) {
                    nextLayerNodes.add(layerNode.right);
                }
            });
            if (afterLayerConsumer != null) {
                afterLayerConsumer.accept(layerNodes.get(layerNodes.size() - 1));
            }
            layerNodes.clear();
            layerNodes = nextLayerNodes;
        }
        return this;
    }

    protected void printWidthPriority() {
        StringBuilder result = new StringBuilder();
        widthPriority(node -> result.append(node.getIntVl()).append(" "), node -> result.append("\r\n"));
        System.out.println(result.toString());
    }

    public static BinaryNode buildDepthPriority(int layerNum) {
        if (layerNum <= 0) {
            return null;
        }
        int nodesNum = new Double(Math.pow(2, layerNum + 1)).intValue();
        LinkedList<Integer> valList = new LinkedList<>();
        IntStream.iterate(nodesNum - 1, x -> x - 1).limit(nodesNum - 1).forEach(val -> valList.push(val));
        Supplier<Integer> valSupplier = () -> valList.poll();
        BinaryNode root = of(valSupplier.get());
        buildDepthPrioritySubNode(root, --layerNum, valSupplier);
        return root;
    }

    public static BinaryNode buildDepthPriority(int layerNum, LinkedList<Integer> vals) {
        if (layerNum <= 0) {
            return null;
        }
        Supplier<Integer> valSupplier = () -> vals.poll();
        BinaryNode root = of(valSupplier.get());
        buildDepthPrioritySubNode(root, --layerNum, valSupplier);
        return root;
    }

    private static void buildDepthPrioritySubNode(BinaryNode parentNode, int layerNum, Supplier<Integer> valSupplier) {
        if (layerNum <= 0 || parentNode == null) {
            return;
        }
        Integer value = valSupplier.get();
        if (value == null) {
            return;
        }
        parentNode.setLeft(of(value));
        parentNode.getLeft().setParent(parentNode);
        value = valSupplier.get();
        if (value == null) {
            return;
        }
        parentNode.setRight(of(value));
        parentNode.getRight().setParent(parentNode);
        buildDepthPrioritySubNode(parentNode.getLeft(), --layerNum, valSupplier);
        buildDepthPrioritySubNode(parentNode.getRight(), layerNum, valSupplier);
    }

    protected static BinaryNode buildDepthPriorityNotTailRecurse(int layerNum) {
        if (layerNum <= 0) {
            return null;
        }
        AtomicInteger value = new AtomicInteger(0);
        BinaryNode root = of(value.incrementAndGet());
        root.setLeft(buildDepthPrioritySubNodeNotTailRecurse(--layerNum, value));
        root.setRight(buildDepthPrioritySubNodeNotTailRecurse(layerNum, value));
        return root;
    }

    private static BinaryNode buildDepthPrioritySubNodeNotTailRecurse(int layerNum, AtomicInteger value) {
        if (layerNum <= 0) {
            return null;
        }
        BinaryNode parent = of(value.incrementAndGet());
        parent.setLeft(buildDepthPrioritySubNodeNotTailRecurse(--layerNum, value));
        parent.setRight(buildDepthPrioritySubNodeNotTailRecurse(layerNum, value));
        return parent;
    }

    protected static BinaryNode buildWidthPriority(int layerNum) {
        if (layerNum <= 0) {
            return null;
        }
        int nodesNum = new Double(Math.pow(2, layerNum + 1)).intValue();
        LinkedList<Integer> valList = new LinkedList<>();
        IntStream.iterate(nodesNum - 1, x -> x - 1).limit(nodesNum - 1).forEach(val -> valList.push(val));
        Supplier<Integer> valSupplier = () -> valList.poll();
        return buildWidthPriority(layerNum, valSupplier);
    }

    protected static BinaryNode buildWidthPriority(int layerNum, LinkedList<Integer> vals) {
        if (layerNum <= 0) {
            return null;
        }
        Supplier<Integer> valSupplier = () -> vals.poll();
        return buildWidthPriority(layerNum, valSupplier);
    }

    protected static BinaryNode buildWidthPriority(int layerNum, Supplier<Integer> valSupplier) {
        if (layerNum <= 0) {
            return null;
        }
        BinaryNode root = of(valSupplier.get());
        List<BinaryNode> preLayerNodes = new ArrayList<>();
        preLayerNodes.add(root);
        while (--layerNum > 0) {
            List<BinaryNode> curLayerNodes = new ArrayList<>();
            for (BinaryNode node : preLayerNodes) {
                Integer value = valSupplier.get();
                if (value == null) {
                    break;
                }
                node.setLeft(of(value));
                node.getLeft().setParent(node);
                value = valSupplier.get();
                if (value == null) {
                    break;
                }
                node.setRight(of(value));
                node.getRight().setParent(node);
                curLayerNodes.add(node.getLeft());
                curLayerNodes.add(node.getRight());
            }
            preLayerNodes.clear();
            preLayerNodes.addAll(curLayerNodes);
        }
        return root;
    }

    protected static BinaryNode build() {
        int i = 1;
        return of(i
                , of(++i
                        , of(++i, of(++i, null, null), of(++i, null, null))
                        , of(++i, of(++i, null, null), of(++i, null, null)))
                , of(++i
                        , of(++i, of(++i, null, null), of(++i, null, null))
                        , of(++i, of(++i, null, null), of(++i, null, null)))
        );
    }

    protected BinaryNode revert() {
        return recursiveRevert(this);
    }

    private BinaryNode recursiveRevert(BinaryNode binaryNode) {
        if (binaryNode != null) {
            BinaryNode left = binaryNode.getLeft();
            binaryNode.setLeft(binaryNode.getRight());
            binaryNode.setRight(left);
            recursiveRevert(left);
            recursiveRevert(binaryNode.getLeft());
        }
        return binaryNode;
    }

    public static void main(String[] args) {
        BinaryNode.buildDepthPriority(4).printWidthPriority();
        BinaryNode.buildDepthPriority(4).revert().printWidthPriority();
    }

    protected static BinaryNode of(int value, BinaryNode left, BinaryNode right) {
        BinaryNode node = new BinaryNode();
        node.setIntVl(value);
        node.setLeft(left);
        node.setRight(right);
        return node;
    }

    protected static BinaryNode of(Integer value) {
        if (value == null) {
            return null;
        }
        BinaryNode node = new BinaryNode();
        node.setIntVl(value);
        return node;
    }

    //    protected void standardPrint() {
//        int layerNum = getLayerNum();
//        List<BinaryNode> layerNodes = new ArrayList<>();
//        layerNodes.add(this);
//        int curLayerNum = 1;
//        while (layerNodes.size() > 0) {
//            int valueBlankNum = 3 * 2 ^ (layerNum - curLayerNum-- - 1) - 1;
//            printLines(curLayerNum, layerNum);
//            printNodes(layerNodes, curLayerNum, layerNum);
//            layerNodes = getNextLayerNodes(layerNodes);
//            curLayerNum++;
//        }
//    }
//
//    private void printLines(int curLayerNum, int totalLayerNum) {
//        int topBlankNum = 3 * 2 ^ (totalLayerNum - curLayerNum ) - 1;
//        int buttomBlankNum = 3 * 2 ^ (totalLayerNum - curLayerNum ) - 1;
//
//    }
    public static BinaryNode build(int nodeNum) {
        BinaryNode root = new BinaryNode();
        for (int i = 0; i < nodeNum; i++) {
            BinaryNode node = new BinaryNode();
            node.setIntVl(i);
            root = addBinaryNode(root, node);
        }
        return root;
    }

    public static BinaryNode addBinaryNode(BinaryNode parent, BinaryNode child) {
        if (parent == null) {
            parent = child;
        } else if (parent.intVl > child.intVl) {
            parent.left = addBinaryNode(parent.left, child);
            parent.left.parent = parent;
        } else if (parent.intVl < child.intVl) {
            parent.right = addBinaryNode(parent.right, child);
            parent.right.parent = parent;
        }
        return parent;
    }

    private int getLayerNum() {
        return 0;
    }

    protected String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected String getValue() {
        return value;
    }

    protected void setValue(String value) {
        this.value = value;
    }

    protected Integer getIntVl() {
        return intVl;
    }

    protected void setIntVl(Integer intVl) {
        this.intVl = intVl;
    }

    protected BinaryNode getLeft() {
        return left;
    }

    public BinaryNode getParent() {
        return parent;
    }

    public void setParent(BinaryNode parent) {
        this.parent = parent;
    }

    protected void setLeft(BinaryNode left) {
        this.left = left;
    }

    protected BinaryNode getRight() {
        return right;
    }

    protected void setRight(BinaryNode right) {
        this.right = right;
    }

    public int position;

    public int getDataLength() {
        return String.valueOf(intVl).length();
    }

    public int calcPosition(int nextPosition) {
        if (left != null) {
            nextPosition = left.calcPosition(nextPosition);
        }

        position = nextPosition;
        nextPosition = position + getDataLength() + 1;

        if (right != null) {
            nextPosition = right.calcPosition(nextPosition);
        }

        return nextPosition;
    }

}
