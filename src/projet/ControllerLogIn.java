package projet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import projet.Compte;

public class ControllerLogIn {
	private Stage stage;
	private JFXButton connexion;
	private JFXButton quitter;
	@FXML 
	private JFXTextField user;
	@FXML
	private JFXPasswordField password;
	@FXML
	private JFXCheckBox check;
	
	Compte compte;
	
	public void clickConnexion() {
		
		Alert alert = null;
		Boolean abort = false;
		if(user.getText().isEmpty() || password.getText().isEmpty()) {
			alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERREUR");
			alert.setHeaderText("Informations manquantes");
			alert.showAndWait();
		} 
		else {
			compte = new Compte();
			compte.setUtilisateur(user.getText());
			compte.setMotDePasse(password.getText());
			if(!this.isMember()) {
				alert = new Alert(AlertType.ERROR);
				alert.setTitle("ERREUR");
				alert.setHeaderText("L'utilisateur "+compte.getUtilisateur()+" "
						+ "indentifié par "+compte.getMotDePasse()+" n'existe pas dans le serveur de base de données");
				alert.showAndWait();
				
			} else {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("homeUser.fxml"));
				Parent root;
				if(check.isSelected()) {
					PrintWriter config;
					try {
						config = new PrintWriter("config.xml");
						config.write("<?xml version=\"1.0\"?>");
						config.write(System.getProperty("line.separator"));
						config.write("<compte>");
						config.write(System.getProperty("line.separator"));
						config.write("<Utilisateur>"+user.getText()+"</Utilisateur>");
						config.write(System.getProperty("line.separator"));
						config.write("<MotDePasse>"+password.getText()+"</MotDePasse>");
						config.write(System.getProperty("line.separator"));
						config.write("</compte>");
						config.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
				try {
					Stage stage = new Stage();
					this.preparerDonnees();
					root = loader.load();
					Controller controller = loader.getController();
					controller.setStage(stage); 
					stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
					stage.setTitle("SNDL-LOG-Analyzer");
					stage.setScene(new Scene(root,1366,700));
					controller.setCompte(compte);
					controller.test();
					this.stage.close();
					stage.show();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
			
		
	
			
	public void clickQuitter() {
		this.stage.close();
	}
		
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public JFXTextField getUser() {
		return this.user;
	}
	
	public JFXPasswordField getPassword() {
		return this.password;
	}
	
	public Boolean isMember() {
		try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost",
					compte.getUtilisateur(),compte.getMotDePasse());
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	public void preparerDonnees() {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1",compte.getUtilisateur(),compte.getMotDePasse());
			Statement statement = connection.createStatement();
			int res = statement.executeUpdate("CREATE DATABASE IF NOT EXISTS Nettoyage");
			connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Nettoyage",compte.getUtilisateur(),compte.getMotDePasse());
			statement = connection.createStatement();
			
			//Vérifier que la table dico_recherche existe
			DatabaseMetaData meta = connection.getMetaData();
		    ResultSet res1 = meta.getTables(null, null, "dico_recherche",new String[] {"TABLE"});  
		    String line,query ="";
		    BufferedReader lire = null;
		    res = statement.executeUpdate("CREATE TABLE IF NOT EXISTS type (" + 
					"id BIGINT NOT NULL AUTO_INCREMENT," +
					"pos BIGINT NOT NULL," + 
					"type CHAR NOT NULL," + 
					"PRIMARY KEY(id)" + 
					")");
			//Créer la Base extraction
			connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1",compte.getUtilisateur(),compte.getMotDePasse());
			statement = connection.createStatement();
			res = statement.executeUpdate("CREATE DATABASE IF NOT EXISTS Extraction");
		} catch (SQLException e) { e.printStackTrace(); }
	}
	
}
