package view;

import simu.model.Asiakas;

public interface ISimulaattorinUI {
	
	// Kontrolleri tarvitsee syötteitä, jotka se välittää Moottorille
	public double getAika();
	public long getViive();
	public int getRuokalinjastot();
	public int getKassat();
	public int getIPKassat();
	public void reset();
	public double getIpKassaMax();
	public double getIpKassaMin();
	public double getKassaMax();
	public double getKassaMin();
	public double getRuokaMax();
	public double getRuokaMin();
	//Kontrolleri antaa käyttöliittymälle tietoa, joita Moottori tuottaa
	public void lisaaAsiakaslistaan(Asiakas asiakas);
	public void simuloinninJalkeen();
	public void setAikaCounter(int aika);
	public void varaaPalvelupiste(int i);
	public void vapautaPalvelupiste(int i);
}
