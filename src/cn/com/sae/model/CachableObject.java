package cn.com.sae.model;

import java.io.ObjectOutputStream;
import java.io.Serializable;

import cn.com.sae.utils.Common;

public class CachableObject implements Serializable {
	private static final long serialVersionUID = 4145056056328426497L;
	public long createDate;
	public long version;
	public Serializable object;

	public CachableObject() {
		createDate = System.currentTimeMillis();
		version = Common.CACHE_OBJECT_VERSION;
		object = null;
	}

	public CachableObject(Serializable s) {
		this();
		this.object = s;
	}
}
