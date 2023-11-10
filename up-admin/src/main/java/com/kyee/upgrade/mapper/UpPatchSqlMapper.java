package com.kyee.upgrade.mapper;

import java.util.List;
import com.kyee.upgrade.domain.UpPatchSql;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 执行SQLMapper接口
 * 
 * @author lijunqiang
 * @date 2022-10-20
 */
@Mapper
@Repository
public interface UpPatchSqlMapper 
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
     * 删除执行SQL
     * 
     * @param id 执行SQLID
     * @return 结果
     */
    public int deleteUpPatchSqlById(Long id);

    /**
     * 批量删除执行SQL
     * 
     * @param patchIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpPatchSqlByIds(@Param("patchIds") String[] patchIds, @Param("loginName") String loginName);

    /**
     * 批量查询执行SQL列表
     *
     * @param ids 执行SQLID
     * @return 执行SQL集合
     */
    List<UpPatchSql> batchSelectSqlList(@Param("ids") List<String> ids);

    /**
     * 根据patchId查询执行SQL
     *
     * @param fileName SQL文件名
     * @param patchId
     * @return 执行SQL
     */
    UpPatchSql selectUpPatchSqlByPatchId(@Param("fileName")String fileName, @Param("patchId")Long patchId);
}
