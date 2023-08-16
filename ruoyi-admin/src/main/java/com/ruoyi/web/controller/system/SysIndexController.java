package com.ruoyi.web.controller.system;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.config.Global;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysMenu;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysMenuService;
import com.ruoyi.system.service.ISysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 首页 业务处理
 *
 * @author ruoyi
 */
@Controller
public class SysIndexController extends BaseController {
    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private ISysNoticeService noticeService;

    // 系统首页
    @GetMapping("/index")
    public String index(ModelMap mmap) {
        // 取身份信息
        SysUser user = ShiroUtils.getSysUser();
        // 根据用户id取出菜单
        List<SysMenu> menus = menuService.selectMenusByUser(user);
        mmap.put("menus", menus);
        mmap.put("user", user);
        mmap.put("copyrightYear", Global.getCopyrightYear());
        mmap.put("demoEnabled", Global.isDemoEnabled());
        return "index";
    }

    // 切换主题
    @GetMapping("/system/switchSkin")
    public String switchSkin(ModelMap mmap) {
        return "skin";
    }

    // 系统介绍
    @GetMapping("/system/home")
    public String main(ModelMap mmap) {
        List<SysNotice> notices = new ArrayList<>();
        SysUser sysUser = ShiroUtils.getSysUser();
        if (sysUser.getRoles().size() == 0) {
            mmap.put("notices", notices);
        } else {
            Long roleId = sysUser.getRoles().get(0).getRoleId();
            notices = noticeService.selectNoticeListByRoleId(roleId);
            notices=notices.stream().filter(tmp-> StrUtil.isNotBlank(tmp.getNoticeType())&&tmp.getNoticeType().equals("2")).collect(Collectors.toList());
            mmap.put("notices", notices);
        }
        return "main";
    }
}
