package com.duangframework.db.convetor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by laotang on 2018/12/4.
 */
public class KvModle implements java.io.Serializable {

    private String collectionName;
    private List<KvItem> queryKvItems = new ArrayList<>();
    private List<KvItem> updateKvItems = new ArrayList<>();

    public KvModle(String collectionName) {
        this.collectionName = collectionName;
    }

    public KvModle(String collectionName, List<KvItem> queryKvItems, List<KvItem> updateKvItems) {
        this.collectionName = collectionName;
        this.queryKvItems = queryKvItems;
        this.updateKvItems = updateKvItems;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public List<KvItem> getQueryKvItem() {
        return queryKvItems;
    }

    public List<KvItem> getUpdateKvItem() {
        return updateKvItems;
    }

    public void addQueryKvItems(KvItem kvItem) {
        queryKvItems.add(kvItem);
    }

    public void addUpdateKvItem(KvItem kvItem) {
        updateKvItems.add(kvItem);
    }
}
