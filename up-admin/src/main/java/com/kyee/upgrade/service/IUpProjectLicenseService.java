package com.kyee.upgrade.service;

import java.text.ParseException;
import java.util.Map;


/**
 * 项目授权码生成Service接口
 *
 * @author jiaoyun
 * @date 2023-06-09
 */
public interface IUpProjectLicenseService
{
    /**
     * 生成license
     *
     * @return license
     */
    Map<String, Object> getLicense(String bindMac, String serverMac, String allowedHospitalCount, String expireDate) throws ParseException;

    Map<String, Object> getEncryptPassword(String password) throws Exception;
}