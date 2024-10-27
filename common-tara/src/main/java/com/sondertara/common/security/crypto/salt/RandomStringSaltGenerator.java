package com.sondertara.common.security.crypto.salt;

import com.sondertara.common.id.NanoId;
import com.sondertara.common.random.Randoms;

public class RandomStringSaltGenerator implements StringSaltGenerator {
    private String alphabet = new String(NanoId.DEFAULT_ALPHABET);
    public RandomStringSaltGenerator(){
    }

    public RandomStringSaltGenerator(String alphabet){
        this.alphabet=alphabet;
    }

    @Override
    public String apply(Integer charsLength) {
        return Randoms.randomString(alphabet,charsLength);
    }
}
