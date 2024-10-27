package com.sondertara.common.collection.tree;

import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.accessor.BasedStringAccessor;
import com.sondertara.common.collection.CollectionUtils;
import lombok.Getter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * @author huangxiaohu.1ih
 */
public class DefaultTreeNode<T extends DefaultTreeNode> extends BasedStringAccessor<String, Map<String, Object>> implements Serializable, ITreeNode<String, T> {
    @Override
    public String id() {
        return getId();
    }

    @Override
    public String pid() {
        return getPid();
    }

    @Override
    public boolean root() {
        return null == pid();
    }

    @Override
    public void markLeaf(Boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    @Override
    public void setChildren(List<T> children) {
        addChildNodes(children);
        this.isLeaf = false;

    }

    private static final long serialVersionUID = 3465696230080207245L;
    private String id;
    private String pid;
    private String name;
    private boolean isParent = false;

    @Getter
    private boolean isLeaf = true;
    private Collection<T> children;

    public DefaultTreeNode(String id, String pid, String name) {
        this(id, pid, name, false);
    }


    public DefaultTreeNode() {
    }

    public DefaultTreeNode(String id, String pid, String name, boolean isParent) {
        this(id, pid, name, isParent, null);
    }

    public DefaultTreeNode(String id, String pid, String name, boolean isParent, Collection<T> children) {
        if (id == null) {
            throw new IllegalArgumentException("node id is null");
        }
        this.id = id;
        this.pid = pid;
        this.name = name;
        if (name == null) {
            this.name = id.toString();
        }
        this.isParent = isParent;
        if (isParent) {
            this.children = children;
        }
        setTarget(new HashMap<>());
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        if (id != null) {
            this.id = id;
        }
    }

    public String getPid() {
        return this.pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public boolean getIsParent() {
        return this.isParent;
    }

    public void setIsParent(boolean isParent) {
        this.isParent = isParent;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    @Override
    public Collection<T> getChildren() {
        return this.children;
    }

    public void setChildren(Collection<T> children) {
        this.children = children;
    }

    public void addChildNode(T treeNode) {
        if (treeNode != null) {
            if (this.children == null) {
                this.children = new LinkedList<>();
            }
            this.children.add(treeNode);
            this.isLeaf = false;
        }
    }

    public void addChildNodes(List<T> treeNodes) {
        if ((treeNodes != null) && (!treeNodes.isEmpty())) {
            if (this.children == null) {
                this.children = new LinkedList<>();
            }
            this.children.addAll(treeNodes);
            this.isLeaf = false;
        }
    }

    public void removeChildNode(T treeNode) {
        if (this.children != null) {
            this.children.remove(treeNode);
            if (CollectionUtils.isEmpty(this.children)) {
                this.isLeaf = true;
            }
        }
    }

    public List<T> removeChildNodes() {
        if ((this.children == null) || (this.children.isEmpty())) {
            return Collections.emptyList();
        }
        List<T> ret = new LinkedList<>(this.children);
        this.children.clear();
        this.isLeaf = true;
        return ret;
    }

    public void clear() {
        if (this.children != null) {
            this.children.clear();
            this.isLeaf = true;
        }
    }

    public DefaultTreeNode getChildNodeById(String childNodeId) {
        if ((this.children == null) || (childNodeId == null)) {
            return null;
        }
        for (DefaultTreeNode node : this.children) {
            if (node.getId().equals(childNodeId)) {
                return node;
            }
        }
        return null;
    }

    public DefaultTreeNode getDescendant(String id) {
        if (getId().equals(id)) {
            return this;
        }

        Collection<T> children = getChildren();
        if (children != null) {
            for (DefaultTreeNode child : children) {
                DefaultTreeNode _node = child.getDescendant(id);
                if (_node != null) {
                    return _node;
                }
            }
        }
        return null;
    }

    public void extractDescendants(List<DefaultTreeNode> descendants, boolean containsSelf) {
        if (containsSelf) {
            descendants.add(this);
        }
        Collection<T> children = getChildren();
        if (children != null) {
            for (DefaultTreeNode child : children) {
                child.extractDescendants(descendants, true);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        T treeNode = (T) o;

        return this.id.equals(treeNode.getId());
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public Object get(String key) {
        return getTarget().get(key);
    }

    @Override
    public String getString(String key, String defaultValue) {
        Object obj = get(key);
        if (ObjectUtils.isNull(obj)) {
            return defaultValue;
        }
        return obj.toString();
    }

    @Override
    public void set(String key, Object value) {
        getTarget().put(key, value);
    }

    @Override
    public void remove(String key) {
        getTarget().remove(key);
    }
}
