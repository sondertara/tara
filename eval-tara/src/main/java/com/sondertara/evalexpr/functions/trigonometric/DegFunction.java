/*
  Copyright 2012-2022 Udo Klimaschewski

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package com.sondertara.evalexpr.functions.trigonometric;

import com.sondertara.evalexpr.Expression;
import com.sondertara.evalexpr.data.EvaluationValue;
import com.sondertara.evalexpr.functions.AbstractFunction;
import com.sondertara.evalexpr.functions.FunctionParameter;
import com.sondertara.evalexpr.parser.Token;

/**
 * Converts an angle measured in radians to an approximately equivalent angle measured in degrees.
 */
@FunctionParameter(name = "radians")
public class DegFunction extends AbstractFunction {
  @Override
  public EvaluationValue evaluate(
      Expression expression, Token functionToken, EvaluationValue... parameterValues) {

    double rad = Math.toDegrees(parameterValues[0].getNumberValue().doubleValue());

    return expression.convertDoubleValue(rad);
  }
}
