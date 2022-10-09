package com.sondertara.common.lang.tree;

import java.util.List;

/**
 * 树节点父类，所有需要使用{@link TreeUtils}工具类形成树形结构等操作的节点都需要实现该接口
 *
 * @param <T> 节点id类型
 * @author huangxiaohu
 */
public interface TreeNode<T, E> {
    /**
     * 获取节点id
     *
     * @return 树节点id
     */
    T id();

    /**
     * 获取该节点的父节点id
     *
     * @return 父节点id
     */
    T pid();

    /**
     * 是否是根节点
     *
     * @return true：根节点
     */
    boolean root();

    /**
     * 是否叶子节点
     *
     * @param isLeaf the leaf node
     */
    void markLeaf(Boolean isLeaf);

    /**
     * 设置节点的子节点列表
     *
     * @param children 子节点
     */
    void setChildren(List<E> children);

    /**
     * 获取所有子节点
     *
     * @return 子节点列表
     */
    List<E> getChildren();
}
