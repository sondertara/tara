package com.sondertara.common.io.stream.obj;

import java.io.ObjectStreamClass;
import java.util.function.Predicate;

/**
 *  */
public interface SecureObjectClassPredicate extends Predicate<ObjectStreamClass> {
    @Override
    public boolean test(ObjectStreamClass objectStreamClass) ;
}
