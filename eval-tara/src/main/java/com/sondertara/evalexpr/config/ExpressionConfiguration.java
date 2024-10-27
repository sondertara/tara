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
package com.sondertara.evalexpr.config;


import com.sondertara.common.collection.Lists;
import com.sondertara.common.collection.Maps;
import com.sondertara.evalexpr.data.DataAccessorIfc;
import com.sondertara.evalexpr.data.EvaluationValue;
import com.sondertara.evalexpr.data.MapBasedDataAccessor;
import com.sondertara.evalexpr.data.conversion.DefaultEvaluationValueConverter;
import com.sondertara.evalexpr.data.conversion.EvaluationValueConverterIfc;
import com.sondertara.evalexpr.functions.FunctionIfc;
import com.sondertara.evalexpr.functions.basic.AbsFunction;
import com.sondertara.evalexpr.functions.basic.AverageFunction;
import com.sondertara.evalexpr.functions.basic.CeilingFunction;
import com.sondertara.evalexpr.functions.basic.CoalesceFunction;
import com.sondertara.evalexpr.functions.basic.FactFunction;
import com.sondertara.evalexpr.functions.basic.FloorFunction;
import com.sondertara.evalexpr.functions.basic.IfFunction;
import com.sondertara.evalexpr.functions.basic.Log10Function;
import com.sondertara.evalexpr.functions.basic.LogFunction;
import com.sondertara.evalexpr.functions.basic.MaxFunction;
import com.sondertara.evalexpr.functions.basic.MinFunction;
import com.sondertara.evalexpr.functions.basic.NotFunction;
import com.sondertara.evalexpr.functions.basic.RandomFunction;
import com.sondertara.evalexpr.functions.basic.RoundFunction;
import com.sondertara.evalexpr.functions.basic.SqrtFunction;
import com.sondertara.evalexpr.functions.basic.SumFunction;
import com.sondertara.evalexpr.functions.basic.SwitchFunction;
import com.sondertara.evalexpr.operators.OperatorIfc;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * The expression configuration can be used to configure various aspects of expression parsing and
 * evaluation. <br>
 * A <code>Builder</code> is provided to create custom configurations, e.g.: <br>
 *
 * <pre>
 *   ExpressionConfiguration config = ExpressionConfiguration.builder().mathContext(MathContext.DECIMAL32).arraysAllowed(false).build();
 * </pre>
 *
 * <br>
 * Additional operators and functions can be added to an existing configuration:<br>
 *
 * <pre>
 *     ExpressionConfiguration.defaultConfiguration()
 *        .withAdditionalOperators(
 *            Maps.entry("++", new PrefixPlusPlusOperator()),
 *            Maps.entry("++", new PostfixPlusPlusOperator()))
 *        .withAdditionalFunctions(Maps.entry("save", new SaveFunction()),
 *            Maps.entry("update", new UpdateFunction()));
 * </pre>
 */
@Builder(toBuilder = true)
@Getter
public class ExpressionConfiguration {

  /** The standard set constants for EvalEx. */
  public static final Map<String, EvaluationValue> StandardConstants =
      Collections.unmodifiableMap(getStandardConstants());

  /** Setting the decimal places to unlimited, will disable intermediate rounding. */
  public static final int DECIMAL_PLACES_ROUNDING_UNLIMITED = -1;

  /** The default math context has a precision of 68 and {@link RoundingMode#HALF_EVEN}. */
  public static final MathContext DEFAULT_MATH_CONTEXT =
      new MathContext(68, RoundingMode.HALF_EVEN);

  /**
   * The default date time formatters used when parsing a date string. Each format will be tried and
   * the first matching will be used.
   *
   * <ul>
   *   <li>{@link DateTimeFormatter#ISO_DATE_TIME}
   *   <li>{@link DateTimeFormatter#ISO_DATE}
   *   <li>{@link DateTimeFormatter#ISO_LOCAL_DATE_TIME}
   *   <li>{@link DateTimeFormatter#ISO_LOCAL_DATE}
   * </ul>
   */
  protected static final List<DateTimeFormatter> DEFAULT_DATE_TIME_FORMATTERS =

