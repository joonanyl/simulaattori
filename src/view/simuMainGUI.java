package view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import controller.IKontrolleri;
import controller.Kontrolleri;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
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

public class simuMainGUI extends Application implements ISimulaattorinUI {

	private IKontrolleri kontrolleri;
	private List<Rectangle> ppTesti = new ArrayList<Rectangle>();
	private Stage primaryStage;
	private AnchorPane root;
	private ObservableList<Asiakas> asiakaslista = FXCollections.observableArrayList();
	private int ruokalinjastot;
	private int kassat;
	private int ipKassat;
	
	@FXML
	private Button kaynnistaBtn;
	@FXML
	private Button hidastaBtn;

	@FXML
	private Button nopeutaBtn;
	@FXML
	private Button asiakkaatBtn;

	@FXML
	private TextField aikaTF;
	@FXML
	private Text aikaCount;

	@FXML
	private TextField viiveTF;

	@FXML
	private VBox ruokaVBox;
	@FXML
	private VBox kassaVBox;
	@FXML
	private VBox ipKassaVbox;

	@FXML
	private Button ruokaPlus;
	@FXML
	private Button kassaPlus;
	@FXML
	private Button ipKassaPlus;
	@FXML
	private Button tyhjennaBtn;

	@Override
	public void start(Stage primaryStage) {
		Trace.setTraceLevel(Level.INFO);
		try {
			this.ppTesti = new ArrayList<Rectangle>();
			this.primaryStage = primaryStage;
			this.primaryStage.setTitle("Simulaattori");
			this.kontrolleri = new Kontrolleri(this);
			this.ruokalinjastot = 0;
			this.kassat = 0;
			this.ipKassat = 0;

			FXMLLoader loader = new FXMLLoader();
			// OG
			// loader.setLocation(simuMainGUI.class.getResource("simuNakyma.fxml"));

			// Testi
			loader.setLocation(simuMainGUI.class.getResource("simuView.fxml"));

			root = (AnchorPane) loader.load();
			Scene scene = new Scene(root);

			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void kaynnistaSimulaatio() {
		this.kontrolleri = new Kontrolleri(this);
		// Tarkastetaan onko viiveen ja simulointiajan tekstikentät tyhjiä
		if (tarkastaSyotteet())
			kontrolleri.kaynnistaSimulointi();
	}

	private boolean tarkastaSyotteet() {
		if (viiveTF.getText() != "" && aikaTF.getText() != "") {
			if (ruokalinjastot == 0 || kassat == 0 || ipKassat == 0) {
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

	public void lisääPalvelupiste(PalvelupisteenTyyppi ppT) {
		Rectangle pp = new Rectangle(75, 75);
		ppTesti.add(pp);
		pp.setFill(Color.GREEN);
		pp.setOnMouseClicked((e) -> {
			showTulos(ppTesti.indexOf(pp));
		});
		
		
		if (ppT == PalvelupisteenTyyppi.RUOKATISKI)
			ruokaVBox.getChildren().add(pp);
		else if (ppT == PalvelupisteenTyyppi.KASSA)
			kassaVBox.getChildren().add(pp);
		else if (ppT == PalvelupisteenTyyppi.IPKASSA)
			ipKassaVbox.getChildren().add(pp);
	}

	public void reset() {
		ruokaVBox.getChildren().clear();
		kassaVBox.getChildren().clear();
		ipKassaVbox.getChildren().clear();

		ppTesti.clear();
		asiakaslista.clear();
		
		ruokalinjastot = 0;
		kassat = 0;
		ipKassat = 0;

		ruokaPlus.setDisable(false);
		kassaPlus.setDisable(false);
		ipKassaPlus.setDisable(false);
		
		aikaCount.setText("Käytettu aika: ");
		
		kontrolleri.reset();
	}

	public void lisaaRuokalinjasto() {
		if (ruokalinjastot < 4) {
			lisääPalvelupiste(PalvelupisteenTyyppi.RUOKATISKI);
			ruokalinjastot++;
			if (ruokalinjastot == 4)
				ruokaPlus.setDisable(true);
		}
	}

	public void lisaaKassa() {
		if (kassat < 3) {
			lisääPalvelupiste(PalvelupisteenTyyppi.KASSA);
			kassat++;
			if (kassat == 3)
				kassaPlus.setDisable(true);
		}
	}

	public void lisaaIPKassa() {
		if (ipKassat < 3) {
			lisääPalvelupiste(PalvelupisteenTyyppi.IPKASSA);
			ipKassat++;
			if (ipKassat == 3)
				ipKassaPlus.setDisable(true);
		}
	}

	public void hidasta() {
		kontrolleri.hidasta();
	}

	public void nopeuta() {
		kontrolleri.nopeuta();
	}

	public ObservableList<Asiakas> getAsiakaslista() {
		return asiakaslista;
	}

	@Override
	public double getAika() {
		return Long.parseLong(aikaTF.getText());
	}

	@Override
	public long getViive() {
		return Long.parseLong(viiveTF.getText());
	}

	public void varaaPalvelupiste(int i) {
		ppTesti.get(i).setFill(Color.RED);
	}

	@Override
	public void vapautaPalvelupiste(int i) {
		ppTesti.get(i).setFill(Color.GREEN);
	}

	public void simuloinninJalkeen() {
			kaynnistaBtn.setDisable(false);
			asiakkaatBtn.setDisable(false);
	}
	// Muuta kenties ProgressBariksi?
	public void setAikaCounter(int aika) {
		aikaCount.setText("Käytettu aika: " + Integer.toString(aika));
	}

	public void lisaaAsiakaslistaan(Asiakas asiakas) {
		asiakaslista.add(asiakas);
	}

	// Toimii jokaisen palvelupisteen (GUI:ssa Rectangle:t) onClick-metodina.
	public void showTulos(int indeksi) {
		Palvelupiste[] tulokset = kontrolleri.getPalvelupisteet();
		if (tulokset != null) {
			Alert a = new Alert(AlertType.INFORMATION);
			a.setTitle("Tulokset");
			a.setHeaderText(
					"Palvelupisteen " + tulokset[indeksi].getPpNimi() + " " + tulokset[indeksi].getPpNum() + " tulokset");
			a.setContentText(tulokset[indeksi].getSimuTulos());
			a.showAndWait();
		}
	}
	
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

	public int getRuokalinjastot() {
		return ruokalinjastot;
	}

	public int getKassat() {
		return kassat;
	}

	public int getIPKassat() {
		return ipKassat;
	}

	public static void main(String[] args) {
		launch(args);
	}
}