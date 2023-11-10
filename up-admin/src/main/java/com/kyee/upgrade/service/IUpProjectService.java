package com.kyee.upgrade.service;

import java.util.List;
import com.kyee.upgrade.domain.UpProject;

/**
 * 项目Service接口
 *
 * @author lijunqiang
 * @date 2021-06-10
 */
public interface IUpProjectService
{
    /**
     * 查询项目
     *
     * @param projectId 项目ID
     * @return 项目
     */
    public UpProject selectUpProjectById(Integer projectId);

    /**
     * 查询项目列表
     *
     * @param upProject 项目
     * @return 项目集合
     */
    public List<UpProject> selectUpProjectList(UpProject upProject);

    /**
     * 查询项目列表
     *
     * @param userId 用户ID
     * @return 项目集合
     */
    public List<UpProject> selectUpProjectListByName(String userId);

    /**
     * 新增项目
     *
     * @param upProject 项目
     * @return 结果
     */
    public int insertUpProject(UpProject upProject);

    /**
     * 修改项目
     *
     * @param upProject 项目
     * @return 结果
     */
    public int updateUpProject(UpProject upProject);

    /**
     * 批量删除项目
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpProjectByIds(String ids);

    /**
     * 删除项目信息
     *
     * @param projectId 项目ID
     * @return 结果
     */
    public int deleteUpProjectById(Integer projectId);

    /**
     * @description：获取子项目列表
     * @author：DangHangHang
     * @date：2023-08-07
     */
    List<UpProject> getProjects();
}
