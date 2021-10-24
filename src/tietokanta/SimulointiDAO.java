package tietokanta;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class SimulointiDAO implements ISimulointiDAO{
	
	private final String className = "Simulointi";
	private SessionFactory sessionFactory;
	private Session session;

	public SimulointiDAO() {
		sessionFactory = HibernateUtil.getSessionFactory();
	}
	
	public boolean lisaaSimulointi(Simulointi sim) {
		boolean status = false;
		Transaction tx = null;
		if (sessionFactory != null) {
			try {
				session = sessionFactory.openSession();
				if(sim != null) {
					tx = session.beginTransaction();
					session.save(sim);
					tx.commit();
					status = true;
				} else
					System.out.println("Simulointi on null");
									
			} catch(HibernateException he) {
				System.out.println("---Simulointi tallentaminen epäonnistui---");
				handleHibernateError(tx);			
			}
		}
		
		finalizeSession();
		return status;
	}
	
	public List<Simulointi> haeSimuloinnit() {
		List<Simulointi> result = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			//from class name (not table), ordered by timestamp  			  			
			result = session.createQuery("from " + className + " order by aikaleima desc", Simulointi.class).getResultList();
			
		}catch(HibernateException e) {
			System.out.println("---Simulointien haku epäonnistui---");
			handleHibernateError(tx);
		}
		
		finalizeSession();			
		return result;
	}
	
	private void handleHibernateError(Transaction tx) {
		if(tx != null)
			tx.rollback();
	}
	
	private void finalizeSession() {
		try {
			if(session != null)
				session.close();
		}catch(HibernateException he) {
			System.out.println("---Istunto ei ole saatu suljettu---");
			he.printStackTrace();
		}
	}
}
