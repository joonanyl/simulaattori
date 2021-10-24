package tietokanta;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.sql.Timestamp;

import simu.model.Palvelupiste;

public class tietokantaTest {
	public static void main(String[] args) {
		//Luodaan DAO objektit
		SimulointiDAO simulointiDAO = new SimulointiDAO();
		PalvelupisteDAO palvelupisteDAO = new PalvelupisteDAO();		
		
		//Luodaan Simulointi objekti
		Simulointi sim = new Simulointi();
		
		//Tämä osa ei tarvi tehdä OmaMoottorissa
		Palvelupiste pp1 = new Palvelupiste();
		pp1.setPpNimi("Ruokatiski");
		pp1.setPpID(1);
		Palvelupiste pp2 = new Palvelupiste();
		pp2.setPpNimi("Kassa");		
		pp2.setPpID(2);
		
		//Asetetaaan mihin simulointiin pp kuuluu (SQL taulukkoon tulee pelkkä simuloinnin id)
		pp1.setSimulointi(sim);
		pp2.setSimulointi(sim);
		
		//Luodaan pp:den Set, jotta niiden tiedot saisi tallennettu yhdellä metoodilla
		Set<Palvelupiste> pps = new HashSet<>();
		pps.add(pp1);
		pps.add(pp2);	
		
		//Laitetaan aikaleima, kun simulointi oli luotu
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		sim.setAikaleima(ts);
		
		//Tallennetaan tiedot sekä simuloinnit että palvelupisteet SQL taulukkoihin
		boolean status1 = simulointiDAO.lisaaSimulointi(sim);
		boolean status2 = palvelupisteDAO.addPalvelupisteSet(pps);
		
		if(status1 == true && status2 == true)
			System.out.println("Tallennus on onnistunut");
		else {
			System.out.println("Tallennus on epäonnistunut");
		}
		
		//Haetaan kaikkien simulointien tiedot kerralla (ei tarvi hakea pp erikseen)
		List<Simulointi> sims = simulointiDAO.haeSimuloinnit();		
		
		System.out.println("---\nTest\n---");		
		System.out.println("Kaikki tallennetut simuloinnit:");
		for(Simulointi s : sims) {
			System.out.println(s + ", simuloinnissa olevien ruokatiskien määrä " + s.getRuokatiskienLkm());
			
			//Saadaan pp:n kaikki tiedot
			Set<Palvelupiste> simPps = s.getPalvelupisteet();
			
			System.out.println("Simuloinnin pp:t");
			for(Palvelupiste pp : simPps) {
				System.out.print(pp);
				
				//Haetaan pp:n asiakkaiden lkm
				int asiakLkm = pp.getAsiakkaidenLkm();
				
				System.out.println(", asiakkaiden lkm: " + asiakLkm);
			}
			System.out.println();
		}					
	}
}
