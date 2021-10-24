package view;

import simu.model.Asiakas;

public interface ISimulaattorinUI {
	
	// Kontrolleri tarvitsee syötteitä, jotka se välittää Moottorille
	public double getAika();
	public long getViive();
	public int getRuokalinjastot();
	public int getKassat();
	public int getIPKassat();
	//Kontrolleri antaa käyttöliittymälle tuloksia, joita Moottori tuottaa
	public void lisaaAsiakaslistaan(Asiakas asiakas);
	public void setLoppuaika(double aika);
	public void simuloinninJalkeen();
	public void setAikaCounter(int aika);
	public void varaaPalvelupiste(int i);
	public void vapautaPalvelupiste(int i);
}
