package com.duangframework.report;

import com.duangframework.mvc.annotation.Controller;
import com.duangframework.mvc.annotation.Import;
import com.duangframework.mvc.annotation.Mapping;
import com.duangframework.mvc.core.BaseController;


/**
 * @author Created by laotang
 * @date createed in 2018/2/6.
 */
@Controller
@Mapping(value = "/duangframework/{flag}/report", desc = "框架信息报告")
public class ReportController extends BaseController {

    @Import
    private ReportService reportService;

    /**
     * 返回所有原始action记录
     */
//    public void actions() {
//        try {
//            returnSuccessJson(reportService.actions());
//        } catch (Exception e) {
//            returnFailJson(e);
//        }
//    }

    /**
     * 返回树型action记录， 以controller mapping value为key,
     */
//    public void treeActions() {
//        try {
//            returnSuccessJson(reportService.treeActions());
//        } catch (Exception e) {
//            returnFailJson(e);
//        }
//    }

    /**
     * 返回系统信息
     */
    public void info() {
        try {
            returnSuccessJson(reportService.info());
        } catch (Exception e) {
            returnFailJson(e);
        }
    }
}
