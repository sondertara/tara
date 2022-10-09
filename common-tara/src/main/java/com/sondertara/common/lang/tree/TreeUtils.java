package com.sondertara.common.lang.tree;

import com.google.common.collect.Lists;
import com.sondertara.common.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

/**
 * 树形结构工具类
 *
 * @author meilin.huang
 * @version 1.0
 * @date 2019-08-24 1:57 下午
 */
public class TreeUtils {

    /**
     * 根据所有树节点列表，生成含有所有树形结构的列表
     *
     * @param nodes 树形节点列表
     * @param <T>   节点类型
     * @return 树形结构列表
     */
    public static <T extends TreeNode<?, T>> List<T> generateTrees(List<T> nodes) {
        List<T> roots = new ArrayList<>();
        for (Iterator<T> ite = nodes.iterator(); ite.hasNext();) {
            T node = ite.next();
            if (node.root()) {
                roots.add(node);
                // 从所有节点列表中删除该节点，以免后续重复遍历该节点
                ite.remove();
            }
        }

        roots.forEach(r -> setChildren(r, nodes));
        return roots;
    }

    /**
     * 从所有节点列表中查找并设置parent的所有子节点
     *
     * @param parent 父节点
     * @param nodes  所有树节点列表
     */
    public static <T extends TreeNode<?, T>> void setChildren(T parent, List<T> nodes) {
        List<T> children = new ArrayList<>();
        Object parentId = parent.id();
        for (Iterator<T> ite = nodes.iterator(); ite.hasNext();) {
            T node = ite.next();
            if (Objects.equals(node.pid(), parentId)) {
                children.add(node);
                // 从所有节点列表中删除该节点，以免后续重复遍历该节点
                ite.remove();
            }
        }
        // 如果孩子为空，则直接返回,否则继续递归设置孩子的孩子
        if (children.isEmpty()) {
            parent.markLeaf(true);
            return;
        }
        parent.markLeaf(false);
        parent.setChildren(children);
        children.forEach(m -> {
            // 递归设置子节点
            setChildren(m, nodes);
        });
    }

    /**
     * 获取指定树节点下的所有叶子节点
     *
     * @param parent 父节点
     * @param <T>    实际节点类型
     * @return 叶子节点
     */
    public static <T extends TreeNode<?, T>> List<T> getLeafList(T parent) {
        List<T> leafs = new ArrayList<>();
        fillLeaf(parent, leafs);
        return leafs;
    }

    /**
     * 将parent的所有叶子节点填充至leafs列表中
     *
     * @param parent   父节点
     * @param leafList 叶子节点列表
     * @param <T>      实际节点类型
     */
    public static <T extends TreeNode<?, T>> void fillLeaf(T parent, List<T> leafList) {
        List<T> children = parent.getChildren();
        // 如果节点没有子节点则说明为叶子节点
        if (CollectionUtils.isEmpty(children)) {
            leafList.add(parent);
            return;
        }
        // 递归调用子节点，查找叶子节点
        for (T child : children) {
            fillLeaf(child, leafList);
        }
    }

    /**
     * 树形结构转化为行数据
     * <p>
     * {"user":"张三","id":1,children:[{"user":"张三1","id":11}]}
     * <p>
     * 转换为
     * ["张三","1","张三1","11"]
     *
     * @param dataList 树形结构数据
     * @param keys     需要取出的字段
     * @param <T>      the type of node
     * @return the row list
     */
    public static <T extends TreeNode<?, T>> List<List<String>> treeValueToRow(List<T> dataList, String... keys) {
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }

        List<List<String>> result = new ArrayList<>();

        for (T item : dataList) {

            iteratorNode(item, new Stack<>(), result, Arrays.asList(keys));
        }

        return result;
    }

    private static <T extends TreeNode<?, T>> void iteratorNode(T root, Stack<String> row, List<List<String>> result,
            List<String> keys) {

        try {
            Class<?> rootClass = root.getClass();
            for (String key : keys) {
                Field field = rootClass.getDeclaredField(key);
                field.setAccessible(true);
                row.push(null == field.get(root) ? "" : field.get(root).toString());

            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (CollectionUtils.isEmpty(root.getChildren())) {
            result.add(Lists.newArrayList(row));
            return;

        }
        for (T child : root.getChildren()) {
            iteratorNode(child, row, result, keys);
            for (int i = 0; i < keys.size(); i++) {
                row.pop();
            }

        }
    }
}
