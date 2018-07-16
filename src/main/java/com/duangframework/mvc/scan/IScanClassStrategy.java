package com.duangframework.mvc.scan;

import java.util.List;

/**
 * CLASS文件扫描策略类接口
 * Created by laotang on 2018/6/16.
 */
public interface IScanClassStrategy {

    /**
     * 返回class集合<br/>
     * map key:  class的全名，包路径+类名称<br/>
     * map value:  类对象<br/>
     * @return
     */
    List<Class<?>> getList() throws Exception;

}
