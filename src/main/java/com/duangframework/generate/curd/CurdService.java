package com.duangframework.generate.curd;

import com.duangframework.db.IdEntity;
import com.duangframework.db.common.Field;
import com.duangframework.db.common.Query;
import com.duangframework.db.common.Update;
import com.duangframework.db.convetor.KvItem;
import com.duangframework.db.mongodb.common.MongoDao;
import com.duangframework.db.mysql.common.Operator;
import com.duangframework.exception.ServiceException;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.dto.PageDto;
import com.duangframework.mvc.dto.SearchListDto;
import com.duangframework.utils.DuangId;
import com.duangframework.vtor.annotation.VtorKit;

import java.util.List;

/**
 * CURD 操作公用类
 * @param <T> 泛型对象
 */
public abstract  class CurdService<T> implements IService<T> {

    /**
     * 保存泛型对象
     * @param  vo 泛型对象
     *@param  mongoDao Dao对象
     *@param  cacheService Cache对象
     * @since 1.0
     * @return  保存成功返回泛型对象， 否则抛出异常
     */
    public T save(T vo, MongoDao<T> mongoDao, ICacheService cacheService) {
        boolean isUpdate = false;
        try {
            VtorKit.validate(vo);
            String id = "";
            if(ToolsKit.isNotEmpty(vo) && vo instanceof IdEntity) {
                id = ((IdEntity)vo).getId();
                if(ToolsKit.isEmpty(id)){
                    ToolsKit.addEntityData(vo);
                } else {
                    isUpdate = true;
                    ToolsKit.updateEntityData(vo);
                }
            }
            T entity = mongoDao.save(vo);
            if(isUpdate && ToolsKit.isNotEmpty(id)) {
                Query query = new Query();
                query.eq(IdEntity.ID_FIELD, id);
                entity = mongoDao.findOne(query);
            }
            cacheService.save(entity);
            return entity;
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    /**
     * 根据id查找泛型对象记录
     * @param id    泛型对象id字段值
     *@param  mongoDao Dao对象
     *@param  cacheService Cache对象
     * @return 查找成功返回泛型对象, 否则抛出异常
     */
    public T findById(String id, MongoDao<T> mongoDao, ICacheService cacheService) {
        if(!ToolsKit.isValidDuangId(id)) {
            throw new ServiceException("findById for ${entityName} is fail: id is not DuangId");
        }
        try {
            T entity = (T)cacheService.findById(id);
            if(ToolsKit.isNotEmpty(entity)) {
                return entity;
            }
            Query<T> query = new Query<>();
            query.eq(IdEntity.ID_FIELD, new DuangId(id));
            T obj =  (T)mongoDao.findOne(query);
            cacheService.save(obj);
            return obj;
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    /**
     * 根据id查找泛型对象记录
     *@param  mongoDao Dao对象
     *@param  cacheService Cache对象
     @param  kvItems kvItem数据对象

     * @return 查找成功返回泛型对象, 否则抛出异常
     */
    public T findByKey(MongoDao<T> mongoDao, ICacheService cacheService, KvItem... kvItems) {
        try {
            return findByKey(mongoDao, cacheService, null, kvItems);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
    /**
     * 根据条件查找泛型对象记录
     *@param  mongoDao Dao对象
     *@param  cacheService Cache对象
     *@param  fieldList   返回字段
     *@param  kvItems kvItem数据对象
     *
     * @since 1.0
     * @return 查找成功返回泛型对象, 否则抛出异常
     */
    public T findByKey(MongoDao<T> mongoDao, ICacheService cacheService, List<String> fieldList, KvItem... kvItems) {
        if(ToolsKit.isEmpty(kvItems)) {
            throw new ServiceException("findByKey for ${entityName} is fail: kvItems is null");
        }
        try {
            return (T)mongoDao.findOne(createQuery(fieldList, kvItems));
        } catch (Exception e) {
            throw new ServiceException("findByKey for ${entityName} is fail: "+ e.getMessage(), e);
        }
    }

    /**
     *  根据ID删除泛型对象记录
     * @param id    泛型对象id字段值
     *@param  mongoDao Dao对象
     *@param  cacheService Cache对象
     * @return  删除成功返回true, 否则抛出异常
     */
    protected boolean deleteById(String id, MongoDao<T> mongoDao, ICacheService cacheService) {
        if(!ToolsKit.isValidDuangId(id)) {
            throw new ServiceException("deleteById for User is fail: id is not DuangId");
        }
        try {
            Query<T> query = new Query<>();
            query.eq(IdEntity.ID_FIELD, new DuangId(id));
            Update update = new Update();
            update.set(IdEntity.STATUS_FIELD, IdEntity.STATUS_FIELD_DELETE);
            mongoDao.update(query, update);
            cacheService.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }


    /**
     * 根据搜索对象搜索符合条件的泛型对象记录
     * @param searchListDto     搜索对象
     *@param  mongoDao Dao对象
     *@param  cacheService Cache对象
     * @return
     */
    public PageDto<T> search(SearchListDto searchListDto, MongoDao<T> mongoDao, ICacheService cacheService) {
        if(ToolsKit.isEmpty(searchListDto)) {
            throw new ServiceException("searchListDto is null");
        }
        try {
            return mongoDao.findPage(ToolsKit.searchDto2Query(searchListDto));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage(), e);
        }
    }

    /**
     * 根据条件查找所有泛型对象记录
     *@param  mongoDao Dao对象
     *@param  cacheService Cache对象
     *@param  fieldList   返回字段
     *@param  kvItems kvItem数据对象
     *
     * @since 1.0
     * @return 查找成功返回泛型对象列表集合, 否则抛出异常
     */
    public List<T> findAllByKey(MongoDao<T> mongoDao, ICacheService cacheService, List<String> fieldList, KvItem... kvItems) {
        if(ToolsKit.isEmpty(kvItems)) {
            throw new ServiceException("findAllByKey for ${entityName} is fail: kvItems is null");
        }
        try {
            return mongoDao.findList(createQuery(fieldList, kvItems));
        } catch (Exception e) {
            throw new ServiceException("findAllByKey for ${entityName} is fail: "+ e.getMessage(), e);
        }
    }

    /**
     * 创建查询对象
     * @param fieldList     查询返回字段
     * @param kvItems     查询条件
     * @return
     */
    private Query createQuery(List<String> fieldList, KvItem... kvItems) {
        Query query = new Query();
        if(ToolsKit.isNotEmpty(fieldList)) {
            query.fields(new Field(fieldList));
        }
        for(KvItem kvItem : kvItems) {
            if(Operator.EQ.equals(kvItem.getOperator())) {
                query.eq(kvItem.getKey(), kvItem.getValue());
            }
            else if(Operator.LIKE.equals(kvItem.getOperator())) {
                query.like(kvItem.getKey(), kvItem.getValue());
            }
            else if(Operator.GTE.equals(kvItem.getOperator())) {
                query.gte(kvItem.getKey(), kvItem.getValue());
            }
            else if(Operator.LTE.equals(kvItem.getOperator())) {
                query.lte(kvItem.getKey(), kvItem.getValue());
            }
            else if(Operator.GT.equals(kvItem.getOperator())) {
                query.gt(kvItem.getKey(), kvItem.getValue());
            }
            else if(Operator.LT.equals(kvItem.getOperator())) {
                query.lt(kvItem.getKey(), kvItem.getValue());
            }
        }
        return query;
    }

}
