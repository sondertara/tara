package com.sondertara.common.collection.tree;

import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@SuppressWarnings("all")
public class SimpleTree implements Tree {
    private static final long serialVersionUID = -9051148743662948065L;
    private List<DefaultTreeNode> nodes = new ArrayList<>();
    private transient Map<String, DefaultTreeNode> nodeMap = new HashMap<>();

    public SimpleTree() {
        this(null);
    }

    public SimpleTree(Collection<DefaultTreeNode> nodes) {
        if (nodes != null) {
            this.nodes.addAll(nodes);
        }
    }

    @Override
    public void addNode(DefaultTreeNode node) {
        this.nodes.add(node);
        this.nodeMap.put(node.id(), node);
    }

    @Override
    public void addNode(String pid, DefaultTreeNode node) {
        node.setPid(pid);
        addNode(node);
    }

    @Override
    public void addNodes(List<DefaultTreeNode> nodes) {
        if (nodes != null) {
            for (DefaultTreeNode node : nodes) {
                addNode(node);
            }
        }
    }

    @Override
    public void addNodes(String pid, List<DefaultTreeNode> nodes) {
        for (DefaultTreeNode node : nodes) {
            addNode(pid, node);
        }
    }

    @Override
    public void removeNode(DefaultTreeNode node, boolean recursion) {
        this.nodes.remove(node);

        this.nodeMap.remove(node.getId());


        if (recursion) {
            Collection<DefaultTreeNode> children = getChildren(node.getId());
            if ((children != null) && (!children.isEmpty())) {
                Iterator<DefaultTreeNode> iter = children.iterator();
                while (iter.hasNext()) {
                    DefaultTreeNode n = (DefaultTreeNode) iter.next();
                    removeNode(n, recursion);
                }
            }
        }
    }

    @Override
    public void removeNode(DefaultTreeNode node) {
        removeNode(node, false);
    }

    @Override
    public List<DefaultTreeNode> removeChildNodes(String pid) {
        if (pid == null) {
            return new ArrayList<>();
        }
        List<DefaultTreeNode> removed = new LinkedList<DefaultTreeNode>();
        removeChildNodes(pid, removed);
        return removed;
    }

    public void removeChildNodes(String pid, List<DefaultTreeNode> removed) {
        DefaultTreeNode node = this.nodeMap.get(pid);
        Collection<DefaultTreeNode> children = getChildren(node.getId());
        if ((children != null) && (!children.isEmpty())) {
            Iterator<DefaultTreeNode> iter = children.iterator();
            while (iter.hasNext()) {
                DefaultTreeNode n = iter.next();
                removeChildNodes(n.getId(), removed);
                removeNode(n, false);
            }
            removed.addAll(children);
        }
    }

    @Override
    public DefaultTreeNode getNodeById(String id) {
        return this.nodeMap.get(id);
    }

    @Override
    public Collection<DefaultTreeNode> getChildren(String id) {
        DefaultTreeNode n = this.nodeMap.get(id);
        if (n != null) {
            List<DefaultTreeNode> children = new LinkedList<DefaultTreeNode>();
            Iterator<DefaultTreeNode> iter = this.nodes.iterator();
            while (iter.hasNext()) {
                DefaultTreeNode node = iter.next();
                if (id.equals(node.getPid())) {
                    children.add(node);
                }
            }
            return children;
        }
        return null;
    }

    @Override
    public Collection<DefaultTreeNode> getNodes() {
        return this.nodes;
    }


    @Override
    public List<DefaultTreeNode> getNodesAsArray() {
        return Lists.asList(CollectionUtils.toArray(this.nodes, DefaultTreeNode[].class));
    }

    @Override
    public void forEach(final BiConsumer<Tree, DefaultTreeNode> cb) throws Throwable {
        Iterator<DefaultTreeNode> iter = this.nodes.iterator();
        while (iter.hasNext()) {
            DefaultTreeNode node = iter.next();
            cb.accept(this, node);
        }
    }

    @Override
    public DefaultTreeNode getParentNode(String treeNodeId) {
        DefaultTreeNode node = getNodeById(treeNodeId);
        if (node != null) {
            return getParentNode(node);
        }
        return null;
    }

    @Override
    public DefaultTreeNode getParentNode(DefaultTreeNode treeNode) {
        return getNodeById(treeNode.getPid());
    }

    @Override
    public void sort(Comparator<DefaultTreeNode> comparator) {
        Collections.sort(this.nodes, comparator);
    }

    @Override
    public List<DefaultTreeNode> getRootNodes() {
        CommonTree cTree = new CommonTree();
        cTree.addNodes(this.nodes);
        List<DefaultTreeNode> roots = cTree.getRootNodes();
        cTree.clear();
        return roots;
    }

    @Override
    public void clear() {
        this.nodes.clear();
        this.nodeMap.clear();
    }

}