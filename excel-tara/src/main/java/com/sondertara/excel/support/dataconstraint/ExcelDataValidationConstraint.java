package com.sondertara.excel.support.dataconstraint;

import java.lang.annotation.Annotation;

public interface ExcelDataValidationConstraint<A extends Annotation>{

 void initialize(A annotation);

    String[] generate();

}
