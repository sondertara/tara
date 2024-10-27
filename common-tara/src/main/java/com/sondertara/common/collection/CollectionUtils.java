package com.sondertara.common.collection;

import com.sondertara.common.base.Assert;
import com.sondertara.common.base.Emptys;
import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.collection.diff.CollectionDiffResult;
import com.sondertara.common.collection.diff.CollectionDiffer;
import com.sondertara.common.collection.diff.KeyBuilder;
import com.sondertara.common.collection.diff.MapDiffResult;
import com.sondertara.common.collection.diff.MapDiffer;
import com.sondertara.common.collection.iter.ArrayIterator;
import com.sondertara.common.collection.iter.EnumerationIterable;
import com.sondertara.common.collection.iter.IteratorIterable;
import com.sondertara.common.collection.iter.WrappedIterable;
import com.sondertara.common.collection.sequence.IterableSequence;
import com.sondertara.common.collection.sequence.ListSequence;
import com.sondertara.common.collection.sequence.SortedSetSequence;
import com.sondertara.common.concurrent.threadlocal.GlobalThreadLocalMap;
import com.sondertara.common.function.Function3;
import com.sondertara.common.function.Functions;
import com.sondertara.common.function.KeyExtractor;
import com.sondertara.common.function.Predicate2;
import com.sondertara.common.function.UnaryOperator2;
import com.sondertara.common.random.IRandom;
import com.sondertara.common.struct.Holder;
import com.sondertara.common.struct.Pair;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * utils
 *
 * @author huangxiaohu
 */
public final class CollectionUtils {


    /**
     * map是否为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * map是否不为空
     */
    public static <K, V> boolean isNotEmpty(Map<K, V> map) {
        return map != null && !map.isEmpty();
    }


    /**
     * list is empty
     *
     * @param list list
     * @return is empty
     */
    public static <E> boolean isEmpty(Collection<E> list) {
        return list == null || list.isEmpty();
    }

    public static <E> boolean isEmpty(Iterable<E> list) {
        return list == null || !list.iterator().hasNext();
    }

    /**
     * list is not empty
     *
     * @param list list
     * @return is not empty
     */
    public static <E> boolean isNotEmpty(Collection<E> list) {
        return list != null && !list.isEmpty();
    }

    /**
     * 把list转换成string，中间以combineChar来连接
     *
     * @param lists       original list
     * @param combineChar char
     * @return strs list
     */
    public static <T> String join(Collection<T> lists, char combineChar) {
        return StreamUtils.join(lists.stream(), String.valueOf(combineChar));
    }

    /**
     * 把list转换成string，中间以combineChar来连接
     *
     * @param lists      list
     * @param combineStr list
     * @return new String
     */
    public static <T> String join(Collection<T> lists, String combineStr) {
        return StreamUtils.join(lists.stream(), combineStr);
    }

    /**
     * 将String类型数组转成Long类型List
     *
     * @param strArr list
     **/
    public static List<Long> strArrToLongList(String[] strArr) {
        List<Long> result = new ArrayList<>();
        if (strArr == null || strArr.length == 0) {
            return result;
        }
        for (String str : strArr) {
            result.add(Long.valueOf(str));
        }
        return result;
    }


    private static class ImmutableEmptyEnumeration<E> implements Enumeration<E> {
        static final ImmutableEmptyEnumeration<Object> EMPTY_ENUMERATION
                = new ImmutableEmptyEnumeration<Object>();

        public boolean hasMoreElements() {
            return false;
        }

        public E nextElement() {
            throw new NoSuchElementException();
        }
    }


    public static <K, V> NonAbsentHashMap<K, V> emptyNonAbsentHashMap(java.util.function.Function<K, V> supplier) {
        Objects.requireNonNull(supplier);
        return new NonAbsentHashMap<K, V>(supplier);
    }

    public static <K, V> WrappedNonAbsentMap<K, V> wrapAsNonAbsentMap(@NonNull Map<K, V> map, @NonNull Function<K, V> supplier) {
        Objects.requireNonNull(map);
        Objects.requireNonNull(supplier);
        return new WrappedNonAbsentMap<K, V>(map, supplier);
    }

    private static <E1, E2> Collection<E2> emptyCollectionByInfer(Collection<E1> prototype) {
        if (prototype == null) {
            return new ArrayList<>();
        }
        if (prototype instanceof Set) {
            return Sets.getEmptySetIfNull(null);
        }
        if (prototype instanceof Queue) {
            return new ArrayList<>();
        }
        if (prototype instanceof List) {
            return Lists.getEmptyListIfNull(null, Lists.ListType.ofList((List<E1>) prototype));
        }
        return new ArrayList<>();
    }

    private static <E1, E2> Collection<E2> emptyCollection(@Nullable Iterable<E1> iterable) {
        if (iterable == null) {
            return new ArrayList<>();
        }
        if (iterable instanceof Collection) {
            return emptyCollectionByInfer((Collection<E1>) iterable);
        }
        return new ArrayList<>();
    }


    public static <E> Collection<E> asCollection(@Nullable Iterable<E> iterable) {
        if (Emptys.isNull(iterable)) {
            return new ArrayList<>();
        }
        if (!(iterable instanceof Collection)) {
            final List<E> list = new ArrayList<>();
            forEach(iterable, e -> list.add(e));
            return list;
        }
        return (Collection<E>) iterable;
    }


    public static <E, C extends Collection<E>> E[] asArray(@Nullable C list, @NonNull Class<E> componentClass) {
        Objects.requireNonNull(componentClass);
        E[] array = ArrayUtils.createArray(componentClass, Emptys.isEmpty(list) ? 0 : list.size());
        if (Emptys.isNotEmpty(list)) {
            list.toArray(array);
        }
        return array;
    }

    /**
     * Convert a list to an array
     */
    public static <E, C extends Collection<E>> E[] toArray(@Nullable C list, @Nullable Class<E[]> clazz) {
        Objects.requireNonNull(clazz);
        if (Emptys.isEmpty(list)) {
            return Arrays.copyOf(new Object[0], 0, clazz);
        }
        // Make a new array of the specified class
        return Arrays.copyOf(list.toArray(), list.size(), clazz);
    }

    /**
     * Convert any object to an immutable Iterable
     */
    public static <E> Iterable<E> asIterable(@Nullable Object object) {
        return asIterable(object, false);
    }

    /**
     * Convert any object to Iterable
     */
    @SuppressWarnings("unchecked")
    public static <E> Iterable<E> asIterable(@Nullable Object object, boolean mutable) {
        if (Emptys.isNull(object)) {
            return Lists.asList(null, mutable, null);
        }

        if (ArrayUtils.isArray(object)) {
            List<E> a = Lists.asList(PrimitiveArrays.<E>wrap(object));
            if (mutable) {
                return Lists.asList(a);
            } else {
                return Collections.unmodifiableList(a);
            }
        }

        if (object instanceof Collection) {
            if (!mutable) {
                return Collections.unmodifiableCollection((Collection<E>) object);
            }
            return (Collection<E>) object;
        }

        if (object instanceof Iterable) {
            if (!mutable) {
                return new WrappedIterable<E>((Iterable<E>) object, false);
            }
            return (Iterable<E>) object;
        }

        if (object instanceof Map) {
            return (Iterable<E>) Lists.asList(ArrayUtils.wrapAsArray(object), mutable, null);
        }

        if (object instanceof Iterator) {
            return new IteratorIterable<>((Iterator<E>) object, mutable);
        }

        if (object instanceof Enumeration) {
            return new EnumerationIterable<E>((Enumeration<E>) object);
        }
        // to char
        if (object instanceof String) {
            char[] chars = ((String) object).toCharArray();
            return new ArrayIterator<>(chars);
        }

        return (Iterable<E>) Lists.asList(ArrayUtils.wrapAsArray(object), mutable, null);
    }

