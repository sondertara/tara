package com.sondertara.excel.support.dataconstraint;


import com.sondertara.excel.meta.annotation.datavalidation.ExcelStringList;

/**
 * @author chenzw
 */
public class ExcelStringListDataValidationConstraint implements ExcelDataValidationConstraint<ExcelStringList> {

    private String[] sList;

    @Override
    public void initialize(ExcelStringList annotation) {
        this.sList = annotation.value();
    }




    @Override
    public String[] generate() {
        return sList;
    }
}
