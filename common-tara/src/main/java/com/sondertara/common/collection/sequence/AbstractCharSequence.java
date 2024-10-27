package com.sondertara.common.collection.sequence;

import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.collection.ArrayUtils;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.Lists;
import com.sondertara.common.collection.iter.IteratorIterable;
import com.sondertara.common.function.Predicate2;
import com.sondertara.common.reflect.type.Primitives;
import com.sondertara.common.struct.Holder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public abstract class AbstractCharSequence<S extends CharSequence> implements Sequence<Character> {
    protected S charSequence;

    @Override
    public Character first() {
        return charSequence.charAt(0);
    }

    @Override
    public Character last() {
        return charSequence.charAt(charSequence.length() - 1);
    }

    @Override
    public boolean isNull() {
        return charSequence == null;
    }

    @Override
    public int size() {
        return charSequence.length();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }


    @Override
    public Iterator<Character> iterator() {
        return new CharIterator(this.charSequence);
    }

    private static class CharIterator implements ListIterator<Character> {
        private int index = 0;
        private final int length;
        private boolean reversed;
        private CharSequence charSequence;

        CharIterator(CharSequence charSequence) {
            this(charSequence, false);
        }

        CharIterator(CharSequence charSequence, boolean reversed) {
            this(0, charSequence, reversed);
        }

        CharIterator(int index, CharSequence charSequence, boolean reversed) {
            this.charSequence = charSequence;
            this.index = index;
            this.length = charSequence.length();
            this.reversed = reversed;
        }

        @Override
        public boolean hasNext() {
            return reversed ? index >= 0 : index < length;
        }

        @Override
        public Character next() {
            if (hasNext()) {
                return charSequence.charAt(reversed ? index-- : index++);
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public boolean hasPrevious() {
            return reversed ? index < length : index > 0;
        }

        @Override
        public Character previous() {
            if (hasPrevious()) {
                return charSequence.charAt(reversed ? index++ : index--);
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public int nextIndex() {
            if (reversed) {
                return index - 1;
            } else {
                return index + 1;
            }
        }

        @Override
        public int previousIndex() {
            if (reversed) {
                return index + 1;
            } else {
                return index - 1;
            }
        }

        @Override
        public void set(Character character) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Character character) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Object[] toArray() {
        List<Character> arr = subList(0, size());
        return arr.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        List<Character> arr = subList(0, size());
        return arr.toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        List<Character> list = c.stream().filter(new Predicate<Object>() {
            @Override
            public boolean test(Object value) {
                return Primitives.isChar(value.getClass());
            }
        }).map(new Function<Object, Character>() {
            @Override
            public Character apply(Object input) {
                return (Character) input;
            }
        }).collect(Collectors.toList());

        Holder<List<Character>> comparedCharsHolder = new Holder<>(list);

        if (ObjectUtils.length(c) != ObjectUtils.length(comparedCharsHolder.get())) {
            return false;
        }
        if (comparedCharsHolder.get().isEmpty()) {
            return false;
        }

        Iterator<Character> iterator = this.iterator();
        Collection<Character> collection = Lists.asList(new IteratorIterable<Character>(iterator));
        CollectionUtils.forEach(collection,
                new BiConsumer<Integer, Character>() {
                    @Override
                    public void accept(Integer index, Character ch) {
                        if (CollectionUtils.contains(comparedCharsHolder.get(), ch)) {
                            comparedCharsHolder.get().remove(ch);
                        }
                    }
                }, new Predicate2<Integer, Character>() {
                    @Override
                    public boolean test(Integer key, Character value) {
                        return comparedCharsHolder.isEmpty();
                    }
                });

        return comparedCharsHolder.isEmpty();
    }

    @Override
    public Character get(int index) {
        return charSequence.charAt(index);
    }

    @Override
    public int indexOf(Object o) {
        Iterator<Character> iter = iterator();
        return indexOfByIterator(iter, o);
    }

    private int indexOfByIterator(Iterator<Character> iter, Object o) {
        int i = -1;
        while (iter.hasNext()) {
            Character c = iter.next();
            i++;
            if (ObjectUtils.equals(c, o)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        Iterator<Character> iter = new CharIterator(this.charSequence, true);
        int index = indexOfByIterator(iter, o);
        if (index == -1) {
            return -1;
        }
        return ArrayUtils.reverseIndex(size(), index);
    }

    @Override
    public ListIterator<Character> listIterator() {
        return new CharIterator(this.charSequence);
    }

    @Override
    public ListIterator<Character> listIterator(int index) {
        return new CharIterator(index, this.charSequence, false);
    }

    @Override
    public List<Character> subList(int fromIndex, int toIndex) {
        int[] validIndexes = ArrayUtils.toPositiveIndexes(size(), fromIndex, toIndex);
        List<Character> characters = new ArrayList<>();
        for (int i = validIndexes[0]; i < validIndexes[1]; i++) {
            characters.add(this.charSequence.charAt(i));
        }
        return characters;
    }
}
