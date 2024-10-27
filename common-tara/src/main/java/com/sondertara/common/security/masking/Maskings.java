package com.sondertara.common.security.masking;


import com.sondertara.common.base.Assert;
import com.sondertara.common.collection.StreamUtils;
import com.sondertara.common.registry.GenericRegistry;
import com.sondertara.common.spi.CommonServiceProvider;

import java.util.function.Consumer;

/**
 *  */
public class Maskings {

    public static final class Strategy {
        public static final String PHONE = "phone";
        public static final String STAR_6 = "STAR_6";

        private Strategy(){

        }
    }

    private static final GenericRegistry<Masker> registry = new GenericRegistry<Masker>();

    static {
        StreamUtils.of(new CommonServiceProvider<Masker>().get(Masker.class)).forEach(new Consumer<Masker>() {
            @Override
            public void accept(Masker masker) {
                registerMasker(masker);
            }
        });
    }

    public static String masking(Masker masker, Object obj) {
        String ret = masker.doTransform(obj);
        return ret;
    }


    public static String masking(String strategy, Object obj) {
        Masker masker = registry.get(strategy);
        Assert.notNull(masker, "the masker strategy {} not found", strategy);
        return masking(masker, obj);
    }

    public static void registerMasker(Masker masker) {
        registry.register(masker);
    }

    private Maskings() {

    }
}
