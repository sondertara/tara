package com.sondertara.common.invocation;

import java.lang.reflect.Constructor;

public interface ConstructorInvocation extends Invocation<Constructor> {
    @Override
    Constructor getJoinPoint();
}
