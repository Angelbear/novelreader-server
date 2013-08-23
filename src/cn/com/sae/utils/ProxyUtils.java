package cn.com.sae.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class ProxyUtils {
	private ProxyUtils() {
	}

	public static Object createProxy(Class<?> proxyClass,
			InvocationHandler invocationHandler) {
		return Proxy.newProxyInstance(proxyClass.getClassLoader(),
				new Class[] { proxyClass }, invocationHandler);

	}
}
