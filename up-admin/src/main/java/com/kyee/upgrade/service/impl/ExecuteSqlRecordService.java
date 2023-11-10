package com.kyee.upgrade.service.impl;

import com.kyee.upgrade.domain.UpPatchSql;
import com.kyee.upgrade.mapper.UpPatchSqlMapper;
import com.ruoyi.common.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

@Service
public class ExecuteSqlRecordService {

    @Autowired
    private UpPatchSqlMapper sqlMapper;

    @Transactional(rollbackFor = Exception.class)
    public void sqlRecordService(Long patchId, String fileName, String createBy) {

        // 根据SQL名查询是否已经存在
        UpPatchSql patchSql = sqlMapper.selectUpPatchSqlByPatchId(fileName, patchId);
        if (!Objects.isNull(patchSql)) {
            // 修改SQL记录
            patchSql.setUpdateBy(createBy);
            patchSql.setUpdateTime(new Date());
            sqlMapper.updateUpPatchSql(patchSql);
        } else {
            // 存储SQL文件 虚拟目录/SQL/XX.SQL
            UpPatchSql sql = new UpPatchSql();
            sql.setPatchId(patchId);
            sql.setSqlName(fileName);
            String sqlPath = Constants.RESOURCE_PREFIX + "/SQL/" + fileName ;
            sql.setSqlPath(sqlPath);
            sql.setCreateBy(createBy);
            sql.setCreateTime(new Date());
            sqlMapper.insertUpPatchSql(sql);
        }
    }
}
