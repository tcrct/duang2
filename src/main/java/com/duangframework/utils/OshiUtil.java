package com.duangframework.utils;

import com.duangframework.report.dto.sysinfo.*;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;

import java.text.DecimalFormat;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class OshiUtil {

    public static SystemRunTimeInfoDto main() {
        try {
            return new SystemRunTimeInfoDto(
                    OshiUtil.getCpuInfo(),
                    OshiUtil.getMemInfo(),
                    OshiUtil.setSysInfo(),
                    OshiUtil.setJvmInfo());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static CpuInfoDto getCpuInfo() throws InterruptedException {
        CpuInfoDto cpuInfoDto = new CpuInfoDto();
        //System.out.println("----------------cpu信息----------------");
        SystemInfo systemInfo = new SystemInfo();
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // 睡眠1s
        TimeUnit.SECONDS.sleep(1);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        int coreNum = processor.getLogicalProcessorCount();
        Double sysUsage = Double.parseDouble(new DecimalFormat("#.##").format(cSys * 1.0 / totalCpu));
        Double userUsage = Double.parseDouble(new DecimalFormat("#.##").format(user * 1.0 / totalCpu));
        Double waitUsage = Double.parseDouble(new DecimalFormat("#.##").format(iowait * 1.0 / totalCpu));
        Double currentUsage = Double.parseDouble(new DecimalFormat("#.##").format(1.0-(idle * 1.0 / totalCpu)));

        System.out.println("----------------cpu信息----------------");
        System.out.println("cpu核数:" + coreNum);
        System.out.println("cpu系统使用率:" + sysUsage+"%");
        System.out.println("cpu用户使用率:" + userUsage+"%");
        System.out.println("cpu当前等待率:" + waitUsage+"%");
        System.out.println("cpu当前使用率:" + currentUsage+"%");

        cpuInfoDto.setCoreNum(coreNum);
        cpuInfoDto.setSysUsage(sysUsage);
        cpuInfoDto.setUserUsage(userUsage);
        cpuInfoDto.setWaitUsage(waitUsage);
        cpuInfoDto.setCurrentUsage(currentUsage);
        return cpuInfoDto;
    }

    public static MemoryInfoDto getMemInfo(){
        MemoryInfoDto memoryInfoDto = new MemoryInfoDto();
        System.out.println("----------------主机内存信息----------------");
        SystemInfo systemInfo = new SystemInfo();
        GlobalMemory memory = systemInfo.getHardware().getMemory();
        //总内存
        long totalByte = memory.getTotal();
        //剩余
        long acaliableByte = memory.getAvailable();
        double memoryUsage =  Double.parseDouble(new DecimalFormat("#.##").format((totalByte-acaliableByte)*1.0/totalByte));
        System.out.println("总内存 = " + formatByte(totalByte));
        System.out.println("使用" + formatByte(totalByte-acaliableByte));
        System.out.println("剩余内存 = " + formatByte(acaliableByte));
        System.out.println("使用率：" + memoryUsage+"%");

        memoryInfoDto.setTotalMenory(totalByte);
        memoryInfoDto.setUsedMenory(totalByte-acaliableByte);
        memoryInfoDto.setFreeMemory(acaliableByte);
        memoryInfoDto.setMemoryUsage(memoryUsage);
        return memoryInfoDto;
    }

    public static SystemInfoDto setSysInfo(){
        SystemInfoDto systemInfoDto = new SystemInfoDto();
        System.out.println("----------------操作系统信息----------------");
        Properties props = System.getProperties();
        //系统名称
        String osName = props.getProperty("os.name");
        //架构名称
        String osArch = props.getProperty("os.arch");
        System.out.println("操作系统名 = " + osName);
        System.out.println("系统架构 = " + osArch);
        systemInfoDto.setOsName(osName);
        systemInfoDto.setOsArch(osArch);
        return systemInfoDto;
    }

    public static JvmInfoDto setJvmInfo(){
        JvmInfoDto jvmInfoDto = new JvmInfoDto();
        System.out.println("----------------jvm信息----------------");
        Properties props = System.getProperties();
        Runtime runtime = Runtime.getRuntime();
        //jvm总内存
        long jvmTotalMemoryByte = runtime.totalMemory();
        //jvm最大可申请
        long jvmMaxMoryByte = runtime.maxMemory();
        //空闲空间
        long freeMemoryByte = runtime.freeMemory();
        //jdk版本
        String jdkVersion = props.getProperty("java.version");
        //jdk路径
        String jdkHome = props.getProperty("java.home");

        double memoryUsage = (jvmTotalMemoryByte-freeMemoryByte)*1.0/jvmTotalMemoryByte;
        memoryUsage = Double.parseDouble(new DecimalFormat("#.##").format(memoryUsage));

        System.out.println("jvm内存总量 = " + formatByte(jvmTotalMemoryByte));
        System.out.println("jvm已使用内存 = " + formatByte(jvmTotalMemoryByte-freeMemoryByte));
        System.out.println("jvm剩余内存 = " + formatByte(freeMemoryByte));
        System.out.println("jvm内存使用率 = " + memoryUsage);
        System.out.println("java版本 = " + jdkVersion);
        //System.out.println("jdkHome = " + jdkHome);

        jvmInfoDto.setTotalMemory(jvmTotalMemoryByte);
        jvmInfoDto.setUsedMemory(jvmTotalMemoryByte-freeMemoryByte);
        jvmInfoDto.setFreeMemory(freeMemoryByte);
        jvmInfoDto.setMemoryUsage(memoryUsage);
        return jvmInfoDto;
    }

    public static void getThread(){
        System.out.println("----------------线程信息----------------");
        ThreadGroup currentGroup =Thread.currentThread().getThreadGroup();

        while (currentGroup.getParent()!=null){
            // 返回此线程组的父线程组
            currentGroup=currentGroup.getParent();
        }
        //此线程组中活动线程的估计数
        int noThreads = currentGroup.activeCount();

        Thread[] lstThreads = new Thread[noThreads];
        //把对此线程组中的所有活动子组的引用复制到指定数组中。
        currentGroup.enumerate(lstThreads);
        for (Thread thread : lstThreads) {
            System.out.println("线程数量："+noThreads+" 线程id：" + thread.getId() + " 线程名称：" + thread.getName() + " 线程状态：" + thread.getState());
        }
    }

    public static String formatByte(long byteNumber){
        //换算单位
        double FORMAT = 1024.0;
        double kbNumber = byteNumber/FORMAT;
        if(kbNumber<FORMAT){
            return new DecimalFormat("#.##KB").format(kbNumber);
        }
        double mbNumber = kbNumber/FORMAT;
        if(mbNumber<FORMAT){
            return new DecimalFormat("#.##MB").format(mbNumber);
        }
        double gbNumber = mbNumber/FORMAT;
        if(gbNumber<FORMAT){
            return new DecimalFormat("#.##GB").format(gbNumber);
        }
        double tbNumber = gbNumber/FORMAT;
        return new DecimalFormat("#.##TB").format(tbNumber);
    }
}
