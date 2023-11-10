package com.kyee.upgrade.mapper;

import com.kyee.upgrade.domain.UpProjectModule;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 模块名Mapper接口
 * 
 * @author lijunqiang
 * @date 2022-06-25
 */
@Mapper
@Repository
public interface UpProjectModuleMapper
{
    /**
     * 查询模块名
     * 
     * @param projectProductId 模块名ID
     * @return 模块名
     */
    UpProjectModule selectUpProjectModuleById(Integer projectProductId);

    /**
     * 查询模块名列表
     * 
     * @param UpProjectModule 模块名
     * @return 模块名集合
     */
    List<UpProjectModule> selectUpProjectModuleList(UpProjectModule UpProjectModule);

    /**
     * 查询模块名列表（根据产品和模块名查询）
     *
     * @param UpProjectModule 模块名
     * @return 模块名集合
     */
    List<UpProjectModule> getModuleList(UpProjectModule UpProjectModule);

    /**
     * 新增模块名
     * 
     * @param UpProjectModule 模块名
     * @return 结果
     */
    int insertUpProjectModule(UpProjectModule UpProjectModule);

    /**
     * 修改模块名
     * 
     * @param UpProjectModule 模块名
     * @return 结果
     */
    int updateUpProjectModule(UpProjectModule UpProjectModule);

    /**
     * 删除模块名
     * 
     * @param projectProductId 模块名ID
     * @return 结果
     */
    int deleteUpProjectModuleById(Integer projectProductId);

    /**
     * 批量删除模块名
     * 
     * @param projectProductIds 需要删除的数据ID
     * @return 结果
     */
    int deleteUpProjectModuleByIds(String[] projectProductIds);

    /**
     * 根据项目id和产品id查询配置文件信息
     * @param UpProjectModule
     * @return
     */
    List<UpProjectModule> selectConfigByProductIdAndProjectId(UpProjectModule UpProjectModule);
}
