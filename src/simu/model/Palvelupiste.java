package simu.model;

import java.text.DecimalFormat;
import java.util.LinkedList;

import eduni.distributions.ContinuousGenerator;
import eduni.distributions.Normal;
import simu.framework.Kello;
import simu.framework.Tapahtuma;
import simu.framework.Tapahtumalista;
import simu.framework.Trace;
import tietokanta.Simulointi;

public class Palvelupiste {

	private LinkedList<Asiakas> jono = new LinkedList<Asiakas>();

	Kello kello = Kello.getInstance();
	private ContinuousGenerator generator;
	private Tapahtumalista tapahtumalista;
	private TapahtumanTyyppi skeduloitavanTapahtumanTyyppi;

	private boolean varattu = false;

	// Palvelupisteen tiedot
	private int ppNum, ppID;
	private static int nextRuokatiskiNum = 1, nextKassaNum = 1, nextIpKassaNum = 1, nextPpId = 0;
	private String ppNimi;
	private PalvelupisteenTyyppi ppTyyppi = null;

	// PRIMARY KEY
	private int id;

	// FOREIGN KEY
	private Simulointi simulointi;

	// Suorituskykysuureet + DB taulukkojen tiedot
	private int asiakkaidenLkm;
	private double aktiiviaika;
	private double kokoOleskAika;
	private double kayttoaste;
	private double suoritusteho;
	private double keskiPalveluaika;
	private double keskiLapiMenoAika;
	private double keskiJononpituus;

	public Palvelupiste() {
	}

	public Palvelupiste(Normal generator, Tapahtumalista tapahtumalista, PalvelupisteenTyyppi ppt, int id) {
		this.tapahtumalista = tapahtumalista;
		this.generator = generator;
		this.skeduloitavanTapahtumanTyyppi = maaritaTT(ppt);
		this.ppTyyppi = ppt;
		this.ppID = id;
		this.setPpInfo();
		this.asiakkaidenLkm = 0;
		this.kokoOleskAika = 0;
	}

	public void lisaaJonoon(Asiakas a) { // Jonon 1. asiakas aina palvelussa
		jono.add(a);
	}

	public Asiakas otaJonosta() { // Poistetaan palvelussa ollut
		varattu = false;
		return jono.poll();
	}

	public void aloitaPalvelu() { // Aloitetaan uusi palvelu, asiakas on jonossa palvelun aikana
		Asiakas palvAsiakas = jono.peek();
		Trace.out(Trace.Level.INFO, this.ppNimi + ": Aloitetaan uusi palvelu asiakkaalle " + palvAsiakas.getId());

		varattu = true;
		double palveluaika = generator.sample();
		tapahtumalista.lisaa(new Tapahtuma(skeduloitavanTapahtumanTyyppi, kello.getAika() + palveluaika, ppID));

		// Suorituskykysureiden päivittäminen
		this.asiakkaidenLkm++;
		aktiiviaika += palveluaika;
		double asSaapJonoonHetki = palvAsiakas.getJonoonSaapumisAika();
		double asPoistHetki = kello.getAika() + palveluaika;
		kokoOleskAika += (asPoistHetki - asSaapJonoonHetki);
		System.out.println();
	}

	public boolean onVarattu() {
		return varattu;
	}

	public boolean onJonossa() {
		return jono.size() != 0;
	}

	public void laskeSuorituskykysuureet() {
		this.kayttoaste = laskeKayttoaste();
		this.suoritusteho = laskeSuoritusteho();
		this.keskiPalveluaika = laskeKeskiPalveluaika();
		this.keskiLapiMenoAika = laskeKeskiLapiMenoAika();
		this.keskiJononpituus = laskeKeskiJononpituus();
	}
	
	// Tulostaa konsoliin palvelupisteen tulokset
	public void raportti() {
		System.out.println("\n" + this.ppNimi + (this.ppNum) + ", suorituskykysuureet:\n");

		System.out.println("Palveltujen asiakkaiden lukumäärä: " + this.getAsiakkaidenLkm());
		System.out.println("Palvelupisteen aktiiviaika: " + this.getAktiiviaika());
		System.out.println("Asiakkaiden kokonaisoleskeluaika palvelupisteessa: " + this.getKokoOleskAika());
		System.out.println("Palvelupisteen käyttöaste: " + this.getKayttoaste());
		System.out.println("Palvelupisteen suoritusteho: " + this.getSuoritusteho());
		System.out.println("Palvelupisteen keskipalveluaika: " + this.getKeskiPalveluaika());
		System.out.println("Palvelupisteen keskiläpimenoaika: " + this.getKeskiLapiMenoAika());
		System.out.println("Palvelupisteen keski jononpituus: " + this.getKeskiJononpituus() + "\n");
	}
	
