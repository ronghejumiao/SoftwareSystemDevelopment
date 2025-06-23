package com.ruoyi.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.RegisterBody;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.service.SysRegisterService;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.common.core.domain.entity.SysRole;
import java.util.List;

/**
 * 注册验证
 * 
 * @author ruoyi
 */
@RestController
public class SysRegisterController extends BaseController
{
    @Autowired
    private SysRegisterService registerService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private ISysRoleService roleService;

    @PostMapping("/register")
    public AjaxResult register(@RequestBody RegisterBody user)
    {
        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser"))))
        {
            return error("当前系统没有开启注册功能！");
        }
        String msg = registerService.register(user);
        return StringUtils.isEmpty(msg) ? success() : error(msg);
    }

    /**
     * 获取注册角色列表
     */
    @GetMapping("/register/roles")
    public AjaxResult getRegisterRoles()
    {
        SysRole role = new SysRole();
        role.setStatus("0"); // 正常状态
        // 调用无数据权限的方法
        List<SysRole> roles = roleService.selectPublicRoleList(role);
        
        // 过滤出教师和学生角色
        List<SysRole> registerRoles = roles.stream()
            .filter(r -> "teacher".equals(r.getRoleKey()) || "student".equals(r.getRoleKey()))
            .collect(java.util.stream.Collectors.toList());
        
        return success(registerRoles);
    }
}
