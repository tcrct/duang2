package com.duangframework.db;


import com.alibaba.fastjson.annotation.JSONField;
import com.duangframework.db.annotation.Id;

import java.util.Date;

public class IdEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public static final String ENTITY_ID_FIELD = "id";
	public static final String ID_FIELD = "_id";
	public static final String CREATETIME_FIELD = "createtime";
	public static final String CREATEUSERID_FIELD = "createuserid";
	public static final String UPDATETIME_FIELD = "updatetime";
	public static final String UPDATEUSERID_FIELD = "updateuserid";
	public static final String STATUS_FIELD = "status";
	public static final String SOURCE_FIELD = "source";
	public static final String STATUS_FIELD_SUCCESS = "审核通过";
	public static final String STATUS_FIELD_DELETE = "已删除";

	@Id
	@JSONField(name=ID_FIELD)
	private String id;

	private Date createtime;			//创建时间

	private String createuserid;		//创建人ID

	private Date updatetime;			//更新时间

	private String updateuserid;		//更新人ID

	private String status;			//数据状态(查数据字典)

	private String source;			//数据来源

	public IdEntity(String id, Date createtime, String createuserid,
                    Date updatetime, String updateuserid, String status, String source) {
		super();
		this.id = id;
		this.createtime = createtime;
		this.createuserid = createuserid;
		this.updatetime = updatetime;
		this.updateuserid = updateuserid;
		this.status = status;
		this.source = source;
	}

	public IdEntity() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

    public void setId(int id) {
        this.id = id+"";
    }

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public String getCreateuserid() {
		return createuserid;
	}

	public void setCreateuserid(String createuserid) {
		this.createuserid = createuserid;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public String getUpdateuserid() {
		return updateuserid;
	}

	public void setUpdateuserid(String updateuserid) {
		this.updateuserid = updateuserid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public String toString() {
		return "IdEntity{" +
				"id='" + id + '\'' +
				", createtime=" + createtime +
				", createuserid='" + createuserid + '\'' +
				", updatetime=" + updatetime +
				", updateuserid='" + updateuserid + '\'' +
				", status='" + status + '\'' +
				", source='" + source + '\'' +
				'}';
	}
}
