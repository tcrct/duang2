package com.duangframework.report.dto.sysinfo;
/**
 * JVM 信息Dto对象
 *
 * @author Laotang
 * @since 1.0
 */
public class JvmInfoDto implements java.io.Serializable {

    /**jvm内存总量,单位(byte)*/
    private Long totalMemory;
    /**jvm已使用内存,单位(byte)*/
    private Long usedMemory;
    /**jvm剩余内存,单位(byte)*/
    private Long freeMemory;
    /**jvm内存使用率*/
    private Double memoryUsage;

    public JvmInfoDto() {
    }

    public JvmInfoDto(Long totalMemory, Long usedMemory, Long freeMemory, Double memoryUsage) {
        this.totalMemory = totalMemory;
        this.usedMemory = usedMemory;
        this.freeMemory = freeMemory;
        this.memoryUsage = memoryUsage;
    }

    public Long getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(Long totalMemory) {
        this.totalMemory = totalMemory;
    }

    public Long getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(Long usedMemory) {
        this.usedMemory = usedMemory;
    }

    public Long getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(Long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public Double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(Double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }
}
