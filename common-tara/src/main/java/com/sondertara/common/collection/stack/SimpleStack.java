package com.sondertara.common.collection.stack;

import com.sondertara.common.base.Emptys;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.iter.ReverseListIterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class SimpleStack<E> implements Stack<E> {
    protected List<E> list;

    public SimpleStack() {
        this.list = new ArrayList<>();
    }

    public SimpleStack(Collection<E> list) {
        this();
        if (Emptys.isNotEmpty(list)) {
            addAll(list);
        }
    }

    @Override
    public E push(E item) {
        list.add(item);
        return item;
    }

    @Override
    public E pop() {
        if (!isEmpty()) {
            return list.remove(size() - 1);
        }
        throw new EmptyStackException();
    }

    @Override
    public E peek() {
        if (!isEmpty()) {
            return list.get(size() - 1);
        }
        throw new EmptyStackException();
    }

    /**
     * @param   o   the desired object.
     */
    @Override
    public int search(Object o) {
        return CollectionUtils.firstOccurrence(this, (E) o);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new ReverseListIterator<E>(list);
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(E e) {
        push(e);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        CollectionUtils.forEach((Collection<E>) c, new Consumer<E>() {
            @Override
            public void accept(E e) {
                add(e);
            }
        });
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }
}
