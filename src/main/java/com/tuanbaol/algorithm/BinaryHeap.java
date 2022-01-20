package com.tuanbaol.algorithm;

import com.tuanbaol.CollectionUtil;
import javafx.scene.effect.Effect;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BinaryHeap {
    public BinaryNode heap;
    public Map<Integer, List<BinaryNode>> layerNodesMap = new HashMap<>();

    public BinaryNode ofParentBiggerHeap(int layerNum, LinkedList<Integer> vals) {
        BinaryNode node = BinaryNode.buildWidthPriority(layerNum, vals);
        reviseTo(node, 2);
        this.heap = layerNodesMap.get(1).get(0);
        return heap;
    }

    public BinaryNode ofLeafBiggerHeap(int layerNum) {
        BinaryNode node = BinaryNode.buildWidthPriority(layerNum);
        reviseTo(node, 1);
        this.heap = layerNodesMap.get(1).get(0);
        return heap;
    }

    public BinaryNode ofLeafBiggerHeap(int layerNum, LinkedList<Integer> vals) {
        BinaryNode node = BinaryNode.buildWidthPriority(layerNum, vals);
        reviseTo(node, 1);
        this.heap = layerNodesMap.get(1).get(0);
        return heap;
    }

    public void reviseTo(BinaryNode ori, int type) {
        buildLayerNodesMap(ori);
        int totalLayerNum = layerNodesMap.size();
        Stream.iterate(totalLayerNum - 1, (x) -> x - 1).limit(totalLayerNum - 1)
                .forEach(layerNum -> {
                    List<BinaryNode> layerNodes = layerNodesMap.get(layerNum);
                    List<BinaryNode> copyLayerNodes = new ArrayList<>(layerNodes);
                    copyLayerNodes.forEach(node -> {
                        swapByType(layerNum, type, node);
                    });
                });
    }

    private void swapByType(int layerNum, int type, BinaryNode node) {
        if (node == null) {
            return;
        }
        BinaryNode left = node.getLeft();
        BinaryNode right = node.getRight();
        int checkRes = checkSwap(node, left, right, type);
        BinaryNode newLeft = null, newRight = null;
        if (checkRes == 1) {
            reviseWithParent(node, left);
            BinaryNode oriSubLeft = left.getLeft();
            node.setLeft(oriSubLeft);
            if (oriSubLeft != null) {
                oriSubLeft.setParent(node);
            }
            BinaryNode oriRight = right;
            BinaryNode oriSubRight = left.getRight();
            node.setRight(oriSubRight);
            if (oriSubRight != null) {
                oriSubRight.setParent(node);
            }
            node.setParent(left);
            left.setLeft(node);
            left.setRight(oriRight);
            if (oriRight != null) {
                oriRight.setParent(left);
            }
            newLeft = node;
            newRight = right;
            layerNodesMapRefresh(layerNum, node, left);
        } else if (checkRes == 2) {
            reviseWithParent(node, right);
            node.setParent(right);
            BinaryNode oriLeft = left;
            BinaryNode oriSubLeft = right.getLeft();
            node.setLeft(oriSubLeft);
            if (oriSubLeft != null) {
                oriSubLeft.setParent(node);
            }
            BinaryNode oriSubRight = right.getRight();
            node.setRight(oriSubRight);
            if (oriSubRight != null) {
                oriSubRight.setParent(node);
            }
            right.setLeft(oriLeft);
            right.setRight(node);
            oriLeft.setParent(right);
            newLeft = left;
            newRight = node;
            layerNodesMapRefresh(layerNum, node, right);
        }
        if (checkRes != 0) {
            swapByType(layerNum + 1, type, newLeft);
            swapByType(layerNum + 1, type, newRight);
        }

    }

    private void reviseWithParent(BinaryNode oriParent, BinaryNode oriSub) {
        BinaryNode grandparent = oriParent.getParent();
        if (grandparent == null) {
            oriSub.setParent(null);
            return;
        }
        if (grandparent.getLeft() == oriParent) {
            grandparent.setLeft(oriSub);
        } else {
            grandparent.setRight(oriSub);
        }
        oriSub.setParent(grandparent);
    }

    private void layerNodesMapRefresh(int layerNum, BinaryNode oriParent, BinaryNode oriSub) {
        List<BinaryNode> parentLayer = layerNodesMap.get(layerNum);
        parentLayer.remove(oriParent);
        parentLayer.add(oriSub);
        List<BinaryNode> subLayer = layerNodesMap.get(layerNum + 1);
        subLayer.remove(oriSub);
        subLayer.add(oriParent);
    }

    private int checkSwap(BinaryNode parent, BinaryNode left, BinaryNode right, int type) {
        Integer parentVal = parent.getIntVl();
        if (left != null && right == null && shouldSwapByType(parentVal, left.getIntVl(), type)) {
            return 1;
        } else if (right != null && left == null && shouldSwapByType(parentVal, right.getIntVl(), type)) {
            return 2;
        } else if (left != null && right != null) {
            Integer leftVal = left.getIntVl();
            Integer rightVal = right.getIntVl();
            if (type == 1) {
                int min = Math.min(leftVal, rightVal);
                if (min < parentVal) {
                    return leftVal == min ? 1 : 2;
                }
            } else {
                int max = Math.max(leftVal, rightVal);
                if (max > parentVal) {
                    return leftVal == max ? 1 : 2;
                }
            }
        }
        return 0;
    }

    private boolean shouldSwapByType(Integer parentVal, Integer subVal, int type) {
        if (type == 1) {
            return subVal < parentVal;
        } else {
            return subVal > parentVal;
        }
    }

    public static void main(String[] args) {
        LinkedList<Integer> vals = CollectionUtil.ofLinkedList(12, 43, 123, 545, 1, 3, 565, 234, 4, 688);
        LinkedList<Integer> vals2 = CollectionUtil.ofLinkedList(12, 43, 123, 545, 1, 3, 565, 234, 4, 688);
        PrintTree.print(BinaryNode.buildWidthPriority(4, vals));
//        BinaryNode parentBiggerHeap = new BinaryHeap().ofParentBiggerHeap(4, vals2);
        BinaryNode leafBiggerHeap = new BinaryHeap().ofLeafBiggerHeap(4, vals2);
//        BinaryNode leafBiggerHeap = new BinaryHeap().ofLeafBiggerHeap(4);
//        BinaryNode parentBiggerHeap = new BinaryHeap().ofParentBiggerHeap(4);

        PrintTree.print(leafBiggerHeap);
        PrintTree.print(leafBiggerHeap.revert());
//        PrintTree.print(parentBiggerHeap);
    }

    private void buildLayerNodesMap(BinaryNode ori) {
        recursive(1, ori);
    }

    private void recursive(int layerNum, BinaryNode parent) {
        if (parent == null) {
            return;
        }
        layerNodesMap.compute(layerNum, (key, list) -> {
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(parent);
            return list;
        });
        recursive(layerNum + 1, parent.getLeft());
        recursive(layerNum + 1, parent.getRight());
    }
}
