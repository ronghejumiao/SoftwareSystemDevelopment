package com.ruoyi.framework.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.model.RegisterBody;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.user.CaptchaException;
import com.ruoyi.common.exception.user.CaptchaExpireException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.service.ISysRoleService;
import java.util.Arrays;

/**
 * 注册校验方法
 * 
 * @author ruoyi
 */
@Component
public class SysRegisterService
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 注册
     */
    @Transactional
    public String register(RegisterBody registerBody)
    {
        String msg = "", username = registerBody.getUsername(), password = registerBody.getPassword();
        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);
        sysUser.setEmail(registerBody.getEmail());
        sysUser.setPhonenumber(registerBody.getPhonenumber());

        // 验证码开关
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled)
        {
            validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());
        }

        if (StringUtils.isEmpty(username))
        {
            msg = "用户名不能为空";
        }
        else if (StringUtils.isEmpty(password))
        {
            msg = "用户密码不能为空";
        }
        else if (StringUtils.isEmpty(registerBody.getNickName()))
        {
            msg = "真实姓名不能为空";
        }
        else if (StringUtils.isEmpty(registerBody.getEmail()))
        {
            msg = "邮箱地址不能为空";
        }
        else if (StringUtils.isEmpty(registerBody.getPhonenumber()))
        {
            msg = "手机号码不能为空";
        }
        else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)
        {
            msg = "账户长度必须在2到20个字符之间";
        }
        else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            msg = "密码长度必须在5到20个字符之间";
        }
        else if (registerBody.getRoleId() == null)
        {
            msg = "请选择角色";
        }
        else if (!userService.checkUserNameUnique(sysUser))
        {
            msg = "保存用户'" + username + "'失败，注册账号已存在";
        }
        else if (!userService.checkEmailUnique(sysUser))
        {
            msg = "保存用户'" + username + "'失败，邮箱已存在";
        }
        else if (!userService.checkPhoneUnique(sysUser))
        {
            msg = "保存用户'" + username + "'失败，手机号码已存在";
        }
        else
        {
            // 验证角色是否存在
            SysRole role = roleService.selectRoleById(registerBody.getRoleId());
            if (role == null)
            {
                msg = "选择的角色不存在";
            }
            else if (!"teacher".equals(role.getRoleKey()) && !"student".equals(role.getRoleKey()))
            {
                msg = "只能选择教师或学生角色";
            }
            else
            {
                sysUser.setNickName(registerBody.getNickName());
                sysUser.setSex(registerBody.getSex());
                sysUser.setPwdUpdateDate(DateUtils.getNowDate());
                sysUser.setPassword(SecurityUtils.encryptPassword(password));
                // 设置用户角色
                sysUser.setRoleIds(new Long[]{registerBody.getRoleId()});
                boolean regFlag = userService.registerUser(sysUser);
                if (!regFlag)
                {
                    msg = "注册失败,请联系系统管理人员";
                }
                else
                {
                    AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.REGISTER, MessageUtils.message("user.register.success")));
                }
            }
        }
        return msg;
    }

    /**
     * 校验验证码
     * 
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid)
    {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null)
        {
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha))
        {
            throw new CaptchaException();
        }
    }
}
