package com.sondertara.common.collection.tree;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;


/**
 * @author huangxiaohu.1ih
 */
public interface Tree extends Serializable {
    void addNode(DefaultTreeNode paramTreeNode);

    void addNode(String pid, DefaultTreeNode paramTreeNode);

    void addNodes(List<DefaultTreeNode> paramList);

    void addNodes(String pid, List<DefaultTreeNode> paramList);

    void removeNode(DefaultTreeNode paramTreeNode, boolean paramBoolean);

    void removeNode(DefaultTreeNode paramTreeNode);

    Collection<DefaultTreeNode> removeChildNodes(String pid);

    DefaultTreeNode getNodeById(String id);

    Collection<DefaultTreeNode> getChildren(String paramString);

    Collection<DefaultTreeNode> getNodes();

    List<DefaultTreeNode> getNodesAsArray();

    DefaultTreeNode getParentNode(String treeNodeId);

    DefaultTreeNode getParentNode(DefaultTreeNode treeNode);

    void forEach(BiConsumer<Tree, DefaultTreeNode> paramCallback) throws Throwable;

    void sort(Comparator<DefaultTreeNode> paramComparator);

    List<DefaultTreeNode> getRootNodes();

    void clear();
}