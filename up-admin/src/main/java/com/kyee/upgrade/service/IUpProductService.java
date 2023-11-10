package com.kyee.upgrade.service;

import java.util.List;
import com.kyee.upgrade.domain.UpProduct;
import com.ruoyi.common.core.domain.CxSelect;

/**
 * 产品Service接口
 *
 * @author lijunqiang
 * @date 2021-06-10
 */
public interface IUpProductService
{
    /**
     * 查询产品
     *
     * @param productId 产品ID
     * @return 产品
     */
    UpProduct selectUpProductById(Integer productId);

    /**
     * 查询产品列表
     *
     * @param upProduct 产品
     * @return 产品集合
     */
    List<UpProduct> selectUpProductList(UpProduct upProduct);

    /**
     * 查询产品列表
     *
     * @param userId 用户Id
     * @return 产品集合
     */
    List<UpProduct> selectUpProductListByName(String userId);

    /**
     * 新增产品
     *
     * @param upProduct 产品
     * @return 结果
     */
    int insertUpProduct(UpProduct upProduct);

    /**
     * 修改产品
     *
     * @param upProduct 产品
     * @return 结果
     */
    int updateUpProduct(UpProduct upProduct);

    /**
     * 批量删除产品
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteUpProductByIds(String ids);

    /**
     * 删除产品信息
     *
     * @param productId 产品ID
     * @return 结果
     */
    int deleteUpProductById(Integer productId);

    /**
     * 获取产品项目级联列表
     *
     * @return 结果
     */
    List<CxSelect> selectProjectAndProduct(Long userId);
}
