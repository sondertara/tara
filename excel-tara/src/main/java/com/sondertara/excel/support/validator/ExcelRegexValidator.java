package com.sondertara.excel.support.validator;

import com.sondertara.excel.exception.ExcelValidationException;
import com.sondertara.excel.meta.annotation.validation.ExcelRegexValue;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class ExcelRegexValidator implements AbstractExcelColumnValidator<ExcelRegexValue> {

    private Pattern pattern;

    private String message;


    @Override
    public void initialize(final ExcelRegexValue excelRegexValue) {
        final String regex = excelRegexValue.regex();
        if (StringUtils.isBlank(regex)) {
            throw new IllegalArgumentException("正则表达式为空!");
        }
        this.pattern = Pattern.compile(regex);
        this.message = excelRegexValue.message();
    }

    @Override
    public boolean validate(final String value) {
        if (StringUtils.isBlank(value)) {
            return true;
        }
        if (pattern.matcher(value).matches()) {
            return true;
        }

        if (!StringUtils.isEmpty(message)) {
            throw new ExcelValidationException(message);
        }
        return false;
    }
}
