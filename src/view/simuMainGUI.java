package view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import controller.IKontrolleri;
import controller.Kontrolleri;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import simu.framework.Trace;
import simu.framework.Trace.Level;
import simu.model.Asiakas;
import simu.model.Palvelupiste;
import simu.model.PalvelupisteenTyyppi;

/**
 * <p>This class is the main GUI thread for the simulator.</p>
 * <p>The class works as an intermediate for directing information between the user and the controller, that works between the view and model packages.</p>
 * 
 * @author Joona Nylander
 */

public class simuMainGUI extends Application implements ISimulaattorinUI {
	/**
	 * @param kontrolleri Kontrolleri-object, which is used to define application behavior
	 * @see <a href="../../controller/IKontrolleri.html">IKontrolleri</a>
	 */
	private IKontrolleri kontrolleri;
	/**
	 * Palvelupisteet is an ArrayList that consists of Rectangle's which represent the service points. 
	 */
	private List<Rectangle> palvelupisteet = new ArrayList<Rectangle>();
	/**
	 * Primary stage for the GUI.
	 */
	private Stage primaryStage;
	/**
	 * Root layout of the primary stage's scene.
	 */
	private AnchorPane root;
	/**
	 * Contains a list of Customers that went through the simulation.
	 */
	private ObservableList<Asiakas> asiakaslista = FXCollections.observableArrayList();
	/**
	 * List of all the food courts in GUI.	
	 */
	private List<Rectangle> ruokalinjastot = new ArrayList<Rectangle>();
	/**
	 * List of all the cashiers in GUI.	
	 */
	private List<Rectangle> kassat = new ArrayList<Rectangle>();
	/**
	 * List of all the self-checkouts in GUI.	
	 */
	private List<Rectangle> ipKassat = new ArrayList<Rectangle>();

	/**
	 * Start button.
	 */
	@FXML
	private Button kaynnistaBtn;
	/**
	 * Button for slowing the simulation speed
	 */
	@FXML
	private Button hidastaBtn;

	/**
	 * Button for raising up the simulation speed
	 */
	@FXML
	private Button nopeutaBtn;
	/**
	 * Button for showing the Customer results window.
	 */
	@FXML
	private Button asiakkaatBtn;
	/**
	 * A TextField for the user to input wanted simulation time.
	 */
	@FXML
	private TextField aikaTF;
	/**
	 * A Text that shows the time used during simulation.
	 */
	@FXML
	private Text aikaCount;
	/**
	 * A ProgressBar that visualizes how the simulation progresses.
	 */
	@FXML
	private ProgressBar progressbar;
	/**
	 * A TextField for the user to input wanted delay.
	 */
	@FXML
	private TextField viiveTF;
	/**
	 * A TextField for the user to input wanted minimum food court time limit.
	 */
	@FXML
	private TextField ruokaMin;
	/**
	 * A TextField for the user to input wanted maximum food court time limit.
	 */
	@FXML
	private TextField ruokaMax;
	/**
	 * A TextField for the user to input wanted minimum cashier time limit.
	 */
	@FXML
	private TextField kassaMin;
	/**
	 * A TextField for the user to input wanted maximum cashier time limit.
	 */
	@FXML
	private TextField kassaMax;
	/**
	 * A TextField for the user to input wanted minimum self-checkout time limit.
	 */
	@FXML
	private TextField ipKassaMin;
	/**
	 * A TextField for the user to input wanted maximum self-checkout time limit.
	 */
	@FXML
	private TextField ipKassaMax;
	/**
	 * A VBox that the food courts are added to
	 */
	@FXML
	private VBox ruokaVBox;
	/**
	 * A VBox that the cashiers are added to
	 */
	@FXML
	private VBox kassaVBox;
	/**
	 * A VBox that the self-checkouts are added to
	 */
	@FXML
	private VBox ipKassaVbox;
	/**
	 * Adds one food court
	 */
	@FXML
	private Button ruokaPlus;
	/**
	 * Adds one cashier
	 */
	@FXML
	private Button kassaPlus;
	/**
	 * Adds one self-checkout
	 */
	@FXML
	private Button ipKassaPlus;
	/**
	 * Resets the value of the simulator.
	 */
	@FXML
	private Button tyhjennaBtn;
	
