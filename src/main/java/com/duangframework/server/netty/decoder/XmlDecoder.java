package com.duangframework.server.netty.decoder;

import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpConstants;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *POST请求，内容格式为XML的解码类
 * @author Created by laotang
 * @date createed in 2018/1/6.
 */
public class XmlDecoder extends AbstractDecoder<Map<String, Object>> {

    public XmlDecoder(FullHttpRequest request) {
        super(request);
    }

    @Override
    public Map<String, Object> decoder() throws Exception {
        String xml = request.content().toString(HttpConstants.DEFAULT_CHARSET);
        if (ToolsKit.isNotEmpty(xml)){
            requestParamsMap.putAll(getMapFromXML(xml));
            requestParamsMap.put(ConstEnums.INPUTSTREAM_STR_NAME.getValue(), Collections.singletonList(xml));
        }
        return requestParamsMap;
    }


    /**
     * xml数据转化成参数map集合
     * @param xmlString xml数据
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public Map<String, List<String>> getMapFromXML(String xmlString) throws ParserConfigurationException, IOException, SAXException {
        Map<String, List<String>> map = new HashMap<>();
        InputStream is = null;
        try {
            // 这里用Dom的方式解析回包的最主要目的是防止API新增回包字段
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            is = getStringStream(xmlString);
            Document document = builder.parse(is);
            // 获取到document里面的全部结点
            NodeList allNodes = document.getFirstChild().getChildNodes();
            int i = 0;
            while (i < allNodes.getLength()) {
                Node node = allNodes.item(i);
                if (node instanceof Element) {
                    String content = node.getTextContent();
                    if(ToolsKit.isNotEmpty(content)) {
                        map.put(node.getNodeName(), Collections.singletonList(content));
                    }
                }
                i++;
            }
        } catch (DOMException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return map;
    }

    private InputStream getStringStream(String sInputString) {
        ByteArrayInputStream tInputStringStream = null;
        if (sInputString != null && !"".equals(sInputString.trim())) {
            tInputStringStream = new ByteArrayInputStream(sInputString.getBytes());
        }
        return tInputStringStream;
    }
}
