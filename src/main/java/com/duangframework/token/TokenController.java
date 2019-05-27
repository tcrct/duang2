package com.duangframework.token;

import com.duangframework.db.enums.LevelEnums;
import com.duangframework.mvc.annotation.Controller;
import com.duangframework.mvc.annotation.Mapping;
import com.duangframework.mvc.core.BaseController;
import com.duangframework.server.common.BootStrap;

import java.util.ArrayList;
import java.util.List;

/**
 *  表单令牌验证
 *  使用方法：
 *  1，Duang里开启tokenHtml()
 *  2，每次打开表单页时，先请求TokenController里的create方法，取得令牌隐藏域html，填充到表单页
 *  3，每次提交时，需要的request header头里设置：
 *      header["htmlToken"] = "duang-htmlToken"
 *  4， 可以在header["htmlTokenId"]或请求参数里设置htmlTokenId = 隐藏域里的值
 */
@Controller
@Mapping(value="/{flag}/duangframework/token",desc = "令牌管理", level = LevelEnums.DIR)
public class TokenController extends BaseController {

    public String create() {
        boolean isTokenHtml = BootStrap.getInstants().isTokenHtml();
        if(!isTokenHtml) {
            return "";
        }
        return TokenManager.createToken(TokenManager.TOKEN_KEY_FIELD);
    }

    // 验证在DuangHeadHandle.java
//    public boolean validate() {
//        return TokenManager.validateToken(this.getRequest(), TOKEN_HTML_FIELD);
//    }
}
