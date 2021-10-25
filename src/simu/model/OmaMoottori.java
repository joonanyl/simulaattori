package simu.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import controller.IKontrolleri;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;
import simu.framework.Kello;
import simu.framework.Moottori;
import simu.framework.Saapumisprosessi;
import simu.framework.Tapahtuma;
import tietokanta.PalvelupisteDAO;
import tietokanta.Simulointi;
import tietokanta.SimulointiDAO;

public class OmaMoottori extends Moottori {
	SimulointiDAO simulointiDAO = new SimulointiDAO();
	PalvelupisteDAO palvelupisteDAO = new PalvelupisteDAO();

	Kello kello = Kello.getInstance();
	Normal ruokatiskiJakauma;
	Normal kassaJakauma;
	Normal ipKassaJakauma;
	Normal[] jakaumat;

	private Saapumisprosessi saapumisprosessi;
	private Palvelupiste[] ruokatiskit, kaikkiKassat, kassat, ipKassat;

	// Asetukset
	private int ruokatiskienLkm, kassojenLkm, ipKassojenLkm;
	private int[] asiakkaatKerralla = { 1, 5 }; // asiakkaiden min ja max lkm, jotka saapuvat ruokalaan kerralla

	private double[] ruokatiskinAikaRajat = { 3d, 6d }; // min palveluaika, maks palveluaika
	private double[] kassanAikaRajat = { 2d, 4d }; // min palveluaika, maks palveluaika
	private double[] ipKassanAikaRajat = { 1.7d, 3d }; // min palveluaika, maks palveluaika

	public OmaMoottori(IKontrolleri kontrolleri) {

		super(kontrolleri);

		// Generoidaan Normal jakaumat palvelupisteille
		Normal ruokatiskiJakauma = this.getNormalGenerator(ruokatiskinAikaRajat[0], ruokatiskinAikaRajat[1]);
		Normal kassaJakauma = this.getNormalGenerator(kassanAikaRajat[0], kassanAikaRajat[1]);
		Normal ipKassaJakauma = this.getNormalGenerator(ipKassanAikaRajat[0], ipKassanAikaRajat[1]);
		jakaumat = new Normal[] { ruokatiskiJakauma, kassaJakauma, ipKassaJakauma };
	}

	@Override
	protected void alustukset() {
		saapumisprosessi.generoiSeuraava(); // Ensimmäinen saapuminen järjestelmään
	}

	@Override
	protected void suoritaTapahtuma(Tapahtuma t) { // B-vaiheen tapahtumat
		Asiakas a;
		switch (t.getTyyppi()) {
		case ARR1:
			// Tarkistaa kumman ruokatiskin jono on lyhyempi ja lisää asiakkaan sen jonoon
			Palvelupiste ruokatiski = lyhyinPpJono(ruokatiskit);

			int asiakkaidenLkm = ThreadLocalRandom.current().nextInt(asiakkaatKerralla[0], asiakkaatKerralla[1] + 1);
			for (int i = 0; i < asiakkaidenLkm; i++) {
				Asiakas uusiAsiakas = new Asiakas();
				ruokatiski.lisaaJonoon(uusiAsiakas);
				uusiAsiakas.setSaapumisaika(kello.getAika());
				uusiAsiakas.setJonoonSaapumisAika(kello.getAika());
			}

			int mihin = ruokatiski.getPpID();
			kontrolleri.varaaPP(mihin);
			saapumisprosessi.generoiSeuraava();
			break;

		case DEP1:
			int mista = t.getPpID();
			kontrolleri.vapautaPP(mista);
			a = palvelupisteet[mista].otaJonosta();
			Palvelupiste kassa = lyhyinPpJono(kaikkiKassat);
			kassa.lisaaJonoon(a);
			a.setJonoonSaapumisAika(kello.getAika());
			mihin = kassa.getPpID();
			kontrolleri.varaaPP(mihin);
			break;

		case DEP2:
			mista = t.getPpID();
			kontrolleri.vapautaPP(mista);
			a = palvelupisteet[mista].otaJonosta();
			a.setPoistumisaika(kello.getAika());
			a.raportti();
			kontrolleri.vieAsiakaslistaan(a);
			break;
		}
		kontrolleri.setAika(super.tapahtumalista.getSeuraavanAika());
	}

