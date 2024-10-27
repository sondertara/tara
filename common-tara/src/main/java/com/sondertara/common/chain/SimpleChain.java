package com.sondertara.common.chain;

import com.sondertara.common.lifecycle.InitializationException;
import com.sondertara.common.struct.counter.IntegerCounter;
import com.sondertara.common.struct.counter.SimpleIntegerCounter;
import com.sondertara.common.struct.counter.ThreadLocalIntegerCounter;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 类似于 Java EE  Servlet 规范中的 FilterChain。
 * 并且需要在 handler 的 handle方法内部 调用 chain.handle(req, resp)
 *
 * @param <REQ>
 * @param <RESP>
 * @author huangxiaohu.1ih
 */
public class SimpleChain<REQ, RESP> extends AbstractChain<REQ, RESP> {
    private final List<Handler<REQ, RESP>> handlers = new ArrayList<>();
    private final IntegerCounter posHolder;

    public SimpleChain() {
        this(false);
    }

    public SimpleChain(IntegerCounter posHolder) {
        this.posHolder = posHolder;
    }

    /**
     * 如果 是独占 chain，则 每一次使用chain时，需要新建一个独有的chain
     */
    public SimpleChain(boolean shareChain) {
        if (shareChain) {
            this.posHolder = new ThreadLocalIntegerCounter();
        } else {
            this.posHolder = new SimpleIntegerCounter();
        }
    }


    @Override
    public void handle(final REQ request, final RESP response) {
        int pos = posHolder.get();
        if (pos < handlers.size() - 1 && pos >= -1) {
            posHolder.increment();
            Handler<REQ, RESP> handler = handlers.get(pos);
            handler.accept(request, response, this);
        }

        if (pos >= this.handlers.size() - 1) {
            // 执行完毕后重置
            posHolder.set(-1);
        }
    }

    @Override
    public void addHandler(@NonNull Handler<REQ, RESP> handler) {
        Objects.requireNonNull(handler);
        handlers.add(handler);
    }

    public static void main(String[] args) {
        SimpleChain<String, String> chain = new SimpleChain<>();
        chain.addHandler(new Handler<String, String>() {
            @Override
            public void init() throws InitializationException {

            }

            @Override
            public void destroy() {

            }

            @Override
            public void accept(String s, String s2, Chain<String, String> stringStringChain) {
                stringStringChain.handle(s, s2);
            }
        });
    }


}
