package com.duangframework.report.dto.sysinfo;
/**
 * 操作系统 信息Dto对象
 *
 * @author Laotang
 * @since 1.0
 */
public class SystemInfoDto implements java.io.Serializable {
    /**操作系统名*/
    private String osName;
    /**系统架构*/
    private String osArch;

    public SystemInfoDto() {
    }

    public SystemInfoDto(String osName, String osArch) {
        this.osName = osName;
        this.osArch = osArch;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsArch() {
        return osArch;
    }

    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }
}
