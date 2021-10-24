package controller;

import simu.model.Asiakas;
import simu.model.Palvelupiste;

public interface IKontrolleri {
	
		// Rajapinta, joka tarjotaan  käyttöliittymälle:
	
		public void kaynnistaSimulointi();
		public void nopeuta();
		public void hidasta();
		
		// Rajapinta, joka tarjotaan moottorille:
		public int getKassat();
		public int getIPKassat();
		public int getRuokalinjastot();
		public void vieAsiakaslistaan(Asiakas asiakas);
		public void setKaynnistaBtn();
		public void naytaLoppuaika(double aika);
		public Palvelupiste[] getPalvelupisteet();
		public void setCounter(double aika);
		public void varaaPP(int i);
		public void vapautaPP(int i);
}
