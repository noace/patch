package com.kyee.upgrade.domain;

import java.io.Serializable;
import java.util.Date;

public class License implements Serializable {

    // 是否和 MAC 地址绑定
    private boolean isBindMac;

    //服务器MAC地址
    private String serverMac;

    //授权到期时间
    private Date expireDate;

    // 允许使用的医院数量
    private int allowedHospitalCount;

    public boolean isBindMac() {
        return isBindMac;
    }

    public void setBindMac(boolean bindMac) {
        isBindMac = bindMac;
    }

    public String getServerMac() {
        return serverMac;
    }

    public void setServerMac(String serverMac) {
        this.serverMac = serverMac;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public int getAllowedHospitalCount() {
        return allowedHospitalCount;
    }

    public void setAllowedHospitalCount(int allowedHospitalCount) {
        this.allowedHospitalCount = allowedHospitalCount;
    }

    @Override
    public String toString() {
        return "License{" +
                "isBindMac=" + isBindMac +
                ", serverMac='" + serverMac + '\'' +
                ", expireDate='" + expireDate + '\'' +
                ", allowedHospitalCount=" + allowedHospitalCount +
                '}';
    }
}
