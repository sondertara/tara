package com.sondertara.common.navigation;

import java.util.List;

/**
 *  */
public interface Navigator<Context> {
    <E> E get(Context context, String pathExpression);

    <E> List<E> getList(Context context, String pathExpression);

    <E> void set(Context context, String pathExpression, E value);

    <E> Class<E> getType(Context context, String pathExpression);

    String getParentPath(String pathExpression);
    String getLeaf(String pathExpression);
}
