package com.duangframework.mvc.render;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.duangframework.exception.MvcException;
import com.duangframework.kit.ToolsKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * JSON格式返回处理方法
 * @author laotang
 */
public class JsonRender extends Render {

	private static final long serialVersionUID = -6757577835886443202L;
	private static Logger logger = LoggerFactory.getLogger(JsonRender.class);

	private Set<String> fieldFilterSet ;  
	
	public class CustomFieldPropertyFilter implements PropertyFilter {
			private Set<String> filterSet = new HashSet<String>();
			@Override
		    public boolean apply(Object source, String name, Object value) {  
	            for(Iterator<String> it =  filterSet.iterator(); it.hasNext();){
//	            	System.out.println(source.getClass().getName()); // TODO 可以通过反射，取得对应的类字段，再组装成key与name比较
	                if(it.next().equals(name)){  
	                    return false;  
	                }  
	            }
		        return true;  
		    }  
		    public CustomFieldPropertyFilter(Set<String> filterSet){
		        this.filterSet = filterSet;  
		    }  
		}  
	
	public static Render init(){
		return new JsonRender(null);
	}
	
	public JsonRender(Object obj){
		this.obj = obj;
	}

	/**
	 * 过滤不返回的字段
	 * @param obj		要返回的对象
	 * @param fieldFilterSet	不返回的字段集合
	 */
	public JsonRender(Object obj, Set<String> fieldFilterSet){
		this.obj = obj;
		this.fieldFilterSet = fieldFilterSet;
	}

	@Override
	public void render() {
		if(null == request || null == response) {
			logger.warn("request or response is null");
			return;
		}
		setDefaultValue2Response(JSON_PLAIN);
		try {
			if(ToolsKit.isNotEmpty(fieldFilterSet)){
				  CustomFieldPropertyFilter customFieldPropertyFilter = new CustomFieldPropertyFilter(fieldFilterSet);
				  SerializeWriter serializeWriter = new SerializeWriter();
				  JSONSerializer serializer = new JSONSerializer(serializeWriter);
				  serializer.setDateFormat("yyyy-MM-dd HH:mm:ss");
				  serializer.getPropertyFilters().add(customFieldPropertyFilter);	
				  //https://github.com/alibaba/fastjson/wiki/Class_Level_SerializeFilter
//				  SerializeConfig.getGlobalInstance().addFilter(A.class, customFieldPropertyFilter);
				  serializer.write(obj);
				  response.write(serializeWriter.toString());
				  fieldFilterSet.clear();
			} else {
				response.write(obj);
			}
		} catch (Exception e) {
			logger.warn("returnJson exception:  "+e.getMessage(), e);
			throw new MvcException(e.getMessage(), e);
		}
	}
}
