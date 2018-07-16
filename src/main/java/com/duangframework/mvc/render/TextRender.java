package com.duangframework.mvc.render;

import com.duangframework.exception.MvcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TextRender.
 */
public class TextRender extends Render {

	private static Logger logger = LoggerFactory.getLogger(TextRender.class);
	
	private static final long serialVersionUID = 4775148244778489992L;
	private String text;
	
	public TextRender(String text) {
		this(text, TEXT_PLAIN);
	}
	
	public TextRender(String text, String contentType) {
		this.text = text;
		TEXT_PLAIN = contentType;
	}
	
	@Override
	public void render() {
		if(null == request || null == response){
			logger.warn("request or response is null");
			return;
		}
		setDefaultValue2Response(TEXT_PLAIN);
		try {
	        response.write(text);
		} catch (Exception e) {
			throw new MvcException(e.getMessage(), e);
		}
	}
}




