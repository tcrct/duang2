package com.duangframework.mvc.render;


import com.duangframework.exception.MvcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * XmlRender.
 */
public class XmlRender extends Render {
	
	private static final long serialVersionUID = 4775148244778489992L;
	private static Logger logger = LoggerFactory.getLogger(XmlRender.class);
	private String xml;
	
	public XmlRender(String xml) {
		this.xml = xml;
	}
	
	public XmlRender(String text, String contentType) {
		this.xml = text;
		XML_PLAIN = contentType;
	}

	@Override
	public void render() {
		if(null == request || null == response) {
			logger.warn("request or response is null");
			return;
		}
		setDefaultValue2Response(XML_PLAIN);
		try {
			response.write(xml);
		} catch (Exception e) {
			throw new MvcException(e.getMessage(), e);
		}
	}
}




