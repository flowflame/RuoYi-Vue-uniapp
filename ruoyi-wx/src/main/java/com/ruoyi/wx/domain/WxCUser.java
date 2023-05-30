package com.ruoyi.wx.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.newBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 微信用户对象 wx_user
 *
 * @author yang
 * @date 2023-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wx_user")
public class WxCUser extends newBaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(value = "uid", type = IdType.AUTO)
    private Long uid;

    /**
     * 微信openid
     */
    @TableField("openid")
    @Excel(name = "微信openid")
    private String openid;

    /**
     * 手机号
     */
    @TableField("phone")
    @Excel(name = "手机号")
    private String phone;

    /**
     * 昵称
     */
    @TableField("nickname")
    @Excel(name = "昵称")
    private String nickname;

    /**
     * 头像
     */
    @TableField("avatar")
    @Excel(name = "头像")
    private String avatar;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 性别
     */
    @TableField("gender")
    @Excel(name = "性别")
    private Integer gender;

    /**
     * 生日
     */
    @TableField("birthday")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "生日", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date birthday;

    /**
     * 状态
     */
    @TableField("disabled")
    @Excel(name = "状态")
    private String disabled;

    /**
     * 最近登录ip
     */
    @TableField("last_ip")
    @Excel(name = "最近登录ip")
    private String lastIp;

    /**
     * 城市
     */
    @TableField("city")
    @Excel(name = "城市")
    private String city;

    /**
     * 注册时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 最近登录时间
     */
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 清除登录敏感信息
     */
    public void setNull() {
        this.uid = 0L;
        this.openid = null;
        this.password = null;
        this.lastIp = null;
        this.createTime = null;
    }
}
