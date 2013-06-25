package cn.com.sae.proxy;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import cn.com.sae.annotation.UseMemcache;
import cn.com.sae.annotation.UseSaeKV;
import cn.com.sae.model.CachableObject;
import cn.com.sae.utils.Common;

import com.sina.sae.kvdb.SaeKV;
import com.sina.sae.kvdb.SaeKVUtil;
import com.sina.sae.memcached.SaeMemcache;

public class CacheInvocationHandler implements InvocationHandler {
	private SaeKV saeKV;
	private SaeMemcache mc;

	private Object proxied;

	public CacheInvocationHandler(Object proxied) {
		super();
		saeKV = new SaeKV();
		mc = new SaeMemcache();
		saeKV.init();
		mc.init();
		this.proxied = proxied;
	}

	private Object getSearchResultFromSaeKV(String func, Object... params)
			throws IOException, ClassNotFoundException {
		StringBuilder sb = new StringBuilder();
		sb.append(func);
		for (int i = 0; i < params.length; i++) {
			sb.append("-");
			sb.append(params[i]);
		}
		String key = sb.toString();
		CachableObject cached = null;
		try {
			cached = (CachableObject) SaeKVUtil.deserializable(saeKV.get(key));
		} catch (Exception e) {
			saeKV.delete(key);
			return null;
		}
		if (cached != null) {
			if (cached.version != Common.CACHE_OBJECT_VERSION
					|| cached.createDate < System.currentTimeMillis()
							- Common.SAE_KV_STORAGE_EXPIRATION) {
				saeKV.delete(key);
			} else {
				if (cached.object != null) {
					return cached.object;
				} else {
					return cached;
				}
			}
		}
		return null;
	}

	private boolean putSearchResultToSaeKV(Object result, String func,
			Object... params) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(func);
		for (int i = 0; i < params.length; i++) {
			sb.append("-");
			sb.append(params[i]);
		}
		String key = sb.toString();
		CachableObject cache = null;
		if (result instanceof CachableObject) {
			cache = (CachableObject) result;
		} else {
			Serializable s = (Serializable) result;
			cache = new CachableObject(s);
		}
		return saeKV.set(key, SaeKVUtil.serializable(cache));
	}

	private Object getSearchResultFromMemcached(String func, Object... params) {
		StringBuilder sb = new StringBuilder();
		sb.append(func);
		for (int i = 0; i < params.length; i++) {
			sb.append("-");
			sb.append(params[i]);
		}
		String key = sb.toString();
		CachableObject cached = null;
		try {
			cached = (CachableObject) mc.get(key);
		} catch (Exception e) {
			mc.delete(key);
			return null;
		}
		if (cached != null) {
			if (cached.version != Common.CACHE_OBJECT_VERSION) {
				mc.delete(key);
			} else {
				if (cached.object != null) {
					return cached.object;
				} else {
					return cached;
				}
			}
		}
		return null;
	}

	private boolean putSearchResultToMemcached(Object result, long expiry,
			String func, Object... params) {
		StringBuilder sb = new StringBuilder();
		sb.append(func);
		for (int i = 0; i < params.length; i++) {
			sb.append("-");
			sb.append(params[i]);
		}
		String key = sb.toString();
		CachableObject cache = null;
		if (result instanceof CachableObject) {
			cache = (CachableObject) result;
		} else {
			Serializable s = (Serializable) result;
			cache = new CachableObject(s);
		}
		return mc.add(key, cache, expiry);
	}

	private boolean putSearchResultToMemcached(Object result, String func,
			Object... params) {
		return putSearchResultToMemcached(result, 30, func, params);
	}

	private void checkIsInit() {
		saeKV.init();
		mc.init();
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		String func = proxied.getClass().getName() + "-" + method.getName();

		checkIsInit();

		if (doesCacheInMemcached(method)) {
			Object cached = getSearchResultFromMemcached(func, args);
			if (cached != null) {
				return cached;
			}
		}

		if (doesCacheInSaeKV(method)) {
			Object cached = getSearchResultFromSaeKV(func, args);
			if (cached != null) {
				putSearchResultToMemcached(cached, func, args);
				return cached;
			}
		}

		Object result = method.invoke(proxied, args);

		if (doesCacheInMemcached(method)) {
			putSearchResultToMemcached(result, func, args);
		}
		if (doesCacheInSaeKV(method)) {
			putSearchResultToSaeKV(result, func, args);
		}

		return result;
	}

	protected boolean doesCacheInMemcached(Method method) {
		return method.isAnnotationPresent(UseMemcache.class);
	}

	protected boolean doesCacheInSaeKV(Method method) {
		return method.isAnnotationPresent(UseSaeKV.class);
	}
}
