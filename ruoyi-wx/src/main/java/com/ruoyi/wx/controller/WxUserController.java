package com.ruoyi.wx.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wx.domain.WxCUser;
import com.ruoyi.wx.service.IWxUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * 微信用户Controller
 *
 * @author yang
 * @date 2023-05-26
 */
@RestController
@RequestMapping("/wx/user")
public class WxUserController extends BaseController {
    @Autowired
    private IWxUserService wxUserService;

    /**
     * 查询微信用户列表
     */
    @PreAuthorize("@ss.hasPermi('wx:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(WxCUser wxCUser) {
        startPage();
        LambdaQueryWrapper<WxCUser> lqw = new LambdaQueryWrapper<WxCUser>();
        // password是java属性名
        lqw.select(WxCUser.class, i -> !i.getProperty().equals("password"));
        if (!ObjectUtil.isNull(wxCUser.getOpenid())) {
            lqw.like(WxCUser::getOpenid, wxCUser.getOpenid());
        }
        if (!ObjectUtil.isNull(wxCUser.getPhone())) {
            lqw.like(WxCUser::getPhone, wxCUser.getPhone());
        }
        if (!ObjectUtil.isNull(wxCUser.getNickname())) {
            lqw.like(WxCUser::getNickname, wxCUser.getNickname());
        }
        if (!ObjectUtil.isNull(wxCUser.getDisabled())) {
            lqw.eq(WxCUser::getDisabled, wxCUser.getDisabled());
        }
        if (!ObjectUtil.isNull(wxCUser.getCity())) {
            lqw.like(WxCUser::getCity, wxCUser.getCity());
        }
        List<WxCUser> list = wxUserService.list(lqw);
        return getDataTable(list);
    }

    /**
     * 导出微信用户列表
     */
    @PreAuthorize("@ss.hasPermi('wx:user:export')")
    @Log(title = "微信用户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WxCUser wxCUser) {
        LambdaQueryWrapper<WxCUser> lqw = new LambdaQueryWrapper<WxCUser>(wxCUser);
        List<WxCUser> list = wxUserService.list(lqw);
        ExcelUtil<WxCUser> util = new ExcelUtil<WxCUser>(WxCUser.class);
        util.exportExcel(response, list, "微信用户数据");
    }

    /**
     * 获取微信用户详细信息
     */
    @PreAuthorize("@ss.hasPermi('wx:user:query')")
    @GetMapping(value = "/{uid}")
    public AjaxResult getInfo(@PathVariable("uid") Long uid) {
        return AjaxResult.success(wxUserService.getById(uid));
    }

    /**
     * 新增微信用户
     */
    @PreAuthorize("@ss.hasPermi('wx:user:add')")
    @Log(title = "微信用户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WxCUser wxCUser) {
        return toAjax(wxUserService.save(wxCUser) ? 1 : 0);
    }

    /**
     * 修改微信用户
     */
    @PreAuthorize("@ss.hasPermi('wx:user:edit')")
    @Log(title = "微信用户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WxCUser wxCUser) {
        return toAjax(wxUserService.updateById(wxCUser) ? 1 : 0);
    }

    /**
     * 禁启微信用户状态
     */
    @PreAuthorize("@ss.hasPermi('wx:user:disable')")
    @Log(title = "微信用户", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@Validated @RequestBody WxCUser wxCUser) {
        return toAjax(wxUserService.updateById(wxCUser) ? 1 : 0);
    }

    /**
     * 删除微信用户
     */
    @PreAuthorize("@ss.hasPermi('wx:user:remove')")
    @Log(title = "微信用户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{uids}")
    public AjaxResult remove(@PathVariable Long[] uids) {
        return toAjax(wxUserService.removeByIds(Arrays.asList(uids)) ? 1 : 0);
    }
}
