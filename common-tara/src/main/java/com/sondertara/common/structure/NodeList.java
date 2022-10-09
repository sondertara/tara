package com.sondertara.common.structure;

import java.util.ArrayList;
import java.util.List;

/**
 * the node list with head point
 *
 * @author huangxiaohu
 * @date 2021/11/17 15:40
 * @since 1.0.0
 */
public class NodeList<E> {

    private class Node {
        public E data;
        public Node next;

        public Node(E data, Node next) {
            this.data = data;
            this.next = next;
        }

        public Node(E data) {
            this.data = data;
        }
    }

    private int size;

    private final Node head;

    public NodeList() {
        // empty head
        head = new Node(null, null);
    }

    public int getSize() {
        return size;
    }

    /**
     * insert with index
     *
     * @param index the index
     * @param data  element
     */
    public void add(int index, E data) {

        if (index < 0 || index > size) {
            throw new IllegalArgumentException("index out bounds");
        }
        // 临时节点指向头节点
        Node tempNode = head;
        for (int i = 0; i < index; i++) {
            tempNode = tempNode.next;
        }
        Node newNode = new Node(data);
        newNode.next = tempNode.next;
        tempNode.next = newNode;
        size++;

    }

    /**
     * add first
     *
     * @param data element
     */
    public void addFirst(E data) {
        add(0, data);

    }

    /**
     * add last
     *
     * @param data element
     */
    public void addLast(E data) {
        add(size, data);

    }

    /**
     * get the element by index
     *
     * @param index index
     * @return element
     */
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("index out bounds");
        }
        Node tempNode = head.next;
        for (int i = 0; i < index; i++) {
            tempNode = tempNode.next;
        }
        return tempNode.data;
    }

    /**
     * get first element
     *
     * @return element
     */
    public E getFirst() {
        return get(0);
    }

    /**
     * get last element
     *
     * @return element
     */
    public E getLast() {
        return get(size - 1);
    }

    public List<E> getAll() {
        List<E> list = new ArrayList<E>();
        Node tempNode = head.next;
        while (tempNode != null) {
            list.add(tempNode.data);
            tempNode = tempNode.next;
        }
        return list;
    }

    /**
     * contains
     *
     * @param data element
     * @return is the list contain element
     */
    public boolean contains(E data) {
        Node node = head.next;
        while (node != null) {
            if (data.equals(node.data)) {
                return true;
            }
            node = node.next;
        }
        return false;
    }

    /**
     * not contains
     *
     * @param data element
     * @return is the element is not in the list
     */
    public boolean notContains(E data) {
        return !contains(data);
    }

    /**
     * is empty
     *
     * @return if the list size is zero
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * set node at index it will replace the old
     *
     * @param index index
     * @param data  node
     */
    public void set(int index, E data) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("index out bounds");
        }
        Node tempNode = head.next;
        for (int i = 0; i < index; i++) {
            tempNode = tempNode.next;
        }
        tempNode.data = data;
    }

    /**
     * set first node
     *
     * @param data node
     */
    public void setFirst(E data) {
        set(0, data);
    }

    /**
     * set last node
     *
     * @param data node
     */
    public void setLast(E data) {
        set(size - 1, data);
    }

    /**
     * remove by index
     *
     * @param index index
     * @return the removed node
     */
    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("index out bounds");
        }
        Node tempNode = head;
        for (int i = 0; i < index; i++) {
            tempNode = tempNode.next;
        }
        Node resNode = tempNode.next;
        E data = resNode.data;
        tempNode.next = tempNode.next.next;
        resNode = null;
        size--;
        return data;
    }

    /**
     * remove first
     *
     * @return the removed node
     */
    public E removeFirst() {
        return remove(0);
    }

    /**
     * remove last
     *
     * @return the removed node
     */
    public E removeLast() {
        return remove(size - 1);
    }
}