	// Palauttaa parametrinä annettujen Palvelupisteiden lyhyimmän jonon
	// perusteella.
	private Palvelupiste lyhyinPpJono(Palvelupiste[] pp) {
		int lyhyinJonoIndeksi = 0;
		ArrayList<Integer> samatJonoPituudetIndeksit = new ArrayList<>();
		samatJonoPituudetIndeksit.add(0);
		int lyhyinJonoPituus = pp[0].getJonoSize();

		for (int i = 1; i < pp.length; i++) {
			if (lyhyinJonoPituus > pp[i].getJonoSize()) {
				lyhyinJonoIndeksi = i;
				lyhyinJonoPituus = pp[i].getJonoSize();
				samatJonoPituudetIndeksit.clear();
			} else if (lyhyinJonoPituus == pp[i].getJonoSize()) {
				samatJonoPituudetIndeksit.add(i);
			}
		}
		if (samatJonoPituudetIndeksit.size() > 0) {
			int min = 0;
			int max = samatJonoPituudetIndeksit.size() - 1;
			lyhyinJonoIndeksi = ThreadLocalRandom.current().nextInt(min, max + 1);
		}

		return pp[lyhyinJonoIndeksi];

	}

	private double laskeMean(double min, double max) {
		return (min + max) / 2;
	}

	private double laskeVar(double max, double mean) {
		return Math.pow((max - mean) / 3, 2);
	}

	private Normal getNormalGenerator(double minAika, double maxAika) {
		double mean = laskeMean(minAika, maxAika);
		double var = laskeVar(maxAika, mean);
		return new Normal(mean, var);
	}

	private void generoiPalvelupisteet(Normal[] jakaumat) {
		int ppID = 0;
		
		// Ruokalinjastot
		for (int i = 0; i < ruokatiskienLkm; i++) {
			ruokatiskit[i] = new Palvelupiste(jakaumat[0], tapahtumalista, PalvelupisteenTyyppi.RUOKATISKI, ppID);
			ppID++;
		}

		// Kassat
		for (int i = 0; i < kassojenLkm; i++) {
			kassat[i] = new Palvelupiste(jakaumat[1], tapahtumalista, PalvelupisteenTyyppi.KASSA, ppID);
			ppID++;
		}

		// IP-Kassat
		for (int i = 0; i < ipKassojenLkm; i++) {
			ipKassat[i] = new Palvelupiste(jakaumat[2], tapahtumalista, PalvelupisteenTyyppi.IPKASSA, ppID);
			ppID++;
		}
	}

	public void luoPalvelupisteet() {
		// Generoidaan palvelupisteet
		ruokatiskienLkm = kontrolleri.getRuokalinjastot();
		kassojenLkm = kontrolleri.getKassat();
		ipKassojenLkm = kontrolleri.getIPKassat();

		ruokatiskit = new Palvelupiste[ruokatiskienLkm];
		kassat = new Palvelupiste[kassojenLkm];
		ipKassat = new Palvelupiste[ipKassojenLkm];
		generoiPalvelupisteet(jakaumat);

		kaikkiKassat = yhdistaPalvelupisteet(kassat, ipKassat);
		palvelupisteet = yhdistaPalvelupisteet(ruokatiskit, kaikkiKassat);

		saapumisprosessi = new Saapumisprosessi(new Negexp(20, 5), tapahtumalista, TapahtumanTyyppi.ARR1);
	}

	private Palvelupiste[] yhdistaPalvelupisteet(Palvelupiste[] pp1, Palvelupiste[] pp2) {
		if (pp1 == null && pp2 == null)
			return new Palvelupiste[0];
		int pp1Lkm = pp1.length;
		int pp2Lkm = pp2.length;
		int resultLkm = pp1Lkm + pp2Lkm;
		Palvelupiste[] result = new Palvelupiste[resultLkm];

		int j = 0;
		for (int i = 0; i < pp1Lkm; i++) {
			result[j] = pp1[i];
			j++;
		}

		for (int i = 0; i < pp2Lkm; i++) {
			result[j] = pp2[i];
			j++;
		}

		return result;
	}

	@Override
	protected void tulokset() {
		System.out.println("Simulointi päättyi kello " + Kello.getInstance().getAika());
		kontrolleri.simuloinninJalkeen();
		Set<Palvelupiste> palvelupisteSet = new HashSet<Palvelupiste>();
		Simulointi sim = new Simulointi();
		for (int i = 0; i < palvelupisteet.length; i++) {
			// HUOM: Pakko laskea suorituskykysuureet ennen raportointia
			palvelupisteet[i].laskeSuorituskykysuureet();
			palvelupisteet[i].raportti();

			// Tietokanta jutut
			palvelupisteet[i].setSimulointi(sim);
			palvelupisteSet.add(palvelupisteet[i]);
		}

		// Tallenna tiedot tietokannalle (simuloinnin meta data + suorituskykysuureet)

		Timestamp ts = new Timestamp(System.currentTimeMillis());
		sim.setAikaleima(ts);
		sim.setRuokatiskienLkm(ruokatiskienLkm);
		sim.setKassojenLkm(kassojenLkm);
		sim.setIpKassojenLkm(ipKassojenLkm);
		sim.setAsiakkaatKerrallaMin(asiakkaatKerralla[0]);
		sim.setAsiakkaatKerrallaMax(asiakkaatKerralla[0]);
		sim.setRuokatiskinAikaMin(ruokatiskinAikaRajat[0]);
		sim.setRuokatiskinAikaMax(ruokatiskinAikaRajat[1]);
		sim.setKassanAikaMin(kassanAikaRajat[0]);
		sim.setKassanAikaMax(kassanAikaRajat[1]);
		sim.setIpKassanAikaMin(ipKassanAikaRajat[0]);
		sim.setIpKassanAikaMax(ipKassanAikaRajat[1]);

		boolean status1 = simulointiDAO.lisaaSimulointi(sim);
		boolean status2 = palvelupisteDAO.addPalvelupisteSet(palvelupisteSet);

		if (status1 == true && status2 == true)
			System.out.println("Tietojen tallennus on onnistunut");
		else {
			System.out.println("Tietojen tallennus on epäonnistunut");
		}
	}

