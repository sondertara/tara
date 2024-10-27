package com.sondertara.common.collection.tree;

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

@SuppressWarnings({"all"})
public class CommonTree implements Tree {
    private static final long serialVersionUID = 1L;
    private transient Map<String, DefaultTreeNode> nodeMap = new HashMap();
    private List<DefaultTreeNode> nodes = new LinkedList<DefaultTreeNode>();

    public CommonTree() {
        this(null);
    }

    public CommonTree(Collection<DefaultTreeNode> nodes) {
        if (nodes != null) {
            this.nodes.addAll(nodes);
        }
    }

    @Override
    public void addNode(DefaultTreeNode node) {
        String pid = node.getPid();
        addNode(pid, node);
    }

    @Override
    public void addNode(String pid, DefaultTreeNode node) {
        if (pid == null) {
            pid = node.getPid();
        }
        node.setPid(pid);

        DefaultTreeNode parentNode = (DefaultTreeNode) this.nodeMap.get(pid);
        if (parentNode != null) {
            parentNode.addChildNode(node);
        } else {
            List<DefaultTreeNode> rootNodes = this.nodes;


            DefaultTreeNode pnode = null;
            boolean isChild = false;
            for (DefaultTreeNode rootNode : rootNodes) {
                if (rootNode.getId().equals(node.getPid())) {
                    pnode = rootNode;
                    break;
                }
            }
            if (pnode != null) {
                isChild = true;
                pnode.addChildNode(node);
            }


            boolean isParent = node.getIsParent();
            if (!isParent) {
                Iterator<DefaultTreeNode> iter = rootNodes.iterator();
                while (iter.hasNext()) {
                    DefaultTreeNode rootNode = (DefaultTreeNode) iter.next();
                    if (node.getId().equals(rootNode.getPid())) {
                        node.setIsParent(true);
                        node.addChildNode(rootNode);
                        iter.remove();
                    }
                }
            }

            if (!isChild) {
                this.nodes.add(node);
            }
        }

        this.nodeMap.put(node.getId(), node);
    }

    @Override
    public void addNodes(List<DefaultTreeNode> nodes) {
        for (DefaultTreeNode node : nodes) {
            addNode(node);
        }
    }

    @Override
    public void addNodes(String pid, List<DefaultTreeNode> nodes) {
        DefaultTreeNode parentNode = (DefaultTreeNode) this.nodeMap.get(pid);
        if (parentNode != null) {
            for (DefaultTreeNode node : nodes) {
                node.setPid(pid);
            }
            parentNode.addChildNodes(nodes);
        } else {
            nodes.addAll(nodes);
        }
    }

    @Override
    public void removeNode(DefaultTreeNode node) {
        removeNode(node, false);
    }

    @Override
    public void removeNode(DefaultTreeNode node, boolean recursion) {
        String pid = node.getPid();
        DefaultTreeNode parentNode = (DefaultTreeNode) this.nodeMap.get(pid);

        if (parentNode == null) {
            this.nodes.remove(node);
        } else {
            parentNode.removeChildNode(node);
        }

        this.nodeMap.remove(node.getId());

        if (recursion) {
            Collection children = node.getChildren();
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
    public Collection<DefaultTreeNode> removeChildNodes(String pid) {
        DefaultTreeNode parentNode = (DefaultTreeNode) this.nodeMap.get(pid);
        if (parentNode == null) {
            return new ArrayList<>();
        }
        Collection<DefaultTreeNode> children = parentNode.removeChildNodes();
        if ((children != null) && (!children.isEmpty())) {
            Iterator<DefaultTreeNode> iter = children.iterator();
            while (iter.hasNext()) {
                DefaultTreeNode n = (DefaultTreeNode) iter.next();
                removeNode(n, true);
            }
        }
        return children;
    }

    @Override
    public DefaultTreeNode getNodeById(String id) {
        return (DefaultTreeNode) this.nodeMap.get(id);
    }

    @Override
    public Collection<DefaultTreeNode> getNodes() {
        return this.nodes;
    }

    @Override
    public List<DefaultTreeNode> getNodesAsArray() {
        List<DefaultTreeNode> descendants = new LinkedList<DefaultTreeNode>();
        for (DefaultTreeNode node : this.nodes) {
            node.extractDescendants(descendants, true);
        }
        return descendants;
    }

    @Override
    public void forEach(BiConsumer<Tree, DefaultTreeNode> cb) throws Throwable {
        forEachCollection(this.nodes, cb);
    }

    private void forEachCollection(Collection<DefaultTreeNode> collection, BiConsumer<Tree, DefaultTreeNode> cb) throws Throwable {
        Iterator<DefaultTreeNode> iter = collection.iterator();
        while (iter.hasNext()) {
            DefaultTreeNode node = (DefaultTreeNode) iter.next();
            cb.accept(this, node);
            Collection<DefaultTreeNode> children = node.getChildren();
            if ((children != null) && (!children.isEmpty())) {
                forEachCollection(children, cb);
            }
        }
    }

    @Override
    public DefaultTreeNode getParentNode(String treeNodeId) {
        DefaultTreeNode node = getNodeById(treeNodeId);
        if (node == null) {
            throw new TreeNodeNotFoundException(treeNodeId);
        }
        return getParentNode(node);
    }

    @Override
    public DefaultTreeNode getParentNode(DefaultTreeNode defaultTreeNode) {
        return getNodeById(defaultTreeNode.getPid());
    }

    @Override
    public void sort(final Comparator<DefaultTreeNode> comparator) {
        try {
            forEachCollection(this.nodes, new BiConsumer<Tree, DefaultTreeNode>() {
                private List<DefaultTreeNode> nodes;

                @Override
                public void accept(Tree tree, DefaultTreeNode node) {
                    Collections.sort(this.nodes, comparator);
                    setNodes(node.getChildren());
                }

                public void setNodes(Collection<DefaultTreeNode> nodes) {
                    this.nodes = new LinkedList<DefaultTreeNode>();
                    if ((nodes != null) && (!nodes.isEmpty())) {
                        this.nodes.addAll(nodes);
                    }
                }
            });
        } catch (Throwable localThrowable) {
        }
    }

    @Override
    public List<DefaultTreeNode> getRootNodes() {
        return this.nodes;
    }

    @Override
    public Collection<DefaultTreeNode> getChildren(String id) {
        DefaultTreeNode node = (DefaultTreeNode) this.nodeMap.get(id);
        if (node != null) {
            return node.getChildren();
        }
        return null;
    }

    @Override
    public void clear() {
        this.nodes.clear();
        this.nodeMap.clear();
    }

}