package com.sondertara.common.collection;


import com.sondertara.common.base.Emptys;
import com.sondertara.common.collection.iter.Iterables;
import com.sondertara.common.concurrent.ConcurrentHashSet;
import com.sondertara.common.math.Maths;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;

public class Sets {
    private Sets() {

    }

    public static <E extends Enum<E>> EnumSet<E> newEnumSet(Iterable<E> iterable, Class<E> elementType) {
        EnumSet<E> set = EnumSet.noneOf(elementType);
        set.addAll(Lists.asList(iterable));
        return set;
    }


    @SafeVarargs
    public static <E> HashSet<E> newHashSet(E... elements) {
        return new HashSet<E>(Arrays.asList(elements));
    }

    public static <E> HashSet<E> newHashSet(Iterable<E> elements) {
        return new HashSet<E>(Lists.asList(elements));
    }

    public static <E> HashSet<E> newHashSet(Iterator<E> elements) {
        HashSet<E> set = new HashSet<>();
        Iterables.addAll(set, elements);
        return set;
    }

    public static <E> HashSet<E> newHashSetWithExpectedSize(int expectedSize) {
        return new HashSet<E>(Maths.max(0, expectedSize));
    }

    public static <E> Set<E> newConcurrentHashSet() {
        return new ConcurrentHashSet<>();
    }

    public static <E> Set<E> newConcurrentHashSet(Iterable<E> elements) {
        Set<E> set = new ConcurrentHashSet<>();
        set.addAll(Lists.asList(elements));
        return set;
    }


    public static <E> LinkedHashSet<E> newLinkedHashSet(Iterable<E> elements) {
        return new LinkedHashSet<E>(Lists.asList(elements));
    }

    @SafeVarargs
    public static <E> LinkedHashSet<E> newLinkedHashSet(@Nullable E... elements) {
        return new LinkedHashSet<E>(Lists.asList(elements));
    }

    public static <E> LinkedHashSet<E> newLinkedHashSetWithExpectedSize(int expectedSize) {
        return new LinkedHashSet<E>(Maths.max(0, expectedSize));
    }


    public static <E extends Comparable> TreeSet<E> newTreeSet(Iterable<E> elements) {
        return new TreeSet<E>(Lists.asList(elements));
    }

    public static <E> TreeSet<E> newTreeSet(E... elements) {
        return new TreeSet<E>(Lists.asList(elements));
    }


    public static <E> Set<E> newIdentityHashSet() {
        return Collections.newSetFromMap(Maps.<E, Boolean>newIdentityHashMap());
    }


    public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet(Iterable<E> elements) {
        return new CopyOnWriteArraySet<E>(Lists.asList(elements));
    }

    public static <E> Set<E> immutableSet() {
        return immutableSet((Set<E>) null);
    }