	// Asetuksien getterit ja setterit
	public int getRuokatiskienLkm() {
		return ruokatiskienLkm;
	}

	public int getKassojenLkm() {
		return kassojenLkm;
	}

	public int getIpKassojenLkm() {
		return ipKassojenLkm;
	}

	public int[] getAsiakkaatKerralla() {
		return asiakkaatKerralla;
	}

	public double[] getRuokatiskinAikaRajat() {
		return ruokatiskinAikaRajat;
	}

	public double[] getKassanAikaRajat() {
		return kassanAikaRajat;
	}

	public double[] getIpKassanAikaRajat() {
		return ipKassanAikaRajat;
	}

	public void setRuokatiskienLkm(int ruokatiskienLkm) {
		if (ruokatiskienLkm >= 0)
			this.ruokatiskienLkm = ruokatiskienLkm;
	}

	public void setKassojenLkm(int kassojenLkm) {
		if (kassojenLkm >= 0)
			this.kassojenLkm = kassojenLkm;
	}

	public void setIpKassojenLkm(int ipKassojenLkm) {
		if (ipKassojenLkm >= 0)
			this.ipKassojenLkm = ipKassojenLkm;
	}

	public void setAsiakkaatKerralla(int[] asiakkaatKerralla) {
		if (asiakkaatKerralla != null && asiakkaatKerralla.length >= 2) {
			if (asiakkaatKerralla[0] >= 0 && asiakkaatKerralla[1] >= 1
					&& asiakkaatKerralla[0] <= asiakkaatKerralla[1]) {
				this.asiakkaatKerralla = asiakkaatKerralla;
			} else {
				System.err.println("setAsiakkaatKerralla(): tarkista parametri");
			}
		} else {
			System.err.println("setAsiakkaatKerralla(): parametri on null tai koko on pienempi kuin 2");
		}
	}

	public void setRuokatiskinAikaRajat(double[] ruokatiskinAikaRajat) {
		if (tarkistaRajat(ruokatiskinAikaRajat, "setRuokatiskinAikaRajat"))
			this.ruokatiskinAikaRajat = ruokatiskinAikaRajat;
	}

	public void setKassanAikaRajat(double[] kassanAikaRajat) {
		if (tarkistaRajat(kassanAikaRajat, "setKassanAikaRajat"))
			this.kassanAikaRajat = kassanAikaRajat;
	}

	public void setIpKassanAikaRajat(double[] ipKassanAikaRajat) {
		if (tarkistaRajat(ipKassanAikaRajat, "setIpKassanAikaRajat"))
			this.ipKassanAikaRajat = ipKassanAikaRajat;
	}

	private boolean tarkistaRajat(double[] rajat, String metoodinNimi) {
		boolean status = false;
		if (rajat != null && rajat.length >= 2) {
			if (rajat[0] >= 0 && rajat[1] >= 1 && rajat[0] <= rajat[1]) {
				status = true;
			} else {
				System.err.println(metoodinNimi + "(): tarkista parametri");
			}
		} else {
			System.err.println(metoodinNimi + "(): parametri on null tai koko on pienempi kuin 2");
		}

		return status;
	}
	
	public void reset() {
		Asiakas a = new Asiakas();
		Palvelupiste pp = new Palvelupiste();
		a.reset();
		pp.reset();
		super.palvelupisteet = null;
		ruokatiskiJakauma = this.getNormalGenerator(ruokatiskinAikaRajat[0], ruokatiskinAikaRajat[1]);
		kassaJakauma = this.getNormalGenerator(kassanAikaRajat[0], kassanAikaRajat[1]);
		ipKassaJakauma = this.getNormalGenerator(ipKassanAikaRajat[0], ipKassanAikaRajat[1]);
	}

}