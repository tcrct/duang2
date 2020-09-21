package com.duangframework.report.dto.sysinfo;

/**
 * CPU 信息Dto对象
 *
 * @author Laotang
 * @since 1.0
 */
public class CpuInfoDto  implements java.io.Serializable {

    /**cpu核数*/
    private Integer coreNum;
    /**cpu系统使用率*/
    private Double sysUsage;
    /**cpu用户使用率*/
    private Double userUsage;
    /**cpu当前等待率*/
    private Double waitUsage;
    /**cpu当前使用率*/
    private Double currentUsage;

    public CpuInfoDto() {
    }

    public CpuInfoDto(Integer coreNum, Double sysUsage, Double userUsage, Double waitUsage, Double currentUsage) {
        this.coreNum = coreNum;
        this.sysUsage = sysUsage;
        this.userUsage = userUsage;
        this.waitUsage = waitUsage;
        this.currentUsage = currentUsage;
    }

    public Integer getCoreNum() {
        return coreNum;
    }

    public void setCoreNum(Integer coreNum) {
        this.coreNum = coreNum;
    }

    public Double getSysUsage() {
        return sysUsage;
    }

    public void setSysUsage(Double sysUsage) {
        this.sysUsage = sysUsage;
    }

    public Double getUserUsage() {
        return userUsage;
    }

    public void setUserUsage(Double userUsage) {
        this.userUsage = userUsage;
    }

    public Double getWaitUsage() {
        return waitUsage;
    }

    public void setWaitUsage(Double waitUsage) {
        this.waitUsage = waitUsage;
    }

    public Double getCurrentUsage() {
        return currentUsage;
    }

    public void setCurrentUsage(Double currentUsage) {
        this.currentUsage = currentUsage;
    }
}