          Lists.newArrayList(
              DateTimeFormatter.ISO_DATE_TIME,
              DateTimeFormatter.ISO_DATE,
              DateTimeFormatter.ISO_LOCAL_DATE_TIME,
              DateTimeFormatter.ISO_LOCAL_DATE,
              DateTimeFormatter.RFC_1123_DATE_TIME);

  /** The operator dictionary holds all operators that will be allowed in an expression. */
  @Builder.Default
  @SuppressWarnings("unchecked")
  private final OperatorDictionaryIfc operatorDictionary =
      MapBasedOperatorDictionary.ofOperators(
          // arithmetic
              Maps.entry("+", new com.sondertara.evalexpr.operators.arithmetic.PrefixPlusOperator()),
              Maps.entry("-", new com.sondertara.evalexpr.operators.arithmetic.PrefixMinusOperator()),
              Maps.entry("+", new com.sondertara.evalexpr.operators.arithmetic.InfixPlusOperator()),
              Maps.entry("-", new com.sondertara.evalexpr.operators.arithmetic.InfixMinusOperator()),
              Maps.entry("*", new com.sondertara.evalexpr.operators.arithmetic.InfixMultiplicationOperator()),
              Maps.entry("/", new com.sondertara.evalexpr.operators.arithmetic.InfixDivisionOperator()),
              Maps.entry("^", new com.sondertara.evalexpr.operators.arithmetic.InfixPowerOfOperator()),
              Maps.entry("%", new com.sondertara.evalexpr.operators.arithmetic.InfixModuloOperator()),
          // booleans
              Maps.entry("=", new com.sondertara.evalexpr.operators.booleans.InfixEqualsOperator()),
              Maps.entry("==", new com.sondertara.evalexpr.operators.booleans.InfixEqualsOperator()),
              Maps.entry("!=", new com.sondertara.evalexpr.operators.booleans.InfixNotEqualsOperator()),
              Maps.entry("<>", new com.sondertara.evalexpr.operators.booleans.InfixNotEqualsOperator()),
              Maps.entry(">", new com.sondertara.evalexpr.operators.booleans.InfixGreaterOperator()),
              Maps.entry(">=", new com.sondertara.evalexpr.operators.booleans.InfixGreaterEqualsOperator()),
              Maps.entry("<", new com.sondertara.evalexpr.operators.booleans.InfixLessOperator()),
              Maps.entry("<=", new com.sondertara.evalexpr.operators.booleans.InfixLessEqualsOperator()),
              Maps.entry("&&", new com.sondertara.evalexpr.operators.booleans.InfixAndOperator()),
              Maps.entry("||", new com.sondertara.evalexpr.operators.booleans.InfixOrOperator()),
              Maps.entry("!", new com.sondertara.evalexpr.operators.booleans.PrefixNotOperator()));

