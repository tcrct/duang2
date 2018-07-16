package com.duangframework.mvc.plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by laotang on 2018/6/12.
 */
public abstract class PluginChain {

    public abstract void addPlugin(List<IPlugin> pluginList) throws Exception;

    public List<IPlugin> getPluginList() throws Exception {
        List<IPlugin> pluginList = new ArrayList();
        addPlugin(pluginList);
        return pluginList;
    }


}
