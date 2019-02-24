package com.duangframework.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 扩展HashMap, 使其可以一个键可以对应多个值<br/>
 * 用法与Map一致
 * @author laotnag
 *
 * @param <K>		键
 * @param <V>		值
 */
public class DuangMaps<K, V> {

	private transient final ConcurrentHashMap<K, List<V>> map;

	public DuangMaps() {
		map = new ConcurrentHashMap	<K,List<V>>();
	}
		
	public void put(K key, V value){
		List<V> list = map.get(key);
		if(null == list || list.isEmpty()){
			list = new ArrayList<V>();
			list.add(value);
			try{
				map.put(key, list);
			} catch (Exception e){
				throw new RuntimeException("put value to DuangMaps Exception: " + e.getMessage() );
			}
		} else{
			list.add(value);
		}
	}
	
	public List<V> get(K key) {
		List<V> list = map.get(key);
		return list.isEmpty() ? null : list;
	}
	
	public boolean isEmpty() {
		return map.isEmpty();
	}
	
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}
	
	public boolean containsValue(V value) {
		return map.containsValue(value);
	}
	
	public Set<Map.Entry<K, List<V>>> entrySet(){
		return map.entrySet();
	}
	
	public Set<K> keySet() {
		return map.keySet();
	}
	
	public void clear() {
		map.clear();
	}
	
}
