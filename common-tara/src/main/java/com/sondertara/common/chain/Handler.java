package com.sondertara.common.chain;


import com.sondertara.common.function.Consumer3;
import com.sondertara.common.lifecycle.Destroyable;
import com.sondertara.common.lifecycle.Initializable;

/**
 * @author huangxiaohu.1ih
 */
public interface Handler<REQ, RESP> extends Consumer3<REQ, RESP, Chain<REQ, RESP>>, Initializable, Destroyable {
}

