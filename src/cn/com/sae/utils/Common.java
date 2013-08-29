package cn.com.sae.utils;

public class Common {
	public static final int CACHE_OBJECT_VERSION = 20130830;
	public static final int MEMCACHED_STORAGE_EXPIRATION = 30;
	public static final int SAE_KV_STORAGE_EXPIRATION = 120;

	public static final int MINITE = 60;
	public static final int HOUR = 60 * MINITE;
	public static final int DAY = 24 * HOUR;
	public static final int WEEK = 7 * DAY;

	public static final String SAE_PRODUCTION_DOMAIN = "xiaoshuoyuedu.sinaapp.com";
	public static final String LOCAL_TEST_DOMAIN = "localhost:8080";

	public static boolean isProduction() {
		String test_mode = System.getenv("TEST_MODE");
		if (test_mode != null && test_mode.equals("1")) {
			return false;
		}
		return true;
	}

}
