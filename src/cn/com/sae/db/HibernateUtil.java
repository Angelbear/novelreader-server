package cn.com.sae.db;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import cn.com.sae.utils.Common;

import com.sina.sae.util.SaeUserInfo;

public class HibernateUtil {

	private static SessionFactory sessionFactoryReadonly;

	private static SessionFactory sessionFactoryWrite;

	private HibernateUtil() {

	}

	private static SessionFactory buildSessionFactory(boolean readonly) {
		try {
			Configuration config = new Configuration()
					.configure("hibernate.cfg.xml");
			if (Common.isProduction()) {
				if (readonly) {
					config.setProperty("hibernate.connection.url",
							"jdbc:mysql://r.rdc.sae.sina.com.cn:3307/app_xiaoshuoyuedu");
				} else {
					config.setProperty("hibernate.connection.url",
							"jdbc:mysql://w.rdc.sae.sina.com.cn:3307/app_xiaoshuoyuedu");
				}
				config.setProperty("hibernate.connection.username",
						SaeUserInfo.getAccessKey());
				config.setProperty("hibernate.connection.password",
						SaeUserInfo.getSecretKey());
			} else {
				if (readonly) {
					config.setProperty("hibernate.connection.username",
							"novelreader_r");
					config.setProperty("hibernate.connection.password",
							"n0velreader_r");
				} else {
					config.setProperty("hibernate.connection.username",
							"novelreader");
					config.setProperty("hibernate.connection.password",
							"n0velreader");
				}
			}
			System.out.println("config OK for readonly " + readonly);
			SessionFactory sessionFactory = config.buildSessionFactory();
			System.out.println("sessionFactory OK for readonly " + readonly);
			return sessionFactory;
		} catch (Throwable ex) {
			System.out.println("Initial SessionFactory creation failed."
					+ ex.getLocalizedMessage());
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory(boolean readonly) {
		if (readonly) {
			if (sessionFactoryReadonly == null) {
				sessionFactoryReadonly = buildSessionFactory(true);
			} 
			return sessionFactoryReadonly;
		} else {
			if (sessionFactoryWrite == null) {
				sessionFactoryWrite = buildSessionFactory(false);
			}
			return sessionFactoryWrite;
		}
	}

	public static void shutdown() {
		// sessionFactoryReadonly.close();
		// sessionFactoryWrite.close();
	}

}