    /**
     * Filter any object with the specified predicate
     */
    public static <E> Collection<E> filter(@Nullable Object anyObject, @NonNull final Predicate<E> predicate) {
        return filter(anyObject, predicate, null);
    }


    /**
     * Filter any object with the specified predicate
     */
    public static <E> Collection<E> filter(@Nullable Object anyObject, @Nullable Predicate<E> consumePredicate, @Nullable final Predicate<E> breakPredicate) {
        Iterable<E> iterable = asIterable(anyObject);
        final Collection<E> result = emptyCollection(iterable);
        consumePredicate = consumePredicate == null ? Functions.truePredicate() : consumePredicate;

        forEach(asCollection(iterable), consumePredicate, result::add, breakPredicate);
        return result;
    }

    /**
     * Filter any object with the specified predicate
     */
    public static <E> Collection<E> filter(@Nullable Object anyObject, @Nullable Predicate2<Integer, E> consumePredicate, @Nullable final Predicate2<Integer, E> breakPredicate) {
        Iterable<E> iterable = asIterable(anyObject);
        final Collection<E> result = emptyCollection(iterable);
        consumePredicate = consumePredicate == null ? Functions.<Integer, E>truePredicate2() : consumePredicate;
        forEach((Collection<E>) asCollection(iterable), consumePredicate, new BiConsumer<Integer, E>() {
            @Override
            public void accept(Integer index, E e) {
                result.add(e);
            }
        }, breakPredicate);
        return result;
    }

    /**
     * Filter a map with the specified predicate
     */
    public static <K, V> Map<K, V> filter(@Nullable Map<K, V> map, @NonNull final Predicate2<K, V> predicate) {
        Objects.requireNonNull(predicate);
        final Map<K, V> result = Maps.emyptMapIfNull(map);
        if (Emptys.isNotEmpty(map)) {
            forEach(map, (BiConsumer<K, V>) (key, value) -> {
                if (predicate.test(key, value)) {
                    result.put(key, value);
                }
            });
        }
        return result;
    }

    /**
     * mapping an iterable to a list
     */
    public static <E, R> Collection<R> toCollect(@Nullable Object anyObject, @NonNull final Function<E, R> mapper) {
        Objects.requireNonNull(mapper);
        Iterable<E> iterable = asIterable(anyObject);
        final Collection<R> result = emptyCollection(iterable);
        forEach(iterable, e -> result.add(mapper.apply(e)));
        return result;
    }

    /**
     * mapping an iterable to a map
     */
    public static <E, K, V> Map<K, V> toMap(@Nullable Object anyObject, @NonNull final Function<E, Pair<K, V>> mapper) {
        Objects.requireNonNull(mapper);
        final Map<K, V> result = new LinkedHashMap<>();
        Iterable<E> iterable = asIterable(anyObject);
        forEach((Iterable<E>) iterable, new Consumer<E>() {
            @Override
            public void accept(E e) {
                Pair<K, V> pair = mapper.apply(e);
                result.put(pair.getKey(), pair.getValue());
            }
        });
        return result;
    }


    /**
     * mapping aMap to a list
     */
    public static <K, V, R, M extends Map<K, V>> List<R> map(@Nullable M map, @NonNull final BiFunction<K, V, R> mapper) {
        Objects.requireNonNull(mapper);
        final List<R> result = new ArrayList<>();
        forEach(map, new BiConsumer<K, V>() {
            @Override
            public void accept(K key, V value) {
                result.add(mapper.apply(key, value));
            }
        });
        return result;
    }


    /**
     * map a collection to another, flat it
     */
    public static <E, R> Collection<R> flatMap(@NonNull final Function<E, R> mapper, @Nullable Collection<E[]> collection) {
        if (Emptys.isEmpty(collection)) {
            return new ArrayList<>();
        }
        Objects.requireNonNull(mapper);
        final Collection<R> list = emptyCollectionByInfer(collection);
        forEach(collection, c -> {
            Collection<R> rs = toCollect(c, mapper);
            list.addAll(rs);
        });
        return list;
    }

    /**
     * map a collection to another, flat it
     */
    public static <E, R, C extends Collection<E>> Collection<R> flatMap(@Nullable C collection, @NonNull final Function<E, R> mapper) {
        if (Emptys.isEmpty(collection)) {
            return new ArrayList<>();
        }
        Objects.requireNonNull(mapper);
        final Collection<R> list = emptyCollectionByInfer(collection);

        forEach(collection, (Consumer<Collection<E>>) c -> {
            Collection<R> rs = toCollect(c, mapper);
            list.addAll(rs);
        });
        return list;
    }

    public static <E> void forEach(Object obj, @NonNull final Consumer<E> consumer) {
        Iterable<E> iterable = asIterable(obj);
        forEach(iterable, null, consumer, null);
    }

    public static <E, C extends Iterable<E>> void forEach(@Nullable C collection, @NonNull final Consumer<E> consumer) {
        forEach(collection, null, consumer, null);
    }

    public static <E, C extends Iterable<E>> void forEach(@Nullable C collection, @Nullable Predicate<E> consumePredicate, @NonNull final Consumer<E> consumer) {
        forEach(collection, consumePredicate, consumer, null);
    }

    public static <E, C extends Iterable<E>> void forEach(@Nullable C collection, @NonNull final Consumer<E> consumer, @Nullable Predicate<E> breakPredicate) {
        forEach(collection, null, consumer, breakPredicate);
    }

    /**
     * Consume every element what matched the consumePredicate
     */
    public static <E, C extends Iterable<E>> void forEach(@Nullable C collection, @Nullable Predicate<E> consumePredicate, @NonNull final Consumer<E> consumer, @Nullable Predicate<E> breakPredicate) {
        if (Emptys.isNotEmpty(collection)) {
            consumePredicate = consumePredicate == null ? Functions.<E>truePredicate() : consumePredicate;
            for (E element : collection) {
                if (consumePredicate.test(element)) {
                    consumer.accept(element);
                }
                if (breakPredicate != null && breakPredicate.test(element)) {
                    break;
                }
            }
        }
    }

    public static <E> void forEach(Object obj, @NonNull final BiConsumer<Integer, E> consumer) {
        Iterable<E> iterable = asIterable(obj);
        forEach(iterable, null, consumer, null);
    }

    public static <E, C extends Collection<E>> void forEach(@Nullable C collection, @NonNull final BiConsumer<Integer, E> consumer) {
        forEach(collection, null, consumer, null);
    }

    /**
     * Iterate every element
     */
    public static <E, C extends Iterable<E>> void forEach(@Nullable C collection, @NonNull final BiConsumer<Integer, E> consumer) {
        forEach(collection, null, consumer, null);
    }


    /**
     * Iterate every element
     */
    public static <E, C extends Iterable<E>> void forEach(@Nullable C collection, @NonNull final BiConsumer<Integer, E> consumer, @Nullable final Predicate2<Integer, E> breakPredicate) {
        forEach(collection, null, consumer, breakPredicate);
    }

    /**
     * Consume every element what matched the consumePredicate
     */
    public static <E, C extends Iterable<E>> void forEach(@Nullable C collection, @Nullable final Predicate2<Integer, E> consumePredicate, @NonNull final BiConsumer<Integer, E> consumer) {
        forEach(collection, consumePredicate, consumer, null);
    }


    /**
     * Consume every element what matched the consumePredicate
     */
    public static <E, C extends Iterable<E>> void forEach(@Nullable C collection, @Nullable Predicate2<Integer, E> consumePredicate, @NonNull final BiConsumer<Integer, E> consumer, @Nullable Predicate2<Integer, E> breakPredicate) {
        consumePredicate = consumePredicate == null ? Functions.<Integer, E>truePredicate2() : consumePredicate;

        if (Emptys.isNotEmpty(collection)) {
            Iterator<E> iterator = collection.iterator();
            for (int i = 0; iterator.hasNext(); i++) {
                E element = iterator.next();
                if (consumePredicate.test(i, element)) {
                    consumer.accept(i, element);
                }
                if (breakPredicate != null && breakPredicate.test(i, element)) {
                    break;
                }
            }
        }
    }

