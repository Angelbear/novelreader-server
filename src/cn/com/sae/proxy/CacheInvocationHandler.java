package cn.com.sae.proxy;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import cn.com.sae.annotation.UseMemcache;
import cn.com.sae.annotation.UseSaeKV;

import com.sina.sae.kvdb.SaeKV;
import com.sina.sae.kvdb.SaeKVUtil;
import com.sina.sae.memcached.SaeMemcache;

public class CacheInvocationHandler implements InvocationHandler {
	protected SaeKV saeKV = new SaeKV();
	protected SaeMemcache mc = new SaeMemcache();

	private Object proxied;

	public CacheInvocationHandler(Object proxied) {
		super();
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
		return SaeKVUtil.deserializable(saeKV.get(key));
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
		return saeKV.set(key, SaeKVUtil.serializable(result));
	}

	private Object getSearchResultFromMemcached(String func, Object... params) {
		StringBuilder sb = new StringBuilder();
		sb.append(func);
		for (int i = 0; i < params.length; i++) {
			sb.append("-");
			sb.append(params[i]);
		}
		String key = sb.toString();
		return mc.get(key);
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
		return mc.add(key, result, expiry);
	}

	private boolean putSearchResultToMemcached(Object result, String func,
			Object... params) {
		return putSearchResultToMemcached(result, 30, func, params);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		System.out.println(method.getName());

		if (doesCacheInMemcached(method)) {
			Object cached = getSearchResultFromMemcached(method.getName(), args);
			if (cached != null) {
				return cached;
			}
		}

		if (doesCacheInSaeKV(method)) {
			Object cached = getSearchResultFromSaeKV(method.getName(), args);
			if (cached != null) {
				return cached;
			}
		}

		Object result = method.invoke(proxied, args);

		if (doesCacheInMemcached(method)) {
			putSearchResultToMemcached(result, method.getName(), args);
		}
		if (doesCacheInSaeKV(method)) {
			putSearchResultToSaeKV(result, method.getName(), args);
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
