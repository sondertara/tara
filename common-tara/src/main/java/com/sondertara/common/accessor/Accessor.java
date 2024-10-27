package com.sondertara.common.accessor;

import com.sondertara.common.collection.ArrayAccessor;
import com.sondertara.common.collection.StringMapAccessor;
import com.sondertara.common.function.ValueGetter;
import com.sondertara.common.net.http.HttpQueryStringAccessor;
import com.sondertara.common.reflect.FieldAccessor;

/**
 * A object's accessor, use it, you can get value from T object;
 * <pre>
 * getXxx(K key): get the Xxx value association to specified key from target
 * getXxx(K key, Xxx default): get the Xxx value association to specified key from target, if can't find the key, return the specified default value
 * </pre>
 *
 * @author jinuo.fang
 * @see StringMapAccessor
 * @see FieldAccessor
 * @see HttpQueryStringAccessor
 * @see ArrayAccessor
 */

public interface Accessor<K, T> extends ValueGetter<K> {
    T getTarget();

    void setTarget(T target);

    void set(K key, Object value);

    void setString(K key, String value);

    void setByte(K key, byte value);

    void setShort(K key, short value);

    void setInteger(K key, int value);

    void setLong(K key, long value);

    void setFloat(K key, float value);

    void setDouble(K key, double value);

    void setBoolean(K key, boolean value);

    void setChar(K key, char value);

    void remove(K key);
}
