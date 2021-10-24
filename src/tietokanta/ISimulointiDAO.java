package tietokanta;

import java.util.List;

public interface ISimulointiDAO {
	boolean lisaaSimulointi(Simulointi sim);
	List<Simulointi> haeSimuloinnit();
}
