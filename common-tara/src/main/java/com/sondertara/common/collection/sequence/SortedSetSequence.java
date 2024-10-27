package com.sondertara.common.collection.sequence;

import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.text.StringUtils;
import com.sondertara.common.collection.ArrayUtils;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.Lists;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class SortedSetSequence<E> implements Sequence<E> {
    private SortedSet<E> set;

    public SortedSetSequence(SortedSet<E> set) {
        this.set = set;
    }

    @Override
    public E first() {
        return set.first();
    }

    @Override
    public E last() {
        return set.last();
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean isEmpty() {
        return ObjectUtils.isEmpty(set);
    }

    @Override
    public boolean contains(Object o) {
        return set.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return set.iterator();
    }

    @Override
    public Object[] toArray() {
        return set.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return set.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return set.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return set.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return set.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return set.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return set.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return set.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return set.retainAll(c);
    }

    @Override
    public void clear() {
        set.clear();
    }

    @Override
    public E get(int index) {
        return Lists.asList(set).get(index);
    }

    @Override
    public E set(int index, E element) {
        E old = remove(index);
        add(element);
        return old;
    }

    @Override
    public void add(int index, E element) {
        add(element);
    }

    @Override
    public E remove(int index) {
        E old = get(index);
        if (old != null) {
            set.remove(old);
        }
        return old;
    }

    @Override
    public int indexOf(Object o) {
        return CollectionUtils.indexOf(Lists.asList(set), (E) o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return CollectionUtils.lastIndexOf(Lists.asList(set), (E) o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return Lists.asList(set).listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return Lists.asList(set).listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        int[] validIndexes = ArrayUtils.toPositiveIndexes(size(), fromIndex, toIndex);
        return Lists.asList(set).subList(validIndexes[0], validIndexes[1]);
    }

    @Override
    public SortedSetSequence<E> subSequence(int fromIndex, int toIndex) {
        List<E> list = subList(fromIndex, toIndex);
        TreeSet<E> set = new TreeSet<E>(this.set.comparator());
        set.addAll(list);
        return new SortedSetSequence<E>(set);
    }

    @Override
    public List<E> asList() {
        return Lists.newArrayList(set);
    }

    @Override
    public String toString() {
        return StringUtils.join(",","{","}",false,this.set);
    }
}
