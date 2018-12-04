package com.duangframework.db.convetor;

/**
 * Created by laotang on 2018/12/4.
 */
public class ConvetorFactory {

    public static ConvetorObject convetor(AbstractConvetorTemplate template) {
        return template.convetor();
    }

}
