package com.kyee.upgrade.mapper;

import java.util.List;
import com.kyee.upgrade.domain.UpProjectProduct;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import org.apache.ibatis.annotations.Param;

/**
 * 项目产品Mapper接口
 *
 * @author lijunqiang
 * @date 2021-06-10
 */
@Mapper
@Repository
public interface UpProjectProductMapper
{
    /**
     * 查询项目产品
     *
     * @param projectProductId 项目产品ID
     * @return 项目产品
     */
    UpProjectProduct selectUpProjectProductById(Integer projectProductId);

    /**
     * 查询项目产品列表
     *
     * @param upProjectProduct 项目产品
     * @return 项目产品集合
     */
    List<UpProjectProduct> selectUpProjectProductList(UpProjectProduct upProjectProduct);

    /**
     * 新增项目产品
     *
     * @param upProjectProduct 项目产品
     * @return 结果
     */
    int insertUpProjectProduct(UpProjectProduct upProjectProduct);

    /**
     * 修改项目产品
     *
     * @param upProjectProduct 项目产品
     * @return 结果
     */
    int updateUpProjectProduct(UpProjectProduct upProjectProduct);

    /**
     * 删除项目产品
     *
     * @param projectProductId 项目产品ID
     * @return 结果
     */
    int deleteUpProjectProductById(Integer projectProductId);

    /**
     * 批量删除项目产品
     *
     * @param projectProductIds 需要删除的数据ID
     * @return 结果
     */
    int deleteUpProjectProductByIds(String[] projectProductIds);

    /**
     * 根据产品Id和项目Id查询未被删除的项目产品列表
     * @param upProjectProduct 项目产品
     * @return List<UpProjectProduct> 项目产品集合
     */
    List<UpProjectProduct> selectByProductIdAndProjectId(UpProjectProduct upProjectProduct);

    /**
     * 获取最新插入数据的ID
     * @return
     */
    Long getLastInsertId();

    /**
     * 查询项目的编译方式
     *
     * @param patchId 补丁包Id
     * @return 项目产品
     */
    UpProjectProduct getBuildTypeWithPatchId(Long patchId);

    /**
     * 根据产品和项目Id查询项目产品
     *
     * @param productId 产品
     * @param projectId 项目
     * @return 项目产品
     */
    UpProjectProduct getUpProjectProductById(@Param("productId") Integer productId, @Param("projectId") Integer projectId);
}
