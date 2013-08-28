package cn.com.sae.db;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import cn.com.sae.utils.Common;

import com.sina.sae.util.SaeUserInfo;

public class HibernateUtil {

	private static SessionFactory sessionFactory;

	private static SessionFactory buildSessionFactory(boolean readonly) {
		try {
			Configuration config = new Configuration().configure(
					"hibernate.cfg.xml");
			if (Common.isProduction()) {
				if (readonly) {
					config.setProperty("hibernate.connection.url", "jdbc:mysql://r.rdc.sae.sina.com.cn:3307/xiaoshuoyuedu");
				} else {
					config.setProperty("hibernate.connection.url", "jdbc:mysql://w.rdc.sae.sina.com.cn:3307/xiaoshuoyuedu");
				}
				config.setProperty("hibernate.connection.username", SaeUserInfo.getAccessKey());
				config.setProperty("hibernate.connection.password", SaeUserInfo.getSecretKey());
				
			}
			SessionFactory sessionFactory = config.buildSessionFactory();
			return sessionFactory;
		} catch (Throwable ex) {
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory(boolean readonly) {
		sessionFactory = buildSessionFactory(readonly);
		return sessionFactory;
	}

	public static void shutdown() {
		sessionFactory.close();
	}

}