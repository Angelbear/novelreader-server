package cn.com.sae.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import cn.com.sae.proxy.BaseInvocationHandler;

public class ProxyUtils {
	private ProxyUtils() {
	}

	public static Object createProxy(Class<?> proxyClass,
			InvocationHandler invocationHandler) {
		return Proxy.newProxyInstance(proxyClass.getClassLoader(),
				new Class[] { proxyClass }, invocationHandler);

	}

	public static Object unwrapProxy(Object proxyObject) {
		try {
			InvocationHandler handler = Proxy.getInvocationHandler(proxyObject);
			if (handler != null && handler instanceof BaseInvocationHandler) {
				BaseInvocationHandler h = (BaseInvocationHandler) handler;
				return ProxyUtils.unwrapProxy(h.getOriginalObject());
			}
		} catch (Exception e) {

		}
		return proxyObject;
	}
}
