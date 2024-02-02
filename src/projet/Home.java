package projet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Home extends Application {

	Compte compte = new Compte();
	
	public void start(Stage stage) throws Exception {
		
		FXMLLoader loader = null;
		Parent root       = null;
				
		//Serveur Mysql éteint
	
		
		if(!this.logIn()) {
			loader = new FXMLLoader(getClass().getResource("log.fxml"));
			root = loader.load();
			ControllerLogIn controllerLog = loader.getController();
			controllerLog.setStage(stage); 
			stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
			stage.setScene(new Scene(root,419,367));
			stage.setResizable(false);
			stage.setTitle("Connexion au serveur de base de données");
			
			//Effets limuneux
			DropShadow ds = new DropShadow();
			ds.setColor(Color.WHITE);
			ds.setOffsetX(0);
			ds.setOffsetY(0);
			controllerLog.getUser().setEffect(ds);
			controllerLog.getPassword().setEffect(ds);
			
			stage.show();
		}
		else if(!this.serviceIsOn()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("ERREUR");
				alert.setHeaderText("ERREUR : le serveur de base de données est injoignable");
				alert.showAndWait();
				//Fermer l'application
				this.stop();
		}
		else if(!this.compteValide()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERREUR");
			alert.setHeaderText("L'utilisateur "+compte.getUtilisateur()+" "
					+ "indentifié par "+compte.getMotDePasse()+" n'existe pas dans le serveur de base de données");
			alert.showAndWait();
		} else {
			this.preparerDonnees();
			loader = new FXMLLoader(getClass().getResource("homeUser.fxml"));
			root = loader.load();
			Controller controller = loader.getController();
			controller.setStage(stage); 
			stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
			stage.setTitle("SNDL-LOG-Analyzer");
			stage.setScene(new Scene(root,1366,700));
			controller.setCompte(compte);
			controller.test();
			stage.show();
		}
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
		    String line;
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
			
	
		
	public Boolean serviceIsOn() {
		try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/",
					compte.getUtilisateur(),compte.getMotDePasse());
		} catch(SQLException e) {
			return false;
		}
		
		return true;
	}
	
	public Boolean logIn() {
		
		try {
			BufferedReader lire = new BufferedReader(new FileReader("config.xml"));
			Pattern pattern1 = Pattern.compile("<Utilisateur>(.*)</Utilisateur>");
			Pattern pattern2 = Pattern.compile("<MotDePasse>(.*)</MotDePasse>");
			Matcher m = null;
			String ligne;
			while((ligne = lire.readLine()) != null) {
				m = pattern1.matcher(ligne);
				if(m.matches()) {
					compte.setUtilisateur(m.group(1));
				}
				m = pattern2.matcher(ligne);
				if(m.matches()) {
					compte.setMotDePasse(m.group(1));
				}
			}
		} catch(IOException e) {
			return false;
		}
		
		
		return true;
	}
	
	public Boolean compteValide() {
		
		try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost",
					compte.getUtilisateur(),compte.getMotDePasse());
		} catch (SQLException e) {
			return false;
		}
		return true;
	}
	
}
	
	

		
		
		
		
		

						
						
			
			
		
	
		
		

