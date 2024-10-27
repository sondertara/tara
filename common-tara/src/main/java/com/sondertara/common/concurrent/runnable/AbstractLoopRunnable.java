package com.sondertara.common.concurrent.runnable;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author songcf
 * @version : SingletonRunnable
 */

@Slf4j
public abstract class AbstractLoopRunnable implements Runnable {

    //private static final int           IDLE          = 0;
    //private static final int           RUNNING       = 1;
    //private static final int           TERMINATED    = 2;
    //private final        AtomicInteger threadStatus  = new AtomicInteger(IDLE);
    private volatile Thread currentThread = null;


    @Override
    final public void run() {
        try {
            log.info("thread onStart: " + selfName());
            onStart();
            process();
        } finally {
            currentThread = null;
            onShutdown();
            log.info("thread onShutdown: " + selfName());
        }
    }

    /**
     * 处理器
     */
    public void process() {
        long sleepNanos = 0;
        long lastReturnTimestamp = 0;
        Map<String, Object> args = new HashMap<>();
        while (true) {
            try {
                sleepNanos = onLoop(args, lastReturnTimestamp);
                lastReturnTimestamp = System.nanoTime();
                if (sleepNanos > 0) {
                    TimeUnit.NANOSECONDS.sleep(sleepNanos);
                }
            } catch (InterruptedException e) {
                log.warn("thread isInterrupted: " + selfName(), e);
                break;
            } catch (Exception e) {
                log.error("thread onLoop failed: " + selfName(), e, "\ntrace:", e);
            } finally {
                if (log.isDebugEnabled()) {
                    log.debug("thread onLoop finish at timestamp :{}", lastReturnTimestamp);
                }
            }
            if (currentThread.isInterrupted()) {
                log.warn("thread isInterrupted: " + selfName());
                break;
            }
        }
    }

    /**
     * 循环体
     *
     * @param context             上下文
     * @param lastReturnTimestamp 上次loop返回时的时间
     * @return
     * @throws InterruptedException
     */
    abstract protected long onLoop(Map<String, Object> context, long lastReturnTimestamp) throws Exception;

    protected void onStart() {
    }

    protected void onShutdown() {
    }


    /**
     * 运行中
     *
     * @return
     */
    public boolean isRunning() {
        return currentThread != null;
    }

    /**
     * 启动
     */
    public void startThread() {
        synchronized (this) {
            if (currentThread == null) {
                currentThread = new Thread(this);
                currentThread.start();
            } else {
                log.error("thread already started: " + selfName());
            }
        }
    }

    /**
     * 停止
     */
    public void stopThread() {
        synchronized (this) {
            if (currentThread != null) {
                currentThread.interrupt();
            }
        }
    }

    private String selfName() {
        return this.getClass().getSimpleName();
    }

}