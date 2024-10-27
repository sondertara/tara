package com.sondertara.common.reflect.immutable;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.FieldValue;
import net.bytebuddy.implementation.bind.annotation.Origin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ImmutableProxyForwarderInteger {

	private ImmutableProxyForwarderInteger() {
	}

	public static Integer forward(@Origin Method method,
								  @FieldValue(ImmutableProxy.DELEGATE_FIELD_NAME) Object delegate,
								  @AllArguments Object[] args) throws InvocationTargetException, IllegalAccessException {
		return (Integer) method.invoke(delegate, args);
	}

}
