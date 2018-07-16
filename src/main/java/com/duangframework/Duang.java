package com.duangframework;

import com.duangframework.db.mongodb.client.MongoClientAdapter;
import com.duangframework.db.mongodb.plugin.MongodbPlugin;
import com.duangframework.mvc.plugin.IPlugin;
import com.duangframework.mvc.plugin.PluginChain;
import com.duangframework.server.Application;

import java.util.List;

/**
 * Duang Duang Duang
 * @author laotang
 */
public final class Duang {

    public static void main( String[] args ) {

        Application.duang().port(9090)
                .plugins(new PluginChain() {
                    @Override
                    public void addPlugin(List<IPlugin> pluginList) throws Exception {
                        pluginList.add(new MongodbPlugin(new MongoClientAdapter.Builder()
                                .host("192.168.0.39")
                                .port(27017)
                                .database("test")
                                .build()
                        ));
                    }
                }).run();
    }
}
