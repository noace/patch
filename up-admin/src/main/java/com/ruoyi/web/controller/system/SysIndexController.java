package com.ruoyi.web.controller.system;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.system.domain.PatchUpgradeInfo;
import com.ruoyi.system.domain.PatchUpgradeInfoOneMonth;
import com.ruoyi.system.mapper.SysConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.ShiroConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysMenu;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.utils.CookieUtils;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.shiro.service.SysPasswordService;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysMenuService;

/**
 * 首页 业务处理
 * 
 * @author ruoyi
 */
@Controller
public class SysIndexController extends BaseController
{
    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private SysPasswordService passwordService;

    @Autowired
    private SysConfigMapper configMapper;

    // 系统首页
    @GetMapping("/index")
    public String index(ModelMap mmap)
    {
        // 取身份信息
        SysUser user = ShiroUtils.getSysUser();
        // 根据用户id取出菜单
        List<SysMenu> menus = menuService.selectMenusByUser(user);
        mmap.put("menus", menus);
        mmap.put("user", user);
        mmap.put("sideTheme", configService.selectConfigByKey("sys.index.sideTheme"));
        mmap.put("skinName", configService.selectConfigByKey("sys.index.skinName"));
        mmap.put("ignoreFooter", configService.selectConfigByKey("sys.index.ignoreFooter"));
        mmap.put("copyrightYear", RuoYiConfig.getCopyrightYear());
        mmap.put("demoEnabled", RuoYiConfig.isDemoEnabled());
        mmap.put("isDefaultModifyPwd", initPasswordIsModify(user.getPwdUpdateDate()));
        mmap.put("isPasswordExpired", passwordIsExpiration(user.getPwdUpdateDate()));

        // 菜单导航显示风格
        String menuStyle = configService.selectConfigByKey("sys.index.menuStyle");
        // 移动端，默认使左侧导航菜单，否则取默认配置
        String indexStyle = ServletUtils.checkAgentIsMobile(ServletUtils.getRequest().getHeader("User-Agent")) ? "index" : menuStyle;

        // 优先Cookie配置导航菜单
        Cookie[] cookies = ServletUtils.getRequest().getCookies();
        for (Cookie cookie : cookies)
        {
            if (StringUtils.isNotEmpty(cookie.getName()) && "nav-style".equalsIgnoreCase(cookie.getName()))
            {
                indexStyle = cookie.getValue();
                break;
            }
        }
        String webIndex = "topnav".equalsIgnoreCase(indexStyle) ? "index-topnav" : "index";
        return webIndex;
    }

    // 锁定屏幕
    @GetMapping("/lockscreen")
    public String lockscreen(ModelMap mmap)
    {
        mmap.put("user", ShiroUtils.getSysUser());
        ServletUtils.getSession().setAttribute(ShiroConstants.LOCK_SCREEN, true);
        return "lock";
    }

    // 解锁屏幕
    @PostMapping("/unlockscreen")
    @ResponseBody
    public AjaxResult unlockscreen(String password)
    {
        SysUser user = ShiroUtils.getSysUser();
        if (StringUtils.isNull(user))
        {
            return AjaxResult.error("服务器超时，请重新登陆");
        }
        if (passwordService.matches(user, password))
        {
            ServletUtils.getSession().removeAttribute(ShiroConstants.LOCK_SCREEN);
            return AjaxResult.success();
        }
        return AjaxResult.error("密码不正确，请重新输入。");
    }

    // 切换主题
    @GetMapping("/system/switchSkin")
    public String switchSkin()
    {
        return "skin";
    }

    // 切换菜单
    @GetMapping("/system/menuStyle/{style}")
    public void menuStyle(@PathVariable String style, HttpServletResponse response)
    {
        CookieUtils.setCookie(response, "nav-style", style);
    }

    // 系统介绍
    @GetMapping("/system/main")
    public String main(ModelMap mmap)
    {
//        List<PatchUpgradeInfo> activeProjectList = configService.activeProjectList();
//        mmap.put("activeProjectLis", activeProjectList);
        List<PatchUpgradeInfoOneMonth> patchUpgradeInfoOneMonthList = configService.activeProjectListOneMonth();
        mmap.put("activeProjectLis", patchUpgradeInfoOneMonthList);
        mmap.put("date", new Date());
        return "main";
    }

    @PostMapping("/searchPublish")
    @ResponseBody
    public AjaxResult searchPublish(String value,String type) {
        ModelMap modelMap = new ModelMap();
        Integer sum = new Integer(0);
        Integer nextSum = new Integer(0);
        try {
            if ("publish".equals(type)){
                sum = configMapper.selectPublish(value);
                nextSum = configMapper.selectPublish(value+"-1");
                String analysisData = getAnalysisData(sum, nextSum);
                modelMap.put("sum",sum);
                modelMap.put("percent",analysisData);

            }else if ("upgrades".equals(type)){
                sum = configMapper.selectUpgrades(value);
                nextSum = configMapper.selectUpgrades(value+"-1");
                String analysisData = getAnalysisData(sum, nextSum);
                modelMap.put("sum",sum);
                modelMap.put("percent",analysisData);
            }else {
                sum = configMapper.selectPatchedNum(value);
                nextSum = configMapper.selectPatchedNum(value+"-1");
                String analysisData = getAnalysisData(sum, nextSum);
                modelMap.put("sum",sum);
                modelMap.put("percent",analysisData);
            }
        } catch (Exception e) {
            return  AjaxResult.error(e.getMessage());
        }
        return new AjaxResult(AjaxResult.Type.SUCCESS, "查询成功" ,modelMap);
    }

