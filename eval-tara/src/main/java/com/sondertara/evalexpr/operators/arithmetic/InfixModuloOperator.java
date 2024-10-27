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

import static com.sondertara.evalexpr.operators.OperatorIfc.OPERATOR_PRECEDENCE_MULTIPLICATIVE;

import com.sondertara.evalexpr.EvaluationException;
import com.sondertara.evalexpr.Expression;
import com.sondertara.evalexpr.data.EvaluationValue;
import com.sondertara.evalexpr.operators.AbstractOperator;
import com.sondertara.evalexpr.operators.InfixOperator;
import com.sondertara.evalexpr.parser.Token;
import java.math.BigDecimal;

/** Remainder (modulo) of two numbers. */
@InfixOperator(precedence = OPERATOR_PRECEDENCE_MULTIPLICATIVE)
public class InfixModuloOperator extends AbstractOperator {

  @Override
  public EvaluationValue evaluate(
      Expression expression, Token operatorToken, EvaluationValue... operands)
      throws EvaluationException {
    EvaluationValue leftOperand = operands[0];
    EvaluationValue rightOperand = operands[1];

    if (leftOperand.isNumberValue() && rightOperand.isNumberValue()) {

      if (rightOperand.getNumberValue().equals(BigDecimal.ZERO)) {
        throw new EvaluationException(operatorToken, "Division by zero");
      }

      return expression.convertValue(
          leftOperand
              .getNumberValue()
              .remainder(
                  rightOperand.getNumberValue(), expression.getConfiguration().getMathContext()));
    } else {
      throw EvaluationException.ofUnsupportedDataTypeInOperation(operatorToken);
    }
  }
}
