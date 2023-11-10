package com.kyee.upgrade.mapper;

import java.util.List;
import com.kyee.upgrade.domain.UpProject;
import com.kyee.upgrade.domain.UpProjectProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 项目Mapper接口
 *
 * @author lijunqiang
 * @date 2021-06-10
 */
@Mapper
public interface UpProjectMapper
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
     * 根据用户姓名查询项目列表
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
     * 删除项目
     *
     * @param projectId 项目ID
     * @return 结果
     */
    public int deleteUpProjectById(Integer projectId);

    /**
     * 批量删除项目
     *
     * @param projectIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpProjectByIds(String[] projectIds);

    /**
     * 查询项目列表
     *
     * @param idList 项目Ids
     * @return 项目集合
     */
    List<UpProject> selectUpProjectListByIds(@Param("idList") List<String> idList);

    /**
     * 查询子项目列表
     *
     * @return 项目集合
     */
    List<String> selectSubUpProjectList();

    /**
     * 查询项目产品
     * @return 项目产品
     */
    List<UpProjectProduct> getProjectAndProductByIds(Integer product , List<String> idList);
}
