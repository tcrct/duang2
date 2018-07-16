package com.duangframework.cache.client.redis;

import com.alibaba.fastjson.TypeReference;
import com.duangframework.cache.CacheModelOptions;
import com.duangframework.cache.SerializableUtils;
import com.duangframework.cache.client.AbstractCacheClient;
import com.duangframework.cache.ds.RedisAdapter;
import com.duangframework.kit.ThreadPoolKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.params.geo.GeoRadiusParam;
import redis.clients.util.SafeEncoder;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author Created by laotang
 * @date createed in 2018/7/5.
 */
public class RedisClient extends AbstractCacheClient<Jedis> {

    private static final Logger logger = LoggerFactory.getLogger(RedisClient.class);

    private JedisPool jedisPool = null;
    private RedisAdapter redisAdapter;

    public RedisClient(RedisAdapter adapter) {
        this.redisAdapter = adapter;
    }

    @Override
    public  String getId() {
        return redisAdapter.getId();
    }

    @Override
    public Jedis getClient()  {
        try {
            if(null == jedisPool) {
                jedisPool = redisAdapter.getSource();
            }
            return jedisPool.getResource();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void close() throws Exception {
        jedisPool.close();
    }

    public <T> T call(JedisAction action) {
        T result = null;
        Jedis jedis = null;
        try {
            jedis = getClient();
            result = (T) action.execute(jedis);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        finally {
            if(null != jedis) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * 根据key取值
     * @param options                        CacheModelOptions对象
     * @return
     */
    public <T> T get(final CacheModelOptions options, final Class<T> typeReference) {
        return call(new JedisAction<T>(){
            @Override
            public T execute(Jedis jedis) {
                byte[] bytes = jedis.get(SafeEncoder.encode(options.getKey()));
                if(ToolsKit.isNotEmpty(bytes)){
                    try {
                        String str = new String(bytes, ConstEnums.DEFAULT_ENCODING.getValue());
                        if(typeReference.equals(String.class)){
                            return (T)str;
                        } else if(typeReference.equals(Integer.class) || typeReference.equals(int.class)){
                            return (T)new Integer(str);
                        } else if(typeReference.equals(Long.class) || typeReference.equals(long.class)){
                            return (T)new Long(str);
                        } else if(typeReference.equals(Double.class) || typeReference.equals(double.class)){
                            return (T)new Double(str);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new JedisException(e.getMessage());
                    }
                    return (T) SerializableUtils.deserialize(bytes, typeReference);
                }
                return null;
            }
        });
    }

    /**
     *  取值
     * @param options                        CacheModelOptions对象
     * @param type		泛型
     * @return
     */
    public <T> T get(final CacheModelOptions options, final TypeReference<T> type){
        return call(new JedisAction<T>(){
            @Override
            public T execute(Jedis jedis) {
                byte[] bytes = jedis.get(SafeEncoder.encode(options.getKey()));
                if(ToolsKit.isNotEmpty(bytes)){
                    return (T)SerializableUtils.deserialize(bytes, type);
                }
                return null;
            }
        });
    }


    public <T> List<T> getArray(final CacheModelOptions options, final Class<T> typeReference){
        return call(new JedisAction<List<T>>(){
            @Override
            public List<T> execute(Jedis jedis) {
                byte[] bytes = jedis.get(SafeEncoder.encode(options.getKey()));
                if(ToolsKit.isNotEmpty(bytes)){
                    try {
                        return (List<T>)SerializableUtils.deserializeArray(bytes, typeReference);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new JedisException(e.getMessage());
                    }
                }
                return null;
            }
        });
    }

    /**
     * 按key-value方式将值保存到redis
     * @param options                        CacheModelOptions对象
     * @param value
     * @return
     */
//    private boolean set(final CacheModelOptions options, final Object value){
//        if(null == value){
//            logger.warn("JedisUtils.set value is null, return false...");
//            return false;
//        }
//        return call(new JedisAction<Boolean>(){
//            @Override
//            public Boolean execute(Jedis jedis) {
//                String result = "";
//                if(value instanceof String){
//                    result = jedis.set(options.getKey(), (String) value);
//                }else{
//                    result = jedis.set(SafeEncoder.encode(options.getKey()), SerializableUtils.serialize(value));
//                }
//                return "OK".equalsIgnoreCase(result);
//            }
//        });
//    }

    /**
     * 按key-value方式将值保存到redis, 缓存时间为seconds, 过期后会自动将该key指向的value删除
     * @param options			CacheModelOptions对象
     * @param value			值
     * @return
     */
    public boolean set(final CacheModelOptions options, final Object value){
        return call(new JedisAction<Boolean>(){
            @Override
            public Boolean execute(Jedis jedis) {
                String result = "";
                if(value instanceof String){
                    result = jedis.setex(options.getKey(), options.getKeyTTL(),  (String) value);
                }else{
                    result = jedis.setex(SafeEncoder.encode(options.getKey()), options.getKeyTTL(), SerializableUtils.serialize(value));
                }
                boolean isOk =  "OK".equalsIgnoreCase(result);
                if(isOk) {
                    expire(options);
                }
                return isOk;
            }
        });
    }


    /**
     * 根据key删除指定的内容
     * @param keys
     * @return
     */
    public Long del(final String... keys){
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.del(keys);
            }
        });
    }


    /**
     * 将内容添加到list里的第一位
     * @param options                        CacheModelOptions对象
     * @param value		内容
     * @return
     */
    public Long lpush(final CacheModelOptions options, final Object value) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                if(value instanceof String){
                    return jedis.lpush(options.getKey(), (String)value);
                }else{
                    return jedis.lpush(SafeEncoder.encode(options.getKey()), SerializableUtils.serialize(value));
                }
            }
        });
    }



    /**
     * 将内容添加到list里的最后一位
     * @param options                        CacheModelOptions对象
     * @param value		内容
     * @return
     */
    public Long rpush(final CacheModelOptions options, final Object value) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                if(value instanceof String){
                    return  jedis.rpush(options.getKey(), (String)value);
                }else{
                    return jedis.rpush(SafeEncoder.encode(options.getKey()), SerializableUtils.serialize(value));
                }
            }
        });
    }

    /**
     * 根据key取出list集合
     * @param options                        CacheModelOptions对象			关键字
     * @param start			开始位置(0表示第一个元素)
     * @param end			结束位置(-1表示最后一个元素)
     * @return
     */
    public List<String> lrange(final CacheModelOptions options, final int start, final int end) {
        return call(new JedisAction<List<String>>(){
            @Override
            public List<String> execute(Jedis jedis) {
                return jedis.lrange(options.getKey(), start, end);
            }
        });
    }

    public long lrem(final CacheModelOptions options, final int count, final Object value){
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                if(value instanceof String){
                    return jedis.lrem(options.getKey(), count, (String)value);
                }else{
                    return  jedis.lrem(SafeEncoder.encode(options.getKey()), count, SerializableUtils.serialize(value));
                }
            }
        });
    }

    /**
     * 向名称为key的hash中添加元素(map)
     * @param options                        CacheModelOptions对象
     * @param values		map<String,?>
     * @return
     */
    public Boolean hmset(final CacheModelOptions options, final Map<String, String> values) {
        return call(new JedisAction<Boolean>() {
            @Override
            public Boolean execute(Jedis jedis) {
                String isok = "";
                if(null != values){
                    Map<byte[], byte[]> map = new HashMap<byte[], byte[]>(values.size());
                    for (Iterator<Map.Entry<String,String>> it = values.entrySet().iterator(); it.hasNext(); ){
                        Map.Entry<String,String> entry = it.next();
                        map.put(SafeEncoder.encode(entry.getKey()), SafeEncoder.encode(entry.getValue()));
                    }
                    isok = jedis.hmset(SafeEncoder.encode(options.getKey()), map);
                    boolean isOk = "OK".equalsIgnoreCase(isok);
                    if(isOk) {
                        expire(options);
                    }
                }
                return false;
            }
        });
    }

    /**
     * 根据key设置过期时间
     * @param options                        CacheModelOptions对象
     * @return
     *  1 如果成功设置过期时间。
    0  如果key不存在或者不能设置过期时间。
     */
    public Long expire(final CacheModelOptions options) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                if(options.getKeyTTL() > 0) {
                    return jedis.expire(options.getKey(), options.getKeyTTL());
                }
                return 0L;
            }
        });
    }

    /**
     * 返回名称为key在hash中fields对应的value
     * @param options                        CacheModelOptions对象
     * @param fields	hash中的field
     * @return
     */
    public List<String> hmget(final CacheModelOptions options, final String... fields) {
        return call(new JedisAction<List<String>>() {
            @Override
            public List<String> execute(Jedis jedis) {
                return jedis.hmget(options.getKey(), fields);
            }
        });
    }

    /**
     * 返回名称为key的hash中fields对应的value
     * @param options                        CacheModelOptions对象
     * @param fields	hash中的field
     * @return
     */
    public Map<String,String> hmgetToMap(final CacheModelOptions options, final String... fields) {
        return call(new JedisAction<Map<String,String>>() {
            @Override
            public Map<String, String> execute(Jedis jedis) {
                List<String> byteList = jedis.hmget(options.getKey(), fields);
                int size  = byteList.size();
                Map<String,String> map = new HashMap<>(size+1);
                for (int i = 0; i < size; i ++) {
                    if(ToolsKit.isNotEmpty(byteList.get(i))){
                        map.put(fields[i], byteList.get(i));
                    }
                }
                return map;
            }
        });
    }

    /**
     * 删除指定hash里的field
     * @param options                        CacheModelOptions对象
     * @param fields
     * @return
     */
    public Long hdel(final CacheModelOptions options, final String... fields) {
        return call(new JedisAction<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                byte[][] byteFields = new byte[fields.length][];
                for (int i = 0; i < fields.length; i++) {
                    byteFields[i] = SafeEncoder.encode(fields[i]);
                }
                return jedis.hdel(SafeEncoder.encode(options.getKey()), byteFields);
            }
        });
    }

    /**
     * 取出指定hash里的所有field
     * @param options                        CacheModelOptions对象
     * @return
     */
    public Set<String> hkeys(final CacheModelOptions options) {
        return call(new JedisAction<Set<String>>() {
            @Override
            public Set<String> execute(Jedis jedis) {
                return jedis.hkeys(options.getKey());
            }
        });
    }

    /**
     * 判断hashmap里面是否存在field的key
     * @param options                        CacheModelOptions对象
     * @param field
     * @return
     */
    public Boolean hexists(final CacheModelOptions options, final String field) {
        return call(new JedisAction<Boolean>() {
            @Override
            public Boolean execute(Jedis jedis) {
                if (null != field) {
                    return jedis.hexists(options.getKey(),  field);
                }
                return false;
            }
        });
    }

    /**
     * 返回名称为key的hash中fields对应的value
     * @param options                        CacheModelOptions对象       关键字
     * @param field    hash中的field
     * @return
     */
    public String hget(final CacheModelOptions options, final String field) {
        return call(new JedisAction<String>() {
            @Override
            public String execute(Jedis jedis) {
                return jedis.hget(options.getKey(),  field);
            }
        });
    }

    /**
     * key返回哈希表key中，所有的域和值
     * @param options                        CacheModelOptions对象
     * @return
     */
    public Map<String,String> hgetAll(final CacheModelOptions options) {
        return call(new JedisAction<Map<String,String>>() {
            @Override
            public Map<String,String> execute(Jedis jedis) {
                return jedis.hgetAll(options.getKey());
            }
        });
    }

    /**
     * 向有序set里添加元素
     * @param options                        CacheModelOptions对象		set的key
     * @param value		对应的value
     * @return
     */
    public Boolean sadd(final CacheModelOptions options, final Object value) {
        return call(new JedisAction<Boolean>(){
            @Override
            public Boolean execute(Jedis jedis) {
                long isok = 0;
                if(value instanceof String){
                    isok = jedis.sadd(options.getKey(), (String)value);
                }else{
                    isok =jedis.sadd(SafeEncoder.encode(options.getKey()), SerializableUtils.serialize(value));
                }
                if(isok > 0) {
                    expire(options);
                }
                return isok == 1 ? true : false;
            }
        });
    }

    /**
     * 返回名称为key的set的基数
     * @param options                        CacheModelOptions对象		set的key
     * @return
     */
    public Long scard(final CacheModelOptions options) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.scard(options.getKey());
            }
        });
    }


    /**
     * 测试member是否是名称为key的set的元素
     * @param options                        CacheModelOptions对象		Set集合的key
     * @param value		值
     * @return
     */
    public Boolean sismember(final CacheModelOptions options, final Object value) {
        return call(new JedisAction<Boolean>(){
            @Override
            public Boolean execute(Jedis jedis) {
                return jedis.sismember(options.getKey(), (String)value);
            }
        });
    }

    /**
     * 慎用，会导致redis等待结果返回，若是集群模式则直接返回null
     * @param pattern		正则表达式
     * @return
     */
    public Set<String> keys(final String pattern){
        return call(new JedisAction<Set<String>>(){
            @Override
            public Set<String> execute(Jedis jedis) {
                return jedis.keys(pattern);
            }
        });
    }

    /**
     * 根据标识取出redis里的集合size
     * @param type		标识("list", "hash", "set")
     * @param options                        CacheModelOptions对象
     * @return
     */
    public Long size(final String type, final CacheModelOptions options) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                if("list".equalsIgnoreCase(type)){
                    return jedis.llen(options.getKey());
                }else if("hash".equalsIgnoreCase(type)){
                    return jedis.hlen(options.getKey());
                }else if("set".equalsIgnoreCase(type)){
                    return jedis.scard(options.getKey());
                }
                return 0L;
            }
        });
    }

    /**
     * 根据key判断值类型
     * @param options                        CacheModelOptions对象
     * @return		类型名称
     */
    public String type(final CacheModelOptions options) {
        return call(new JedisAction<String>() {
            @Override
            public String execute(Jedis jedis) {
                return jedis.type(options.getKey());
            }
        });
    }

    /**
     * 判断KEY是否存在
     * @param options                        CacheModelOptions对象
     * @return			存在返回true
     */
    public Boolean exists(final CacheModelOptions options) {
        return call(new JedisAction<Boolean>() {
            @Override
            public Boolean execute(Jedis jedis) {
                return jedis.exists(options.getKey());
            }
        });
    }

    /**
     * 保存ZSet<String>
     * @param options                        CacheModelOptions对象
     * @param sort
     * @param value
     * @return
     */
    public Boolean zadd(final CacheModelOptions options ,final double sort ,final String value){
        return call(new JedisAction<Boolean>(){
            @Override
            public Boolean execute(Jedis jedis) {
                try {
                    long isok =  jedis.zadd(options.getKey(), sort, value);
                    if(isok>0) {
                        expire(options);
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        });
    }

    /**
     * 删除ZSet元素
     * @param options                        CacheModelOptions对象
     * @param value
     * @return
     */
    public Long zrem(final CacheModelOptions options ,final String value){
        return call(new JedisAction<Long >(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.zrem(options.getKey(), value);
            }
        });
    }

    /**
     * 由小到大获取member成员在该key的位置
     * @param options                        CacheModelOptions对象
     * @param member
     * @return
     */
    public Long zrank(final CacheModelOptions options, final String member){
        return call(new JedisAction<Long >(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.zrank(options.getKey(), member);
            }
        });
    }

    /**
     * 由大到小获取member成员在该key的位置
     * @param options                        CacheModelOptions对象
     * @param member
     * @return
     */
    public Long zrevrank(final CacheModelOptions options,final String member){
        return call(new JedisAction<Long >(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.zrevrank(options.getKey(), member);
            }
        });
    }

    /**
     * 升序获取zset元素
     * @param options                        CacheModelOptions对象
     * @return
     */
    public List<String> zrevrank(final CacheModelOptions options){
        return call(new JedisAction<List<String> >(){
            @Override
            public List<String> execute(Jedis jedis) {
                return new ArrayList<String>( jedis.zrange(options.getKey(), 0, -1) );
            }
        });
    }

    /**
     * 升序获取zset元素
     * @param options                        CacheModelOptions对象
     * @param start
     * @param end
     * @return
     */
    public List<String> zrevrank(final CacheModelOptions options,final int start, final int end){
        return call(new JedisAction<List<String> >(){
            @Override
            public List<String> execute(Jedis jedis) {
                int e = end;
                if(e > 0){e--;}
                return new ArrayList<String>( jedis.zrange(options.getKey(), start, e) );
            }
        });
    }

    /**
     * 降序获取zset元素
     * @param options                        CacheModelOptions对象
     * @return
     */
    public List<String> zrevrange(final CacheModelOptions options){
        return call(new JedisAction<List<String> >(){
            @Override
            public List<String> execute(Jedis jedis) {
                return new ArrayList<String>( jedis.zrevrange(options.getKey(), 0, -1) );
            }
        });
    }

    /**
     * 降序获取zset元素
     * @param options                        CacheModelOptions对象
     * @param start
     * @param end
     * @return
     */
    public List<String> zrevrange(final CacheModelOptions options,final int start, final int end){
        return call(new JedisAction<List<String> >(){
            @Override
            public List<String> execute(Jedis jedis) {
                int e = end;
                if(e > 0){e--;}
                return new ArrayList<String>( jedis.zrevrange(options.getKey(),start, e) );
            }
        });
    }

    public List<String> zrange(final CacheModelOptions options,final int start, final int end){
        return call(new JedisAction<List<String> >(){
            @Override
            public List<String> execute(Jedis jedis) {
                return new ArrayList<String>( jedis.zrange(options.getKey(), start, end) );
            }
        });
    }


    /**
     * 根据区间段获取集合内排名成员--倒序
     * @param options                        CacheModelOptions对象   分组key
     * @param start 开始位
     * @param end   结束位  当为-1时，为取所有值
     * @return
     */
    public Set<Tuple> zrevrangeWithScores(final CacheModelOptions options, final int start, final int end) {
        return call(new JedisAction<Set<Tuple>>() {
            @Override
            public Set<Tuple> execute(Jedis jedis) {
                return jedis.zrevrangeWithScores(options.getKey(), start, end);
            }
        });
    }

    /**
     * 根据key获取list长度
     * @param options                        CacheModelOptions对象
     * @return
     */
    public Long llen(final CacheModelOptions options){
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.llen(options.getKey());
            }
        });
    }

    /**
     * 根据key删除并返回list尾元素
     * @param options                        CacheModelOptions对象
     * @returnrpop
     */
    public String rpop(final CacheModelOptions options){
        return call(new JedisAction<String>(){
            @Override
            public String  execute(Jedis jedis) {
                return jedis.rpop(options.getKey());
            }
        });
    }

    /**
     * 将名称为key的hash中field的value增加integer
     * @param options                        CacheModelOptions对象
     * @param field
     * @param integer
     * @return
     */
    public Long hincrby(final CacheModelOptions options,final String field,final Integer integer){
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                Long isok =  jedis.hincrBy(options.getKey(), field, integer);
                if(isok > 0) {
                    expire(options);
                }
                return isok;
            }
        });
    }

    /**
     * 向名称为key的hash中添加元素field<—>value
     * @param options                        CacheModelOptions对象
     * @param field
     * @param value
     * @return
     */
    public Long hset(final CacheModelOptions options,final String field, final Object value){
        return call(new JedisAction<Long >(){
            @Override
            public Long  execute(Jedis jedis) {
                if(value instanceof String){
                    return jedis.hset(options.getKey(), field, (String)value);
                } else {
                    return jedis.hset(SafeEncoder.encode(options.getKey()), SafeEncoder.encode(field), SerializableUtils.serialize(value));
                }
            }
        });
    }


    /**
     * 返回名称为key的zset中score>=min且score<=max的所有元素
     *
     * @param options                        CacheModelOptions对象
     * @param min
     * @param max
     * @return
     */
    public Set<String> zrangebyscore(final CacheModelOptions options, final double min, final double max) {
        return call(new JedisAction<Set<String>>() {
            @Override
            public Set<String> execute(Jedis jedis) {
                return jedis.zrangeByScore(options.getKey(), min, max);
            }
        });
    }

    /**
     * 返回名称为options.getKey()的zset中score>=min且score<=max结果之间的区间数据 <br/>
     *  offset, count就相当于sql中limit的用法 <br/>
     *  select * from table where score >=min and score <=max limit offset count
     *
     * @param options                        CacheModelOptions对象
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    public Set<String> zrangebyscore(final CacheModelOptions options, final double min, final double max, final int offset, final int count) {
        return call(new JedisAction<Set<String>>() {
            @Override
            public Set<String> execute(Jedis jedis) {
                return jedis.zrangeByScore(options.getKey(), min, max, offset, count);
            }
        });
    }


    /**
     * 返回名称为key的zset中score>=min且score<=max结果之间的区间数据 <br/>
     *  offset, count就相当于sql中limit的用法 <br/>
     *  select * from table where score >=min and score <=max limit offset count
     *
     * @param options                        CacheModelOptions对象
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    public List<TupleDto> zrangeByScoreWithScores(final CacheModelOptions options, final double min, final double max, final int offset, final int count) {
        return call(new JedisAction<List<TupleDto>>() {
            @Override
            public List<TupleDto> execute(Jedis jedis) {
                Set<Tuple> tupleSet = jedis.zrangeByScoreWithScores(options.getKey(), min, max, offset, count);
                List<TupleDto> tupleDtoList = new ArrayList<>();
                if(ToolsKit.isNotEmpty(tupleSet)) {
                    for(Tuple tuple : tupleSet) {
                        tupleDtoList.add(new TupleDto(tuple.getElement(), BigDecimal.valueOf(tuple.getScore()).doubleValue()));
                    }
                }
                return tupleDtoList;
            }
        });
    }


    /**
     * 删除名称为key的zset中score>=min且score<=max的所有元素
     */
    public Long zremrangebyscore(final CacheModelOptions options, final double min, final double max) {
        return call(new JedisAction<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                return jedis.zremrangeByScore(options.getKey(), min, max);
            }
        });
    }

    /**
     * 删除名称为KEY的zeset中rank>=min且rank<=max的所有元素
     * @param options                        CacheModelOptions对象
     * @param start
     * @param max
     * @return
     */
    public Long zremrangebyrank(final CacheModelOptions options, final int start,final int max){
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.zremrangeByRank(SafeEncoder.encode(options.getKey()), start, max);
            }
        });
    }


    /**
     * 为某个key自增1
     * @param options                        CacheModelOptions对象
     * @return
     */
    public Long incr(final CacheModelOptions options){
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                Long isok =  jedis.incr(SafeEncoder.encode(options.getKey()));
                if(isok > 0) {
                    expire(options);
                }
                return isok;
            }
        });
    }

    /**
     * 为某个key自减1
     * @param options                        CacheModelOptions对象
     * @return
     */
    public Long decr(final CacheModelOptions options){
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                Long count = jedis.decr(SafeEncoder.encode(options.getKey()));
                if(count>0) {
                    expire(options);
                }
                return count;
            }
        });
    }

    /**
     * 获取set的基数
     * @param options                        CacheModelOptions对象
     * @return
     */
    public Long zcard(final CacheModelOptions options){
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.zcard(SafeEncoder.encode(options.getKey()));
            }
        });
    }

    /**
     * 返回key的有效时间
     * @param options                        CacheModelOptions对象
     * @return
     */
    public Long ttl(final CacheModelOptions options){
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.ttl(SafeEncoder.encode(options.getKey()));
            }
        });
    }

    /**
     * 删除set里面和member相同的元素
     * @param options                        CacheModelOptions对象
     * @param member
     * @return
     */
    public Long srem(final CacheModelOptions options,final String member){
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.srem(options.getKey(), member);
            }
        });
    }

    /**
     * 获取set对象
     * @param options                        CacheModelOptions对象
     * @return
     */
    public Set<String> smembers(final CacheModelOptions options){
        return call(new JedisAction<Set<String>>(){
            @Override
            public Set<String> execute(Jedis jedis) {
                return jedis.smembers(options.getKey());
            }
        });
    }

    /**
     * 获取zset里面元素的soce
     * @param options                        CacheModelOptions对象
     * @return
     */
    public Double zscore(final CacheModelOptions options,final String member){
        return call(new JedisAction<Double>(){
            @Override
            public Double execute(Jedis jedis) {
                return jedis.zscore(options.getKey(), member);
            }
        });
    }

    /**
     * 返回 key 指定的哈希集中所有字段的值
     * @param options                        CacheModelOptions对象
     * @return
     */
    public List<String> hvals(final CacheModelOptions options){
        return call(new JedisAction<List<String>>(){
            @Override
            public List<String> execute(Jedis jedis) {
                return jedis.hvals(options.getKey());
            }
        });
    }

    @SuppressWarnings("unused")
    private <T> T batctGet(final Set<String> keys) {
        return call(new JedisAction<T>(){
            @Override
            @SuppressWarnings("unchecked")
            public T execute(Jedis jedis){
                Map<String,String> result = new HashMap<String, String>(keys.size());
                Pipeline p =jedis.pipelined();
                Map<String,Response<Map<String,String>>> responses = new HashMap<String,Response<Map<String,String>>>(keys.size());
                for(String key : keys) {
                    responses.put(key, p.hgetAll(key));
                }
                for(Iterator<String> it = responses.keySet().iterator(); it.hasNext();){
                    String key = it.next();
                    result.put(key, responses.get(key).get().get(key));
                }
                return (T)result;
            }
        });
    }

    /**
     * 添加地理位置
     * @param options                        CacheModelOptions对象					地理位置集合KEY
     * @param longitude		经度
     * @param latitude			纬度
     * @param member		集合成员值
     * @return
     */
    public long geoadd(final CacheModelOptions options, final double longitude, final double latitude, final String member) {
        return call(new JedisAction<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                return jedis.geoadd(options.getKey(), longitude, latitude, member);
            }
        });
    }

    /**
     * 添加地理位置
     * @param options                        CacheModelOptions对象		地理位置集合KEY
     * @param memberCoordinateMap		成员集合值
     * @return
     */
    public long geoadd(final CacheModelOptions options, final Map<String, GeoCoordinate> memberCoordinateMap) {
        return call(new JedisAction<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                return jedis.geoadd(options.getKey(), memberCoordinateMap);
            }
        });
    }

    /**
     * 根据名称获取地理位置信息
     * @param options                        CacheModelOptions对象					地理位置集合KEY
     * @param members		成员值
     * @return
     */
    public List<GeoCoordinate> geopos(final CacheModelOptions options, final String... members) {
        return call(new JedisAction<List<GeoCoordinate>>() {
            @Override
            public List<GeoCoordinate> execute(Jedis jedis) {
                return jedis.geopos(options.getKey(), members);
            }
        });
    }

    /**
     * 计算两个位置之间的距离
     * @param options                        CacheModelOptions对象						地理位置集合KEY
     * @param member1			成员值
     * @param member2			成员值
     * @parma unit						单位(M/KM)
     * @return
     */
    public Double geodist(final CacheModelOptions options, final String member1, final String member2, final GeoUnit unit) {
        return call(new JedisAction<Double>() {
            @Override
            public Double execute(Jedis jedis) {
                return jedis.geodist(options.getKey(), member1, member2, unit);
            }
        });
    }

    /**
     * 获取指定范围内的位置信息
     * @param options                        CacheModelOptions对象					地理位置集合KEY
     * @param longitude		经度
     * @param latitude			纬度
     * @param radius				半径范围
     * @param unit					单位(M/KM)
     * @return
     */
    public List<GeoRadiusResponse> georadius(final CacheModelOptions options, final double longitude, final double latitude, final double radius, final GeoUnit unit) {
        return call(new JedisAction<List<GeoRadiusResponse>>() {
            @Override
            public List<GeoRadiusResponse> execute(Jedis jedis) {
                return jedis.georadius(options.getKey(), longitude, latitude, radius, unit);
            }
        });
    }

    /**
     * 获取指定范围内的位置信息
     * @param options                        CacheModelOptions对象					地理位置集合KEY
     * @param longitude		经度
     * @param latitude			纬度
     * @param radius				半径范围
     * @param unit					单位(M/KM)
     * @param param			查询条件参数
     * @return
     */
    public List<GeoRadiusResponse> georadius(final CacheModelOptions options, final double longitude, final double latitude, final double radius, final GeoUnit unit, final GeoRadiusParam param) {
        return call(new JedisAction<List<GeoRadiusResponse>>() {
            @Override
            public List<GeoRadiusResponse> execute(Jedis jedis) {
                return jedis.georadius(options.getKey(), longitude, latitude, radius, unit, param);
            }
        });
    }

    /**
     * 获取存储集合范围内的位置信息
     * @param options                        CacheModelOptions对象				地理位置集合KEY
     * @param member		成员名称
     * @param radius			半径范围
     * @param unit				单位(M/KM)
     * @return
     */
    public List<GeoRadiusResponse> georadiusByMember(final CacheModelOptions options, final String member, final double radius, final GeoUnit unit) {
        return call(new JedisAction<List<GeoRadiusResponse>>() {
            @Override
            public List<GeoRadiusResponse> execute(Jedis jedis) {
                return jedis.georadiusByMember(options.getKey(), member, radius, unit);
            }
        });
    }

    /**
     * 订阅消息
     * @param listener			订阅监听器
     * @param channels		订阅渠道
     */
    public void subscribe(final RedisListener listener, final List<String> channels) {
        if (channels.isEmpty()) {
            throw new NullPointerException("channels is null");
        }
        try {
            final String[] channelsArray = channels.toArray(new String[] {});
            ThreadPoolKit.execute(new Runnable() {
                @Override
                public void run() {
                    Jedis jedis = getClient();
                    jedis.subscribe(listener, channelsArray);
                }
            });
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            logger.warn("#############: subscribe " + channels + " done!");
        }
    }

    /**
     * 模式匹配方式订阅消息
     * @param listener			订阅监听器
     * @param channels		订阅渠道
     * @return
     */
    public void psubscribe(final RedisListener listener, final List<String> channels) {
        if (channels.isEmpty()) {
            throw new NullPointerException("channels is null");
        }
        try {
            final String[] channelsArray = channels.toArray(new String[] {});
            ThreadPoolKit.execute(new Runnable() {
                @Override
                public void run() {
                    Jedis jedis = getClient();
                    jedis.psubscribe(listener, channelsArray);
                }
            });
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            logger.warn("#############: psubscribe " + channels + " done!");
        }
    }

    /**
     * 发布消息
     * @param message
     * @return
     */
    public long publish(final RedisMessage message ) {
        return call(new JedisAction<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                byte[] channel = SafeEncoder.encode(message.getChannel());
                byte[] bytes = SerializableUtils.serialize(message.getBody());
                return jedis.publish(channel, bytes);
            }
        });
    }



}
