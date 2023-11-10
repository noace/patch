package com.kyee.upgrade.mapper;

import java.util.List;
import com.kyee.upgrade.domain.UpProjectConfigFile;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 项目产品Mapper接口
 * 
 * @author lijunqiang
 * @date 2022-06-25
 */
@Mapper
@Repository
public interface UpProjectConfigFileMapper 
{
    /**
     * 查询项目产品
     * 
     * @param projectProductId 项目产品ID
     * @return 项目产品
     */
    public UpProjectConfigFile selectUpProjectConfigFileById(Integer projectProductId);

    /**
     * 查询项目产品列表
     * 
     * @param upProjectConfigFile 项目产品
     * @return 项目产品集合
     */
    public List<UpProjectConfigFile> selectUpProjectConfigFileList(UpProjectConfigFile upProjectConfigFile);

    /**
     * 新增项目产品
     * 
     * @param upProjectConfigFile 项目产品
     * @return 结果
     */
    public int insertUpProjectConfigFile(UpProjectConfigFile upProjectConfigFile);

    /**
     * 修改项目产品
     * 
     * @param upProjectConfigFile 项目产品
     * @return 结果
     */
    public int updateUpProjectConfigFile(UpProjectConfigFile upProjectConfigFile);

    /**
     * 删除项目产品
     * 
     * @param projectProductId 项目产品ID
     * @return 结果
     */
    public int deleteUpProjectConfigFileById(Integer projectProductId);

    /**
     * 批量删除项目产品
     * 
     * @param projectProductIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpProjectConfigFileByIds(String[] projectProductIds);

    /**
     * 根据项目id和产品id查询配置文件信息
     * @param upProjectConfigFile
     * @return
     */
    List<UpProjectConfigFile> selectConfigByProductIdAndProjectId(UpProjectConfigFile upProjectConfigFile);
}
