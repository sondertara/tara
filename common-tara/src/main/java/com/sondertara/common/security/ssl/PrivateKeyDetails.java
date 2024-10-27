package com.sondertara.common.security.ssl;


import com.sondertara.common.base.Assert;
import com.sondertara.common.collection.ArrayUtils;

import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 * Private key details.
 *
 *  */
public final class PrivateKeyDetails {

    private final String type;
    private final X509Certificate[] certChain;

    public PrivateKeyDetails(final String type, final X509Certificate[] certChain) {
        super();
        this.type = Assert.notNull(type, "Private key type");
        this.certChain = ArrayUtils.copy(certChain);
    }

    public String getType() {
        return type;
    }

    public X509Certificate[] getCertChain() {
        return ArrayUtils.copy(certChain);
    }

    @Override
    public String toString() {
        return type + ':' + Arrays.toString(certChain);
    }

}