  /** The function dictionary holds all functions that will be allowed in an expression. */
  @Builder.Default
  @SuppressWarnings("unchecked")
  private final FunctionDictionaryIfc functionDictionary =
      MapBasedFunctionDictionary.ofFunctions(
          // basic functions
          Maps.entry("ABS", new AbsFunction()),
          Maps.entry("AVERAGE", new AverageFunction()),
          Maps.entry("CEILING", new CeilingFunction()),
          Maps.entry("COALESCE", new CoalesceFunction()),
          Maps.entry("FACT", new FactFunction()),
          Maps.entry("FLOOR", new FloorFunction()),
          Maps.entry("IF", new IfFunction()),
          Maps.entry("LOG", new LogFunction()),
          Maps.entry("LOG10", new Log10Function()),
          Maps.entry("MAX", new MaxFunction()),
          Maps.entry("MIN", new MinFunction()),
          Maps.entry("NOT", new NotFunction()),
          Maps.entry("RANDOM", new RandomFunction()),
          Maps.entry("ROUND", new RoundFunction()),
          Maps.entry("SQRT", new SqrtFunction()),
          Maps.entry("SUM", new SumFunction()),
          Maps.entry("SWITCH", new SwitchFunction()),
          // trigonometric
          Maps.entry("ACOS", new com.sondertara.evalexpr.functions.trigonometric.AcosFunction()),
          Maps.entry("ACOSH", new com.sondertara.evalexpr.functions.trigonometric.AcosHFunction()),
          Maps.entry("ACOSR", new com.sondertara.evalexpr.functions.trigonometric.AcosRFunction()),
          Maps.entry("ACOT", new com.sondertara.evalexpr.functions.trigonometric.AcotFunction()),
          Maps.entry("ACOTH", new com.sondertara.evalexpr.functions.trigonometric.AcotHFunction()),
          Maps.entry("ACOTR", new com.sondertara.evalexpr.functions.trigonometric.AcotRFunction()),
          Maps.entry("ASIN", new com.sondertara.evalexpr.functions.trigonometric.AsinFunction()),
          Maps.entry("ASINH", new com.sondertara.evalexpr.functions.trigonometric.AsinHFunction()),
          Maps.entry("ASINR", new com.sondertara.evalexpr.functions.trigonometric.AsinRFunction()),
          Maps.entry("ATAN", new com.sondertara.evalexpr.functions.trigonometric.AtanFunction()),
          Maps.entry("ATAN2", new com.sondertara.evalexpr.functions.trigonometric.Atan2Function()),
          Maps.entry("ATAN2R", new com.sondertara.evalexpr.functions.trigonometric.Atan2RFunction()),
          Maps.entry("ATANH", new com.sondertara.evalexpr.functions.trigonometric.AtanHFunction()),
          Maps.entry("ATANR", new com.sondertara.evalexpr.functions.trigonometric.AtanRFunction()),
          Maps.entry("COS", new com.sondertara.evalexpr.functions.trigonometric.CosFunction()),
          Maps.entry("COSH", new com.sondertara.evalexpr.functions.trigonometric.CosHFunction()),
          Maps.entry("COSR", new com.sondertara.evalexpr.functions.trigonometric.CosRFunction()),
          Maps.entry("COT", new com.sondertara.evalexpr.functions.trigonometric.CotFunction()),
          Maps.entry("COTH", new com.sondertara.evalexpr.functions.trigonometric.CotHFunction()),
          Maps.entry("COTR", new com.sondertara.evalexpr.functions.trigonometric.CotRFunction()),
          Maps.entry("CSC", new com.sondertara.evalexpr.functions.trigonometric.CscFunction()),
          Maps.entry("CSCH", new com.sondertara.evalexpr.functions.trigonometric.CscHFunction()),
          Maps.entry("CSCR", new com.sondertara.evalexpr.functions.trigonometric.CscRFunction()),
          Maps.entry("DEG", new com.sondertara.evalexpr.functions.trigonometric.DegFunction()),
          Maps.entry("RAD", new com.sondertara.evalexpr.functions.trigonometric.RadFunction()),
          Maps.entry("SIN", new com.sondertara.evalexpr.functions.trigonometric.SinFunction()),
          Maps.entry("SINH", new com.sondertara.evalexpr.functions.trigonometric.SinHFunction()),
          Maps.entry("SINR", new com.sondertara.evalexpr.functions.trigonometric.SinRFunction()),
          Maps.entry("SEC", new com.sondertara.evalexpr.functions.trigonometric.SecFunction()),
          Maps.entry("SECH", new com.sondertara.evalexpr.functions.trigonometric.SecHFunction()),
          Maps.entry("SECR", new com.sondertara.evalexpr.functions.trigonometric.SecRFunction()),
          Maps.entry("TAN", new com.sondertara.evalexpr.functions.trigonometric.TanFunction()),
          Maps.entry("TANH", new com.sondertara.evalexpr.functions.trigonometric.TanHFunction()),
          Maps.entry("TANR", new com.sondertara.evalexpr.functions.trigonometric.TanRFunction()),
          // string functions
          Maps.entry("STR_CONTAINS", new com.sondertara.evalexpr.functions.string.StringContains()),
          Maps.entry("STR_ENDS_WITH", new com.sondertara.evalexpr.functions.string.StringEndsWithFunction()),
          Maps.entry("STR_FORMAT", new com.sondertara.evalexpr.functions.string.StringFormatFunction()),
          Maps.entry("STR_LOWER", new com.sondertara.evalexpr.functions.string.StringLowerFunction()),
          Maps.entry("STR_STARTS_WITH", new com.sondertara.evalexpr.functions.string.StringStartsWithFunction()),
          Maps.entry("STR_TRIM", new com.sondertara.evalexpr.functions.string.StringTrimFunction()),
          Maps.entry("STR_UPPER", new com.sondertara.evalexpr.functions.string.StringUpperFunction()),
          // date time functions
          Maps.entry("DT_DATE_NEW", new com.sondertara.evalexpr.functions.datetime.DateTimeNewFunction()),
          Maps.entry("DT_DATE_PARSE", new com.sondertara.evalexpr.functions.datetime.DateTimeParseFunction()),
          Maps.entry("DT_DATE_FORMAT", new com.sondertara.evalexpr.functions.datetime.DateTimeFormatFunction()),
          Maps.entry("DT_DATE_TO_EPOCH", new com.sondertara.evalexpr.functions.datetime.DateTimeToEpochFunction()),
          Maps.entry("DT_DURATION_NEW", new com.sondertara.evalexpr.functions.datetime.DurationNewFunction()),
          Maps.entry("DT_DURATION_FROM_MILLIS", new com.sondertara.evalexpr.functions.datetime.DurationFromMillisFunction()),
          Maps.entry("DT_DURATION_TO_MILLIS", new com.sondertara.evalexpr.functions.datetime.DurationToMillisFunction()),
          Maps.entry("DT_DURATION_PARSE", new com.sondertara.evalexpr.functions.datetime.DurationParseFunction()),
          Maps.entry("DT_NOW", new com.sondertara.evalexpr.functions.datetime.DateTimeNowFunction()),
          Maps.entry("DT_TODAY", new com.sondertara.evalexpr.functions.datetime.DateTimeTodayFunction()));

