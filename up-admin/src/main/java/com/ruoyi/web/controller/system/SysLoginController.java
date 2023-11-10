package com.ruoyi.web.controller.system;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kyee.common.auth.CommonAuthUtil;
import com.kyee.common.auth.ResultEntity;
import com.kyee.common.auth.UserInfoVo;
import com.kyee.upgrade.utils.DotNetToJavaStringHelper;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.security.Md5Utils;
import com.ruoyi.framework.shiro.service.SysPasswordService;
import com.ruoyi.system.domain.SysUserRole;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.mapper.SysUserRoleMapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 登录验证
 * 
 * @author ruoyi
 */
@Controller
public class SysLoginController extends BaseController
{

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysPasswordService passwordService;

    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response)
    {
        // 如果是Ajax请求，返回Json字符串。
        if (ServletUtils.isAjaxRequest(request))
        {
            return ServletUtils.renderString(response, "{\"code\":\"1\",\"msg\":\"未登录或登录超时。请重新登录\"}");
        }

        return "login";
    }

    @PostMapping("/login")
    @ResponseBody
    public AjaxResult ajaxLogin(String username, String password, Boolean rememberMe)
    {

        String msg = "用户名或密码错误";

        // 数据库中存在该用户
        SysUser sysUser = sysUserMapper.selectUserByLoginName(username);
        if (!Objects.isNull(sysUser)) {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);
            Subject subject = SecurityUtils.getSubject();
            try
            {
                subject.login(token);
                return success();
            }
            catch (AuthenticationException e)
            {
                if (StringUtils.isNotEmpty(e.getMessage()))
                {
                    msg = e.getMessage();
                }
                return error(msg);
            }
        } else {
            // 办公平台登录
            ResultEntity resultEntity = CommonAuthUtil.verifyUser(username,password,"KyeeUpGrade");
            if(resultEntity.getSuccess()) {

                SysUser newUser;
                UserInfoVo userInfo = CommonAuthUtil.getUserInfo(username, resultEntity.getData());
                if(userInfo != null){
                    // 将办公平台的用户数据存一份
                    SysUser user = new SysUser();
                    user.setUserName(userInfo.getUSER_NAME());
                    user.setLoginName(userInfo.getLOGIN_NAME());
                    // 密码加密
                    user.setSalt(ShiroUtils.randomSalt());
                    user.setPassword(passwordService.encryptPassword(username, password, user.getSalt()));
                    sysUserMapper.insertUser(user);

                    newUser = sysUserMapper.selectUserByLoginName(username);
                    // 没有该用户信息，默认开发角色
                    List<SysUserRole> userRoleList = new ArrayList<>();
                    SysUserRole role = new SysUserRole();
                    role.setUserId(newUser.getUserId());
                    role.setRoleId(2L);
                    userRoleList.add(role);
                    sysUserRoleMapper.batchUserRole(userRoleList);
                    UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);
                    Subject subject = SecurityUtils.getSubject();
                    subject.login(token);
                    return success();
                } else {
                    return error(msg);
                }
            }
        }
        return error(msg);
    }

    @GetMapping("/unauth")
    public String unauth()
    {
        return "error/unauth";
    }
}
