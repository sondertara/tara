package com.sondertara.common.model;

import lombok.Getter;

/**
 * @author huangxiaohu
 */

@Getter
public enum RestFulCodeEnum {
    /**
     * Connect error
     */
    REQUEST_ERROR(100001,"未知错误"),

    /**
     * Http status 400
     */
    PARAM_EMPTY(400001, "参数为空"), PARAM_PARSE_ERROR(400002, "参数解析异常"), CONTENT_TYPE_ERROR(400003,"Content-type类型错误"),
    /**
     * Http status 401
     */
    INVALID_AUTHORITY(401001, "非法鉴权信息"), NEED_LOGIN(401002, "需要登录信息"),
    /**
     * Http status 403
     */
    CLIENT_FORBIDDEN(403001, "非受信任的客户端访问"), PERMISSION_FORBIDDEN(403002, "权限不足"),
    /**
     * Http status 404
     */
    URL_NOT_FOUND(404001, "API不存在"), NO_DATA(404002, "数据不存在"), CONNECTION_REFUSED(404003, "未知服务,连接拒绝"),
    /**
     * Http status 405
     */
    METHOD_NOT_ALLOWED(405001, "API请求类型不支持"),
    /**
     * Http status 406
     */
    ACCEPT_TYPE_ERROR(406001, "客户端Accept类型错误"),
    /**
     * Http status 408
     */
    SOCKET_TIMEOUT(408001, "API请求超时"), CONNECT_TIMEOUT(408001, "API连接超时"),
    /**
     * Http status 409
     */
    PARAM_STATE_CONFLICT(409001, "状态不合法"), PARAM_EXISTS_CONFLICT(409002, "数据已存在"),
    /**
     * Http status 415
     */
    MEDIA_TYPE_ERROR(415001, "客户端Content-Type类型错误"),
    /**
     * Http status 429
     */
    TOO_MANY_REQUESTS(429001, "请求过于频繁"),
    /**
     * Http status 500
     */
    INTERNAL_ERROR(500001, "系统内部异常,请联系管理员"),
    /**
     * Http status 503
     */
    SERVICE_UNAVAILABLE(503001, "服务维护中"),

    ;

    final int code;
    final String message;

    RestFulCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
