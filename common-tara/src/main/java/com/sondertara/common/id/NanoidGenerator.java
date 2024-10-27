package com.sondertara.common.id;

/**
 *
 * @author huangxiaohu.1ih
 */
public class NanoidGenerator implements IdGenerator<String> {

    @Override
    public String get() {
        return NanoId.randomNanoId();
    }

}
