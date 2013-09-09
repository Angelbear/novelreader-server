package cn.com.sae.proxy;

public class BaseInvocationHandler {

	protected Object proxied;

	public BaseInvocationHandler(Object proxied) {
		this.proxied = proxied;
	}

	public Object getOriginalObject() {
		return this.proxied;
	}
}
