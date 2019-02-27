package com.duangframework.report;

import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.annotation.Service;
import com.duangframework.mvc.core.helper.RouteHelper;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.mvc.route.Route;
import com.duangframework.report.dto.FrameworkInfoDto;
import com.duangframework.report.dto.MappingDto;
import com.duangframework.server.common.BootStrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * 提供基于duang框架的项目信息报告
 *
 * @author Created by laotang
 * @date createed in 2018/1/31.
 */
@Service
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private static FrameworkInfoDto infoDto;


    private Map<String, Route> getActionMapping() {
        Map<String, Route> actionMapping = new HashMap<>();
        actionMapping.putAll(RouteHelper.getRouteMap());
        actionMapping.putAll(RouteHelper.getRestfulRouteMap());
        return actionMapping;
    }

    private List<String> getAllActionKeys() {
        List<String> allActionKeys = new ArrayList<>(getActionMapping().keySet());
        Collections.sort(allActionKeys);
        return allActionKeys;
    }

    private Map<String, Route> actions() {
        List<String> keyList = getAllActionKeys();
        Map<String, Route> treeMap = new TreeMap<>();
        for (String key : keyList) {
            if(key.contains(ConstEnums.FRAMEWORK_MAPPING_KEY.getValue())) {
                continue;
            }
            treeMap.put(key, getActionMapping().get(key));
        }
        return treeMap;
    }

    private List<MappingDto> treeActions() {
        List<String> keyList = getAllActionKeys();
        Map<String, Route> treeMap = new TreeMap<>();
        Map<String, List<Route>> treeItemMap = new TreeMap<>();
        for (String key : keyList) {
            if(key.contains(ConstEnums.FRAMEWORK_MAPPING_KEY.getValue())) {
                continue;
            }
            Route action = getActionMapping().get(key);
//            ActionInfoDto infoDto = new ActionInfoDto();
//            ReportUtils.conversionDto(action, infoDto);
            String controllerKey = action.getControllerMapperKey();
            if(treeItemMap.containsKey(controllerKey)) {
                treeItemMap.get(controllerKey).add(action);
            } else {
                if(!treeMap.containsKey(controllerKey)) {
                    treeMap.put(controllerKey, action.getControllerRoute());
                }
                List<Route> itemList = new ArrayList<>();
                itemList.add(action);
                treeItemMap.put(controllerKey, itemList);
            }
        }
        Map<String, Map> mapList = new TreeMap();
        List<MappingDto> mappingDtoList = new ArrayList<>(mapList.size());
        for(Iterator<Map.Entry<String, Route>> iterator = treeMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, Route> entry = iterator.next();
            String controllerMappingKey = entry.getKey();
            Route controllerMapping = entry.getValue();
            List<Route> actionsMappingList = treeItemMap.get(controllerMappingKey);
            MappingDto mappingDto = new MappingDto(controllerMappingKey, controllerMapping, actionsMappingList);
            mappingDtoList.add(mappingDto);
        }
//        mapList.put("controller", treeMap);
//        mapList.put("method", treeItemMap);
        return mappingDtoList;
    }

    /**
     *  项目信息报告汇总
     * @return  FrameworkInfoDto
     */
    public FrameworkInfoDto info() {
        if(ToolsKit.isNotEmpty(infoDto)){
            return infoDto;
        }
        infoDto = new FrameworkInfoDto();
        infoDto.setComputerInfo(ComputerInfo.getInstance());
        BootStrap bootStrap = BootStrap.getInstants();
        if(ToolsKit.isNotEmpty(bootStrap)) {
            infoDto.setHost(bootStrap.getHost());
            infoDto.setProt(bootStrap.getPort());
            infoDto.setSsl(bootStrap.isSslEnabled());
        }
        List<MappingDto> mappingDtoList = treeActions();
        if(ToolsKit.isNotEmpty(mappingDtoList)) {
            infoDto.setControllerCount(ToolsKit.isEmpty(mappingDtoList) ? 0 : mappingDtoList.size());
            int actionsCount = 0;
            for(MappingDto dto : mappingDtoList) {
                List<Route> actionsList = dto.getActionsMappingList();
                if(ToolsKit.isEmpty(actionsList)) {
                    continue;
                }
                actionsCount += actionsList.size();
            }
            infoDto.setActionCount(actionsCount);
        }
        infoDto.setAuthor(ConstEnums.FRAMEWORK_OWNER.getValue());
        infoDto.setMappingDtoList(mappingDtoList); // 所有api接口信息
        return infoDto;
    }


    /**
     * 自动生成api文档
     * 遍历出所有的Action，取出每个method的@Mapping注解，再判断是否存在@Param
     * 如果有则进行内容生成
     *    Param注解分自定义类型参数及基础参数，自定义类型的要结合@Vtor的注解一并使用
     */
    public void autoCreateApiDocument() {

    }
}
