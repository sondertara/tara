package com.sondertara.common.reflect.parameter;

import com.sondertara.common.lifecycle.Initializable;

import java.lang.reflect.Constructor;

public interface ConstructorParameterSupplier extends ParameterSupplier<Constructor, ConstructorParameter>, Initializable {
}
