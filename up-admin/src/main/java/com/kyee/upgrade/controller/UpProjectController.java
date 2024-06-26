package com.kyee.upgrade.controller;

import com.kyee.upgrade.domain.UpProject;
import com.kyee.upgrade.mapper.UpProjectMapper;
import com.kyee.upgrade.service.IUpProjectService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目Controller
 * 
 * @author lijunqiang
 * @date 2021-06-10
 */
@Controller
@RequestMapping("/upgrade/project")
public class UpProjectController extends BaseController
{
    private String prefix = "upgrade/project";

    @Autowired
    private IUpProjectService upProjectService;

    @Autowired
    private UpProjectMapper upProjectMapper;

    @RequiresPermissions("upgrade:project:view")
    @GetMapping()
    public String project()
    {
        return prefix + "/project";
    }

    /**
     * 查询项目列表
     */
    @RequiresPermissions("upgrade:project:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(UpProject upProject)
    {
        startPage();
        List<UpProject> list = upProjectService.selectUpProjectList(upProject);
        for (UpProject project : list) {
            String subProjectId = project.getSubProjectId();
            if (StringUtils.isNotEmpty(subProjectId)) {
                String[] projectIds = subProjectId.split(",");
                List<UpProject> upProjects = upProjectMapper.selectUpProjectListByIds(Arrays.asList(projectIds));
                List<String> projectName = upProjects.stream().map(UpProject::getProjectName).collect(Collectors.toList());
                project.setSubProjectName(StringUtils.join(projectName, ","));
            }
        }
        return getDataTable(list);
    }

    /**
     * 导出项目列表
     */
    @RequiresPermissions("upgrade:project:export")
    @Log(title = "项目", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(UpProject upProject)
    {
        List<UpProject> list = upProjectService.selectUpProjectList(upProject);
        ExcelUtil<UpProject> util = new ExcelUtil<UpProject>(UpProject.class);
        return util.exportExcel(list, "项目数据");
    }

    /**
     * 新增项目
     */
    @GetMapping("/add")
    public String add(ModelMap map)
    {
        List<UpProject> projects = upProjectService.getProjects();
        List<UpProject> subProjectList = projects.stream().filter(s -> StringUtils.isEmpty(s.getSubProjectId())).collect(Collectors.toList());
        map.put("projects", subProjectList);
        return prefix + "/add";
    }

    /**
     * 新增保存项目
     */
    @RequiresPermissions("upgrade:project:add")
    @Log(title = "项目", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(UpProject upProject)
    {
        return toAjax(upProjectService.insertUpProject(upProject));
    }

    /**
     * 修改项目
     */
    @GetMapping("/edit/{projectId}")
    public String edit(@PathVariable("projectId") Integer projectId, ModelMap mmap)
    {
        UpProject upProject = upProjectService.selectUpProjectById(projectId);
        mmap.put("upProject", upProject);

        List<UpProject> projects = upProjectService.getProjects();
        List<UpProject> subProjectList = projects.stream().filter(s -> StringUtils.isEmpty(s.getSubProjectId())).collect(Collectors.toList());
        String subProjectId = upProject.getSubProjectId();
        if (StringUtils.isNotEmpty(subProjectId)) {
            String[] split = subProjectId.split(",");
            for (String s : split) {
                for (UpProject project : subProjectList) {
                    if (s.equals(project.getProjectId() + "")) {
                        project.setFlag(true);
                        break;
                    }
                }
            }
        }
        mmap.put("projects", subProjectList);
        return prefix + "/edit";
    }

    /**
     * 修改保存项目
     */
    @RequiresPermissions("upgrade:project:edit")
    @Log(title = "项目", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(UpProject upProject)
    {
        return toAjax(upProjectService.updateUpProject(upProject));
    }

    /**
     * 删除项目
     */
    @RequiresPermissions("upgrade:project:remove")
    @Log(title = "项目", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(upProjectService.deleteUpProjectByIds(ids));
    }
}
