package projet;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import projet.Compte;
import projet.ExportRecherche;
import projet.ExportTelechargement;

public class ControllerExport {
	
	private Stage stage;
	private Compte compte;
	private JFXButton tel;
	private JFXButton rech;
	
	Boolean abort;
	
	public void clickRech() {
		
		abort = false;
		if(!this.tableExiste("extraction", "event_recherche")
				|| this.tableVide("extraction", "event_recherche")) {
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Aucunes données à exporter");
			alert.showAndWait();		
			this.stage.close();
		} else {
			
			Stage stage1 = new Stage();
	        Pane layout = new Pane();
			JFXSpinner bar = new JFXSpinner();
			bar.setEffect(new Reflection());
			bar.setLayoutX(170);
			bar.setLayoutY(125);
			bar.setPrefHeight(200);
			bar.setPrefWidth(150);
			bar.setProgress(0);
			
			///
			Label label1 = new Label("");
			label1.setLayoutX(135);
			label1.setLayoutY(100);
			label1.setFont(Font.font ("Serif", 18));
			///
			
			///
			Label label2 = new Label("");
			label2.setLayoutX(140);
			label2.setLayoutY(325);
			label2.setFont(Font.font ("Serif", 18));
			///
			
			///
			layout.getChildren().addAll(bar,label1,label2);
			stage1.setScene(new Scene(layout,500,500));
			stage1.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
			stage1.initModality(Modality.APPLICATION_MODAL);
			stage1.setTitle("Export des données de recherche vers Excel");
			stage1.setResizable(false);
			///
			
			ExportRecherche export = new ExportRecherche();
			FileChooser chooser = new FileChooser();
			File file = chooser.showSaveDialog(new Stage());
			if (file != null) {
				Thread threadExport = new Thread(new Runnable() {
					public void run() {
						Task<Integer> task = export.export(bar, label1, label2, compte,file.getAbsolutePath());
						Thread t = new Thread(task);
						t.setDaemon(true);
						t.start();
						while(t.isAlive()) {
							if(abort) t.interrupt();
						}
						Platform.runLater(() -> {
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setHeaderText("Données exportées avec succès");
							alert.showAndWait();
							stage.close();
							stage1.close();
						});
					}
				});
				threadExport.start();
				stage1.showAndWait();
				stage1.setOnCloseRequest(e -> {
					e.consume();
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("AVERTISSEMENT");
					alert.setHeaderText("Arrêt du processus d'exportation");
					alert.setContentText("Voulez-vous arrêter le processus ?");
					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == ButtonType.OK) {
						abort = true;
						stage1.close();
					}
				});
				if(abort) {
					threadExport.interrupt();
					stage1.close();
				}
			}
		}
	}
		
	public void clickTel() {
		
		abort = false;
		if(!this.tableExiste("extraction", "event_telechargement")
				|| this.tableVide("extraction", "event_telechargement")) {
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Aucunes données à exporter");
			alert.showAndWait();		
			this.stage.close();
		} else {
			
			Stage stage1 = new Stage();
	        Pane layout = new Pane();
			JFXSpinner bar = new JFXSpinner();
			bar.setEffect(new Reflection());
			bar.setLayoutX(170);
			bar.setLayoutY(125);
			bar.setPrefHeight(200);
			bar.setPrefWidth(150);
			bar.setProgress(0);
			
			///
			Label label1 = new Label("");
			label1.setLayoutX(135);
			label1.setLayoutY(100);
			label1.setFont(Font.font ("Serif", 18));
			///
			
			///
			Label label2 = new Label("");
			label2.setLayoutX(140);
			label2.setLayoutY(325);
			label2.setFont(Font.font ("Serif", 18));
			///
			
			///
			layout.getChildren().addAll(bar,label1,label2);
			stage1.setScene(new Scene(layout,500,500));
			stage1.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
			stage1.initModality(Modality.APPLICATION_MODAL);
			stage1.setTitle("Export des données de téléchargement vers Excel");
			stage1.setResizable(false);
			///
			
			ExportTelechargement export = new ExportTelechargement();
			FileChooser chooser = new FileChooser();
			File file = chooser.showSaveDialog(new Stage());
			if (file != null) {
				Thread threadExport = new Thread(new Runnable() {
					public void run() {
						Task<Integer> task = export.export(bar, label1, label2, compte,file.getAbsolutePath());
						Thread t = new Thread(task);
						t.setDaemon(true);
						t.start();
						while(t.isAlive());
							Platform.runLater(() -> {
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setHeaderText("Données exportées avec succès");
							alert.showAndWait();
							stage1.close();
							stage.close();
							});
					}
				});
				threadExport.start();
				stage.setOnCloseRequest(e -> {
					e.consume();
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("AVERTISSEMENT");
					alert.setHeaderText("Arrêt du processus d'exportation");
					alert.setContentText("Voulez-vous arrêter le processus ?");
					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == ButtonType.OK) {
						abort = true;
						stage.close();
					}
				});
				if(abort) {
					threadExport.interrupt();
					stage.close();
				}
				stage.showAndWait();
			}
		}
	}

		
		
		
		
		
	
		
		
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void setCompte(Compte compte) {
		this.compte = compte;
	}
	
	public Boolean tableExiste(String base,String table) {
		
		Connection connection  = null;
		Statement statement    = null;
		DatabaseMetaData meta  = null;
		ResultSet res          = null;
		try {
			connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/"+base,
					compte.getUtilisateur(),compte.getMotDePasse());
			statement = connection.createStatement();
			meta = connection.getMetaData();
			res = meta.getTables(null, null,table,new String[] {"TABLE"}); 
			if(res.next()) return true;
			else return false;
		} catch (SQLException e) { e.printStackTrace(); }
		return null;
	}
	
	public Boolean tableVide(String base, String table) {
		Connection connection;
		try {
			connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/"+base,
					compte.getUtilisateur(),compte.getMotDePasse());
			Statement statement = connection.createStatement();
			ResultSet res = statement.executeQuery("SELECT ID FROM "+table+" WHERE ID != 0");
			if(res.next()) {
				return false;
			} else {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
