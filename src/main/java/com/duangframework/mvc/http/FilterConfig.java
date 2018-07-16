package com.duangframework.mvc.http;

import java.util.Enumeration;

/**
 * @author Created by laotang
 * @date createed in 2018/6/12.
 */
public interface FilterConfig {

    String getFilterName();

//    ServletContext getServletContext();

    String getInitParameter(String key);

    Enumeration<String> getInitParameterNames();
}
