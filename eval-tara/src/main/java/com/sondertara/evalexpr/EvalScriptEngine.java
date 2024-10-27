package com.sondertara.evalexpr;

import com.sondertara.common.collection.Lists;
import com.sondertara.evalexpr.config.ExpressionConfiguration;
import com.sondertara.evalexpr.parser.ParseException;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author huangxiaohu.1ih
 * @date 2024/10/9 17:17
 */
public class EvalScriptEngine extends AbstractScriptEngine {
    private final ScriptEngineFactory factory;

    EvalScriptEngine(ScriptEngineFactory factory) {
        this.factory = factory;
    }

    public EvalScriptEngine() {
        this(new EvalScriptEngineFactory());
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        Map<String, Object> params = new HashMap<>(8);
        if (null != context) {
            Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);

            params.putAll(bindings);
        }
        Expression expression = new Expression(script, ExpressionConfiguration.builder().singleQuoteStringLiteralsAllowed(true).build());
        expression.withValues(params);
        try {
            return expression.evaluate().getValue();
        } catch (EvaluationException | ParseException e) {
            throw new ScriptException(e);
        }
    }

    private static String readFully(Reader reader) throws ScriptException {
        // 8K at a time
        char[] arr = new char[8 * 1024];
        StringBuilder buf = new StringBuilder();
        int numChars;
        try {
            while ((numChars = reader.read(arr, 0, arr.length)) > 0) {
                buf.append(arr, 0, numChars);
            }
        } catch (IOException exp) {
            throw new ScriptException(exp);
        }
        return buf.toString();
    }

    @Override
    public Object eval(Reader reader, ScriptContext context) throws ScriptException {
        return eval(readFully(reader), context);
    }

    @Override
    public Bindings createBindings() {
        return new SimpleBindings();
    }


    @Override
    public ScriptEngineFactory getFactory() {
        return this.factory;
    }

    public static class EvalScriptEngineFactory implements ScriptEngineFactory {
        @Override
        public String getEngineName() {
            return "TaraEval";
        }

        @Override
        public String getEngineVersion() {
            return "1.0";
        }

        @Override
        public List<String> getExtensions() {
            return Lists.newArrayList("TaraEval");
        }

        @Override
        public List<String> getMimeTypes() {
            return Lists.newArrayList("TaraEval");
        }

        @Override
        public List<String> getNames() {
            return Lists.newArrayList("TaraEval");
        }

        @Override
        public String getLanguageName() {
            return "TaraEval";
        }

        @Override
        public String getLanguageVersion() {
            return "1.0";
        }

        @Override
        public Object getParameter(String key) {
            if (ScriptEngine.NAME.equals(key)) {
                return "TaraEval";
            } else if (ScriptEngine.ENGINE.equals(key)) {
                return getEngineName();
            } else if (ScriptEngine.ENGINE_VERSION.equals(key)) {
                return "1.0";
            } else if (ScriptEngine.LANGUAGE.equals(key)) {
                return "TaraEval";
            } else if (ScriptEngine.LANGUAGE_VERSION.equals(key)) {
                return "1.0";
            } else if ("THREADING".equals(key)) {
                return "MULTITHREADED";
            } else {
                throw new IllegalArgumentException("Invalid key");
            }
        }

        @Override
        public String getMethodCallSyntax(String obj, String m, String... args) {
            return null;
        }

        @Override
        public String getOutputStatement(String toDisplay) {
            return null;
        }

        @Override
        public String getProgram(String... statements) {
            return null;
        }

        @Override
        public ScriptEngine getScriptEngine() {
            return new EvalScriptEngine(this);
        }
    }
}
