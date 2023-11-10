package com.kyee.upgrade.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.druid.filter.config.ConfigTools;
import com.kyee.upgrade.domain.License;
import com.kyee.upgrade.service.IUpProjectLicenseService;
import com.kyee.upgrade.utils.LicenseUtil;
import org.springframework.stereotype.Service;


/**
 * 项目授权码生成Service业务层处理
 *
 * @author jiaoyun
 * @date 2023-06-09
 */
@Service
public class UpProjectLicenseServiceImpl implements IUpProjectLicenseService
{


    /**
     * 生成license
     * @return license
     */
    @Override
    public Map<String, Object> getLicense(String bindMac, String serverMac, String allowedHospitalCount, String expireDate) throws ParseException {
        License licenseInfo = new License();
        Map<String,Object> licenseMap = new HashMap<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if ("Y".equals(bindMac)){
            licenseInfo.setBindMac(true);
        }else {
            licenseInfo.setBindMac(false);
        }

        licenseInfo.setServerMac(serverMac);
        licenseInfo.setAllowedHospitalCount(Integer.parseInt(allowedHospitalCount));
        licenseInfo.setExpireDate(formatter.parse(expireDate));
        String license = LicenseUtil.getLicense(licenseInfo);

        licenseMap.put("license",license);

        return licenseMap;
    }

    /**
     * 加密数据库密码
     * @return encrypt
     */
    @Override
    public Map<String, Object> getEncryptPassword(String password) throws Exception {

        Map<String,Object> encryptMap = new HashMap<>();

        String encrypt = ConfigTools.encrypt(password); //RAS非对称加密后

        encryptMap.put("encryptPassword",encrypt);

        return encryptMap;
    }
}