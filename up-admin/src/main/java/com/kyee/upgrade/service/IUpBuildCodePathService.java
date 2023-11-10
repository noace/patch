package com.kyee.upgrade.service;

import java.util.List;
import com.kyee.upgrade.domain.UpBuildCodePath;

/**
 * 代码变更路径Service接口
 * 
 * @author lijunqiang
 * @date 2021-10-22
 */
public interface IUpBuildCodePathService 
{
    /**
     * 查询代码变更路径
     * 
     * @param codePathId 代码变更路径ID
     * @return 代码变更路径
     */
    public UpBuildCodePath selectUpBuildCodePathById(Long codePathId);

    /**
     * 查询代码变更路径列表
     * 
     * @param upBuildCodePath 代码变更路径
     * @return 代码变更路径集合
     */
    public List<UpBuildCodePath> selectUpBuildCodePathList(UpBuildCodePath upBuildCodePath);

    /**
     * 新增代码变更路径
     * 
     * @param upBuildCodePath 代码变更路径
     * @return 结果
     */
    public int insertUpBuildCodePath(UpBuildCodePath upBuildCodePath);

    /**
     * 修改代码变更路径
     * 
     * @param upBuildCodePath 代码变更路径
     * @return 结果
     */
    public int updateUpBuildCodePath(UpBuildCodePath upBuildCodePath);

    /**
     * 批量删除代码变更路径
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpBuildCodePathByIds(String ids);

    /**
     * 删除代码变更路径信息
     * 
     * @param codePathId 代码变更路径ID
     * @return 结果
     */
    public int deleteUpBuildCodePathById(Long codePathId);
}
