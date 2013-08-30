package cn.com.sae.db;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import cn.com.sae.utils.Common;

import com.sina.sae.util.SaeUserInfo;

public class HibernateUtil {

	private static SessionFactory sessionFactoryReadonly = buildSessionFactory(true);
	
	private static SessionFactory sessionFactoryWrite = buildSessionFactory(false);
	
	private HibernateUtil() {
		
	}

	private static SessionFactory buildSessionFactory(boolean readonly) {
		try {
			Configuration config = new Configuration().configure(
					"hibernate.cfg.xml");
			if (Common.isProduction()) {
				if (readonly) {
					config.setProperty("hibernate.connection.url", "jdbc:mysql://r.rdc.sae.sina.com.cn:3307/app_xiaoshuoyuedu");
				} else {
					config.setProperty("hibernate.connection.url", "jdbc:mysql://w.rdc.sae.sina.com.cn:3307/app_xiaoshuoyuedu");
				}
				config.setProperty("hibernate.connection.username", SaeUserInfo.getAccessKey());
				config.setProperty("hibernate.connection.password", SaeUserInfo.getSecretKey());
			}
			SessionFactory sessionFactory = config.buildSessionFactory();
			Logger log = Logger.getLogger("org.hibernate");
			log.setLevel(Level.ERROR);
			return sessionFactory;
		} catch (Throwable ex) {
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory(boolean readonly) {
		if (readonly) {
			if (sessionFactoryReadonly == null || sessionFactoryReadonly.isClosed()) {
				sessionFactoryReadonly = buildSessionFactory(true);
			}
			return sessionFactoryReadonly;
		} else {
			if (sessionFactoryWrite == null || sessionFactoryWrite.isClosed()) {
				sessionFactoryWrite = buildSessionFactory(false);
			}
			return sessionFactoryWrite;
		}
	}

	public static void shutdown() {
		//sessionFactoryReadonly.close();
		//sessionFactoryWrite.close();
	}

}