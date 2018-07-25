package com.duangframework.server.netty.handler;

import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * @author Created by laotang
 * @date createed in 2018/6/7.
 */
public class ProgressiveFutureListener implements ChannelProgressiveFutureListener {

    private static Logger logger = LoggerFactory.getLogger(ProgressiveFutureListener.class);

    private RandomAccessFile raf;
    private File file;
    //下载完成后，删除文件
    private boolean isDelete;

    public ProgressiveFutureListener(RandomAccessFile raf, File file, boolean isDelete) {
        this.raf = raf;
        this.file = file;
        this.isDelete = isDelete;
    }

    @Override
    public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
        if (total < 0) { // total unknown
            logger.debug("{} Transfer progress: {}", future.channel(), progress);
        } else {
            logger.debug("{} Transfer progress: {}/{}", future.channel(), progress, total);
        }
    }

    /**
     * 下载完成
     * @param future
     */
    @Override
    public void operationComplete(ChannelProgressiveFuture future) {
        try {
            if(future.isSuccess()) {
                raf.close();
                if (isDelete && file.exists()) {
                    file.delete();
                    logger.warn("delete download file [" + file.getAbsolutePath() + "] is success!");
                }
                logger.debug("{} Transfer complete.", future.channel());
            }
        } catch (Exception e) {
            logger.error("RandomAccessFile close error", e);
        }
    }

    public static ProgressiveFutureListener build(RandomAccessFile raf, File file, boolean isDelete) {
        return new ProgressiveFutureListener(raf, file, isDelete);
    }

}