    public static <E> void forEach(@Nullable E[] array, @NonNull final Consumer<E> consumer) {
        forEach(array, new BiConsumer<Integer, E>() {
            @Override
            public void accept(Integer key, E value) {
                consumer.accept(value);
            }
        }, null);
    }

    /**
     * Iterate every element
     */
    public static <E> void forEach(@Nullable E[] array, @NonNull BiConsumer<Integer, E> consumer) {
        forEach(array, consumer, null);
    }

    /**
     * Iterate every element
     */
    public static <E> void forEach(@Nullable E[] array, @NonNull BiConsumer<Integer, E> consumer, @Nullable final Predicate2<Integer, E> breakPredicate) {
        forEach(array, null, consumer, breakPredicate);
    }

    /**
     * consume every element that matched the consumePredicate
     */
    public static <E> void forEach(@Nullable E[] array, @Nullable final Predicate2<Integer, E> consumePredicate, @NonNull BiConsumer<Integer, E> consumer) {
        forEach(array, consumePredicate, consumer, null);
    }

    /**
     * consume every element that matched the consumePredicate
     */
    public static <E> void forEach(@Nullable E[] array, @Nullable Predicate<E> consumePredicate, @NonNull Consumer<E> consumer, @Nullable final Predicate<E> breakPredicate) {
        consumePredicate = consumePredicate == null ? Functions.<E>truePredicate() : consumePredicate;

        if (Emptys.isNotEmpty(array)) {
            for (int i = 0; i < array.length; i++) {
                E element = array[i];
                if (consumePredicate.test(element)) {
                    consumer.accept(element);
                }
                if (breakPredicate != null && breakPredicate.test(element)) {
                    break;
                }
            }
        }
    }

    /**
     * consume every element that matched the consumePredicate
     */
    public static <E> void forEach(@Nullable E[] array, @Nullable Predicate2<Integer, E> consumePredicate, @NonNull BiConsumer<Integer, E> consumer, @Nullable final Predicate2<Integer, E> breakPredicate) {
        consumePredicate = consumePredicate == null ? Functions.<Integer, E>truePredicate2() : consumePredicate;

        if (Emptys.isNotEmpty(array)) {
            for (int i = 0; i < array.length; i++) {
                E element = array[i];
                if (consumePredicate.test(i, element)) {
                    consumer.accept(i, element);
                }
                if (breakPredicate != null && breakPredicate.test(i, element)) {
                    break;
                }
            }
        }
    }


    /**
     * Iterate every element
     */
    public static <K, V, M extends Map<? extends K, ? extends V>> void forEach(@Nullable M map, @NonNull BiConsumer<K, V> consumer) {
        forEach(map, null, consumer, null);
    }

    /**
     * Iterate every element
     */
    public static <K, V, M extends Map<? extends K, ? extends V>> void forEach(@Nullable M map, @NonNull BiConsumer<K, V> consumer, @Nullable final Predicate2<K, V> breakPredicate) {
        forEach(map, null, consumer, breakPredicate);
    }

    /**
     * consume every element what matched the consumePredicate
     */
    public static <K, V, M extends Map<? extends K, ? extends V>> void forEach(@Nullable M map, @Nullable final Predicate2<K, V> consumePredicate, @NonNull BiConsumer<K, V> consumer) {
        forEach(map, consumePredicate, consumer, null);
    }

