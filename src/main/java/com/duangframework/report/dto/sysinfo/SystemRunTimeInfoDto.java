package com.duangframework.report.dto.sysinfo;

/**
 * 系统运行时的状态信息Dto
 *
 * @author Laotang
 * @since 1.0
 * @date 2020-09-18
 */
public class SystemRunTimeInfoDto implements java.io.Serializable {

    /**
     * cpu信息
     */
    private CpuInfoDto cpuInfoDto;
    /**
     * 主机内存信息
     */
    private MemoryInfoDto memoryInfoDto;
    /**
     * 线程信息
     */
    private ThreadInfoDto threadInfoDto;
    /**
     * 操作系统信息
     */
    private SystemInfoDto systemInfoDto;
    /**
     * jvm信息
     */
    private JvmInfoDto jvmInfoDto;

    public SystemRunTimeInfoDto() {
    }

    public SystemRunTimeInfoDto(CpuInfoDto cpuInfoDto, MemoryInfoDto memoryInfoDto, SystemInfoDto systemInfoDto, JvmInfoDto jvmInfoDto) {
        this.cpuInfoDto = cpuInfoDto;
        this.memoryInfoDto = memoryInfoDto;
        this.systemInfoDto = systemInfoDto;
        this.jvmInfoDto = jvmInfoDto;
    }

    public CpuInfoDto getCpuInfoDto() {
        return cpuInfoDto;
    }

    public void setCpuInfoDto(CpuInfoDto cpuInfoDto) {
        this.cpuInfoDto = cpuInfoDto;
    }

    public MemoryInfoDto getMemoryInfoDto() {
        return memoryInfoDto;
    }

    public void setMemoryInfoDto(MemoryInfoDto memoryInfoDto) {
        this.memoryInfoDto = memoryInfoDto;
    }

    public ThreadInfoDto getThreadInfoDto() {
        return threadInfoDto;
    }

    public void setThreadInfoDto(ThreadInfoDto threadInfoDto) {
        this.threadInfoDto = threadInfoDto;
    }

    public SystemInfoDto getSystemInfoDto() {
        return systemInfoDto;
    }

    public void setSystemInfoDto(SystemInfoDto systemInfoDto) {
        this.systemInfoDto = systemInfoDto;
    }

    public JvmInfoDto getJvmInfoDto() {
        return jvmInfoDto;
    }

    public void setJvmInfoDto(JvmInfoDto jvmInfoDto) {
        this.jvmInfoDto = jvmInfoDto;
    }
}
