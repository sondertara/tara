package com.sondertara.common.id;

import com.sondertara.common.exception.ExceptionUtils;
import com.sondertara.common.id.vm.UID;
import com.sondertara.common.id.vm.VMID;
import com.sondertara.common.net.Nets;
import com.sondertara.common.os.JdkUtils;

import java.nio.charset.StandardCharsets;

public class VMIdGenerator implements IdGenerator<String> {
    private volatile byte[] address;

    @Override
    public String get() {
        if (address == null) {
            address = this.address;
            if (address == null) {
                try {
                    String ip = Nets.getLocalIp();
                    this.address = ip.getBytes(StandardCharsets.UTF_8);
                    address = this.address;
                } catch (Exception ex) {
                    throw ExceptionUtils.wrapAsRuntimeException(ex);
                }
            }
        }
        return new VMID(address, String.valueOf(JdkUtils.PID), new UID()).toString();
    }
}
