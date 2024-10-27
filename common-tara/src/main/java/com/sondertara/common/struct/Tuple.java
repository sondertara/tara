package com.sondertara.common.struct;



import com.sondertara.common.base.CloneSupport;
import com.sondertara.common.base.Valid;
import com.sondertara.common.collection.ArrayUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * 元组
 *
 * @author Jiahang Li
 * @version 1.0.0
 *  */
public class Tuple extends CloneSupport<Tuple> implements Serializable, Iterable<Object> {

    private static final long serialVersionUID = 228374192841290087L;

    private final Object[] members;

    public Tuple(Object... members) {
        Valid.notEmpty(members, "arguments size is zero");
        this.members = members;
    }

    /**
     * 创建元组
     *
     * @param members 元素
     * @return Tuple
     */
    public static Tuple of(Object... members) {
        return new Tuple(members);
    }

    /**
     * 获取指定位置元素
     *
     * @param <T>   T
     * @param index index
     * @return 元素
     */
    @SuppressWarnings("unchecked")
    public <T> T get(int index) {
        return (T) members[index];
    }

    /**
     * 获得所有元素
     *
     * @return 获得所有元素
     */
    public Object[] getMembers() {
        return this.members;
    }

    /**
     * 获取元组长度
     *
     * @return 长度
     */
    public int size() {
        return members.length;
    }

    @Override
    public int hashCode() {
        return 31 * Arrays.deepHashCode(members);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Tuple other = (Tuple) obj;
        return Arrays.deepEquals(members, other.members);
    }


    public boolean isEmpty() {
        return ArrayUtils.isEmpty(members);
    }


    public boolean isNotEmpty() {
        return ArrayUtils.isNotEmpty(members);
    }

    /**
     * @return stream
     */
    public Stream<?> stream() {
        if (this.isEmpty()) {
            return Stream.empty();
        } else {
            return Stream.of(members);
        }
    }

    /**
     * 获取 Optional
     *
     * @param index 索引
     * @param <T>   T
     * @return Optional
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> optional(int index) {
        int size = this.size();
        if (size > index) {
            return Optional.ofNullable((T) members[index]);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Iterator<Object> iterator() {

        return Arrays.stream(members).iterator();
    }

    @Override
    public Spliterator<Object> spliterator() {
        return Arrays.spliterator(members);
    }

    @Override
    public void forEach(Consumer<? super Object> action) {
        Arrays.stream(members).forEach(action);

    }

    @Override
    public String toString() {
        return Arrays.toString(members);
    }


}