  /** The math context to use. */
  @Builder.Default private final MathContext mathContext = DEFAULT_MATH_CONTEXT;

  /**
   * The data accessor is responsible for accessing variable and constant values in an expression.
   * The supplier will be called once for each new expression, the default is to create a new {@link
   * MapBasedDataAccessor} instance for each expression, providing a new storage for each
   * expression.
   */
  @Builder.Default
  private final Supplier<DataAccessorIfc> dataAccessorSupplier = MapBasedDataAccessor::new;

  /**
   * Default constants will be added automatically to each expression and can be used in expression
   * evaluation.
   */
  @Builder.Default
  private final Map<String, EvaluationValue> defaultConstants = getStandardConstants();

  /** Support for arrays in expressions are allowed or not. */
  @Builder.Default private final boolean arraysAllowed = true;

  /** Support for structures in expressions are allowed or not. */
  @Builder.Default private final boolean structuresAllowed = true;

  /**
   * Support for the binary (undefined) data type is allowed or not.
   *
   * @since 3.3.0
   */
  @Builder.Default private final boolean binaryAllowed = false;

  /** Support for implicit multiplication, like in (a+b)(b+c) are allowed or not. */
  @Builder.Default private final boolean implicitMultiplicationAllowed = true;

  /** Support for single quote string literals, like in 'Hello World' are allowed or not. */
  @Builder.Default private final boolean singleQuoteStringLiteralsAllowed = false;

  /**
   * The power of operator precedence, can be set higher {@link
   * OperatorIfc#OPERATOR_PRECEDENCE_POWER_HIGHER} or to a custom value.
   */
  @Builder.Default private final int powerOfPrecedence = OperatorIfc.OPERATOR_PRECEDENCE_POWER;

  /**
   * If specified, only the final result of the evaluation will be rounded to the specified number
   * of decimal digits, using the MathContexts rounding mode.
   *
   * <p>The default value of _DECIMAL_PLACES_ROUNDING_UNLIMITED_ will disable rounding.
   */
  @Builder.Default private final int decimalPlacesResult = DECIMAL_PLACES_ROUNDING_UNLIMITED;

  /**
   * If specified, all results from operations and functions will be rounded to the specified number
   * of decimal digits, using the MathContexts rounding mode.
   *
   * <p>Automatic rounding is disabled by default. When enabled, EvalEx will round all input
   * variables, constants, intermediate operation and function results and the final result to the
   * specified number of decimal digits, using the current rounding mode. Using a value of
   * _DECIMAL_PLACES_ROUNDING_UNLIMITED_ will disable automatic rounding.
   */
  @Builder.Default private final int decimalPlacesRounding = DECIMAL_PLACES_ROUNDING_UNLIMITED;

