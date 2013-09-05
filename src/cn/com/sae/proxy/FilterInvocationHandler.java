package cn.com.sae.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import cn.com.sae.annotation.MustFilterField;
import cn.com.sae.annotation.UseFilter;
import cn.com.sae.crawler.WebSiteCrawler;

public class FilterInvocationHandler implements InvocationHandler {

	
	private Object proxied;

	public FilterInvocationHandler(Object proxied) {
		super();
		this.proxied = proxied;
	}
	
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Class<?> returnClass = method.getReturnType();
		Object result = method.invoke(proxied, args);
		if (method.isAnnotationPresent(UseFilter.class) && (proxied instanceof WebSiteCrawler)) {
			WebSiteCrawler crawler = (WebSiteCrawler)proxied;
			 for(Field field : returnClass.getFields()) {
				 if (field.isAnnotationPresent(MustFilterField.class)) {
					 String src = (String)field.get(result);
					 String dst = src;
					 if (crawler.filterStrings() != null) {
						 for (String str : crawler.filterStrings()) {
							 dst = src.replaceAll(str, "");
						 }
						 field.set(result, dst);
					 }
				 }
			 }
		}
		return result;
	}

}
