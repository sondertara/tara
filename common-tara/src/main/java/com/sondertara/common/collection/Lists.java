package com.sondertara.common.collection;

import com.sondertara.common.base.Assert;
import com.sondertara.common.base.Emptys;
import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.base.Valid;
import com.sondertara.common.collection.iter.Iterables;
import com.sondertara.common.math.Maths;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.math.RoundingMode;
import java.util.AbstractList;
import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class Lists {
    private Lists() {
    }

    @SafeVarargs
    public static <E> ArrayList<E> newArrayList(E... elements) {
        ArrayList<E> list = new ArrayList<>(elements.length);
        Collections.addAll(list, elements);
        return list;
    }


    public static <E> ArrayList<E> newArrayList(Iterable<E> elements) {
        return newArrayList(elements.iterator());

    }

    public static <E> ArrayList<E> newArrayList(Iterator<E> elements) {
        ArrayList<E> list = new ArrayList<>();
        Iterables.addAll(list, elements);
        return list;
    }

    public static <E> ArrayList<E> newArrayListWithCapacity(int initialArraySize) {
        return new ArrayList<E>(Maths.max(0, initialArraySize));
    }

    public static <E> ArrayList<E> newArrayListWithExpectedSize(int estimatedSize) {
        return new ArrayList<E>(Maths.max(0, estimatedSize));
    }


    @SafeVarargs
    public static <E> LinkedList<E> newLinkedList(E... elements) {
        LinkedList<E> list = new LinkedList<>();
        Collections.addAll(list, elements);
        return list;
    }

    public static <E> LinkedList<E> newLinkedList(Iterable<E> elements) {
        return new LinkedList<E>(Lists.asList(elements));
    }

    public static <E> LinkedList<E> newLinkedList(@Nullable Iterator<E> elements) {
        LinkedList<E> list = new LinkedList<>();
        Iterables.addAll(list, elements);
        return list;
    }

    public static <E> List<E> asList(@Nullable Iterable<E> iterable) {
        return asList(iterable, true);
    }

    public static <E> List<E> asList(@Nullable Iterable<E> iterable, boolean mutable) {
        if (Emptys.isNull(iterable)) {
            return new ArrayList<>();
        }
        if (!(iterable instanceof List)) {
            return StreamUtils.of(iterable).collect(Collectors.toList());
        }
        List<E> list = (List<E>) iterable;
        if (!mutable) {
            return Collections.unmodifiableList(list);
        }
        return list;
    }

    /**
     * Convert an array to a ArrayList
     */
    @SafeVarargs
    public static <E> List<E> asList(@Nullable E... array) {
        return asList(array, true, ListType.ArrayList);
    }

    /**
     * Convert an array to a ArrayList or a LinkedList
     */
    public static <E> List<E> asList(@Nullable E[] array, ListType listType) {
        return asList(array, true, listType);
    }

    /**
     * Convert an array to a List, if the 'mutable' argument is true, will return an unmodifiable List
     */
    public static <E> List<E> asList(@Nullable E[] array, boolean mutable, ListType listType) {
        List<E> immutableList = Emptys.isEmpty(array) ? Collections.<E>emptyList() : Arrays.asList(array);
        if (listType == null) {
            listType = ListType.ArrayList;
        }
        List<E> list;
        switch (listType) {
            case LinkedList:
                list = new LinkedList<E>(immutableList);
                break;
            case ArrayList:
                list = new ArrayList<E>(immutableList);
                break;
            case STACK:
                list = new Stack<E>();
                list.addAll(immutableList);
                break;
            case VECTOR:
                list = new Vector<E>(immutableList);
                break;
            case CopyOnWrite:
                list = new CopyOnWriteArrayList<E>(immutableList);
                break;
            default:
                list = new ArrayList<E>(immutableList);
                break;
        }
        if (!mutable) {
            list = Collections.unmodifiableList(list);
        }
        return list;
    }


    /**
     * Avoid NPE, create an empty, new list when the specified list is null
     */
    public static <E> List<E> getEmptyListIfNull(@Nullable List<E> list) {
        return getEmptyListIfNull(list, null);
    }

    /**
     * @see #getEmptyListIfNull(List)
     */
    public static <E> List<E> getEmptyListIfNull(@Nullable List<E> list, @Nullable ListType listType) {
        if (list == null) {
            if (listType == null) {
                return new ArrayList<>();
            }
            switch (listType) {
                case LinkedList:
                    list = new LinkedList<>();
                    break;
                case CopyOnWrite:
                    list = new CopyOnWriteArrayList<E>();
                    break;
                case STACK:
                    list = new Stack<E>();
                    break;
                case VECTOR:
                    list = new Vector<E>();
                    break;
                case ArrayList:
                    list = new ArrayList<>();
                    break;
                default:
                    list = new ArrayList<>();
                    break;
            }
        }
        return list;
    }


    public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList() {
        return new CopyOnWriteArrayList<E>();
    }

    public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList(Iterable<E> elements) {
        return new CopyOnWriteArrayList<E>(Lists.asList(elements));
    }

    public static <E> List<E> asList(E first, E[] rest) {
        List<E> list = new ArrayList<E>();
        list.add(first);
        CollectionUtils.addAll(list, rest);
        return list;
    }

    public static <E> List<E> asList(E first, E second, E[] rest) {
        List<E> list = new ArrayList<E>();
        list.add(first);
        list.add(second);
        CollectionUtils.addAll(list, rest);
        return list;
    }


    public enum ListType {
        ArrayList,
        LinkedList,
        CopyOnWrite,
        VECTOR,
        STACK;

        public static ListType ofList(@Nullable List list) {
            if (list == null) {
                return ArrayList;
            }
            return inferListType(list);
        }

        public static ListType ofList(Class<?> clazz) {
            if (java.util.ArrayList.class.equals(clazz)) {
                return ListType.ArrayList;

            } else if (java.util.LinkedList.class.equals(clazz)) {
                return ListType.LinkedList;

            } else if (com.sondertara.common.collection.stack.Stack.class.equals(clazz)) {
                return ListType.STACK;

            } else if (java.util.Vector.class.equals(clazz)) {
                return ListType.VECTOR;

            } else if (CopyOnWriteArrayList.class.equals(clazz)) {
                return ListType.CopyOnWrite;
            } else {
                return ListType.ArrayList;
            }
        }
    }


    private static ListType inferListType(@NonNull List list) {
        if (list instanceof CopyOnWriteArrayList) {
            return ListType.CopyOnWrite;
        }
        if (list instanceof LinkedList) {
            return ListType.LinkedList;
        }
        if (list instanceof com.sondertara.common.collection.stack.Stack) {
            return ListType.STACK;
        }
        if (list instanceof Vector) {
            return ListType.VECTOR;
        }
        if (list instanceof ArrayList) {
            return ListType.ArrayList;
        }
        return ListType.ArrayList;
    }

    public static <E> List<E> immutableList(Collection<E> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        return Collections.unmodifiableList(new ArrayList<>(list));
    }

    public static <E> List<E> immutableList() {
        return Collections.unmodifiableList(new ArrayList<>());
    }

    public static <E> List<E> immutableArrayList(List<E> list) {
        return Collections.unmodifiableList(ObjectUtils.defaultIfNull(list, Collections.emptyList()));
    }

    /**
     *
     */
    @SafeVarargs
    public static <E> List<E> immutableList(E... elements) {
        return immutableList(newArrayList(elements));
    }

    public static <E> List<E> immutableList(List<E> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        return Collections.unmodifiableList(list);
    }


    public static <F extends @Nullable Object, T extends @Nullable Object> List<T> transform(
            List<F> fromList, Function<? super F, ? extends T> function) {
        return (fromList instanceof RandomAccess)
                ? new TransformingRandomAccessList<>(fromList, function)
                : new TransformingSequentialList<>(fromList, function);
    }

    /**
     * Implementation of a sequential transforming list.
     *
     * @see Lists#transform
     */
    private static class TransformingSequentialList<
            F extends @Nullable Object, T extends @Nullable Object>
            extends AbstractSequentialList<T> implements Serializable {
        final List<F> fromList;
        final Function<? super F, ? extends T> function;

        TransformingSequentialList(List<F> fromList, Function<? super F, ? extends T> function) {
            this.fromList = Assert.notNull(fromList);
            this.function = Assert.notNull(function);
        }

        /**
         * The default implementation inherited is based on iteration and removal of each element which
         * can be overkill. That's why we forward this call directly to the backing list.
         */
        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            fromList.subList(fromIndex, toIndex).clear();
        }

        @Override
        public int size() {
            return fromList.size();
        }

        @Override
        public boolean isEmpty() {
            return fromList.isEmpty();
        }

        @Override
        public ListIterator<T> listIterator(final int index) {
            return new TransformedListIterator<F, T>(fromList.listIterator(index)) {
                @Override
                T transform(F from) {
                    return function.apply(from);
                }
            };
        }

        @Override
        public boolean removeIf(Predicate<? super T> filter) {
            Assert.notNull(filter);
            return fromList.removeIf(element -> filter.test(function.apply(element)));
        }

        private static final long serialVersionUID = 0;
    }

    /**
     * Implementation of a transforming random access list. We try to make as many of these methods
     * pass-through to the source list as possible so that the performance characteristics of the
     * source list and transformed list are similar.
     *
     * @see Lists#transform
     */
    private static class TransformingRandomAccessList<
            F extends @Nullable Object, T extends @Nullable Object>
            extends AbstractList<T> implements RandomAccess, Serializable {
        final List<F> fromList;
        final Function<? super F, ? extends T> function;

        TransformingRandomAccessList(List<F> fromList, Function<? super F, ? extends T> function) {
            this.fromList = Assert.notNull(fromList);
            this.function = Assert.notNull(function);
        }

        /**
         * The default implementation inherited is based on iteration and removal of each element which
         * can be overkill. That's why we forward this call directly to the backing list.
         */
        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            fromList.subList(fromIndex, toIndex).clear();
        }

        @Override

        public T get(int index) {
            return function.apply(fromList.get(index));
        }

        @Override
        public Iterator<T> iterator() {
            return listIterator();
        }

        @Override
        public ListIterator<T> listIterator(int index) {
            return new TransformedListIterator<F, T>(fromList.listIterator(index)) {
                @Override
                T transform(F from) {
                    return function.apply(from);
                }
            };
        }

        @Override
        public boolean isEmpty() {
            return fromList.isEmpty();
        }

        @Override
        public boolean removeIf(Predicate<? super T> filter) {
            Assert.notNull(filter);
            return fromList.removeIf(element -> filter.test(function.apply(element)));
        }

        @Override

        public T remove(int index) {
            return function.apply(fromList.remove(index));
        }

        @Override
        public int size() {
            return fromList.size();
        }

        private static final long serialVersionUID = 0;
    }

    /**
     * Returns consecutive {@linkplain List#subList(int, int) sublists} of a list, each of the same
     * size (the final list may be smaller). For example, partitioning a list containing {@code [a, b,
     * c, d, e]} with a partition size of 3 yields {@code [[a, b, c], [d, e]]} -- an outer list
     * containing two inner lists of three and two elements, all in the original order.
     *
     * <p>The outer list is unmodifiable, but reflects the latest state of the source list. The inner
     * lists are sublist views of the original list, produced on demand using {@link List#subList(int,
     * int)}, and are subject to all the usual caveats about modification as explained in that API.
     *
     * @param list the list to return consecutive sublists of
     * @param size the desired size of each sublist (the last may be smaller)
     * @return a list of consecutive sublists
     * @throws IllegalArgumentException if {@code partitionSize} is nonpositive
     */
    public static <T extends @Nullable Object> List<List<T>> partition(List<T> list, int size) {
        Assert.notNull(list);
        Valid.isTrue(size > 0);
        return (list instanceof RandomAccess)
                ? new RandomAccessPartition<>(list, size)
                : new Partition<>(list, size);
    }

    private static class Partition<T extends @Nullable Object> extends AbstractList<List<T>> {
        final List<T> list;
        final int size;

        Partition(List<T> list, int size) {
            this.list = list;
            this.size = size;
        }

        @Override
        public List<T> get(int index) {
            Assert.checkIndex(index, size());
            int start = index * size;
            int end = Math.min(start + size, list.size());
            return list.subList(start, end);
        }

        @Override
        public int size() {
            return (int) Maths.div(list.size(), size, 0, RoundingMode.CEILING);
        }

        @Override
        public boolean isEmpty() {
            return list.isEmpty();
        }
    }

    private static class RandomAccessPartition<T extends @Nullable Object> extends Partition<T>
            implements RandomAccess {
        RandomAccessPartition(List<T> list, int size) {
            super(list, size);
        }
    }

    /**
     * Returns a view of the specified {@code CharSequence} as a {@code List<Character>}, viewing
     * {@code sequence} as a sequence of Unicode code units. The view does not support any
     * modification operations, but reflects any changes to the underlying character sequence.
     *
     * @param sequence the character sequence to view as a {@code List} of characters
     * @return an {@code List<Character>} view of the character sequence
     * @since 7.0
     */
    public static List<Character> charactersOf(CharSequence sequence) {
        return new CharSequenceAsList(Assert.notNull(sequence));
    }


    private static final class CharSequenceAsList extends AbstractList<Character> {
        private final CharSequence sequence;

        CharSequenceAsList(CharSequence sequence) {
            this.sequence = sequence;
        }

        @Override
        public Character get(int index) {
            Assert.checkIndex(index, size()); // for GWT
            return sequence.charAt(index);
        }

        @Override
        public int size() {
            return sequence.length();
        }
    }

    /**
     * Returns a reversed view of the specified list. For example, {@code
     * Lists.reverse(Arrays.asList(1, 2, 3))} returns a list containing {@code 3, 2, 1}. The returned
     * list is backed by this list, so changes in the returned list are reflected in this list, and
     * vice-versa. The returned list supports all of the optional list operations supported by this
     * list.
     *
     * <p>The returned list is random-access if the specified list is random access.
     *
     * @since 7.0
     */
    public static <T extends @Nullable Object> List<T> reverse(List<T> list) {
        if (list instanceof ReverseList) {
            return ((ReverseList<T>) list).getForwardList();
        } else if (list instanceof RandomAccess) {
            return new RandomAccessReverseList<>(list);
        } else {
            return new ReverseList<>(list);
        }
    }

    private static class ReverseList<T extends @Nullable Object> extends AbstractList<T> {
        private final List<T> forwardList;

        ReverseList(List<T> forwardList) {
            this.forwardList = Assert.notNull(forwardList);
        }

        List<T> getForwardList() {
            return forwardList;
        }

        private int reverseIndex(int index) {
            int size = size();
            Assert.checkIndex(index, size);
            return (size - 1) - index;
        }

        private int reversePosition(int index) {
            int size = size();
            Assert.checkIndex(index, size);
            return size - index;
        }

        @Override
        public void add(int index, T element) {
            forwardList.add(reversePosition(index), element);
        }

        @Override
        public void clear() {
            forwardList.clear();
        }

        @Override

        public T remove(int index) {
            return forwardList.remove(reverseIndex(index));
        }

        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            subList(fromIndex, toIndex).clear();
        }

        @Override

        public T set(int index, T element) {
            return forwardList.set(reverseIndex(index), element);
        }

        @Override

        public T get(int index) {
            return forwardList.get(reverseIndex(index));
        }

        @Override
        public int size() {
            return forwardList.size();
        }

        @Override
        public List<T> subList(int fromIndex, int toIndex) {
            Valid.checkPositionIndexes(fromIndex, toIndex, size());
            return reverse(forwardList.subList(reversePosition(toIndex), reversePosition(fromIndex)));
        }

        @Override
        public Iterator<T> iterator() {
            return listIterator();
        }

        @Override
        public ListIterator<T> listIterator(int index) {
            int start = reversePosition(index);
            final ListIterator<T> forwardIterator = forwardList.listIterator(start);
            return new ListIterator<T>() {

                boolean canRemoveOrSet;

                @Override
                public void add(T e) {
                    forwardIterator.add(e);
                    forwardIterator.previous();
                    canRemoveOrSet = false;
                }

                @Override
                public boolean hasNext() {
                    return forwardIterator.hasPrevious();
                }

                @Override
                public boolean hasPrevious() {
                    return forwardIterator.hasNext();
                }

                @Override

                public T next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    canRemoveOrSet = true;
                    return forwardIterator.previous();
                }

                @Override
                public int nextIndex() {
                    return reversePosition(forwardIterator.nextIndex());
                }

                @Override

                public T previous() {
                    if (!hasPrevious()) {
                        throw new NoSuchElementException();
                    }
                    canRemoveOrSet = true;
                    return forwardIterator.next();
                }

                @Override
                public int previousIndex() {
                    return nextIndex() - 1;
                }

                @Override
                public void remove() {
                    Valid.isTrue(canRemoveOrSet, "no calls to next() since the last call to remove()");
                    forwardIterator.remove();
                    canRemoveOrSet = false;
                }

                @Override
                public void set(T e) {
                    Valid.checkState(canRemoveOrSet);
                    forwardIterator.set(e);
                }
            };
        }
    }

    private static class RandomAccessReverseList<T extends @Nullable Object> extends ReverseList<T>
            implements RandomAccess {
        RandomAccessReverseList(List<T> forwardList) {
            super(forwardList);
        }
    }

    /**
     * An implementation of {@link List#hashCode()}.
     */
    static int hashCodeImpl(List<?> list) {
        // TODO(lowasser): worth optimizing for RandomAccess?
        int hashCode = 1;
        for (Object o : list) {
            hashCode = 31 * hashCode + (o == null ? 0 : o.hashCode());

            hashCode = ~~hashCode;
            // needed to deal with GWT integer overflow
        }
        return hashCode;
    }


    /**
     * An implementation of {@link List#addAll(int, Collection)}.
     */
    static <E extends @Nullable Object> boolean addAllImpl(
            List<E> list, int index, Iterable<? extends E> elements) {
        boolean changed = false;
        ListIterator<E> listIterator = list.listIterator(index);
        for (E e : elements) {
            listIterator.add(e);
            changed = true;
        }
        return changed;
    }

    /**
     * An implementation of {@link List#indexOf(Object)}.
     */
    static int indexOfImpl(List<?> list, @Nullable Object element) {
        if (list instanceof RandomAccess) {
            return indexOfRandomAccess(list, element);
        } else {
            ListIterator<?> listIterator = list.listIterator();
            while (listIterator.hasNext()) {
                if (Objects.equals(element, listIterator.next())) {
                    return listIterator.previousIndex();
                }
            }
            return -1;
        }
    }

    private static int indexOfRandomAccess(List<?> list, @Nullable Object element) {
        int size = list.size();
        if (element == null) {
            for (int i = 0; i < size; i++) {
                if (list.get(i) == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (element.equals(list.get(i))) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * An implementation of {@link List#lastIndexOf(Object)}.
     */
    static int lastIndexOfImpl(List<?> list, @Nullable Object element) {
        if (list instanceof RandomAccess) {
            return lastIndexOfRandomAccess(list, element);
        } else {
            ListIterator<?> listIterator = list.listIterator(list.size());
            while (listIterator.hasPrevious()) {
                if (Objects.equals(element, listIterator.previous())) {
                    return listIterator.nextIndex();
                }
            }
            return -1;
        }
    }

    private static int lastIndexOfRandomAccess(List<?> list, @Nullable Object element) {
        if (element == null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                if (list.get(i) == null) {
                    return i;
                }
            }
        } else {
            for (int i = list.size() - 1; i >= 0; i--) {
                if (element.equals(list.get(i))) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Returns an implementation of {@link List#listIterator(int)}.
     */
    static <E extends @Nullable Object> ListIterator<E> listIteratorImpl(List<E> list, int index) {
        return new AbstractListWrapper<>(list).listIterator(index);
    }


    private static class AbstractListWrapper<E extends @Nullable Object> extends AbstractList<E> {
        final List<E> backingList;

        AbstractListWrapper(List<E> backingList) {
            this.backingList = Assert.notNull(backingList);
        }

        @Override
        public void add(int index, E element) {
            backingList.add(index, element);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            return backingList.addAll(index, c);
        }

        @Override

        public E get(int index) {
            return backingList.get(index);
        }

        @Override

        public E remove(int index) {
            return backingList.remove(index);
        }

        @Override

        public E set(int index, E element) {
            return backingList.set(index, element);
        }

        @Override
        public boolean contains(@Nullable Object o) {
            return backingList.contains(o);
        }

        @Override
        public int size() {
            return backingList.size();
        }
    }

    private static class RandomAccessListWrapper<E extends @Nullable Object>
            extends AbstractListWrapper<E> implements RandomAccess {
        RandomAccessListWrapper(List<E> backingList) {
            super(backingList);
        }
    }

    /**
     * Used to avoid http://bugs.sun.com/view_bug.do?bug_id=6558557
     */
    static <T extends @Nullable Object> List<T> cast(Iterable<T> iterable) {
        return (List<T>) iterable;
    }

}