	/**
	 * Starts the GUI.
	 */
	@Override
	public void start(Stage primaryStage) {
		Trace.setTraceLevel(Level.INFO);
		try {
			this.palvelupisteet = new ArrayList<Rectangle>();
			this.primaryStage = primaryStage;
			this.primaryStage.setTitle("Simulaattori");
			this.kontrolleri = new Kontrolleri(this);

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(simuMainGUI.class.getResource("simuView.fxml"));

			root = (AnchorPane) loader.load();
			Scene scene = new Scene(root);

			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Starts the simulation.
	 */
	public void kaynnistaSimulaatio() {
		this.kontrolleri = new Kontrolleri(this);
		// Tarkastetaan onko viiveen ja simulointiajan tekstikentät täytetty
		if (tarkastaSyotteet()) {
			kontrolleri.kaynnistaSimulointi();
			kaynnistaBtn.setDisable(true);
			kassaPlus.setDisable(true);
			ruokaPlus.setDisable(true);
			ipKassaPlus.setDisable(true);
		}
	}
	/**
	 * Checks if the time and delay inputs are empty.
	 * @return True if the inputs are not empty. False if at least one of them are.
	 */
	private boolean tarkastaSyotteet() {
		if (viiveTF.getText() != "" && aikaTF.getText() != "") {
			if (ruokalinjastot.size() == 0 || kassat.size() == 0 || ipKassat.size() == 0) {
				Alert a = new Alert(AlertType.WARNING);
				a.setTitle("Varoitus");
				a.setHeaderText("Palvelupisteitä on liian vähän!");
				a.setContentText("Jokaista palvelupistetyyppiä on oltava vähintään yksi.");
				a.showAndWait();
				return false;
			} else
				return true;
		} else {
			Alert a = new Alert(AlertType.WARNING);
			a.setTitle("Varoitus");
			a.setHeaderText("Antamasi syötteet ovat väärin tai puuttuvat");
			a.setContentText("Tarkasta antamasi syötteet uudelleen.");
			a.showAndWait();
			return false;
		}
	}
	/**
	 * Creates a service point.
	 */
	public void lisääPalvelupiste(PalvelupisteenTyyppi ppT) {
		Rectangle pp = new Rectangle(75, 75);
		palvelupisteet.add(pp);
		pp.setFill(Color.GREEN);

		// Tapahtumankäsittelijän luonti PalvelupisteenTyypin perusteella
		// Palvelupisteet luodaan OmaMoottorissa järjestyksessä
		// Ruokatiski->Kassa->IPKassa, jonka takia pp:n naytaTulos-metodin
		// parametri-indeksi annetaan samassa järjestyksessä

		if (ppT == PalvelupisteenTyyppi.RUOKATISKI) {
			ruokaVBox.getChildren().add(pp);
			ruokalinjastot.add(pp);
			pp.setOnMouseClicked((e) -> {
				naytaTulos(ruokalinjastot.indexOf(pp));
			});
		} else if (ppT == PalvelupisteenTyyppi.KASSA) {
			kassaVBox.getChildren().add(pp);
			kassat.add(pp);
			pp.setOnMouseClicked((e) -> {
				naytaTulos(kassat.indexOf(pp) + ruokalinjastot.size());
			});
		} else if (ppT == PalvelupisteenTyyppi.IPKASSA) {
			ipKassaVbox.getChildren().add(pp);
			ipKassat.add(pp);
			pp.setOnMouseClicked((e) -> {
				naytaTulos(ipKassat.indexOf(pp) + ruokalinjastot.size() + kassat.size());
			});
		}
	}
	/**
	 * Resets the values of the simulator.
	 */
	// Palauttaa oletusarvot takaisin, jotta simulaation voi ajaa uudelleen.
	public void reset() {
		ruokaVBox.getChildren().clear();
		kassaVBox.getChildren().clear();
		ipKassaVbox.getChildren().clear();

		aikaTF.setText("");
		viiveTF.setText("");
		ruokaMin.setText("");
		ruokaMax.setText("");
		kassaMin.setText("");
		kassaMax.setText("");
		ipKassaMin.setText("");
		ipKassaMax.setText("");

		palvelupisteet.clear();
		asiakaslista.clear();

		ruokalinjastot.clear();
		kassat.clear();
		ipKassat.clear();

		ruokaPlus.setDisable(false);
		kassaPlus.setDisable(false);
		ipKassaPlus.setDisable(false);

		aikaCount.setText("Käytettu aika: ");
		progressbar.setProgress(0);
		kontrolleri.reset();
		kaynnistaBtn.setDisable(false);
	}
	
	/**
	 * Creates a food court. 
	 */
	// Maksimimäärä palvelupisteitä/ppTyyppi: 6
	public void lisaaRuokalinjasto() {
		if (ruokalinjastot.size() < 6) {
			lisääPalvelupiste(PalvelupisteenTyyppi.RUOKATISKI);
			if (ruokalinjastot.size() == 6)
				ruokaPlus.setDisable(true);
		}
	}
	
	/**
	 * Creates a cashier.
	 */
	public void lisaaKassa() {
		if (kassat.size() < 6) {
			lisääPalvelupiste(PalvelupisteenTyyppi.KASSA);
			if (kassat.size() == 6)
				kassaPlus.setDisable(true);
		}
	}

	/**
	 * Creates a self-checkout.
	 */
	public void lisaaIPKassa() {
		if (ipKassat.size() < 6) {
			lisääPalvelupiste(PalvelupisteenTyyppi.IPKASSA);
			if (ipKassat.size() == 6)
				ipKassaPlus.setDisable(true);
		}
	}
	
	/**
	 * Shows the result of a single service point.
	 */
	// Toimii jokaisen palvelupisteen (GUI:ssa Rectangle:t) onClick-metodina.
	// Hakee kontrollerilta moottorin palvelupisteet ja näyttää parametrin tulokset.
	private void naytaTulos(int indeksi) {
		try {
			Palvelupiste[] tulokset = kontrolleri.getPalvelupisteet();
			if (tulokset != null) {
				Alert a = new Alert(AlertType.INFORMATION);
				a.setTitle("Tulokset");
				a.setHeaderText("Palvelupisteen " + tulokset[indeksi].getPpNimi() + " " + tulokset[indeksi].getPpNum()
						+ " tulokset");
				a.setContentText(tulokset[indeksi].getSimuTulos());
				a.showAndWait();
			}
		} catch (NullPointerException e) {
			throw e;
		}
	}
	
	/**
	 * Opens the customer result window and fills it with data from the controller.
	 */
	// Luo uuden ikkunan, jossa näkyy asiakkaiden simulointitulokset
	public void avaaAsiakasIkkuna() {
		// TableView esittää asiakkaiden simutulokset
		TableView<Asiakas> tv = new TableView<Asiakas>();
		// Luodaan kolumnit ja asetetaan arvojen nimet, jotka lisätään niihin
		TableColumn<Asiakas, Integer> idColumn = new TableColumn<>("Asiakasnumero");
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

		TableColumn<Asiakas, Double> aikaColumn = new TableColumn<>("Käytetty aika");
		aikaColumn.setCellValueFactory(new PropertyValueFactory<>("kokonaisaika"));

		tv.getColumns().add(idColumn);
		tv.getColumns().add(aikaColumn);

		idColumn.setResizable(false);
		aikaColumn.setResizable(false);
		idColumn.prefWidthProperty().bind(tv.widthProperty().multiply(0.5));
		aikaColumn.prefWidthProperty().bind(tv.widthProperty().multiply(0.5));

		// Lisätään asiakkaat TableViewiin
		for (Asiakas a : asiakaslista) {
			tv.getItems().add(a);
		}

		VBox vbox = new VBox(tv);
		Scene asiakasScene = new Scene(vbox, 300, 400);
		Stage asiakasIkkuna = new Stage();

		asiakasIkkuna.setTitle("Asiakkaat");
		asiakasIkkuna.setScene(asiakasScene);
		asiakasIkkuna.showAndWait();
	}

	/**
	 * Slows down the simulation speed.
	 */
	public void hidasta() {
		kontrolleri.hidasta();
	}

	/**
	 * Speeds up the simulation speed.
	 */
	public void nopeuta() {
		kontrolleri.nopeuta();
	}
	
	/**
	 * Allocates a service point in the GUI. 
	 */
	public void varaaPalvelupiste(int i) {
		palvelupisteet.get(i).setFill(Color.RED);
	}
	
	/**
	 * Deallocates a service point in the GUI.
	 */
	@Override
	public void vapautaPalvelupiste(int i) {
		palvelupisteet.get(i).setFill(Color.GREEN);
	}
	/**
	 * Method to be used after the simulation is done, it is used to control the GUI buttons.
	 */
	public void simuloinninJalkeen() {
		asiakkaatBtn.setDisable(false);
	}
	/**
	 * Sets the time for aikaCount and progress for progressbar. 
	 */
	public void setAikaCounter(int aika) {
		aikaCount.setText("Käytettu aika: " + Integer.toString(aika));
		progressbar.setProgress(aika / Double.parseDouble(aikaTF.getText()));
	}
	/**
	 * Adds a customer to the customer list.
	 */
	public void lisaaAsiakaslistaan(Asiakas asiakas) {
		asiakaslista.add(asiakas);
	}
	/**
	 * @return time
	 */
	@Override
	public double getAika() {
		Long aika = (long) 0;
		try {
			aika = Long.parseLong(aikaTF.getText());
		} catch (NumberFormatException e) {
			Alert a = new Alert(AlertType.WARNING);
			a.setTitle("Varoitus");
			a.setHeaderText("Antamasi aika on syötettu annettu väärin");
			a.setContentText("Tarkasta antamasi arvo. Muista, että voit syöttää vain numeroita!");
			a.showAndWait();
		}
		return aika;
	}
	
	/**
	 * @return delay
	 */
	@Override
	public long getViive() {
		Long viive = (long) 0;
		try {
			viive = Long.parseLong(viiveTF.getText());
		} catch (NumberFormatException e) {
			Alert a = new Alert(AlertType.WARNING);
			a.setTitle("Varoitus");
			a.setHeaderText("Antamasi viive on annettu väärin");
			a.setContentText("Tarkasta antamasi arvo. Muista, että voit syöttää vain numeroita!");
			a.showAndWait();
		}
		return viive;
	}
	
	/**
	 * @return food courts arraylist size
	 */
	public int getRuokalinjastot() {
		return ruokalinjastot.size();
	}
	/**
	 * @return cashier arraylist size
	 */
	public int getKassat() {
		return kassat.size();
	}
	/**
	 * @return self-checkout arraylist size
	 */
	public int getIPKassat() {
		return ipKassat.size();
	}
	/**
	 * @return food courts minimum time limit
	 */
	public double getRuokaMin() {
		if (ruokaMin.getText() != "")
			return Double.parseDouble(ruokaMin.getText());
		else
			return 0;
	}
	/**
	 * @return food courts maximum time limit
	 */
	public double getRuokaMax() {
		if (ruokaMax.getText() != "")
			return Double.parseDouble(ruokaMax.getText());
		else
			return 0;
	}
	/**
	 * @return cashiers minimum time limit
	 */
	public double getKassaMin() {
		if (kassaMin.getText() != "")
			return Double.parseDouble(kassaMin.getText());
		else
			return 0;
	}
	/**
	 * @return cashiers maximum time limit
	 */
	public double getKassaMax() {
		if (kassaMax.getText() != "")
			return Double.parseDouble(kassaMax.getText());
		else
			return 0;
	}
	/**
	 * @return self-checkouts minimum time limit
	 */
	public double getIpKassaMin() {
		if (ipKassaMin.getText() != "")
			return Double.parseDouble(ipKassaMin.getText());
		else
			return 0;
	}
	/**
	 * @return self-checkouts minimum time limit
	 */
	public double getIpKassaMax() {
		if (ipKassaMax.getText() != "")
			return Double.parseDouble(ipKassaMax.getText());
		else
			return 0;
	}
	/**
	 * Launches the GUI.
	 */
	public static void main(String[] args) {
		launch(args);
	}
}