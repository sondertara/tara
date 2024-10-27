package com.sondertara.common.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 变量风格枚举对象
 *
 * @author Jiahang Li
 * @version 1.0.0
 *  */
public enum VariableStyles {

    /**
     * 小驼峰命名法
     */
    CAMEL {
        @Override
        public String toKebabCase(String variable) {
            return styleConvert(variable, "-");
        }

        @Override
        public String toSnakeCase(String variable) {
            return styleConvert(variable, "_");
        }

        @Override
        public String toPascalCase(String variable) {
            return styleConvert(variable, 0);
        }

        @Override
        public String toCamelCase(String variable) {
            return variable;
        }
    },

    /**
     * 大驼峰命名法
     */
    PASCAL {
        @Override
        public String toKebabCase(String variable) {
            return styleConvert(variable, "-");
        }

        @Override
        public String toSnakeCase(String variable) {
            return styleConvert(variable, "_");
        }

        @Override
        public String toPascalCase(String variable) {
            return variable;
        }

        @Override
        public String toCamelCase(String variable) {
            return styleConvert(variable, 1);
        }
    },

    /**
     * 蛇形命名法
     */
    SNAKE {
        @Override
        public String toKebabCase(String variable) {
            return styleConvert(variable, "_", "-");
        }

        @Override
        public String toSnakeCase(String variable) {
            return variable;
        }

        @Override
        public String toPascalCase(String variable) {
            return styleConvert(variable, false);
        }

        @Override
        public String toCamelCase(String variable) {
            return styleConvert(variable, true);
        }
    },

    /**
     * 脊柱命名法
     */
    KEBAB {
        @Override
        public String toKebabCase(String variable) {
            return variable;
        }

        @Override
        public String toSnakeCase(String variable) {
            return styleConvert(variable, "-", "_");
        }

        @Override
        public String toPascalCase(String variable) {
            return styleConvert(variable, false);
        }

        @Override
        public String toCamelCase(String variable) {
            return styleConvert(variable, true);
        }
    };

    /**
     * 脊柱分隔符
     */
    private static final String KEBAB_TOKENIZER = "-";

    /**
     * 蛇形分隔符
     */
    private static final String SNAKE_TOKENIZER = "_";

    /**
     * 大驼峰正则
     */
    private static final Pattern PASCAL_PATTERN = Pattern.compile("[A-Z]([a-z\\d]+)?");

    /**
     * 蛇形正则
     */
    private static final Pattern SNAKE_PATTERN = Pattern.compile("([A-Za-z\\d]+)(_)?");

    /**
     * 脊柱正则
     */
    private static final Pattern KEBAB_PATTERN = Pattern.compile("([A-Za-z\\d]+)(-)?");

    /**
     * 转化变量命名风格
     *
     * @param variable 变量
     * @param e        命名风格
     * @return 变量
     */
    public static String convert(String variable, VariableStyles e) {
        if (StringUtils.isBlank(variable)) {
            return variable;
        }
        VariableStyles styleType = VariableStyles.getType(variable);
        if (e == styleType) {
            return variable;
        }
        switch (styleType) {
            case CAMEL:
                switch (e) {
                    case PASCAL:
                        return styleConvert(variable, 0);
                    case SNAKE:
                        return styleConvert(variable, "_");
                    case KEBAB:
                        return styleConvert(variable, "-");
                    default:
                        return variable;
                }
            case PASCAL:
                switch (e) {
                    case CAMEL:
                        return styleConvert(variable, 1);
                    case SNAKE:
                        return styleConvert(variable, "_");
                    case KEBAB:
                        return styleConvert(variable, "-");
                    default:
                        return variable;
                }
            case SNAKE:
                switch (e) {
                    case CAMEL:
                        return styleConvert(variable, true);
                    case PASCAL:
                        return styleConvert(variable, false);
                    case KEBAB:
                        return styleConvert(variable, "_", "-");
                    default:
                        return variable;
                }
            case KEBAB:
                switch (e) {
                    case CAMEL:
                        return styleConvert(variable, true);
                    case PASCAL:
                        return styleConvert(variable, false);
                    case SNAKE:
                        return styleConvert(variable, "-", "_");
                    default:
                        return variable;
                }
            default:
                return variable;
        }
    }

    // -------------------- 转化格式私有方法 --------------------

    private static String styleConvert(String variable, String before, String after) {
        return variable.toLowerCase().replaceAll(before, after);
    }

    private static String styleConvert(String variable, String tokenizer) {
        variable = String.valueOf(variable.charAt(0)).toUpperCase().concat(variable.substring(1));
        StringBuilder sb = new StringBuilder();
        Matcher matcher = PASCAL_PATTERN.matcher(variable);
        while (matcher.find()) {
            sb.append(matcher.group().toLowerCase())
                    .append(matcher.end() == variable.length() ? StringUtils.EMPTY : tokenizer);
        }
        return sb.toString();
    }

    private static String styleConvert(String variable, boolean small) {
        String tokenizer;
        Pattern pattern;
        if (variable.contains(KEBAB_TOKENIZER)) {
            tokenizer = KEBAB_TOKENIZER;
            pattern = KEBAB_PATTERN;
        } else {
            tokenizer = SNAKE_TOKENIZER;
            pattern = SNAKE_PATTERN;
        }
        StringBuilder sb = new StringBuilder();
        Matcher matcher = pattern.matcher(variable);
        int i = 0;
        while (matcher.find()) {
            String word = matcher.group();
            if (++i == 1 && small) {
                sb.append(Character.toLowerCase(word.charAt(0)));
            } else {
                sb.append(Character.toUpperCase(word.charAt(0)));
            }
            int index = word.lastIndexOf(tokenizer);
            if (index > 0) {
                sb.append(word, 1, index);
            } else {
                sb.append(word.substring(1));
            }
        }
        return sb.toString();
    }

    private static String styleConvert(String variable, int t) {
        if (t == 0) {
            return Character.toUpperCase(variable.charAt(0)) + variable.substring(1);
        } else {
            return Character.toLowerCase(variable.charAt(0)) + variable.substring(1);
        }
    }

    /**
     * 获取变量的命名风格
     *
     * @param variable 变量
     * @return 代码风格
     */
    private static VariableStyles getType(String variable) {
        if (StringUtils.isBlank(variable)) {
            return CAMEL;
        }
        if (variable.contains(SNAKE_TOKENIZER)) {
            return SNAKE;
        } else if (variable.contains(KEBAB_TOKENIZER)) {
            return KEBAB;
        } else {
            char f = variable.charAt(0);
            if (f >= 65 && f <= 90) {
                return PASCAL;
            } else {
                return CAMEL;
            }
        }
    }

    /**
     * 转为小驼峰命名
     *
     * @param variable 变量
     * @return ignore
     */
    public abstract String toCamelCase(String variable);

    /**
     * 转为大驼峰命名
     *
     * @param variable 变量
     * @return ignore
     */
    public abstract String toPascalCase(String variable);

    /**
     * 转为蛇形命名
     *
     * @param variable 变量
     * @return ignore
     */
    public abstract String toSnakeCase(String variable);

    /**
     * 转为脊柱命名
     *
     * @param variable 变量
     * @return ignore
     */
    public abstract String toKebabCase(String variable);

}

