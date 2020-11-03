package com.duangframework.mvc.core.helper;

import com.duangframework.mvc.plugin.IPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 插件链辅助类
 *
 * @author Created by laotang
 * @date createed in 2018/6/21.
 */
public class PluginHelper {

    private static final Logger logger = LoggerFactory.getLogger(PluginHelper.class);

    /**
     * 插件集合
     */
    private static final List<IPlugin> plugins = new ArrayList<>();

    public static List<IPlugin> getPlugins() {
        return plugins;
    }

    public static void setPluginList(List<IPlugin> pluginList) {
        plugins.addAll(pluginList);
    }

    public static void setPlugin(IPlugin plugin) {
        plugins.add(plugin);
    }

    /**
     * 加载插件
     *
     * @throws Exception
     */
    public static void start() {
        for (Iterator<IPlugin> it = getPlugins().iterator(); it.hasNext(); ) {
            IPlugin plugin = it.next();
            if (null != plugin) {
                try {
                    plugin.start();
                    logger.warn(plugin.getClass().getName() + " start success...");
                } catch (Exception ex) {
                    logger.warn(plugin.getClass().getName() + " start fail: " + ex.getMessage(), ex);
                }
            }
        }
    }


    /**
     * 停止插件
     */
    public static void stop() {
        if (plugins.isEmpty()) {
            return;
        }
        for (int i = plugins.size() - 1; i >= 0; i--) {
            try {
                plugins.get(i).stop();
                logger.warn(plugins.get(i).getClass().getName() + " stop success...");
            } catch (Exception ex) {
                logger.warn(plugins.get(i).getClass().getName() + " stop fail: " + ex.getMessage(), ex);
            }
        }
        plugins.clear();
    }

}
