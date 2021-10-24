package tietokanta;

import java.util.Set;

import simu.model.Palvelupiste;

public interface IPalvelupisteDAO {
	boolean addPalvelupiste(Palvelupiste pp);
	boolean addPalvelupisteSet(Set<Palvelupiste> ppSet);
}
