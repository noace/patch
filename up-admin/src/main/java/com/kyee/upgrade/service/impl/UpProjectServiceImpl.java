package com.kyee.upgrade.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kyee.upgrade.mapper.UpProjectMapper;
import com.kyee.upgrade.domain.UpProject;
import com.kyee.upgrade.service.IUpProjectService;
import com.ruoyi.common.core.text.Convert;

/**
 * 项目Service业务层处理
 *
 * @author lijunqiang
 * @date 2021-06-10
 */
@Service
public class UpProjectServiceImpl implements IUpProjectService
{
    @Autowired
    private UpProjectMapper upProjectMapper;

    /**
     * 查询项目
     *
     * @param projectId 项目ID
     * @return 项目
     */
    @Override
    public UpProject selectUpProjectById(Integer projectId)
    {
        return upProjectMapper.selectUpProjectById(projectId);
    }

    /**
     * 查询项目列表
     *
     * @param upProject 项目
     * @return 项目
     */
    @Override
    public List<UpProject> selectUpProjectList(UpProject upProject)
    {
        return upProjectMapper.selectUpProjectList(upProject);
    }

    /**
     * 查询项目列表
     *
     * @param userId 用户ID
     * @return 项目
     */
    @Override
    public List<UpProject> selectUpProjectListByName(String userId)
    {
        return upProjectMapper.selectUpProjectListByName(userId);
    }

    /**
     * 新增项目
     *
     * @param upProject 项目
     * @return 结果
     */
    @Override
    public int insertUpProject(UpProject upProject)
    {
        upProject.setCreateTime(DateUtils.getNowDate());
        return upProjectMapper.insertUpProject(upProject);
    }

    /**
     * 修改项目
     *
     * @param upProject 项目
     * @return 结果
     */
    @Override
    public int updateUpProject(UpProject upProject)
    {
        upProject.setUpdateTime(DateUtils.getNowDate());
        return upProjectMapper.updateUpProject(upProject);
    }

    /**
     * 删除项目对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteUpProjectByIds(String ids)
    {
        return upProjectMapper.deleteUpProjectByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除项目信息
     *
     * @param projectId 项目ID
     * @return 结果
     */
    @Override
    public int deleteUpProjectById(Integer projectId)
    {
        return upProjectMapper.deleteUpProjectById(projectId);
    }
}
