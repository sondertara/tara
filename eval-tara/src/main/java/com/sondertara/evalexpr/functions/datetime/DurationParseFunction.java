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
package com.sondertara.evalexpr.functions.datetime;

import com.sondertara.evalexpr.Expression;
import com.sondertara.evalexpr.data.EvaluationValue;
import com.sondertara.evalexpr.functions.AbstractFunction;
import com.sondertara.evalexpr.functions.FunctionParameter;
import com.sondertara.evalexpr.parser.Token;
import java.time.Duration;

/**
 * Converts the given ISO-8601 duration string representation to a duration value. E.g. "P2DT3H4M"
 * parses 2 days, 3 hours and 4 minutes.
 */
@FunctionParameter(name = "value")
public class DurationParseFunction extends AbstractFunction {
  @Override
  public EvaluationValue evaluate(
      Expression expression, Token functionToken, EvaluationValue... parameterValues) {
    String text = parameterValues[0].getStringValue();
    return expression.convertValue(Duration.parse(text));
  }
}
