package com.kyee.upgrade.mapper;

import java.util.List;
import com.kyee.upgrade.domain.UpProduct;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 产品Mapper接口
 *
 * @author lijunqiang
 * @date 2021-06-10
 */
@Mapper
@Repository
public interface UpProductMapper
{
    /**
     * 查询产品
     *
     * @param productId 产品ID
     * @return 产品
     */
    public UpProduct selectUpProductById(Integer productId);

    /**
     * 查询产品列表
     *
     * @param upProduct 产品
     * @return 产品集合
     */
    public List<UpProduct> selectUpProductList(UpProduct upProduct);

    /**
     * 查询产品列表
     *
     * @param userId 用户Id
     * @return 产品集合
     */
    public List<UpProduct> selectUpProductListByName(String userId);

    /**
     * 新增产品
     *
     * @param upProduct 产品
     * @return 结果
     */
    public int insertUpProduct(UpProduct upProduct);

    /**
     * 修改产品
     *
     * @param upProduct 产品
     * @return 结果
     */
    public int updateUpProduct(UpProduct upProduct);

    /**
     * 删除产品
     *
     * @param productId 产品ID
     * @return 结果
     */
    public int deleteUpProductById(Integer productId);

    /**
     * 批量删除产品
     *
     * @param productIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpProductByIds(String[] productIds);
}