        /**
         *  **计算月增长率**
         *    sameMonth 本月金额
         *    lastMonth 上月金额
         */
        private static String getAnalysisData(int sameMonth, int lastMonth) {
            // 创建一个数值格式化对象
            NumberFormat numberFormat = NumberFormat.getInstance();
            // 设置精确到小数点后2位
            numberFormat.setMaximumFractionDigits(0);
            if (lastMonth > 0 && sameMonth > 0) {
                int growthNum = sameMonth - lastMonth;
                return numberFormat.format((float)growthNum / (float) lastMonth * 100)+ "%";
            } else if (lastMonth > 0 && sameMonth == 0) {
                //如果上个数大于0，下个数为0，增长率为 0
                return "0.00%";
            } else if (lastMonth == 0 && sameMonth > 0) {
                //如果下个数大于0，上个数为0，增长率为 0
                return  "0.00%";
            } else {
                return "0.00%";
            }
        }

    @PostMapping("/loadStatisticsChart")
    @ResponseBody
    public AjaxResult loadStatisticsChart(ModelMap mmap) {
        try {
        List<Map> upPatchData = configMapper.selectUpPatchData();
        List<Map> countUpgradesData = configMapper.selectUpPatchClientData();
        Map<Object, List<Map>> upPatchDataMap = upPatchData.stream().collect(Collectors.groupingBy(a -> a.get("timestamp")));
        Map<Object, List<Map>> countUpgradesDataMap = countUpgradesData.stream().collect(Collectors.groupingBy(a -> a.get("timestamp")));

        //获取最近其他的数据
        List<String> dateList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < 7; i++) {
            Date date = DateUtils.addDays(new Date(), -i);
            String formatDate = sdf.format(date);
            dateList.add(formatDate);
        }
        Collections.sort(dateList);
        List<String> countUpPatchList = new ArrayList<>();
        List<String> countUpgradesList = new ArrayList<>();
        dateList.forEach(d->{
            if (upPatchDataMap.get(d)!= null){
                countUpPatchList.add(upPatchDataMap.get(d).get(0).get("count").toString());
            }else {
                countUpPatchList.add("0");
            }
            if (countUpgradesDataMap.get(d)!=null){
                countUpgradesList.add(countUpgradesDataMap.get(d).get(0).get("count").toString());
            }else {
                countUpgradesList.add("0");
            }
        });
        mmap.put("countUpPatchList",  countUpPatchList);
        mmap.put("countUpgradesList",  countUpgradesList);
        mmap.put("timeList", dateList);
        //发包总数
        mmap.put("allMonthPublish", configMapper.selectPublish(null));
        //最近一个月发包数
        mmap.put("oneMonthPublish", configMapper.selectPublish("近一月"));
        //最近一个月正常升级数
        mmap.put("oneMonthUpgrades", configMapper.selectUpgrades("近一月"));
        //活跃用户
        Integer sum = configMapper.selectActiveUser("近一月");
        Integer nextSum = configMapper.selectActiveUser("近一月-1");
        String analysisData = getAnalysisData(sum, nextSum);
        mmap.put("activeUser", sum);
        mmap.put("activeUserPercent", analysisData);

        } catch (Exception e) {
            return  AjaxResult.error(e.getMessage());
        }
        return new AjaxResult(AjaxResult.Type.SUCCESS, "查询成功" ,mmap);
    }


    // 检查初始密码是否提醒修改
    public boolean initPasswordIsModify(Date pwdUpdateDate)
    {
        Integer initPasswordModify = Convert.toInt(configService.selectConfigByKey("sys.account.initPasswordModify"));
        return initPasswordModify != null && initPasswordModify == 1 && pwdUpdateDate == null;
    }

    // 检查密码是否过期
    public boolean passwordIsExpiration(Date pwdUpdateDate)
    {
        Integer passwordValidateDays = Convert.toInt(configService.selectConfigByKey("sys.account.passwordValidateDays"));
        if (passwordValidateDays != null && passwordValidateDays > 0)
        {
            if (StringUtils.isNull(pwdUpdateDate))
            {
                // 如果从未修改过初始密码，直接提醒过期
                return true;
            }
            Date nowDate = DateUtils.getNowDate();
            return DateUtils.differentDaysByMillisecond(nowDate, pwdUpdateDate) > passwordValidateDays;
        }
        return false;
    }
}
