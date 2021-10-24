package tietokanta;

import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import simu.model.Palvelupiste;

public class PalvelupisteDAO implements IPalvelupisteDAO{
	private SessionFactory sessionFactory;
	private Session session;

	public PalvelupisteDAO() {
		sessionFactory = HibernateUtil.getSessionFactory();
	}
	
	public boolean addPalvelupiste(Palvelupiste pp) {
		boolean status = false;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			if(pp != null) {
				tx = session.beginTransaction();
				session.save(pp);
				tx.commit();
				status = true;
			} else
				System.out.println("Palvelupiste on null");
								
		} catch(HibernateException he) {
			System.out.println("---Palvelupisteen tallentaminen epäonnistui---");
			handleHibernateError(tx);			
		}
		
		finalizeSession();
		return status;
	}
	
	//return true only if all pp added
	public boolean addPalvelupisteSet(Set<Palvelupiste> ppSet) {
		boolean status = true;
		for(Palvelupiste pp : ppSet) {
			if(status == true)
				status = addPalvelupiste(pp);
			else 
				addPalvelupiste(pp);
		}
		
		return status;
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
