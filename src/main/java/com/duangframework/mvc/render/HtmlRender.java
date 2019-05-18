package com.duangframework.mvc.render;


import com.duangframework.exception.MvcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * HtmlRender.
 * @author laotang
 */
public class HtmlRender extends Render {

	private static final long serialVersionUID = 4775148244778489993L;
	private static Logger logger = LoggerFactory.getLogger(HtmlRender.class);
	private String html;

	public HtmlRender(String html) {
		this.html = html;
	}

	public HtmlRender(String text, String contentType) {
		this.html = text;
		HTML_PLAIN = contentType;
	}

	@Override
	public void render() {
		if(null == request || null == response) {
			logger.warn("request or response is null");
			return;
		}
		setDefaultValue2Response(HTML_PLAIN);
		try {
			response.write(html);
		} catch (Exception e) {
			throw new MvcException(e.getMessage(), e);
		}
	}
}




