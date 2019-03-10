package com.duangframework.generate.curd;

import com.duangframework.db.convetor.KvItem;
import com.duangframework.mvc.dto.PageDto;
import com.duangframework.mvc.dto.SearchListDto;

import java.util.List;

/**
 * Service层公共接口
 * Created by laotang on 2019/3/2.
 */
public interface IService<T> {

    /**保存泛型对象*/
    T save(T entity);

    /**根据id查找泛型对象记录*/
    T findById(String id);

    /**根据条件查找泛型对象记录*/
    T findByKey(List<String> fieldList, KvItem... kvItems);

    /** 根据ID删除泛型对象记录*/
    boolean deleteById(String id);

    /**根据搜索对象搜索符合条件的泛型对象记录*/
    PageDto<T> search(SearchListDto searchListDto);

    /**根据条件查找所有泛型对象记录*/
    List<T> findAllByKey(List<String> fieldList, KvItem... kvItems);
}
