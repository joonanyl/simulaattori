package tietokanta;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
	private static final SessionFactory sessionFactory = buildSessionFactory();
	
	public static SessionFactory buildSessionFactory() {
		SessionFactory sessionFactory = null;
		
		try {
			Configuration configuration = new Configuration();
			//Set configuration file
			configuration.configure("hibernate.cfg.xml");
			sessionFactory = configuration.buildSessionFactory();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return sessionFactory;
	}
	
	public static SessionFactory getSessionFactory() {	return sessionFactory;	}
}