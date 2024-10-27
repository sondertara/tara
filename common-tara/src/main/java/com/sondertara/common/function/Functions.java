package com.sondertara.common.function;

import com.sondertara.common.base.Assert;
import com.sondertara.common.base.Emptys;
import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.text.StringUtils;
import com.sondertara.common.collection.Lists;
import com.sondertara.common.function.predicate.EmptyPredicate;
import com.sondertara.common.math.Maths;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Functions {
    private Functions() {
    }

    /*******************************************
     *   Function, Mapper
     *******************************************/
    public static <E> Function<E, E> noopFunction() {
        return new Function<E, E>() {
            @Override
            public E apply(E input) {
                return input;
            }
        };
    }


    public static <E> Function<E, String> toStringFunction() {
        return input -> {
            Objects.requireNonNull(input);
            return input.toString();
        };
    }

    public static BiFunction<Integer, Integer, Integer> maxIntegerFunction() {
        return Maths::max;
    }

    public static BiFunction<Float, Float, Float> maxFloatFunction() {
        return Maths::maxFloat;
    }

    public static BiFunction<Long, Long, Long> maxLongFunction() {
        return new BiFunction<Long, Long, Long>() {
            @Override
            public Long apply(Long a, Long b) {
                return Maths.maxLong(a, b);
            }
        };
    }

    public static BiFunction<Double, Double, Double> maxDoubleFunction() {
        return new BiFunction<Double, Double, Double>() {
            @Override
            public Double apply(Double a, Double b) {
                return Maths.maxDouble(a, b);
            }
        };
    }

    public static BiFunction<Integer, Integer, Integer> minIntegerFunction() {
        return new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) {
                return Maths.min(a, b);
            }
        };
    }

    public static BiFunction<Float, Float, Float> minFloatFunction() {
        return new BiFunction<Float, Float, Float>() {
            @Override
            public Float apply(Float a, Float b) {
                return Maths.minFloat(a, b);
            }
        };
    }

    public static BiFunction<Long, Long, Long> minLongFunction() {
        return new BiFunction<Long, Long, Long>() {
            @Override
            public Long apply(Long a, Long b) {
                return Maths.minLong(a, b);
            }
        };
    }

    public static BiFunction<Double, Double, Double> minDoubleFunction() {
        return new BiFunction<Double, Double, Double>() {
            @Override
            public Double apply(Double a, Double b) {
                return Maths.minDouble(a, b);
            }
        };
    }

    public static BiFunction<Integer, Integer, Integer> sumIntegerFunction() {
        return new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) {
                return Maths.sum(a, b);
            }
        };
    }

    public static BiFunction<Float, Float, Float> sumFloatFunction() {
        return new BiFunction<Float, Float, Float>() {
            @Override
            public Float apply(Float a, Float b) {
                return Maths.sumFloat(a, b);
            }
        };
    }

    public static BiFunction<Long, Long, Long> sumLongFunction() {
        return new BiFunction<Long, Long, Long>() {
            @Override
            public Long apply(Long a, Long b) {
                return Maths.sumLong(a, b);
            }
        };
    }

    public static BiFunction<Double, Double, Double> sumDoubleFunction() {
        return new BiFunction<Double, Double, Double>() {
            @Override
            public Double apply(Double a, Double b) {
                return Maths.sumDouble(a, b);
            }
        };
    }


    public static Function<String, String> toLowerCase() {
        return new Function<String, String>() {
            @Override
            public String apply(String input) {
                return input.toLowerCase();
            }
        };
    }

    public static Function<String, String> toUpperCase() {
        return new Function<String, String>() {
            @Override
            public String apply(String input) {
                return input.toUpperCase();
            }
        };
    }


    /**********************************************
     *   Predicate
     **********************************************/
    public static <E> Predicate<E> nonNullPredicate() {
        return new Predicate<E>() {
            @Override
            public boolean test(E value) {
                return value != null;
            }
        };
    }


    public static <E> Predicate<E> nullPredicate() {
        return new Predicate<E>() {
            @Override
            public boolean test(E value) {
                return value == null;
            }
        };
    }

    public static <E1, E2> Predicate2<E1, E2> nonNullPredicate2() {
        return new Predicate2<E1, E2>() {
            @Override
            public boolean test(E1 e1, E2 value) {
                return value != null;
            }
        };
    }


    public static <E1, E2> Predicate2<E1, E2> nullPredicate2() {
        return new Predicate2<E1, E2>() {
            @Override
            public boolean test(E1 e1, E2 value) {
                return value == null;
            }
        };
    }

    public static <E> Predicate<E> emptyPredicate() {
        return EmptyPredicate.IS_EMPTY_PREDICATE;
    }

    public static <E> Predicate<E> isInstancePredicate(final Class<E> itfc) {
        return new Predicate<E>() {
            @Override
            public boolean test(E obj) {
                return itfc.isInstance(obj);
            }
        };
    }

    public static <E> Predicate<E> notEmptyPredicate() {
        return EmptyPredicate.IS_NOT_EMPTY_PREDICATE;
    }

    public static <E> Predicate<E> trueFilter() {
        return new Predicate<E>() {
            @Override
            public boolean test(E e) {
                return true;
            }
        };
    }

    public static <E> Predicate<E> falseFilter() {
        return new Predicate<E>() {
            @Override
            public boolean test(E e) {
                return false;
            }
        };
    }

    public static <E> Predicate<E> truePredicate() {
        return booleanPredicate(true);
    }

    public static <E> Predicate<E> falsePredicate() {
        return booleanPredicate(false);
    }

    public static <E> Predicate<E> booleanPredicate(final boolean value) {
        return element -> value;
    }

    public static <E1, E2> Predicate2<E1, E2> truePredicate2() {
        return booleanPredicate2(true);
    }

    public static <E> Predicate<E> reversePredicate(final Predicate<E> predicate) {
        return new Predicate<E>() {
            @Override
            public boolean test(E e) {
                return !predicate.test(e);
            }
        };
    }

    public static <E1, E2> Predicate2<E1, E2> reversePredicate(final Predicate2<E1, E2> predicate) {
        return new Predicate2<E1, E2>() {
            @Override
            public boolean test(E1 key, E2 value) {
                return !predicate.test(key, value);
            }
        };
    }

    public static <E1, E2> Predicate2<E1, E2> falsePredicate2() {
        return booleanPredicate2(false);
    }

    public static <E1, E2> Predicate2<E1, E2> booleanPredicate2(final boolean value) {
        return new Predicate2<E1, E2>() {
            @Override
            public boolean test(E1 e1, E2 e2) {
                return value;
            }
        };
    }

    public static <E> Predicate<E> allPredicate(List<Predicate<E>> predicates) {
        return new Predicate<E>() {
            @Override
            public boolean test(final E value) {
                return predicates.stream().allMatch(new Predicate<Predicate<E>>() {
                    @Override
                    public boolean test(Predicate<E> predicate) {
                        return predicate.test(value);
                    }
                });
            }
        };
    }

    public static <E> Predicate<E> allPredicate(@NonNull Predicate<E>... predicates) {
        Assert.isTrue(Emptys.isNotEmpty(predicates));
        Assert.isTrue(predicates.length >= 1);
        return allPredicate(Lists.asList(predicates));
    }

    public static <E> Predicate<E> anyPredicate(List<Predicate<E>> predicates) {
        return new Predicate<E>() {
            @Override
            public boolean test(final E value) {
                return predicates.stream().anyMatch(new Predicate<Predicate<E>>() {
                    @Override
                    public boolean test(Predicate<E> filter) {
                        return filter.test(value);
                    }
                });
            }
        };
    }

    public static <E> Predicate<E> anyPredicate(@NonNull Predicate<E>... predicates) {
        Assert.isTrue(Emptys.isNotEmpty(predicates));
        Assert.isTrue(predicates.length >= 1);
        return anyPredicate(Lists.asList(predicates));
    }

    public static <E> Predicate<E> nonePredicate(List<Predicate<E>> predicates) {
        return new Predicate<E>() {
            @Override
            public boolean test(final E value) {
                return predicates.stream().noneMatch(new Predicate<Predicate<E>>() {
                    @Override
                    public boolean test(Predicate<E> filter) {
                        return filter.test(value);
                    }
                });
            }
        };
    }

    public static <E> Predicate<E> nonePredicate(Predicate<E>... predicates) {
        Assert.isTrue(Emptys.isNotEmpty(predicates));
        Assert.isTrue(predicates.length >= 1);
        return nonePredicate(Lists.asList(predicates));
    }

    public static <E> Predicate<E> andPredicate(@NonNull Predicate<E>... predicates) {
        return allPredicate(predicates);
    }

    public static <E> Predicate<E> orPredicate(@NonNull Predicate<E>... predicates) {
        return anyPredicate(predicates);
    }

    public static <E1, E2> Predicate2<E1, E2> deepEqualsPredicate() {
        return new Predicate2<E1, E2>() {
            @Override
            public boolean test(E1 v1, E2 v2) {
                return ObjectUtils.equals(v1, v2);
            }
        };
    }

    public static <E> Predicate<E> equalsPredicate(final E obj) {
        return new Predicate<E>() {
            @Override
            public boolean test(E value) {
                return ObjectUtils.equals(obj, value);
            }
        };
    }

    public static <E> Predicate<E> notEqualsPredicate(final E obj) {
        return new Predicate<E>() {
            @Override
            public boolean test(E value) {
                return !ObjectUtils.equals(obj, value);
            }
        };
    }

    public static <E1, E2> Predicate2<E1, E2> equalsPredicate() {
        return new Predicate2<E1, E2>() {
            @Override
            public boolean test(E1 v1, E2 v2) {
                return ObjectUtils.equals(v1, v2);
            }
        };
    }

    public static Predicate<String> stringContainsPredicate(final String cantained) {
        return stringContainsPredicate(cantained, false);
    }

    public static Predicate<String> stringContainsPredicate(final String cantained, final boolean ignoreCase) {
        Assert.isTrue(Emptys.isNotEmpty(cantained));
        return new Predicate<String>() {
            @Override
            public boolean test(String value) {
                return StringUtils.contains(value, cantained, ignoreCase);
            }
        };
    }


    /**********************************************
     * Function
     **********************************************/
    public static <K, V> Function<K, List<V>> emptyArrayListSupplier() {
        return new Function<K, List<V>>() {
            @Override
            public List<V> apply(K input) {
                return new ArrayList<>();
            }
        };
    }

    public static <K, V> Function<K, List<V>> emptyLinkedListSupplier() {
        return new Function<K, List<V>>() {
            @Override
            public List<V> apply(K input) {
                return new LinkedList<>();
            }
        };
    }

    public static <E> Supplier<HashSet<E>> emptyHashSetSupplier() {
        return new Supplier<HashSet<E>>() {
            @Override
            public HashSet<E> get() {
                return new HashSet<>();
            }
        };
    }

    public static <I, E> Function<I, HashSet<E>> emptyHashSetFunction() {
        return new Function<I, HashSet<E>>() {
            @Override
            public HashSet<E> apply(I input) {
                return new HashSet<>();
            }
        };
    }

    public static <I, E> Function<I, LinkedHashSet<E>> emptyLinkedHashSetSupplier() {
        return new Function<I, LinkedHashSet<E>>() {
            @Override
            public LinkedHashSet<E> apply(I input) {
                return new LinkedHashSet<>();
            }
        };
    }

    public static <E> Supplier<TreeSet<E>> emptyTreeSetSupplier0(final Comparator<E> comparator) {
        return new Supplier<TreeSet<E>>() {
            @Override
            public TreeSet<E> get() {
                return new TreeSet<>(comparator);
            }
        };
    }

    public static <I, V> Function<I, Set<V>> emptyTreeSetSupplier(final Comparator<V> comparator) {
        return new Function<I, Set<V>>() {
            @Override
            public Set<V> apply(Object input) {
                return new TreeSet<>(comparator);
            }
        };
    }

    public static <T> Function<T, T> noopSupplier() {
        return new Function<T, T>() {
            @Override
            public T apply(T input) {
                return input;
            }
        };
    }

    /******************************************
     *  noop consumer
     ******************************************/
    public static <T> Consumer<T> noopConsumer() {
        return new Consumer<T>() {
            @Override
            public void accept(T t) {
                // NOOP
            }
        };
    }

    public static <T1, T2> BiConsumer<T1, T2> noopConsumer2() {
        return new BiConsumer<T1, T2>() {
            @Override
            public void accept(T1 t1, T2 t2) {
                // NOOP
            }
        };
    }
}
