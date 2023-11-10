package com.kyee.upgrade.service;

import java.util.List;
import com.kyee.upgrade.domain.UpProjectProduct;

/**
 * 项目产品Service接口
 *
 * @author lijunqiang
 * @date 2021-06-10
 */
public interface IUpProjectProductService
{
    /**
     * 查询项目产品
     *
     * @param projectProductId 项目产品ID
     * @return 项目产品
     */
    public UpProjectProduct selectUpProjectProductById(Integer projectProductId);

    /**
     * 查询项目产品列表
     *
     * @param upProjectProduct 项目产品
     * @return 项目产品集合
     */
    public List<UpProjectProduct> selectUpProjectProductList(UpProjectProduct upProjectProduct);

    /**
     * 新增项目产品
     *
     * @param upProjectProduct 项目产品
     * @return 结果
     */
    public int insertUpProjectProduct(UpProjectProduct upProjectProduct);

    /**
     * 修改项目产品
     *
     * @param upProjectProduct 项目产品
     * @return 结果
     */
    public int updateUpProjectProduct(UpProjectProduct upProjectProduct);

    /**
     * 批量删除项目产品
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpProjectProductByIds(String ids);

    /**
     * 删除项目产品信息
     *
     * @param projectProductId 项目产品ID
     * @return 结果
     */
    public int deleteUpProjectProductById(Integer projectProductId);

    /**
     * 查询项目产品
     *
     * @param upProjectProduct
     * @return 项目产品
     */
    public List<UpProjectProduct> selectByProductIdAndProjectId(UpProjectProduct upProjectProduct);
}
