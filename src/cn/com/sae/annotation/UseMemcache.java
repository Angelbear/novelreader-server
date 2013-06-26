package cn.com.sae.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.com.sae.utils.Common;

@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UseMemcache {
	int expiry() default Common.MEMCACHED_STORAGE_EXPIRATION;
}
