package com.sondertara.common.net;

import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.text.StringUtils;

import java.util.Objects;

/**
 * @author huangxiaohu.1ih
 */
public class NetworkAddress implements Comparable<NetworkAddress> {
    private String host;
    private int port;
    private AddrMode addrMode;

    public enum AddrMode {
        V4, V6, HOST
    }


    public NetworkAddress() {

    }

    public NetworkAddress(String host, int port) {
        this(host, port, AddrMode.HOST);
    }

    public NetworkAddress(String host, int port, AddrMode addrMode) {
        this.host = host;
        if (port > 0) {
            this.port = port;
        }
        this.addrMode = addrMode;
    }

    public AddrMode getAddrMode() {
        return addrMode;
    }

    public void setAddrMode(AddrMode addrMode) {
        this.addrMode = addrMode;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public int compareTo(NetworkAddress o) {
        return this.toString().compareTo(o.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        NetworkAddress that = (NetworkAddress) o;
        if (port != that.port) {
            return false;
        }
        return ObjectUtils.equals(host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }

    @Override
    public String toString() {
        // return port > 0 ? (host + ":" + port) : host;
        return show(ShowStyle.BSON);
    }

    public String show(){
        return show(ShowStyle.BSON);
    }

    public String show(ShowStyle style){
        style = ObjectUtils.defaultIfEmpty(style, ShowStyle.BSON);
        switch (style){
            case URL:
                return port > 0 ? (host + ":" + port) : host;
            case BSON:
            default:
               return StringUtils.format( "{host: {}, port:{}, addrMode: {}}", host, port, addrMode);
        }
    }


    public static enum ShowStyle{
        URL,
        BSON
    }

}
