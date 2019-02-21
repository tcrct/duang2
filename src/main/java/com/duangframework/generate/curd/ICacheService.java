package com.duangframework.generate.curd;

public interface ICacheService<T> {

    /**
     * 保存 T 泛型对象到Redis缓存
     * @param T  泛型对象，须有id值
     */
    void save(T entity);

    /**
     * 根据ID查找缓存里的泛型对象
     * @param id   泛型对象id字段值
     * @return 查找成功返回泛型对象
     */
    T findById(String id);


    /**
     * 根据ID删除缓存里的泛型对象
     * @param id    泛型对象id字段值
     * @return  删除成功返回true
     */
    boolean deleteById(String id);
}
