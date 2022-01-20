package com.tuanbaol.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrintTree{
 
    public static void main(String[] args){
        print(10);
//        print(50, 30,2,35,80,70,801,6560,75,55,65,72,78,52,58,51,53);
//        print(50, 30,25,35,80,70,81,60,75,55,65,78,52,58,51);
//        print(1,2,3);
//        print(3,2,1);
//        print(100,200,300);
//        print(8,3,10,1,6,14,4,7,13);
//        print(8,7,6,5,4,3,2,1,9,10,11,12,13,14,15,16);
//        print(8,3,10,1,14);
//        print(50,20,70,15,35,65,86,10,45,60,90);
//        print(8,3,10,6,14);
//        print(8,3,10,6,7,14,15);
//        print(45,12,53,3,37,100,24,61,90,78);
//        print(500000,20000,7000,654321,12345,2,22,55555,987654321,12345678, 11111);
    }
 
    public static void print(int nodesNum){
//        printBinaryNodes(Arrays.asList(BinaryNode.build(nodesNum)));
        BinaryNode root = BinaryNode.buildDepthPriority(5);
        print(root);
    }

    public static void print(BinaryNode root) {
        root.calcPosition(0);
        printBinaryNodes(Arrays.asList(root));
        System.out.println();
        System.out.println();
        System.out.println("________________________________________________________________________________________");
        System.out.println();
        System.out.println();
    }

    public static void printBinaryNodes(List<BinaryNode> BinaryNodes){
        List<BinaryNode> newBinaryNodes = new ArrayList<>(BinaryNodes.size() * 2);
        String xuxianRow = "", shuxianRow = "", dataRow = "";
 
        int upPosition = 0;
        int upDataLen = 0;
        for(int i = 0; i < BinaryNodes.size(); i++){
            BinaryNode BinaryNode = BinaryNodes.get(i);
            //数据行
            int printSpaceNum = BinaryNode.position - upPosition - upDataLen;
            for(int j = 0; j < printSpaceNum; j++){
                dataRow += " ";
            }
            dataRow += BinaryNode.intVl;
 
            //虚线行和竖线行
            BinaryNode parent = BinaryNode.parent;
            if(parent != null){
                int spaceNum = 0, xuxianLimit = 0, type = 0;
                if(BinaryNode == parent.left){
                    spaceNum = BinaryNode.position - xuxianRow.length() + BinaryNode.getDataLength() / 2;
                    if(parent.right == null){
                        type = 1;
                        xuxianLimit = parent.position + 1;
                    }
                    else{
                        type = 2;
                        xuxianLimit = parent.right.position + (int)Math.ceil(parent.right.getDataLength() / 2.0);
                    }
                }
                else if(parent.left == null){
                    type = 3;
                    spaceNum = parent.position + parent.getDataLength() - 1 - xuxianRow.length();
                    xuxianLimit = BinaryNode.position + (int)Math.ceil(BinaryNode.getDataLength() / 2.0);
                }
 
                if(type > 0){
                    for(int j = 0; j < spaceNum; j++){
                        xuxianRow += " ";
                        shuxianRow += " ";
                    }
 
                    int xuxianLength = xuxianLimit - xuxianRow.length();
                    for(int j = 0; j < xuxianLength; j++){
                        xuxianRow += "-";
                        shuxianRow += (j == 0 && type != 3) || (j == xuxianLength - 1 && type != 1) ? "|" : " ";
                    }
                }
            }
 
            //记录子节点为打印下一行做准备
            if(BinaryNode.left != null){
                newBinaryNodes.add(BinaryNode.left);
            }
            if(BinaryNode.right != null){
                newBinaryNodes.add(BinaryNode.right);
            }
            upPosition = BinaryNode.position;
            upDataLen = BinaryNode.getDataLength();
        }
        if(BinaryNodes.get(0).parent != null){
            System.out.println(xuxianRow);
            System.out.println(shuxianRow);
        }
        System.out.println(dataRow);
 
        if(!newBinaryNodes.isEmpty()){
            printBinaryNodes(newBinaryNodes);
        }
    }
 


}
