package com.ruoyi.wx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.wx.domain.WxCUser;
import com.ruoyi.wx.mapper.WxUserMapper;
import com.ruoyi.wx.service.IWxUserService;
import org.springframework.stereotype.Service;

/**
 * 微信用户Service业务层处理
 *
 * @author yang
 * @date 2023-05-26
 */
@Service
public class WxUserServiceImpl extends ServiceImpl<WxUserMapper, WxCUser> implements IWxUserService {

}