  /**
   * If set to true (default), then the trailing decimal zeros in a number result will be stripped.
   */
  @Builder.Default private final boolean stripTrailingZeros = false;

  /**
   * If set to true (default), then variables can be set that have the name of a constant. In that
   * case, the constant value will be removed and a variable value will be set.
   */
  @Builder.Default private final boolean allowOverwriteConstants = true;

  /** The time zone id. By default, the system default zone ID is used. */
  @Builder.Default private final ZoneId zoneId = ZoneId.systemDefault();

  /** The locale. By default, the system default locale is used. */
  @Builder.Default private final Locale locale = Locale.getDefault();

  /**
   * The date-time formatters. When parsing, each format will be tried and the first matching will
   * be used. For formatting, only the first will be used.
   *
   * <p>By default, the {@link ExpressionConfiguration#DEFAULT_DATE_TIME_FORMATTERS} are used.
   */
  @Builder.Default
  private final List<DateTimeFormatter> dateTimeFormatters = DEFAULT_DATE_TIME_FORMATTERS;

  /** The converter to use when converting different data types to an {@link EvaluationValue}. */
  @Builder.Default
  private final EvaluationValueConverterIfc evaluationValueConverter =
      new DefaultEvaluationValueConverter();

  /**
   * Convenience method to create a default configuration.
   *
   * @return A configuration with default settings.
   */
  public static ExpressionConfiguration defaultConfiguration() {
    return ExpressionConfiguration.builder().build();
  }

  /**
   * Adds additional operators to this configuration.
   *
   * @param operators variable number of arguments with a map entry holding the operator name and
   *     implementation. <br>
   *     Example: <code>
   *        ExpressionConfiguration.defaultConfiguration()
   *          .withAdditionalOperators(
   *            Maps.entry("++", new PrefixPlusPlusOperator()),
   *            Maps.entry("++", new PostfixPlusPlusOperator()));
   *     </code>
   * @return The modified configuration, to allow chaining of methods.
   */
  @SafeVarargs
  public final ExpressionConfiguration withAdditionalOperators(
      Map.Entry<String, OperatorIfc>... operators) {
    Arrays.stream(operators)
        .forEach(entry -> operatorDictionary.addOperator(entry.getKey(), entry.getValue()));
    return this;
  }

  /**
   * Adds additional functions to this configuration.
   *
   * @param functions variable number of arguments with a map entry holding the functions name and
   *     implementation. <br>
   *     Example: <code>
   *        ExpressionConfiguration.defaultConfiguration()
   *          .withAdditionalFunctions(
   *            Maps.entry("save", new SaveFunction()),
   *            Maps.entry("update", new UpdateFunction()));
   *     </code>
   * @return The modified configuration, to allow chaining of methods.
   */
  @SafeVarargs
  public final ExpressionConfiguration withAdditionalFunctions(
      Map.Entry<String, FunctionIfc>... functions) {
    Arrays.stream(functions)
        .forEach(entry -> functionDictionary.addFunction(entry.getKey(), entry.getValue()));
    return this;
  }

  private static Map<String, EvaluationValue> getStandardConstants() {

    Map<String, EvaluationValue> constants = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    constants.put("TRUE", EvaluationValue.TRUE);
    constants.put("FALSE", EvaluationValue.FALSE);
    constants.put(
        "PI",
        EvaluationValue.numberValue(
            new BigDecimal(
                "3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679")));
    constants.put(
        "E",
        EvaluationValue.numberValue(
            new BigDecimal(
                "2.71828182845904523536028747135266249775724709369995957496696762772407663")));
    constants.put("NULL", EvaluationValue.NULL_VALUE);

    constants.put(
        "DT_FORMAT_ISO_DATE_TIME",
        EvaluationValue.stringValue("yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX]['['VV']']"));
    constants.put(
        "DT_FORMAT_LOCAL_DATE_TIME", EvaluationValue.stringValue("yyyy-MM-dd'T'HH:mm:ss[.SSS]"));
    constants.put("DT_FORMAT_LOCAL_DATE", EvaluationValue.stringValue("yyyy-MM-dd"));

    return constants;
  }
}
