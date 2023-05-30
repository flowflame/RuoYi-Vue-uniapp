package com.ruoyi.framework.web.service;

import lombok.Data;

/**
 * 微信登录用户身份权限
 *
 * @author ruoyi
 */

@Data
public class WxUser {
    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一标识
     */
    private String uuid;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * openid
     */
    private String openid;

    /**
     * 用户名
     */
    private String nickname;

    /**
     * 手机号
     */
    private String phone;
}
