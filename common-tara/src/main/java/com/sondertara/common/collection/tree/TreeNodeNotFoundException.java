package com.sondertara.common.collection.tree;

import com.sondertara.common.exception.ExceptionMessage;

public class TreeNodeNotFoundException extends RuntimeException {
    public TreeNodeNotFoundException(String nodeId) {
        super(new ExceptionMessage("Can't find a tree node with id: {0}", nodeId).getMessage());
    }
}