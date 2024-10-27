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
package com.sondertara.evalexpr.operators.arithmetic;

import com.sondertara.evalexpr.EvaluationException;
import com.sondertara.evalexpr.Expression;
import com.sondertara.evalexpr.data.EvaluationValue;
import com.sondertara.evalexpr.operators.AbstractOperator;
import com.sondertara.evalexpr.operators.PrefixOperator;
import com.sondertara.evalexpr.parser.Token;

/** Unary prefix plus. */
@PrefixOperator(leftAssociative = false)
public class PrefixPlusOperator extends AbstractOperator {

  @Override
  public EvaluationValue evaluate(
      Expression expression, Token operatorToken, EvaluationValue... operands)
      throws EvaluationException {
    EvaluationValue operator = operands[0];

    if (operator.isNumberValue()) {
      return expression.convertValue(
          operator.getNumberValue().plus(expression.getConfiguration().getMathContext()));
    } else {
      throw EvaluationException.ofUnsupportedDataTypeInOperation(operatorToken);
    }
  }
}