	// Palauttaa palvelupisteen simulointitulokset String-muodossa.
	public String getSimuTulos() {
		// Tulokset ilmoitetaan DecimalFormatin avulla 2 desimaalin tarkkuudella.
		DecimalFormat df = new DecimalFormat("#.##");
		return "Palveltujen asiakkaiden lukumäärä: " + this.getAsiakkaidenLkm() + "\nAktiiviaika: "
				+ df.format(this.getAktiiviaika()) + "\nAsiakkaiden kokonaisoleskeluaika palvelupisteessa: "
				+ df.format(this.getKokoOleskAika()) + "\nKäyttöaste: " + df.format(this.getKayttoaste())
				+ "\nSuoritusteho: " + df.format(this.getSuoritusteho()) + "\nPalveluajan keskiarvo: "
				+ df.format(this.getKeskiPalveluaika()) + "\nLäpimenoajan keskiarvo: "
				+ df.format(this.getKeskiLapiMenoAika()) + "\nJonon keskimääräinen pituus: "
				+ df.format(this.getKeskiJononpituus()) + "\n";
	}

	private void setPpInfo() {
		switch (this.ppTyyppi) {
		case RUOKATISKI:
			this.ppNum = nextRuokatiskiNum;
			ppNimi = "Ruokatiski";
			nextRuokatiskiNum++;
			break;
		case KASSA:
			this.ppNum = nextKassaNum;
			ppNimi = "Kassa";
			nextKassaNum++;
			break;
		case IPKASSA:
			this.ppNum = nextIpKassaNum;
			ppNimi = "IP kassa";
			nextIpKassaNum++;
			break;
		}
		nextPpId++;
	}

	private TapahtumanTyyppi maaritaTT(PalvelupisteenTyyppi ppt) {
		TapahtumanTyyppi tt = null;
		if (ppt == PalvelupisteenTyyppi.RUOKATISKI)
			tt = TapahtumanTyyppi.DEP1;
		else if (ppt == PalvelupisteenTyyppi.KASSA || ppt == PalvelupisteenTyyppi.IPKASSA)
			tt = TapahtumanTyyppi.DEP2;

		return tt;
	}

	// DB tiedot (id, simulointi + suorituskykysuureet + nimi)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Simulointi getSimulointi() {
		return simulointi;
	}

	public void setSimulointi(Simulointi simulointi) {
		this.simulointi = simulointi;
	}

	// Suorituskykysuureet

	// getters
	public int getAsiakkaidenLkm() {
		return asiakkaidenLkm;
	}

	public double getAktiiviaika() {
		return aktiiviaika;
	}

	public double getKokoOleskAika() {
		return kokoOleskAika;
	}

	public double getKayttoaste() {
		return kayttoaste;
	}

	public double getSuoritusteho() {
		return suoritusteho;
	}

	public double getKeskiPalveluaika() {
		return keskiPalveluaika;
	}

	public double getKeskiLapiMenoAika() {
		return keskiLapiMenoAika;
	}

	public double getKeskiJononpituus() {
		return keskiJononpituus;
	}

	// setters
	public void setAsiakkaidenLkm(int asiakkaidenLkm) {
		this.asiakkaidenLkm = asiakkaidenLkm;
	}

	public void setAktiiviaika(double aktiiviaika) {
		this.aktiiviaika = aktiiviaika;
	}

	public void setKokoOleskAika(double kokoOleskAika) {
		this.kokoOleskAika = kokoOleskAika;
	}

	public void setKayttoaste(double kayttoaste) {
		this.kayttoaste = kayttoaste;
	}

	public void setSuoritusteho(double suoritusteho) {
		this.suoritusteho = suoritusteho;
	}

	public void setKeskiPalveluaika(double keskiPalveluaika) {
		this.keskiPalveluaika = keskiPalveluaika;
	}

	public void setKeskiLapiMenoAika(double keskiLapiMenoAika) {
		this.keskiLapiMenoAika = keskiLapiMenoAika;
	}

	public void setKeskiJononpituus(double keskiJononpituus) {
		this.keskiJononpituus = keskiJononpituus;
	}

	// laskeminen
	private double laskeKayttoaste() {
		return this.aktiiviaika / kello.getAika();
	}

	private double laskeSuoritusteho() {
		return this.asiakkaidenLkm / kello.getAika();
	}

	private double laskeKeskiPalveluaika() {
		if (this.asiakkaidenLkm > 0)
			return this.aktiiviaika / this.asiakkaidenLkm;
		return 0d;
	}

	private double laskeKeskiLapiMenoAika() {
		if (this.asiakkaidenLkm > 0)
			return this.kokoOleskAika / this.asiakkaidenLkm;
		return 0d;
	}

	private double laskeKeskiJononpituus() {
		return this.kokoOleskAika / kello.getAika();
	}

	public int getJonoSize() {
		return this.jono.size();
	}

	public int getPpID() {
		return this.ppID;
	}

	public void setPpID(int ppID) {
		this.ppID = ppID;
	}

	public String getPpNimi() {
		return this.ppNimi;
	}

	public void setPpNimi(String ppNimi) {
		this.ppNimi = ppNimi;
	}

	public int getPpNum() {
		return this.ppNum;
	}

	public void reset() {
		nextRuokatiskiNum = 1;
		nextKassaNum = 1; 
		nextIpKassaNum = 1;
		nextPpId = 0;
	}
	
	@Override
	public String toString() {
		return "Palvelupiste " + ppNimi;
	}
}
