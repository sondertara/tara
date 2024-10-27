package com.sondertara.common.security.ssl;


import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.text.StringTemplates;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CipherSuite {
    @NonNull
    private String name;
    @Nullable
    private String alias;

    private String keyExchangeAlgorithm;
    /**
     * 当 anonymous 为true时，它为null
     */
    private String authcAlgorithm;
    private String encryptAlgorithm;
    private String macAlgorithm;

    private boolean export;

    private boolean anonymous=false;

    private List<SSLProtocolVersion> protocolVersions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getKeyExchangeAlgorithm() {
        return keyExchangeAlgorithm;
    }

    public void setKeyExchangeAlgorithm(String keyExchangeAlgorithm) {
        this.keyExchangeAlgorithm = keyExchangeAlgorithm;
    }

    public String getAuthcAlgorithm() {
        return authcAlgorithm;
    }

    public void setAuthcAlgorithm(String authcAlgorithm) {
        this.authcAlgorithm = authcAlgorithm;
    }

    public String getEncryptAlgorithm() {
        return encryptAlgorithm;
    }

    public void setEncryptAlgorithm(String encryptAlgorithm) {
        this.encryptAlgorithm = encryptAlgorithm;
    }

    public String getMacAlgorithm() {
        return macAlgorithm;
    }

    public void setMacAlgorithm(String macAlgorithm) {
        this.macAlgorithm = macAlgorithm;
    }

    public boolean isExport() {
        return export;
    }

    public void setExport(boolean export) {
        this.export = export;
    }

    public List<SSLProtocolVersion> getProtocolVersions() {
        return protocolVersions;
    }

    public void setProtocolVersions(List<SSLProtocolVersion> protocolVersions) {
        this.protocolVersions = protocolVersions;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    @Override
    public String toString() {
        Map<String,String> map =new HashMap<String,String>();
        map.put("name", name);
        map.put("keyExchangeAlgorithm", ObjectUtils.defaultIfEmpty(keyExchangeAlgorithm,"NULL"));
        map.put("authcAlgorithm", anonymous?"anon":ObjectUtils.defaultIfEmpty(authcAlgorithm,"NULL"));
        map.put("encryptAlgorithm", ObjectUtils.defaultIfEmpty(encryptAlgorithm,"NULL"));
        map.put("macAlgorithm",ObjectUtils.defaultIfEmpty(macAlgorithm,"NULL"));
        return StringTemplates.formatWithMap("${name}\tKe=${keyExchangeAlgorithm}\tAu=${authcAlgorithm}\tEnc=${encryptAlgorithm}\tMac=${macAlgorithm}", map );
    }
}
