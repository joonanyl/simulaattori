package controller;

import javafx.application.Platform;
import simu.framework.IMoottori;
import simu.model.Asiakas;
import simu.model.OmaMoottori;
import simu.model.Palvelupiste;
import view.ISimulaattorinUI;
/**
 * The class is a controller class between the GUI and the Motor. It is used to transfer information from GUI to Motor and vice versa.
 * @author Joona Nylander, Mikhail Deriabin, Oliver Hamberg
 *
 */
public class Kontrolleri implements IKontrolleri{   // UUSI
	/**
	 * A motor variable.
	 */
	private IMoottori moottori; 
	/**
	 * A GUI variable.
	 */
	private ISimulaattorinUI ui;
	/**
	 * The constructor needs a GUI as parameter. The GUI and Motor are initialized in the constructor.
	 * @see <a href="simuMainGUI.html">GUI</a>
	 * @see <a href="OmaMoottori.html">Motor</a>
	 */
	public Kontrolleri(ISimulaattorinUI ui) {
		this.ui = ui;
		moottori = new OmaMoottori(this); // luodaan uusi moottorisäie jokaista simulointia varten	
	}
	/**
	 * The method passes the call to start the simulation. It also gives information from the GUI needed to start the simulation.
	 */	
	@Override
	public void kaynnistaSimulointi() {	
		moottori.luoPalvelupisteet();
		moottori.setSimulointiaika(ui.getAika());
		moottori.setViive(ui.getViive());
		((Thread)moottori).start();
	}
	/**
	 * Slows down the simulation speed.
	 */
	@Override
	public void hidasta() { // hidastetaan moottorisäiettä
		moottori.setViive((long)(moottori.getViive()*1.10));
	}
	/**
	 * Speeds up the simulation speed.
	 */
	@Override
	public void nopeuta() { // nopeutetaan moottorisäiettä
		moottori.setViive((long)(moottori.getViive()*0.9));
	}
	/**
	 * Allocates an service point in the GUI.
	 */
	public void varaaPP(int indeksi) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ui.varaaPalvelupiste(indeksi);
			}
		});
	}
	/**
	 * Deallocates an service point in the GUI.
	 */
	public void vapautaPP(int indeksi) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				ui.vapautaPalvelupiste(indeksi);
			}
		});
	}
	/**
	 * Calls the GUI to run method simuloinninJalkeen();
	 */
	public void simuloinninJalkeen() {
		ui.simuloinninJalkeen();
	}
	/**
	 * Passes the information of current time to GUI.
	 */
	public void setAika(double aika) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				ui.setAikaCounter((int) aika);
			}
		});
		
	}
	/**
	 * @return Service points
	 */
	public Palvelupiste[] getPalvelupisteet(){	return moottori.getPalvelupisteet(); }

	/**
	 * Passes the customer to be added to customer list in GUI.
	 */
	@Override
	public void vieAsiakaslistaan(Asiakas asiakas) {
		ui.lisaaAsiakaslistaan(asiakas);
	}
	/**
	 * @return Cashiers
	 */
	@Override
	public int getKassat() {
		return ui.getKassat();
	}
	/**
	 * @return Self-checkouts
	 */
	@Override
	public int getIPKassat() {
		return ui.getIPKassat();
	}
	/**
	 * @return Food courts
	 */
	@Override
	public int getRuokalinjastot() {
		return ui.getRuokalinjastot();
	}
	/**
	 * Calls the reset method of the motor.
	 */
	public void reset() {
		moottori.reset();
	}
	/**
	 * @return maximum time limit of the self-checkouts
	 */
	@Override
	public double getIpKassaMax() {
		return ui.getIpKassaMax();
	}
	/**
	 * @return minimum time limit of the self-checkouts
	 */
	@Override
	public double getIpKassaMin() {
		return ui.getIpKassaMin();
	}
	/**
	 * @return maximum time limit of the cashiers
	 */
	@Override
	public double getKassaMax() {
		return ui.getKassaMax();
	}
	/**
	 * @return minimum time limit of the cashiers
	 */
	@Override
	public double getKassaMin() {
		return ui.getKassaMin();
	}
	/**
	 * @return maximum time limit of the food courts
	 */
	@Override
	public double getRuokaMax() {
		return ui.getRuokaMax();
	}
	/**
	 * @return minimum time limit of the food courts
	 */
	@Override
	public double getRuokaMin() {
		return ui.getRuokaMin();
	}
}