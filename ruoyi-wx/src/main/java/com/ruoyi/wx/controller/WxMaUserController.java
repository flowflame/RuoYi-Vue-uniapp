package com.ruoyi.wx.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.service.WxLoginService;
import com.ruoyi.framework.web.service.WxUser;
import com.ruoyi.wx.domain.WxCUser;
import com.ruoyi.wx.service.IWxUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * 微信小程序用户接口
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/wx/user/{appid}")
public class WxMaUserController {
    private final WxMaService wxMaService;
    @Autowired
    private WxLoginService wxLoginService;

    @Autowired
    private IWxUserService wxUserService;

    /**
     * 登陆接口
     */
    @GetMapping("/login")
    public R login(@PathVariable String appid, String code, String token) {
        System.out.println("token:" + token);
        WxUser wxUser = null;
        if (StringUtils.isNotEmpty(token)) {
            wxUser = wxLoginService.getWxUser(token);
            System.out.println(wxUser);
        }
        if (StringUtils.isEmpty(token) || StringUtils.isNull(wxUser)) {

            if (StringUtils.isBlank(code)) {
                return R.fail("empty jscode");
            }
            if (!wxMaService.switchover(appid)) {
                throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
            }
            try {
                WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
                log.info(session.getSessionKey());
                log.info(session.getOpenid());
                wxUser = new WxUser();
                wxUser.setOpenid(session.getOpenid());
                QueryWrapper<WxCUser> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(WxCUser::getOpenid, session.getOpenid());
                WxCUser cUser = wxUserService.getOne(wrapper);
                if (StringUtils.isNotNull(cUser)) {
                    if (StringUtils.isNotEmpty(cUser.getNickname())) {
                        wxUser.setNickname(cUser.getNickname());
                        String newToken = wxLoginService.createToken(wxUser);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("token", newToken);
                        cUser.setNull();
                        map.put("user", cUser);
                        return R.ok(map, "登录成功");
                    } else {
                        //非首次，未授权
                        String newToken = wxLoginService.createToken(wxUser);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("token", newToken);
                        map.put("user", null);
                        return R.ok(map, "非首次，未授权");
                    }
                } else {
                    //首次尝试登录
                    String newToken = wxLoginService.createToken(wxUser);
                    WxCUser user = new WxCUser();
                    user.setOpenid(session.getOpenid());
                    wxUserService.save(user);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("token", newToken);
                    map.put("user", null);
                    return R.fail(map, "未登录，请授权");
                }

            } catch (WxErrorException e) {
                log.error(e.getMessage(), e);
                return R.fail(e.toString());
            }
        } else {
            //token、WxUser都不为null
            String nickname = wxUser.getNickname();
            if (StringUtils.isNotBlank(nickname)) {
                QueryWrapper<WxCUser> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(WxCUser::getOpenid, wxUser.getOpenid());
                WxCUser user = wxUserService.getOne(wrapper);
                //更新缓存
                wxLoginService.setWxUser(wxUser);
                HashMap<String, Object> map = new HashMap<>();
                map.put("token", token);
                user.setNull();
                map.put("user", user);
                return R.ok(map, "登录成功");
            } else {
                wxLoginService.setWxUser(wxUser);
                HashMap<String, Object> map = new HashMap<>();
                map.put("token", token);
                map.put("user", null);
                return R.ok(map, "非首次，未授权");
            }
        }
    }

    /**
     * 将授权信息保存到缓存和数据库中
     *
     * @param cUser   微信授权获取的信息
     * @param request 请求
     * @return 授权成功提醒
     */
    @PostMapping("auth")
    public R auth(@RequestBody WxCUser cUser, HttpServletRequest request) {
        String token = wxLoginService.getToken(request);
        WxUser wxUser = wxLoginService.getWxUser(token);
        wxUser.setNickname(cUser.getNickname());
        wxLoginService.setWxUser(wxUser);
        QueryWrapper<WxCUser> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(WxCUser::getOpenid, wxUser.getOpenid());
        wxUserService.update(cUser, wrapper);
        return R.ok(null, "授权成功");
    }


    /**
     * <pre>
     * 获取用户信息接口
     * </pre>
     */
    @GetMapping("/info")
    public String info(@PathVariable String appid, String sessionKey,
                       String signature, String rawData, String encryptedData, String iv) {
        if (!wxMaService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }

        // 用户信息校验
        if (!wxMaService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            WxMaConfigHolder.remove();//清理ThreadLocal
            return "user check failed";
        }

        // 解密用户信息
        WxMaUserInfo userInfo = wxMaService.getUserService().getUserInfo(sessionKey, encryptedData, iv);
        WxMaConfigHolder.remove();//清理ThreadLocal
//        return JsonUtils.toJson(userInfo);
        return userInfo.toString();
    }

    /**
     * <pre>
     * 获取用户绑定手机号信息
     * </pre>
     */
    @GetMapping("/phone")
    public String phone(@PathVariable String appid, String sessionKey, String signature,
                        String rawData, String encryptedData, String iv) {
        if (!wxMaService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }

        // 用户信息校验
        if (!wxMaService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            WxMaConfigHolder.remove();//清理ThreadLocal
            return "user check failed";
        }

        // 解密
        WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);
        WxMaConfigHolder.remove();//清理ThreadLocal
//        return JsonUtils.toJson(phoneNoInfo);
        return phoneNoInfo.toString();
    }

}
