package tietokanta;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Set;

import simu.model.Palvelupiste;

public class Simulointi {

	public Simulointi(){}
	
	//PRIMARY KEY
	private int id;
	
	private Set<Palvelupiste> palvelupisteet;
	
	//FIELDS
	private Timestamp aikaleima;	
	private int ruokatiskienLkm, kassojenLkm, ipKassojenLkm;
	private int asiakkaatKerrallaMin, asiakkaatKerrallaMax;
	private double ruokatiskinAikaMin, ruokatiskinAikaMax;
	private double kassanAikaMin, kassanAikaMax;
	private double ipKassanAikaMin, ipKassanAikaMax;
	
	public String getFormattedAikaleima() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		if(this.aikaleima != null)
			return dateFormat.format(this.aikaleima);
		else
			return "";
	}
	
	//Getterit
	public int getId() {	return id;	}
	public Timestamp getAikaleima() {	return aikaleima;	}
	public int getRuokatiskienLkm() {	return ruokatiskienLkm;	}
	public int getKassojenLkm() {	return kassojenLkm;	}
	public int getIpKassojenLkm() {	return ipKassojenLkm;	}
	public int getAsiakkaatKerrallaMin() {	return asiakkaatKerrallaMin;	}
	public int getAsiakkaatKerrallaMax() {	return asiakkaatKerrallaMax;	}
	public double getRuokatiskinAikaMin() {	return ruokatiskinAikaMin;	}
	public double getRuokatiskinAikaMax() {	return ruokatiskinAikaMax;	}
	public double getKassanAikaMin() {	return kassanAikaMin;	}
	public double getKassanAikaMax() {	return kassanAikaMax;	}
	public double getIpKassanAikaMin() {	return ipKassanAikaMin;	}
	public double getIpKassanAikaMax() {	return ipKassanAikaMax;	}
	
	public Set<Palvelupiste> getPalvelupisteet() {	return palvelupisteet;	}
	
	//Setterit	
	public void setId(int id) {	this.id = id;	}	
	public void setAikaleima(Timestamp aikaleima) {	this.aikaleima = aikaleima;	}	
	public void setRuokatiskienLkm(int ruokatiskienLkm) {	this.ruokatiskienLkm = ruokatiskienLkm;	}
	public void setKassojenLkm(int kassojenLkm) {	this.kassojenLkm = kassojenLkm;	}
	public void setIpKassojenLkm(int ipKassojenLkm) {	this.ipKassojenLkm = ipKassojenLkm;	}
	public void setAsiakkaatKerrallaMin(int asiakkaatKerrallaMin) {	this.asiakkaatKerrallaMin = asiakkaatKerrallaMin;	}
	public void setAsiakkaatKerrallaMax(int asiakkaatKerrallaMax) {	this.asiakkaatKerrallaMax = asiakkaatKerrallaMax;	}
	public void setRuokatiskinAikaMin(double ruokatiskinAikaMin) {	this.ruokatiskinAikaMin = ruokatiskinAikaMin;	}
	public void setRuokatiskinAikaMax(double ruokatiskinAikaMax) {	this.ruokatiskinAikaMax = ruokatiskinAikaMax;	}
	public void setKassanAikaMin(double kassanAikaMin) {	this.kassanAikaMin = kassanAikaMin;	}
	public void setKassanAikaMax(double kassanAikaMax) {	this.kassanAikaMax = kassanAikaMax;	}
	public void setIpKassanAikaMin(double ipKassanAikaMin) {	this.ipKassanAikaMin = ipKassanAikaMin;	}
	public void setIpKassanAikaMax(double ipKassanAikaMax) {	this.ipKassanAikaMax = ipKassanAikaMax;	}
	
	public void setPalvelupisteet(Set<Palvelupiste> palvelupisteet) {	this.palvelupisteet = palvelupisteet;	}
	
	@Override
	public String toString() {
		return "Simulointi " + this.id + " pvm: " + getFormattedAikaleima();
	}
}
