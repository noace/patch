package com.kyee.upgrade.service;

import java.util.List;
import com.kyee.upgrade.domain.UpPatchSql;

import javax.servlet.http.HttpServletResponse;

/**
 * 执行SQLService接口
 * 
 * @author lijunqiang
 * @date 2022-10-20
 */
public interface IUpPatchSqlService 
{
    /**
     * 查询执行SQL
     * 
     * @param id 执行SQLID
     * @return 执行SQL
     */
    public UpPatchSql selectUpPatchSqlById(Long id);

    /**
     * 查询执行SQL列表
     * 
     * @param upPatchSql 执行SQL
     * @return 执行SQL集合
     */
    public List<UpPatchSql> selectUpPatchSqlList(UpPatchSql upPatchSql);

    /**
     * 新增执行SQL
     * 
     * @param upPatchSql 执行SQL
     * @return 结果
     */
    public int insertUpPatchSql(UpPatchSql upPatchSql);

    /**
     * 修改执行SQL
     * 
     * @param upPatchSql 执行SQL
     * @return 结果
     */
    public int updateUpPatchSql(UpPatchSql upPatchSql);

    /**
     * 批量删除执行SQL
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpPatchSqlByIds(String ids);

    /**
     * 删除执行SQL信息
     * 
     * @param id 执行SQLID
     * @return 结果
     */
    public int deleteUpPatchSqlById(Long id);

    /**
     * 批量查询执行SQL列表
     *
     * @param ids 执行SQLID
     * @return 执行SQL集合
     */
    String batchSelectSqlList(String ids) throws Exception;
}
