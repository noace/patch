package com.kyee.upgrade.mapper;

import com.kyee.upgrade.domain.UpProjectServer;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UpgradeDatabaseMapper {

    /**
     * 根据服务Id获取数据库信息
     * @param serverId
     * @return
     */
    UpProjectServer getDatabaseInfo(Integer serverId);
}
