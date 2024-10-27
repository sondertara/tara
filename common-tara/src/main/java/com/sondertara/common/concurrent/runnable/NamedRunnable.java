package com.sondertara.common.concurrent.runnable;

import com.sondertara.common.text.StringUtils;
import lombok.Getter;

import java.util.UUID;

/**
 * NamedRunnable related
 *
 * @author yanhom
 *  */
public class NamedRunnable implements Runnable {

    private final Runnable runnable;

    @Getter
    private final String name;

    public NamedRunnable(Runnable runnable, String name) {
        this.runnable = runnable;
        this.name = name;
    }

    @Override
    public void run() {
        this.runnable.run();
    }

    public static NamedRunnable of(Runnable runnable, String name) {
        if (StringUtils.isBlank(name)) {
            name = runnable.getClass().getSimpleName() + "-" + UUID.randomUUID();
        }
        return new NamedRunnable(runnable, name);
    }
}