    public static <E> Set<E> immutableSet(Set<E> set) {
        if (set == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(set);
    }

    /**
     *
     */
    public static <E> Set<E> immutableSet(Collection<E> collection) {
        return Collections.unmodifiableSet(Sets.asSet(collection));
    }

    /**
     *
     */
    @SafeVarargs
    public static <E> Set<E> immutableSet(E... elements) {
        return Collections.unmodifiableSet(Sets.asSet(elements));
    }

    public static <E> Set<E> asSet(Enumeration<E> iterator) {
        Set<E> list = new LinkedHashSet<>();
        if (iterator != null) {
            while (iterator.hasMoreElements()) {
                list.add(iterator.nextElement());
            }
        }
        return list;
    }

    /**
     * Convert an array to a ArrayList
     */
    public static <E> Set<E> asSet(@Nullable E... array) {
        return asSet(array, true, SetType.HashSet);
    }

    /**
     * Convert an array to a ArrayList or a LinkedList
     */
    public static <E> Set<E> asSet(@Nullable E[] array, SetType setType) {
        return asSet(array, true, setType);
    }

    public static <E> Set<E> asSet(@Nullable Iterable<E> iterable) {
        return asSet(iterable, true);
    }

    public static <E> Set<E> asSet(@Nullable Iterable<E> iterable, boolean mutable) {
        if (Emptys.isNull(iterable)) {
            Set set = new HashSet<>();
            return mutable ? set : Collections.unmodifiableSet(set);
        }

        Collection<E> c = (iterable instanceof Collection) ? (Collection) iterable : Lists.asList(iterable);
        Set set = new HashSet(c);
        return mutable ? set : Collections.unmodifiableSet(set);
    }

    /**
     * Convert an array to a List, if the 'mutable' argument is true, will return an unmodifiable List
     */
    public static <E> Set<E> asSet(@Nullable E[] array, boolean mutable, @Nullable SetType setType) {
        List<E> immutableList = Emptys.isEmpty(array) ? Collections.emptyList() : Arrays.asList(array);
        if (setType == null) {
            setType = SetType.HashSet;
        }
        Set<E> set = null;
        switch (setType) {
            case HashSet:
                set = new HashSet<E>(immutableList);
                if (!mutable) {
                    set = Collections.unmodifiableSet(set);
                }
                break;
            case LinkedHashSet:
                set = new LinkedHashSet<E>(immutableList);
                if (!mutable) {
                    set = Collections.unmodifiableSet(set);
                }
                break;
            case TreeSet:
                TreeSet tset = new TreeSet<E>(immutableList);
                if (!mutable) {
                    set = Collections.unmodifiableSortedSet(tset);
                }
                break;
            case NonDistinctTreeSet:
                NonDistinctTreeSet tset2 = new NonDistinctTreeSet<>(immutableList);
                if (!mutable) {
                    set = Collections.unmodifiableSortedSet(tset2);
                }
                break;
            default:
                set = new HashSet<E>(immutableList);
                if (!mutable) {
                    set = Collections.unmodifiableSet(set);
                }
                break;
        }

        return set;
    }


    /**
     * Avoid NPE, create an empty, new set when the specified set is null
     */
    public static <E> Set<E> getEmptySetIfNull(@Nullable Set<E> set) {
        return getEmptySetIfNull(set, null);
    }

    /**
     * @see #getEmptySetIfNull(Set)
     */
    public static <E> Set<E> getEmptySetIfNull(@Nullable Set<E> set, @Nullable SetType setType) {
        if (set == null) {
            if (setType == null) {
                return new HashSet<>();
            }
            switch (setType) {
                case HashSet:
                    set = new HashSet<>();
                    break;
                case TreeSet:
                    set = new TreeSet<>();
                    break;
                case LinkedHashSet:
                    set = new LinkedHashSet<>();
                    break;
                default:
                    set = new HashSet<>();
                    break;
            }
        }
        return set;
    }

    public enum SetType {
        HashSet,
        LinkedHashSet,
        TreeSet,
        NonDistinctTreeSet;

        public static SetType ofSet(@Nullable Set set) {
            if (set == null) {
                return HashSet;
            }
            return inferSetType(set);
        }

        public static SetType ofSet(Class<?> setClass) {
            if (java.util.HashSet.class.isAssignableFrom(setClass)){
                return HashSet;
            } else if (java.util.LinkedHashSet.class.isAssignableFrom(setClass)){
                return LinkedHashSet;
            } else if (java.util.TreeSet.class.isAssignableFrom(setClass)){
                return TreeSet;
            } else {
                return HashSet;
            }
        }
    }


    private static SetType inferSetType(@NonNull Set set) {
        Objects.requireNonNull(set);
        if (set instanceof SortedSet) {
            return SetType.TreeSet;
        }
        if (set instanceof HashSet) {
            if (set instanceof LinkedHashSet) {
                return SetType.LinkedHashSet;
            }
            return SetType.HashSet;
        }
        return SetType.HashSet;
    }

}
