package com.sondertara.excel.parser;


import com.sondertara.excel.ExcelHelper;

import java.util.List;


/**
 * @author Chimm Huang
 */
public class DataVariableParserVisitor extends VariableParserBaseVisitor<Object> {

    private Object data;

    public DataVariableParserVisitor(Object data) {
        this.data = data;
    }


    @Override
    public Object visitFormulaCall(VariableParserParser.FormulaCallContext ctx) {
        return super.visitFormulaCall(ctx);
    }

    @Override
    public Object visitLiter(VariableParserParser.LiterContext ctx) {
        return super.visitLiter(ctx);
    }

    /**
     * multiply | divide
     * e.g. A1*A2
     */
    @Override
    public Object visitMulDiv(VariableParserParser.MulDivContext ctx) {
        return substitutionVariable(ctx.getText(), ctx.expr());
    }

    @Override
    public Object visitAddSub(VariableParserParser.AddSubContext ctx) {
        return substitutionVariable(ctx.getText(), ctx.expr());
    }

    @Override
    public Object visitVar(VariableParserParser.VarContext ctx) {
        String propName = ctx.getText();
        return ExcelHelper.parseCellVariable(data, propName);
    }

    @Override
    public Object visitParens(VariableParserParser.ParensContext ctx) {
        return super.visitParens(ctx);
    }

    @Override
    public Object visitExcelArray(VariableParserParser.ExcelArrayContext ctx) {
        return super.visitExcelArray(ctx);
    }

    @Override
    public Object visitName(VariableParserParser.NameContext ctx) {
        return super.visitName(ctx);
    }

    /**
     * exprList
     * e.g. A1,A2,${demo.value}
     */
    @Override
    public String visitExprList(VariableParserParser.ExprListContext ctx) {
        return substitutionVariable(ctx.getText(), ctx.expr());
    }

    @Override
    public Object visitQualifiedName(VariableParserParser.QualifiedNameContext ctx) {
        return super.visitQualifiedName(ctx);
    }

    @Override
    public Object visitVariableExpr(VariableParserParser.VariableExprContext ctx) {
        return super.visitVariableExpr(ctx);
    }

    @Override
    public Object visitVariable(VariableParserParser.VariableContext ctx) {
        return super.visitVariable(ctx);
    }

    /**
     * formula
     * e.g. SUM(A1,A2,${demo.value})
     */
    @Override
    public String visitFormula(VariableParserParser.FormulaContext ctx) {
        String formula = ctx.getText();
        VariableParserParser.ExprListContext exprListContext = ctx.exprList();
        String oldExprList = exprListContext.getText();
        String newExprList = visitExprList(exprListContext);
        return formula.replace(oldExprList, newExprList);
    }

    @Override
    public Object visitArrayIdx(VariableParserParser.ArrayIdxContext ctx) {
        return super.visitArrayIdx(ctx);
    }

    @Override
    public Object visitArray(VariableParserParser.ArrayContext ctx) {
        return super.visitArray(ctx);
    }

    @Override
    public Object visitLiteral(VariableParserParser.LiteralContext ctx) {
        return super.visitLiteral(ctx);
    }

    /**
     * take the final result of each expression, replace it and return.
     *
     * @param expr            expression in string form
     * @param exprContextList expression list
     */
    private String substitutionVariable(String expr, List<VariableParserParser.ExprContext> exprContextList) {
        for (VariableParserParser.ExprContext exprContext : exprContextList) {
            String exprContextText = exprContext.getText();
            if (exprContextText.startsWith("$")) {
                Object visit = super.visit(exprContext);
                expr = expr.replace(exprContextText, visit == null ? "" : visit.toString());
            }
        }
        return expr;
    }
}
