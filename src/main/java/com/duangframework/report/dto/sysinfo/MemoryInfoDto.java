package com.duangframework.report.dto.sysinfo;
/**
 * 内存 信息Dto对象
 *
 * @author Laotang
 * @since 1.0
 */
public class MemoryInfoDto implements java.io.Serializable {

    /**总内存,单位(byte)**/
    private Long totalMenory;
    /**已使用的内存,单位(byte)*/
    private Long usedMenory;
    /**剩余内存,单位(byte)*/
    private Long freeMemory;
    /**内存使用率*/
    private Double memoryUsage;

    public MemoryInfoDto() {
    }

    public MemoryInfoDto(Long totalMenory, Long usedMenory, Long freeMemory, Double memoryUsage) {
        this.totalMenory = totalMenory;
        this.usedMenory = usedMenory;
        this.freeMemory = freeMemory;
        this.memoryUsage = memoryUsage;
    }

    public Long getTotalMenory() {
        return totalMenory;
    }

    public void setTotalMenory(Long totalMenory) {
        this.totalMenory = totalMenory;
    }

    public Long getUsedMenory() {
        return usedMenory;
    }

    public void setUsedMenory(Long usedMenory) {
        this.usedMenory = usedMenory;
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
