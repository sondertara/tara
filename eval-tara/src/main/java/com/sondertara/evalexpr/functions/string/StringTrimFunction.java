/*
  Copyright 2012-2024 Udo Klimaschewski

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
package com.sondertara.evalexpr.functions.string;

import com.sondertara.evalexpr.EvaluationException;
import com.sondertara.evalexpr.Expression;
import com.sondertara.evalexpr.data.EvaluationValue;
import com.sondertara.evalexpr.functions.AbstractFunction;
import com.sondertara.evalexpr.functions.FunctionParameter;
import com.sondertara.evalexpr.parser.Token;

/**
 * Returns the given string with all leading and trailing space removed.
 *
 * @author LeonardoSoaresDev
 */
@FunctionParameter(name = "string")
public class StringTrimFunction extends AbstractFunction {
  @Override
  public EvaluationValue evaluate(
      Expression expression, Token functionToken, EvaluationValue... parameterValues)
      throws EvaluationException {
    return expression.convertValue(parameterValues[0].getStringValue().trim());
  }
}