    /**
     * consume every element what matched the consumePredicate
     */
    public static <K, V, M extends Map<? extends K, ? extends V>> void forEach(@Nullable M map, @Nullable Predicate2<K, V> consumePredicate, @NonNull BiConsumer<K, V> consumer, @Nullable final Predicate2<K, V> breakPredicate) {
        Objects.requireNonNull(consumer);
        consumePredicate = consumePredicate == null ? Functions.<K, V>truePredicate2() : consumePredicate;
        if (Emptys.isNotEmpty(map)) {
            for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
                if (consumePredicate.test(entry.getKey(), entry.getValue())) {
                    consumer.accept(entry.getKey(), entry.getValue());
                }
                if (breakPredicate != null && breakPredicate.test(entry.getKey(), entry.getValue())) {
                    break;
                }
            }
        }
    }

    public static <E, C extends Collection<E>> Integer firstOccurrence(C c, final E item) {
        return firstOccurrence(c, new Predicate2<Integer, E>() {
            @Override
            public boolean test(Integer key, E value) {
                return ObjectUtils.equals(value, item);
            }
        });
    }

    public static <E, C extends Collection<E>> int firstOccurrence(C c, Predicate2<Integer, E> predicate) {
        List<Pair<Integer, E>> pairs = findNPairs(c, predicate, 1);
        if (Emptys.isEmpty(pairs)) {
            return -1;
        }
        Pair<Integer, E> pair = pairs.get(0);
        return pair.getKey();
    }


    /**
     * find the first matched element, null if not found
     */
    public static <E, C extends Iterable<E>, O> O firstMap(@Nullable C collection, @NonNull final BiFunction<Integer, E, O> mapper) {
        return firstMap(collection, mapper, (Predicate<O>) null);
    }

    /**
     * map every element in the collection with the mapper,
     * break the traverse if the mapped result match the breakPredicate
     * <p>
     * return the mapped result
     */
    public static <E, C extends Iterable<E>, O> O firstMap(@Nullable C collection, @NonNull final BiFunction<Integer, E, O> mapper, final Predicate<O> breakPredicate) {
        final Holder<O> holder = new Holder<O>();
        forEach(collection, new BiConsumer<Integer, E>() {
            @Override
            public void accept(Integer index, E value) {
                holder.set(mapper.apply(index, value));
            }
        }, new Predicate2<Integer, E>() {
            @Override
            public boolean test(Integer index, E value) {
                return breakPredicate == null ? !holder.isNull() : breakPredicate.test(holder.get());
            }
        });
        return holder.get();
    }

    /**
     * map every element in the collection with the mapper,
     * break the traverse if the mapped result match the breakPredicate
     * <p>
     * return the mapped result
     */
    public static <E, C extends Iterable<E>, O> O firstMap(@Nullable C collection, @NonNull final BiFunction<Integer, E, O> mapper, final Predicate2<E, O> breakPredicate) {
        final Holder<O> holder = new Holder<O>();
        forEach(collection, new BiConsumer<Integer, E>() {
            @Override
            public void accept(Integer index, E element) {
                holder.set(mapper.apply(index, element));
            }
        }, new Predicate2<Integer, E>() {
            @Override
            public boolean test(Integer index, E element) {
                return breakPredicate == null ? !holder.isNull() : breakPredicate.test(element, holder.get());
            }
        });
        return holder.get();
    }


    /**
     * find the first matched element, null if not found
     */
    public static <K, V, O> O firstMap(@Nullable Map<K, V> map, @NonNull final BiFunction<K, V, O> mapper) {
        return firstMap(map, mapper, null);
    }

    /**
     * find the first matched element, null if not found
     */
    public static <K, V, O> O firstMap(@Nullable Map<K, V> map, @NonNull final BiFunction<K, V, O> mapper, final Predicate<O> breakPredicate) {
        final Holder<O> holder = new Holder<O>();
        forEach(map, new BiConsumer<K, V>() {
            @Override
            public void accept(K index, V value) {
                holder.set(mapper.apply(index, value));
            }
        }, new Predicate2<K, V>() {
            @Override
            public boolean test(K key, V value) {
                return breakPredicate == null ? !holder.isNull() : breakPredicate.test(holder.get());
            }
        });
        return holder.get();
    }

    /**
     * find the first matched element, null if not found
     */
    public static <E, C extends Collection<E>> E findFirst(@Nullable C collection) {
        return findFirst(collection, Functions.<E>nonNullPredicate());
    }

    /**
     * find the first matched element, null if not found
     */
    public static <E, C extends Collection<E>> E findFirst(@Nullable C collection, @Nullable Predicate<E> predicate) {
        List<E> list = findN(collection, predicate, 1);
        if (Emptys.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /**
     * find the first matched element, null if not found
     */
    public static <K, V> Map.Entry<? extends K, ? extends V> findFirst(@Nullable Map<? extends K, ? extends V> map, @Nullable Predicate2<K, V> predicate) {
        if (map == null) {
            return null;
        }
        if (Emptys.isNotEmpty(map)) {
            if (predicate != null) {
                for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
                    if (predicate.test(entry.getKey(), entry.getValue())) {
                        return entry;
                    }
                }
            } else {
                return map.entrySet().iterator().next();
            }
        }
        return null;
    }

    /**
     * find the first matched element, null if not found
     */
    public static <E, C extends Collection<E>> List<E> findN(@Nullable C collection, @Nullable Predicate<E> predicate, final int n) {
        final List<E> ret = new ArrayList<>();
        if (n <= 0 || Emptys.isEmpty(collection)) {
            return ret;
        }

        forEach(collection, predicate, new Consumer<E>() {
            @Override
            public void accept(E e) {
                ret.add(e);
            }
        }, value -> ret.size() == n);

        return ret;
    }


    /**
     * find the first matched element, null if not found
     */
    public static <E, C extends Collection<E>> List<Pair<Integer, E>> findNPairs(@Nullable C collection, @Nullable Predicate2<Integer, E> predicate, final int n) {
        final List<Pair<Integer, E>> ret = new ArrayList<>();
        if (n <= 0 || Emptys.isEmpty(collection)) {
            return ret;
        }

        forEach(collection, predicate, new BiConsumer<Integer, E>() {
            @Override
            public void accept(Integer index, E e) {
                ret.add(new Pair<Integer, E>(index, e));
            }
        }, new Predicate2<Integer, E>() {
            @Override
            public boolean test(Integer index, E value) {
                return ret.size() == n;
            }
        });
        return ret;
    }


    /**
     * find the first matched element, null if not found
     */
    public static <K, V> Map<? extends K, ? extends V> findN(@Nullable Map<? extends K, ? extends V> map, @Nullable Predicate2<K, V> predicate, final int n) {
        final Map<K, V> ret = new LinkedHashMap<>();
        if (n <= 0 || map == null || map.isEmpty()) {
            return ret;
        }
        forEach(map, predicate, new BiConsumer<K, V>() {
            @Override
            public void accept(K key, V value) {
                ret.put(key, value);
            }
        }, new Predicate2<K, V>() {
            @Override
            public boolean test(K key, V value) {
                return n == ret.size();
            }
        });
        return ret;
    }

    /**
     * remove all elements that match the condition
     *
     * @return whether has any element removed
     * @throws UnsupportedOperationException, NullPointException
     */
    public static <E, C extends Collection<E>> boolean removeIf(@Nullable C collection, @NonNull Predicate<E> predicate) {
        boolean hasRemoved = false;
        if (Emptys.isNotEmpty(collection)) {
            hasRemoved = removeIf(collection.iterator(), predicate);
        }
        return hasRemoved;
    }

    /**
     * 从 collection 中移除 所有出现在 removed中的
     *
     * @param collection
     * @param removed
     */
    public static <E, C extends Collection<E>> void removeAll(C collection, final C removed) {
        removeIf(collection, new Predicate<E>() {
            @Override
            public boolean test(E element) {
                return removed.contains(element);
            }
        });
    }

    public static <E> boolean removeIf(@Nullable Iterator<E> iterator, @NonNull Predicate<E> predicate) {
        Objects.requireNonNull(iterator);
        Objects.requireNonNull(predicate);
        boolean hasRemoved = false;
        while (iterator.hasNext()) {
            E e = iterator.next();
            if (predicate.test(e)) {
                try {
                    iterator.remove();
                } catch (UnsupportedOperationException ex) {
                    break;
                }
                hasRemoved = true;
            }
        }
        return hasRemoved;
    }

    /**
     * remove all elements that match the map
     *
     * @return whether has any element removed
     * @throws UnsupportedOperationException, NullPointException
     */
    public static <K, V> boolean removeIf(@Nullable Map<K, V> map, @NonNull Predicate2<K, V> predicate) {
        Objects.requireNonNull(predicate);
        boolean hasRemoved = false;
        if (Emptys.isNotEmpty(map)) {
            Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<K, V> e = iterator.next();
                if (predicate.test(e.getKey(), e.getValue())) {
                    iterator.remove();
                    hasRemoved = true;
                }
            }
        }
        return hasRemoved;
    }

    public static <T> boolean anyMatch(@NonNull Predicate predicate, T... array) {
        return anyMatch(Lists.asList(array), predicate);
    }


    /**
     * has any element match the specified condition
     *
     * @return whether has any element removed
     */
    public static <E, C extends Collection<E>> boolean anyMatch(@Nullable C collection, @NonNull Predicate<E> predicate) {
        Objects.requireNonNull(predicate);
        if (Emptys.isNotEmpty(collection)) {
            // 不能采用 findFirst ，原因是 如果 predicat 就是来 取null 或者 empty的，而collection 中恰好有 null 或者 empty，那么取到时，结果也是 null
            // 没办法说清楚返回值是否为取到的结果
            // E e = findFirst(collection, predicate);
            // return e != null;
            final Holder<Boolean> found = new Holder<Boolean>(false);
            forEach(collection, predicate, new Consumer<E>() {
                @Override
                public void accept(E e) {
                    found.set(true);
                }
            }, new Predicate<E>() {
                @Override
                public boolean test(E value) {
                    return found.get();
                }
            });
            return found.get();
        }
        return false;
    }


    /**
     * has any element match the specified condition
     *
     * @return whether has any element removed
     */
    public static <K, V, M extends Map<? extends K, ? extends V>> boolean anyMatch(@Nullable M map, @NonNull Predicate2<K, V> predicate) {
        Objects.requireNonNull(predicate);
        if (Emptys.isNotEmpty(map)) {
            Map.Entry<? extends K, ? extends V> entry = findFirst(map, predicate);
            return entry != null;
        }
        return false;
    }

    public static boolean allMatch(@NonNull Predicate predicate, @Nullable Object... collection) {
        return allMatch(Lists.asList(collection), predicate);
    }

    /**
     * whether all elements match the specified condition or not
     *
     * @return whether has any element removed
     */
    public static <E, C extends Collection<E>> boolean allMatch(@Nullable C collection, @NonNull Predicate<E> predicate) {
        Objects.requireNonNull(predicate);
        if (Emptys.isNotEmpty(collection)) {
            for (E e : collection) {
                if (!predicate.test(e)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * whether all elements match the specified condition or not
     *
     * @return whether has any element removed
     */
    public static <K, V, M extends Map<? extends K, ? extends V>> boolean allMatch(@Nullable M map, @NonNull Predicate2<K, V> predicate) {
        Objects.requireNonNull(predicate);
        if (Emptys.isNotEmpty(map)) {
            for (Map.Entry<? extends K, ? extends V> e : map.entrySet()) {
                if (!predicate.test(e.getKey(), e.getValue())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean noneMatch(@NonNull Predicate predicate, @Nullable Object... collection) {
        return noneMatch(Lists.asList(collection), predicate);
    }

    /**
     * has no any element match the specified condition ?
     *
     * @return whether has any element removed
     */
    public static <E, C extends Collection<E>> boolean noneMatch(@Nullable C collection, @NonNull Predicate<E> predicate) {
        Objects.requireNonNull(predicate);
        if (Emptys.isNotEmpty(collection)) {
            for (E e : collection) {
                if (predicate.test(e)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * has no any element match the specified condition ?
     *
     * @return whether has any element removed
     */
    public static <K, V, M extends Map<? extends K, ? extends V>> boolean noneMatch(@Nullable M map, @NonNull Predicate2<K, V> predicate) {
        Objects.requireNonNull(predicate);
        if (Emptys.isNotEmpty(map)) {
            for (Map.Entry<? extends K, ? extends V> e : map.entrySet()) {
                if (predicate.test(e.getKey(), e.getValue())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static <E, C extends Collection<E>> Set<E> distinct(@Nullable C collection) {
        return new LinkedHashSet<E>(Emptys.isEmpty(collection) ? Collections.EMPTY_LIST : collection);
    }

    public static <E> E[] limit(E[] array, int maxSize) {

        E[] target = Arrays.copyOf(array, maxSize);
        return target;
    }

    /**
     * truncate a collection using subList(0, maxSize)
     */
    public static <E, C extends Collection<E>> List<E> limit(@Nullable C collection, int maxSize) {
        if (Emptys.isEmpty(collection)) {
            return new ArrayList<>();
        }

        Assert.isTrue(maxSize >= 0);

        List<E> list = (collection instanceof List) ? (List<E>) collection : new LinkedList<E>(collection);
        if (list.size() <= maxSize) {
            return list;
        }
        return list.subList(0, maxSize);
    }

    public static <E> E[] skip(@Nullable E[] array, int size) {
        Assert.checkIndex(size, array.length + 1);
        E[] target = Arrays.copyOfRange(array, size, array.length);
        return target;
    }

    /**
     * skip n elements, get a collection using subList(n, size)
     */
    public static <E, C extends Collection<E>> List<E> skip(@Nullable C collection, int n) {
        if (Emptys.isEmpty(collection)) {
            return new ArrayList<>();
        }
        Assert.isTrue(n >= 0);
        List<E> list = (collection instanceof List) ? (List<E>) collection : new LinkedList<E>(collection);
        if (list.size() <= n) {
            return new ArrayList<>();
        }
        return list.subList(n, list.size());
    }

    public static <E, K> List<List<E>> partitionBy(Iterator<E> c, Function<E, K> classifier) {
        return Lists.asList(collect(c, partitioningBy(classifier)).values());
    }

    public static <E, K> List<List<E>> partitionBy(Iterator<E> c, BiFunction<Integer, E, K> classifier) {
        return Lists.asList(collect(c, partitioningBy(classifier)).values());
    }

    public static <E, K> List<List<E>> partitionBy(E[] c, Function<E, K> classifier) {
        return Lists.asList(collect(c, partitioningBy(classifier)).values());
    }

    public static <E, K> List<List<E>> partitionBy(E[] c, BiFunction<Integer, E, K> classifier) {
        return Lists.asList(collect(c, partitioningBy(classifier)).values());
    }

    public static <E, K> List<List<E>> partitionBy(Iterable<E> c, Function<E, K> classifier) {
        return Lists.asList(collect(c, partitioningBy(classifier)).values());
    }

    public static <E, K> List<List<E>> partitionBy(Iterable<E> c, BiFunction<Integer, E, K> classifier) {
        return Lists.asList(collect(c, partitioningBy(classifier)).values());
    }

    /**
     * 第二个参数为分区总数，不是单个分区中的数据量
     */
    public static <E> List<List<E>> partitionByCount(Iterable<E> c, final int partitionCount) {
        Assert.isTrue(partitionCount > 0);
        return partitionBy(c, new BiFunction<Integer, E, Integer>() {
            @Override
            public Integer apply(Integer index, E element) {
                return index % partitionCount;
            }
        });
    }

    /**
     * 第二个参数为单个分区中的数据量
     *
     * @param c
     * @param partitionSize
     * @param <E>
     */
    public static <E> List<List<E>> partitionBySize(Iterable<E> c, final int partitionSize) {
        Assert.isTrue(partitionSize > 0);
        return partitionBy(c, new BiFunction<Integer, E, Integer>() {
            @Override
            public Integer apply(Integer index, E element) {
                return index / partitionSize;
            }
        });
    }


    /**
     * sort a collection, return an new list. it is different to
     * Collections.sort(list) is that : Collections.sort() return void
     */
    public static <K, V, M extends Map<K, V>> M sort(@Nullable M map, @NonNull Comparator<K> comparator) {
        Objects.requireNonNull(comparator);
        M result = (M) new TreeMap<>(comparator);
        if (Emptys.isNotEmpty(map)) {
            result.putAll(map);
        }
        return result;
    }

    public static <E> List<E> reverse(@Nullable List<E> list) {
        return reverse(list, false);
    }

    /**
     * Reverse a list, return an new list when the argument 'newOne' is true
     */
    public static <E> List<E> reverse(@Nullable List<E> list, boolean newOne) {
        if (Emptys.isEmpty(list)) {
            return (list == null || newOne) ? Collections.emptyList() : list;
        }
        if (!newOne) {
            Collections.reverse(list);
            return list;
        } else {
            List<E> newList = new ArrayList<E>();
            int i = list.size() - 1;
            while (i >= 0) {
                newList.add(list.get(i));
                i--;
            }
            return newList;
        }
    }

    public static <K, V> int count(@Nullable Map<K, V> map) {
        return Emptys.isEmpty(map) ? 0 : map.size();
    }

    public static <E, C extends Collection<E>> int count(@Nullable C collection) {
        return Emptys.isEmpty(collection) ? 0 : collection.size();
    }

    public static int count(@Nullable Object anyObject) {
        final Holder<Integer> count = new Holder<Integer>(0);
        forEach(asCollection(asIterable(anyObject)), null, new Consumer<Object>() {
            @Override
            public void accept(Object object) {
                count.set(count.get() + 1);
            }
        }, null);
        return count.get();
    }

    public static <E> E max(@NonNull Object object, @NonNull final Comparator<E> comparator) {
        Objects.requireNonNull(comparator);
        Iterable<E> iterable = asIterable(object);
        return reduce(iterable, new UnaryOperator2<E>() {
            @Override
            public E apply(E input1, E input2) {
                return comparator.compare(input1, input2) >= 0 ? input1 : input2;
            }
        });
    }

    public static <E> E min(@Nullable Object object, @NonNull final Comparator<E> comparator) {
        Objects.requireNonNull(comparator);
        Iterable<E> iterable = asIterable(object);
        return reduce(iterable, (input1, input2) -> comparator.compare(input1, input2) <= 0 ? input1 : input2);
    }

    public static <E, R> R collect(@Nullable Object anyObject, @NonNull Collector<E, R, R> collector) {
        Objects.requireNonNull(collector);
        return collect(anyObject, collector.supplier(), collector.accumulator());
    }

    public static <E, R> R collect(@Nullable Object anyObject, @NonNull Supplier<R> containerFactory, @NonNull final BiConsumer<R, E> consumer) {
        Objects.requireNonNull(containerFactory);
        Objects.requireNonNull(consumer);
        final R container = containerFactory.get();
        forEach(asCollection(asIterable(anyObject)), (Consumer<E>) e -> consumer.accept(container, e));
        return container;
    }

    public static <E> Collection<E> collect(@Nullable Object anyObject, @NonNull final Collection<E> container) {
        Objects.requireNonNull(container);
        Supplier<Collection<E>> containerFactory = () -> container;
        BiConsumer<Collection<E>, E> consumer = Collection::add;
        return collect(anyObject, containerFactory, consumer);
    }

    public static <X, Y, E, C extends Collection<E>> CollectionDiffResult<E> diff(@Nullable X oldObject, @NonNull final Function<X, E> oldMapper, @Nullable Y newObject, @NonNull final Function<Y, E> newMapper) {
        return diff(oldObject, oldMapper, newObject, newMapper, null);
    }

    public static <X, Y, E, C extends Collection<E>> CollectionDiffResult<E> diff(@Nullable X oldObject, @NonNull final Function<X, E> oldMapper, @Nullable Y newObject, @NonNull final Function<Y, E> newMapper, @Nullable Comparator<E> elementComparator) {
        return diff(oldObject, oldMapper, newObject, newMapper, elementComparator, null);
    }

    public static <X, Y, E, C extends Collection<E>> CollectionDiffResult<E> diff(@Nullable X oldObject, @NonNull final Function<X, E> oldMapper, @Nullable Y newObject, @NonNull final Function<Y, E> newMapper, @Nullable Comparator<E> elementComparator, @Nullable KeyBuilder<String, E> keyBuilder) {
        Collection<E> oldCollection = toCollect(oldObject, oldMapper);
        Collection<E> newCollection = toCollect(oldObject, oldMapper);
        return diff(oldCollection, newCollection, elementComparator, keyBuilder);
    }


    public static <E, C extends Collection<E>> CollectionDiffResult<E> diff(@Nullable C oldCollection, @Nullable C newCollection) {
        return diff(oldCollection, newCollection, null);
    }

    public static <E, C extends Collection<E>> CollectionDiffResult<E> diff(@Nullable C oldCollection, @Nullable C newCollection, @Nullable Comparator<E> elementComparator) {
        return diff(oldCollection, newCollection, elementComparator, null);
    }

    public static <E, C extends Collection<E>> CollectionDiffResult<E> diff(@Nullable C oldCollection, @Nullable C newCollection, @Nullable Comparator<E> elementComparator, @Nullable KeyBuilder<String, E> keyBuilder) {
        CollectionDiffer<E> differ = new CollectionDiffer<E>();
        differ.setComparator(elementComparator);
        if (keyBuilder != null) {
            differ.diffUsingMap(keyBuilder);
        }
        return differ.diff(oldCollection, newCollection);
    }

    public static <K, V, M extends Map<K, V>> MapDiffResult<K, V> diff(@Nullable M oldMap, @Nullable M newMap) {
        return diff(oldMap, newMap, null);
    }

    public static <K, V, M extends Map<K, V>> MapDiffResult<K, V> diff(@Nullable M oldMap, @Nullable M newMap, @Nullable Comparator<V> valueComparator) {
        return diff(oldMap, newMap, valueComparator, null);
    }

    public static <K, V, M extends Map<K, V>> MapDiffResult<K, V> diff(@Nullable M oldMap, @Nullable M newMap, @Nullable Comparator<V> valueComparator, @Nullable Comparator<K> keyComparator) {
        MapDiffer<K, V> differ = new MapDiffer<K, V>();
        differ.setValueComparator(valueComparator);
        differ.setKeyComparator(keyComparator);
        return differ.diff(oldMap, newMap);
    }

    public static <E, C extends Collection<E>> void addAll(@NonNull final C target, @Nullable Iterable<E> iterable) {
        Objects.requireNonNull(target);
        if (Emptys.isNotEmpty(iterable)) {
            forEach(iterable, target::add);
        }
    }

    @SafeVarargs
    public static <E, C extends Collection<E>> void addAll(@NonNull C target, @Nullable E... iterator) {
        addAll(target, asIterable(iterator));
    }

    public static <E, C extends Collection<E>> void addAll(@NonNull C target, @Nullable Iterator<E> iterator) {
        addAll(target, asIterable(iterator));
    }

    public static <E, C extends Collection<E>> void addTo(@NonNull C targetCollection, @Nullable C srcCollection) {
        addAll(targetCollection, srcCollection);
    }

    public static <E1, C1 extends Collection<E1>, E2, C2 extends Collection<E2>> void addTo(C1 srcCollection, C2 targetCollection, Function<E1, E2> mapper) {
        Collection<E2> mapped = toCollect(srcCollection, mapper);
        addTo(mapped, targetCollection);
    }



    public static <E, C extends Collection<E>> C merge(@Nullable C c1, @Nullable C c2) {
        return merge(c1, c2, true);
    }

    public static <E, C extends Collection<E>> C merge(@Nullable final C c1, @Nullable C c2, boolean newOne) {
        if (newOne) {
            Set<E> set = new LinkedHashSet<>();
            if (Emptys.isNotEmpty(c1)) {
                set.addAll(c1);
            }
            if (Emptys.isNotEmpty(c2)) {
                set.addAll(c2);
            }
            return (C) set;
        } else {
            Objects.requireNonNull(c1);
            if (Emptys.isNotEmpty(c2)) {
                forEach(c2, new Consumer<E>() {
                    @Override
                    public void accept(E e) {
                        if (!c1.contains(e)) {
                            c1.add(e);
                        }
                    }
                });
            }
            return c1;
        }
    }

    public static <K, V, M extends Map<K, V>> M merge(@Nullable M map1, @Nullable M map2) {
        return merge(map1, map2, true);
    }

    public static <K, V, M extends Map<K, V>> M merge(@Nullable M map1, @Nullable M map2, boolean newOne) {
        if (newOne) {
            Map<K, V> map = new HashMap<>();
            if (Emptys.isNotEmpty(map1)) {
                map.putAll(map1);
            }
            if (Emptys.isNotEmpty(map2)) {
                map.putAll(map2);
            }
            return (M) map;
        } else {
            Objects.requireNonNull(map1);
            if (Emptys.isNotEmpty(map2)) {
                map1.putAll(map2);
            }
            return map1;
        }
    }

    /**
     * test c1 contains any element in c2
     */
    public static <E, C1 extends Collection<E>, C2 extends Collection<E>> boolean containsAny(final C1 c1, C2 c2) {
        if (Emptys.isEmpty(c1)) {
            return false;
        }
        return anyMatch(c2, new Predicate<E>() {
            @Override
            public boolean test(E value) {
                return c1.contains(value);
            }
        });
    }

    public static <E> boolean contains(E[] collection, final E obj) {
        return contains(collection, obj, null);
    }

    public static <E> boolean contains(Collection<E> collection, final E obj) {
        return contains(collection, obj, null);
    }

    public static <E> boolean contains(E[] collection, @Nullable final E obj, @Nullable Predicate<E> predicate) {
        return contains(asCollection(asIterable(collection)), obj, predicate);
    }

    public static <E> boolean contains(Collection<E> collection, @Nullable final E obj, @Nullable Predicate<E> predicate) {
        Predicate<E> p = predicate == null ? Functions.<E>equalsPredicate(obj) : predicate;
        return anyMatch(collection, p);
    }

    /**
     * test c1 contains all elements in c2
     */
    public static <E, C1 extends Collection<E>, C2 extends Collection<E>> boolean containsAll(final C1 c1, C2 c2) {
        if (Emptys.isEmpty(c1)) {
            return false;
        }
        return allMatch(c2, new Predicate<E>() {
            @Override
            public boolean test(E value) {
                return c1.contains(value);
            }
        });
    }

    /**
     * test c1 contains all elements in c2
     */
    public static <E, C1 extends Collection<E>, C2 extends Collection<E>> boolean containsNone(final C1 c1, C2 c2) {
        if (Emptys.isEmpty(c1)) {
            return true;
        }
        return noneMatch(c2, new Predicate<E>() {
            @Override
            public boolean test(E value) {
                return c1.contains(value);
            }
        });
    }

    public static <E, C1 extends Collection<E>, C2 extends Collection<E>> Set<E> intersection(final C1 c1, final C2 c2) {
        final Set<E> set = new LinkedHashSet<>();
        if (Emptys.isEmpty(c1) || Emptys.isEmpty(c2)) {
            return set;
        }
        List<E> allElements = new ArrayList<>();
        allElements.addAll(c1);
        allElements.addAll(c2);
        forEach((Collection<E>) allElements, new Predicate<E>() {
            @Override
            public boolean test(E element) {
                return set.contains(element) || (c1.contains(element) && c2.contains(element));
            }
        }, new Consumer<E>() {
            @Override
            public void accept(E e) {
                set.add(e);
            }
        }, null);
        return set;
    }

    public static <E, C1 extends Collection<E>, C2 extends Collection<E>> Set<E> union(final C1 c1, final C2 c2) {
        Set<E> allElements = new LinkedHashSet<>();
        allElements.addAll(Emptys.isEmpty(c1) ? Collections.emptyList() : c1);
        allElements.addAll(Emptys.isEmpty(c2) ? Collections.emptyList() : c2);
        return allElements;
    }

    public static <E> E reduce(@Nullable E[] iterable, UnaryOperator2<E> operator) {
        return reduce(asIterable(iterable), operator);
    }

    public static <E, C extends Iterable<E>> E reduce(@Nullable Iterable<E> iterable, UnaryOperator2<E> operator) {
        if (Emptys.isEmpty(iterable)) {
            return null;
        }
        Objects.requireNonNull(operator);
        List<E> list = Lists.asList(iterable);
        E result = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            result = operator.apply(result, list.get(i));
        }
        return result;
    }

    public static <K, E> Map<K, List<E>> groupBy(@Nullable E[] iterable, @NonNull final Function<E, K> classifier) {
        return groupBy(asIterable(iterable), classifier, null);
    }

    public static <K, E> Map<K, List<E>> groupBy(@Nullable E[] iterable, @NonNull final Function<E, K> classifier, @Nullable Supplier<Map<K, List<E>>> mapFactory) {
        return groupBy(asIterable(iterable), classifier, mapFactory);
    }

    public static <K, E, C extends Collection<E>> Map<K, List<E>> groupBy(@Nullable C iterable, @NonNull final Function<E, K> classifier) {
        return groupBy(iterable, classifier, null);
    }

    public static <K, E, C extends Collection<E>> Map<K, List<E>> groupBy(@Nullable Iterable<E> iterable, @NonNull final Function<E, K> classifier, @Nullable Supplier<Map<K, List<E>>> mapFactory) {
        Objects.requireNonNull(classifier);
        iterable = asIterable(iterable);
        if (mapFactory == null) {
            mapFactory = new Supplier<Map<K, List<E>>>() {
                @Override
                public Map<K, List<E>> get() {
                    return new HashMap<K, List<E>>();
                }
            };
        }
        Map<K, List<E>> map = mapFactory.get();
        Objects.requireNonNull(map);
        final WrappedNonAbsentMap<K, List<E>> wrappedNonAbsentMap = WrappedNonAbsentMap.wrap(map, new Function<K, List<E>>() {
            @Override
            public List<E> apply(K key) {
                return new ArrayList<E>();
            }
        });
        forEach(asCollection(asIterable(iterable)), new Consumer<E>() {
            @Override
            public void accept(E e) {
                K group = classifier.apply(e);
                wrappedNonAbsentMap.get(group).add(e);
            }
        });
        return map;
    }


    /*****************************************************************
     *      Collector Factory:
     *****************************************************************/


    public static <E, K> Collector<E, Map<K, List<E>>, Map<K, List<E>>> groupingBy(@NonNull final Function<E, K> classifier, @NonNull final Supplier<Map<K, List<E>>> mapFactory) {
        Objects.requireNonNull(classifier);
        Objects.requireNonNull(mapFactory);
        return new SimpleCollector<>(mapFactory, (map, e) -> {
            K group = classifier.apply(e);
            List<E> list = map.computeIfAbsent(group, k -> new ArrayList<E>());
            list.add(e);
        }, new BinaryOperator<Map<K, List<E>>>() {
            @Override
            public Map<K, List<E>> apply(Map<K, List<E>> kListMap, Map<K, List<E>> kListMap2) {
                kListMap.putAll(kListMap2);
                return kListMap;
            }
        }, StreamUtils.CH_ID);
    }

    public static <E, K> Collector<E, Map<K, List<E>>, Map<K, List<E>>> groupingBy(@NonNull final BiFunction<Integer, E, K> classifier, @NonNull final Supplier<Map<K, List<E>>> mapFactory) {
        Objects.requireNonNull(classifier);
        Objects.requireNonNull(mapFactory);
        return new SimpleCollector<>(mapFactory, new BiConsumer<Map<K, List<E>>, E>() {
            private int index = 0;

            @Override
            public void accept(Map<K, List<E>> map, E e) {
                K group = classifier.apply(index, e);
                List<E> list = map.get(group);
                if (list == null) {
                    list = new ArrayList<E>();
                    map.put(group, list);
                }
                list.add(e);
                index++;
            }
        }, (kListMap, kListMap2) -> {
            kListMap.putAll(kListMap2);
            return kListMap;
        }, StreamUtils.CH_ID);
    }


    public static <E, K> Collector<E, Map<K, List<E>>, Map<K, List<E>>> partitioningBy(@NonNull final Function<E, K> classifier) {
        return groupingBy(classifier, () -> new NonAbsentTreeMap<K, List<E>>(input -> new ArrayList<E>()));
    }

    public static <E, K> Collector<E, Map<K, List<E>>, Map<K, List<E>>> partitioningBy(@NonNull final BiFunction<Integer, E, K> classifier) {
        return groupingBy(classifier, () -> new NonAbsentTreeMap<K, List<E>>(new Function<K, List<E>>() {
            @Override
            public List<E> apply(K input) {
                return new ArrayList<E>();
            }
        }));
    }

    /**
     * Randomly permutes the specified list using a default source of
     * randomness.  All permutations occur with approximately equal
     * likelihood.<p>
     * <p>
     * The hedge "approximately" is used in the foregoing description because
     * default source of randomness is only approximately an unbiased source
     * of independently chosen bits. If it were a perfect source of randomly
     * chosen bits, then the algorithm would choose permutations with perfect
     * uniformity.<p>
     * <p>
     * This implementation traverses the list backwards, from the last element
     * up to the second, repeatedly swapping a randomly selected element into
     * the "current position".  Elements are randomly selected from the
     * portion of the list that runs from the first element to the current
     * position, inclusive.<p>
     * <p>
     * This method runs in linear time.  If the specified list does not
     * implement the {@link RandomAccess} interface and is large, this
     * implementation dumps the specified list into an array before shuffling
     * it, and dumps the shuffled array back into the list.  This avoids the
     * quadratic behavior that would result from shuffling a "sequential
     * access" list in place.
     *
     * @param list the list to be shuffled.
     * @throws UnsupportedOperationException if the specified list or
     *                                       its list-iterator does not support the <tt>set</tt> operation.
     */
    public static <E> void shuffle(@NonNull List<E> list) {
        shuffle(list, GlobalThreadLocalMap.getRandom());
    }

    /**
     * Randomly permute the specified list using the specified source of
     * randomness.  All permutations occur with equal likelihood
     * assuming that the source of randomness is fair.<p>
     * <p>
     * This implementation traverses the list backwards, from the last element
     * up to the second, repeatedly swapping a randomly selected element into
     * the "current position".  Elements are randomly selected from the
     * portion of the list that runs from the first element to the current
     * position, inclusive.<p>
     * <p>
     * This method runs in linear time.  If the specified list does not
     * implement the {@link RandomAccess} interface and is large, this
     * implementation dumps the specified list into an array before shuffling
     * it, and dumps the shuffled array back into the list.  This avoids the
     * quadratic behavior that would result from shuffling a "sequential
     * access" list in place.
     *
     * @param list the list to be shuffled.
     * @param rnd  the source of randomness to use to shuffle the list.
     * @throws UnsupportedOperationException if the specified list or its
     *                                       list-iterator does not support the <tt>set</tt> operation.
     */
    public static <E> void shuffle(@NonNull List<E> list, @NonNull IRandom rnd) {
        int size = list.size();
        if (size < 5 || list instanceof RandomAccess) {
            for (int i = size; i > 1; i--) {
                swap(list, i - 1, rnd.nextInt(i));
            }
        } else {
            Object arr[] = list.toArray();

            // Shuffle array
            for (int i = size; i > 1; i--) {
                swap(arr, i - 1, rnd.nextInt(i));
            }

            // Dump array back into list
            ListIterator it = list.listIterator();
            for (int i = 0; i < arr.length; i++) {
                it.next();
                it.set(arr[i]);
            }
        }
    }


    /**
     * Swaps the elements at the specified positions in the specified list.
     * (If the specified positions are equal, invoking this method leaves
     * the list unchanged.)
     *
     * @param list The list in which to swap elements.
     * @param i    the index of one element to be swapped.
     * @param j    the index of the other element to be swapped.
     * @throws IndexOutOfBoundsException if either <tt>i</tt> or <tt>j</tt>
     *                                   is out of range (i &lt; 0 || i &gt;= list.size()
     *                                   || j &lt; 0 || j &gt;= list.size()).
     */
    public static <E> void swap(@NonNull List<E> list, int i, int j) {
        final List<E> l = list;
        l.set(i, l.set(j, l.get(i)));
    }

    /**
     * Swaps the two specified elements in the specified array.
     */
    public static <E> void swap(@NonNull E[] arr, int i, int j) {
        ArrayUtils.swap(arr, i, j);
    }

    public static <E> int indexOf(List<E> list, E e) {
        return indexOf(list, e, 0);
    }

    public static <E> int indexOf(List<E> list, E e, int startIndex) {
        return indexOf(list, e, startIndex, Emptys.isEmpty(list) ? 0 : list.size());
    }

    /**
     * [startIndex, endIndex)
     *
     * @param list
     * @param e
     * @param startIndex 包含
     * @param endIndex   不包含
     * @param <E>
     */
    public static <E> int indexOf(List<E> list, E e, int startIndex, int endIndex) {
        if (list == null || list.isEmpty()) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (startIndex >= list.size()) {
            return -1;
        }
        if (endIndex <= 0) {
            return -1;
        }
        if (endIndex > list.size()) {
            endIndex = list.size();
        }
        if (startIndex >= endIndex) {
            return -1;
        }
        ArrayList<E> arrayList = (list instanceof ArrayList) ? (ArrayList<E>) list : Lists.newArrayList(list);
        for (int i = startIndex; i < endIndex; i++) {
            if (ObjectUtils.equals(arrayList.get(i), e)) {
                return i;
            }
        }
        return -1;
    }

    public static <E> int lastIndexOf(List<E> list, E e) {
        return lastIndexOf(list, e, 0);
    }

    public static <E> int lastIndexOf(List<E> list, E e, int startIndex) {
        return lastIndexOf(list, e, startIndex, Emptys.isEmpty(list) ? 0 : list.size());
    }

    /**
     * [startIndex, endIndex)
     *
     * @param list
     * @param e
     * @param startIndex
     * @param endIndex
     * @param <E>
     */
    public static <E> int lastIndexOf(List<E> list, E e, int startIndex, int endIndex) {
        if (list == null || list.isEmpty()) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (startIndex >= list.size()) {
            return -1;
        }
        if (endIndex <= 0) {
            return -1;
        }
        if (endIndex > list.size()) {
            endIndex = list.size();
        }
        if (startIndex >= endIndex) {
            return -1;
        }
        ArrayList<E> arrayList = (list instanceof ArrayList) ? (ArrayList<E>) list : Lists.newArrayList(list);
        for (int i = endIndex - 1; i >= startIndex; i--) {
            if (ObjectUtils.equals(arrayList.get(i), e)) {
                return i;
            }
        }
        return -1;
    }

    public static <E> int indexOf(E[] list, E e) {
        return indexOf(Lists.asList(list), e);
    }

    public static <E> int indexOf(E[] list, E e, int startIndex) {
        return indexOf(Lists.asList(list), e, startIndex);
    }

    public static <E> int indexOf(E[] list, E e, int startIndex, int endIndex) {
        return indexOf(Lists.asList(list), e, startIndex, endIndex);
    }

    public static <E> int lastIndexOf(E[] list, E e) {
        return lastIndexOf(Lists.asList(list), e);
    }

    public static <E> int lastIndexOf(E[] list, E e, int startIndex) {
        return lastIndexOf(Lists.asList(list), e, startIndex);
    }

    public static <E> int lastIndexOf(E[] list, E e, int startIndex, int endIndex) {
        return lastIndexOf(Lists.asList(list), e, startIndex, endIndex);
    }

    public static <E> E apply(E input, Function<E, E>... mappers) {
        for (int i = 0; i < mappers.length; i++) {
            input = mappers[i].apply(input);
        }
        return input;
    }

    public static <E> boolean isFirst(@Nullable E obj, @Nullable List<E> list) {
        if (ObjectUtils.isEmpty(list)) {
            return false;
        }
        return ObjectUtils.equals(new ListSequence<E>(list).first(), obj);
    }

    public static <E> boolean isLast(@Nullable E obj, @Nullable List<E> list) {
        if (ObjectUtils.isEmpty(list)) {
            return false;
        }
        return ObjectUtils.equals(new ListSequence<E>(list).last(), obj);
    }

    public static <E> boolean isFirst(@Nullable E obj, @Nullable Iterable<E> list) {
        if (ObjectUtils.isEmpty(list)) {
            return false;
        }
        return ObjectUtils.equals(new IterableSequence<E>(list).first(), obj);
    }

    public static <E> boolean isLast(@Nullable E obj, @Nullable Iterable<E> list) {
        if (ObjectUtils.isEmpty(list)) {
            return false;
        }
        return ObjectUtils.equals(new IterableSequence<E>(list).last(), obj);
    }

    public static <E> boolean isFirst(@Nullable E obj, @Nullable SortedSet<E> set) {
        if (ObjectUtils.isEmpty(set)) {
            return false;
        }
        return ObjectUtils.equals(new SortedSetSequence<E>(set).first(), obj);
    }

    public static <E> boolean isLast(@Nullable E obj, @Nullable SortedSet<E> set) {
        if (ObjectUtils.isEmpty(set)) {
            return false;
        }
        return ObjectUtils.equals(new SortedSetSequence<E>(set).last(), obj);
    }

    /**
     * Assumption: coll1.size <= coll2.size
     */
    public static <K, V1, V2, R> Map<K, R> loopJoin(Collection<V1> coll1, Collection<V2> coll2,
                                                    KeyExtractor<K, V1> extractor1, KeyExtractor<K, V2> extractor2,
                                                    Function3<K, V1, V2, R> joinFunction) {
        Map<K, R> results = new HashMap<>();
        for (V2 v2 : coll2) {
            K key = extractor2.extract(v2);

            for (V1 v1 : coll1) {
                if (extractor1.extract(v1).equals(key)) {
                    results.put(key, joinFunction.apply(key, v1, v2));
                    break;
                }
            }
        }
        return results;
    }

    /**
     * Internal: coll1 is turned into HashMap
     */
    public static <K, V1, V2, R> Map<K, R> hashJoin(Collection<V1> coll1, Collection<V2> coll2,
                                                    KeyExtractor<K, V1> extractor1, KeyExtractor<K, V2> extractor2,
                                                    Function3<K, V1, V2, R> joinFunction) {
        Map<K, R> results = new HashMap<>();

        Map<K, V1> map1 = new HashMap<>();
        for (V1 v1 : coll1) {
            map1.put(extractor1.extract(v1), v1);
        }

        for (V2 v2 : coll2) {
            K key = extractor2.extract(v2);
            V1 v1 = map1.get(key);
            if (v1 != null) {
                R result = joinFunction.apply(key, v1, v2);
                results.put(key, result);
            }
        }
        return results;
    }

    /**
     * Assumption: map1.size <= map2.size
     */
    public static <K, V1, V2, R> Map<K, R> mapsJoin(Map<K, V1> map1, Map<K, V2> map2,
                                                    Function3<K, V1, V2, R> joinFunction) {
        Map<K, R> results = new HashMap<>();
        for (Map.Entry<K, V1> entry1 : map1.entrySet()) {
            K key = entry1.getKey();
            V2 v2 = map2.get(key);
            if (v2 != null) {
                R result = joinFunction.apply(key, entry1.getValue(), v2);
                results.put(key, result);
            }
        }
        return results;
    }

}