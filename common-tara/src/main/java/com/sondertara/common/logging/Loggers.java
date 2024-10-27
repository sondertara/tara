package com.sondertara.common.logging;

import com.sondertara.common.base.Emptys;
import com.sondertara.common.collection.ArrayUtils;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.exception.ExceptionUtils;
import com.sondertara.common.text.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class Loggers {
    private Loggers() {

    }

    public static void log(@NonNull Throwable ex) {
        log(1, null, null, ex, (Function<Object[], String>) null);
    }

    public static void log(@Nullable Logger logger, @Nullable Level level, final @Nullable Throwable ex, String message, Object... args) {
        log(logger, level, null, ex, message, args);
    }

    public static void log(@Nullable Logger logger, @Nullable Level level, @Nullable Marker marker, final @Nullable Throwable ex, String message, Object... args) {
        log(1, logger, level, marker, ex, buildPlaceholderMessageFunctionOrNull(message, args), args);
    }

    public static void log(int count, @Nullable Logger logger, @Nullable Level level, final @Nullable Throwable ex, @Nullable String message, Object... args) {
        log(count, logger, level, ex, buildPlaceholderMessageFunctionOrNull(message, args), args);
    }

    public static void log(int count, @Nullable Logger logger, @Nullable Level level, final @Nullable Throwable ex, Function<Object[], String> messageFunction, Object... args) {
        log(count, logger, level, null, ex, messageFunction, args);
    }

    /**
     * 把消息记录到日志中
     *
     * @param logger          the logger，默认为 ExceptionUtils.logger
     * @param level           log level, 默认为 ERROR
     * @param messageFunction 异常消息
     * @param ex              异常
     */
    public static void log(int count, @Nullable Logger logger, @Nullable Level level, @Nullable final Marker marker, final @Nullable Throwable ex, Function<Object[], String> messageFunction, Object... args) {
        Objects.requireNonNull(messageFunction, "the message supplier is null");
        if (logger == null) {
            if (ex == null) {
                logger = getLogger(Loggers.class);
            } else {
                logger = getLogger(ExceptionUtils.class);
            }
        }
        if (level == null) {
            if (ex == null) {
                level = Level.INFO;
            } else {
                level = Level.ERROR;
            }
        }
        if (count < 1) {
            count = 1;
        }
        if (count > 3) {
            count = 3;
        }

        final String message = messageFunction.apply(args);
        final Logger lgr = logger;
        switch (level) {
            case TRACE:
                if (logger.isTraceEnabled()) {
                    CollectionUtils.forEach(ArrayUtils.range(count), new Consumer<Integer>() {
                        @Override
                        public void accept(Integer index) {
                            if (marker != null) {
                                if (ex != null) {
                                    lgr.trace(marker, message, ex);
                                } else {
                                    lgr.trace(marker, message);
                                }
                            } else {
                                if (ex != null) {
                                    lgr.trace(message, ex);
                                } else {
                                    lgr.trace(message);
                                }
                            }
                        }
                    });

                }
                break;
            case DEBUG:
                if (logger.isDebugEnabled()) {
                    CollectionUtils.forEach(ArrayUtils.range(count), new Consumer<Integer>() {
                        @Override
                        public void accept(Integer index) {
                            if (marker != null) {
                                if (ex != null) {
                                    lgr.debug(marker, message, ex);
                                } else {
                                    lgr.debug(marker, message);
                                }
                            } else {
                                if (ex != null) {
                                    lgr.debug(message, ex);
                                } else {
                                    lgr.debug(message);
                                }
                            }
                        }
                    });
                }
                break;
            case INFO:
                if (logger.isInfoEnabled()) {
                    CollectionUtils.forEach(ArrayUtils.range(count), new Consumer<Integer>() {
                        @Override
                        public void accept(Integer index) {
                            if (marker != null) {
                                if (ex != null) {
                                    lgr.info(marker, message, ex);
                                } else {
                                    lgr.info(marker, message);
                                }
                            } else {
                                if (ex != null) {
                                    lgr.info(message, ex);
                                } else {
                                    lgr.info(message);
                                }
                            }
                        }
                    });
                }
                break;
            case WARN:
                if (logger.isWarnEnabled()) {
                    CollectionUtils.forEach(ArrayUtils.range(count), new Consumer<Integer>() {
                        @Override
                        public void accept(Integer index) {
                            if (marker != null) {
                                if (ex != null) {
                                    lgr.warn(marker, message, ex);
                                } else {
                                    lgr.warn(marker, message);
                                }
                            } else {
                                if (ex != null) {
                                    lgr.warn(message, ex);
                                } else {
                                    lgr.warn(message);
                                }
                            }
                        }
                    });
                }
                break;
            case ERROR:
                if (logger.isErrorEnabled()) {
                    CollectionUtils.forEach(ArrayUtils.range(count), new Consumer<Integer>() {
                        @Override
                        public void accept(Integer index) {
                            if (marker != null) {
                                if (ex != null) {
                                    lgr.error(marker, message, ex);
                                } else {
                                    lgr.error(marker, message);
                                }
                            } else {
                                if (ex != null) {
                                    lgr.error(message, ex);
                                } else {
                                    lgr.error(message);
                                }
                            }
                        }
                    });
                }
                break;
            default:
                CollectionUtils.forEach(ArrayUtils.range(count), new Consumer<Integer>() {
                    @Override
                    public void accept(Integer index) {
                        if (marker != null) {
                            if (ex != null) {
                                lgr.warn(marker, message, ex);
                            } else {
                                lgr.warn(marker, message);
                            }
                        } else {
                            if (ex != null) {
                                lgr.warn(message, ex);
                            } else {
                                lgr.warn(message);
                            }
                        }
                    }
                });
                break;
        }
    }


    private static Function<Object[], String> buildPlaceholderMessageFunctionOrNull(final String message, Object args) {
        Function<Object[], String> supplier = null;
        if (Emptys.isNotEmpty(message)) {
            if (Emptys.isEmpty(args)) {
                supplier = new Function<Object[], String>() {
                    @Override
                    public String apply(Object[] input) {
                        return message;
                    }
                };
            } else {
                supplier = new Function<Object[], String>() {
                    @Override
                    public String apply(Object[] input) {
                        return StringUtils.format(message, input);
                    }
                };
            }
        }
        return supplier;
    }

    public static Logger getLogger(String loggerName) {
        return LoggerFactory.getLogger(loggerName);
    }

    public static Logger getLogger(Class clazz) {
        return LoggerFactory.getLogger(clazz);
    }


    public static boolean isEnabled(@NonNull Logger logger, @NonNull Level level) {
        Objects.requireNonNull(level);
        Objects.requireNonNull(logger);

        boolean enabled;
        switch (level) {
            case TRACE:
                enabled = logger.isTraceEnabled();
                break;
            case DEBUG:
                enabled = logger.isDebugEnabled();
                break;
            case INFO:
                enabled = logger.isInfoEnabled();
                break;
            case WARN:
                enabled = logger.isWarnEnabled();
                break;
            case ERROR:
                enabled = logger.isErrorEnabled();
                break;
            default:
                enabled = logger.isErrorEnabled();
                break;
        }
        return enabled;
    }

    private static Logger doGetLogger(Object logger) {
        Logger lg = null;
        if (logger != null) {
            if (logger instanceof Logger) {
                lg = (Logger) logger;
            } else if (logger instanceof String) {
                lg = Loggers.getLogger((String) logger);
            } else if (logger instanceof Class) {
                lg = Loggers.getLogger((Class) logger);
            }

        }
        if (lg == null) {
            lg = Loggers.getLogger(Loggers.class);
        }
        return lg;
    }


    public static void debug(Object logger, String message, Object... parameters) {
        Logger lg = doGetLogger(logger);

        if (lg.isDebugEnabled()) {
            lg.debug(message, parameters);
        }
    }


    public static void info(Object logger, String message, Object... parameters) {
        Logger lg = doGetLogger(logger);

        if (lg.isInfoEnabled()) {
            lg.info(message, parameters);
        }
    }

    public static void warn(Object logger, String message, Object... parameters) {
        Logger lg = doGetLogger(logger);

        if (lg.isWarnEnabled()) {
            lg.warn(message, parameters);
        }
    }

    public static void error(Object logger, String message, Object... parameters) {
        Logger lg = doGetLogger(logger);

        if (lg.isErrorEnabled()) {
            lg.error(message, parameters);
        }
    }

}
