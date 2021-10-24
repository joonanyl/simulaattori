package controller;

import javafx.application.Platform;
import simu.framework.IMoottori;
import simu.model.Asiakas;
import simu.model.OmaMoottori;
import simu.model.Palvelupiste;
import view.ISimulaattorinUI;

public class Kontrolleri implements IKontrolleri{   // UUSI
	
	private IMoottori moottori; 
	private ISimulaattorinUI ui;
	private ISimulaattorinUI mainUI;
	
	public Kontrolleri(ISimulaattorinUI ui) {
		this.mainUI = ui;
		moottori = new OmaMoottori(this); // luodaan uusi moottorisäie jokaista simulointia varten
		
	}
		
	@Override
	public void kaynnistaSimulointi() {	
		moottori.luoPalvelupisteet();
		moottori.setSimulointiaika(mainUI.getAika());
		moottori.setViive(mainUI.getViive());
		((Thread)moottori).start();
	}
	
	@Override
	public void hidasta() { // hidastetaan moottorisäiettä
		moottori.setViive((long)(moottori.getViive()*1.10));
	}

	@Override
	public void nopeuta() { // nopeutetaan moottorisäiettä
		moottori.setViive((long)(moottori.getViive()*0.9));
	}
	
	@Override
	public void naytaLoppuaika(double aika) {
		Platform.runLater(()->ui.setLoppuaika(aika)); 
	}

	public void varaaPP(int indeksi) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mainUI.varaaPalvelupiste(indeksi);
			}
		});
	}
	
	public void vapautaPP(int indeksi) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mainUI.vapautaPalvelupiste(indeksi);
			}
		});
	}
	
	public void setKaynnistaBtn() {
		mainUI.simuloinninJalkeen();
	}
	
	public void setCounter(double aika) {
		mainUI.setAikaCounter((int) aika);
	}
	
	public Palvelupiste[] getPalvelupisteet(){	return moottori.getPalvelupisteet(); }

	@Override
	public void vieAsiakaslistaan(Asiakas asiakas) {
		mainUI.lisaaAsiakaslistaan(asiakas);
	}

	@Override
	public int getKassat() {
		return mainUI.getKassat();
	}

	@Override
	public int getIPKassat() {
		return mainUI.getIPKassat();
	}

	@Override
	public int getRuokalinjastot() {
		return mainUI.getRuokalinjastot();
	}
}