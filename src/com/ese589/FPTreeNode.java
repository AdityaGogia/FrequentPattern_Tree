package com.ese589;

import java.util.ArrayList;

public class FPTreeNode {

    public FPTreeNode(String item) {
        this.item = item;
        next = null;
        children = new ArrayList<FPTreeNode>();
        root = false;
    }

    boolean root;
    String item;
    ArrayList<FPTreeNode> children;
    FPTreeNode parent;
    FPTreeNode next;
    int frequencyOfItemInTree;

    boolean isRoot(){
        return root;
    }

}
