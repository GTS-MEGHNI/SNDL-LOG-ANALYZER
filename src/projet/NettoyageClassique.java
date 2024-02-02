package projet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.sql.DatabaseMetaData;

import com.jfoenix.controls.JFXSpinner;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;

public class NettoyageClassique extends Nettoyage{

	
	public HashMap<String,Integer> map = new HashMap();
	
	public Task nettoyer(Compte compte, JFXSpinner bar, Label label1, Label label2) {
		
		Task<Integer> task = new Task<Integer>() {
			
			protected Integer call() throws Exception {
				
				Platform.runLater(() -> {
					label1.setText("Nettoyage classique en cours ...");
					label2.setText("Veuillez patienter s'il vous plaît...");
				});
				
				Connection connection 	= null;
				Statement statement   	= null;
				DatabaseMetaData meta   = null;
				ResultSet res          	= null;
				ResultSet resStat       = null;
				int set = 0;
				int rowDeleted = 0;
				
				//Se connecter à la base de données de nettoyage 
				try {
					connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Nettoyage",compte.getUtilisateur(),compte.getMotDePasse());
				} catch (SQLException e1) { e1.printStackTrace(); }
				
				try {
					
					statement = connection.createStatement();
					meta = connection.getMetaData();
					
					///Statistiques
					res = meta.getColumns(null, null, "event", "statut");
					if(res.next()) {
						resStat = statement.executeQuery("SELECT COUNT(*) FROM event WHERE statut = 404");
						if(resStat.next()) map.put("404",resStat.getInt(1));
					} else map.put("404",0);
					
					resStat = statement.executeQuery("SELECT COUNT(*) FROM event WHERE "
							+ "locate('.png',url) != 0 OR locate('.gif',url) != 0 "
							+ "OR locate('.jpg',url) != 0 OR locate('.bmp',url) != 0 "
							+ "OR locate('.ico',url) != 0 OR locate('.jpeg',url) != 0");
					if(resStat.next()) map.put("images",resStat.getInt(1));
					resStat = statement.executeQuery("SELECT COUNT(*) FROM event WHERE locate('.js',url) != 0"); 
					if(resStat.next()) map.put("js",resStat.getInt(1));
					resStat = statement.executeQuery("SELECT COUNT(*) FROM event WHERE locate('.css',url) != 0");
					if(resStat.next()) map.put("css",resStat.getInt(1));
					resStat = statement.executeQuery("SELECT COUNT(*) FROM event WHERE locate('robots.txt',url) != 0");
					if(resStat.next()) map.put("robots",resStat.getInt(1));
					resStat = statement.executeQuery("SELECT COUNT(*) FROM event WHERE nom = '-'");
					if(resStat.next()) map.put("user",resStat.getInt(1));
					
					
					//nettoyer selon le statut de recherche
					res = meta.getColumns(null, null, "event", "statut");
					if(res.next()) {
						set = statement.executeUpdate("DELETE FROM Event WHERE statut < 200 or statut > 399");
						rowDeleted += set;
					}
					//nettoyer selon la taille de la page 
					res = meta.getColumns(null, null, "event", "taille");
					if(res.next()) {
						set = statement.executeUpdate("DELETE FROM Event where taille = 0");
						rowDeleted += set;
					}

					//nettoyer les images	
					set = statement.executeUpdate("DELETE from event where "
							+ "locate('.png',url) != 0 OR locate('.gif',url) != 0 " + 
							"OR locate('.jpg',url) != 0 OR locate('.bmp',url) != 0 "
							+ "OR locate('.ico',url) != 0 OR locate('.jpeg',url) != 0");
					rowDeleted += set;

					//supprimer les fichiers javascript
					set = statement.executeUpdate("DELETE from EVENT where locate('.js',url) != 0");
					rowDeleted += set;

					//supprimer les fichiers CSS
					set = statement.executeUpdate("DELETE from EVENT where locate('.css',url) != 0");
					rowDeleted += set;

					//supprimer les requêtes des robots web
					set = statement.executeUpdate("DELETE FROM event where locate('robots.txt',url) != 0");
					rowDeleted += set;

					//supprimer les evenement sans nom d'utilisateur ou sans session
					set = statement.executeUpdate("DELETE FROM event WHERE nom = '-' OR session = '-'");
					rowDeleted += set;

					//Supprimer la colonne statut 
					res = meta.getColumns(null, null, "event", "statut");
					if(res.next()) set = statement.executeUpdate("ALTER TABLE Event DROP statut");
					
					//Supprimer la colonne méthode
					res = meta.getColumns(null, null, "event", "méthode");
					if(res.next()) set = statement.executeUpdate("ALTER TABLE Event DROP méthode");
					
					//Supprimer la colonne protocole
					res = meta.getColumns(null, null, "event", "protocole");
					if(res.next()) set = statement.executeUpdate("ALTER TABLE Event DROP protocole");
					
					//Supprimer la colonne taille
					res = meta.getColumns(null, null, "event", "taille");
					if(res.next()) set = statement.executeUpdate("ALTER TABLE Event DROP taille");
					
					//Supprimer la colonne IP
					res = meta.getColumns(null, null, "event", "ip");
					if(res.next()) set = statement.executeUpdate("ALTER TABLE Event DROP IP");
					
				} catch (SQLException e) { e.printStackTrace(); }
				
				Platform.runLater(() -> {
					label1.setText("Nettoyage classique terminé");
					label2.setText("");
					
				});
				map.put("total", rowDeleted);
				return rowDeleted;
			}
		};
		return task;
	}
		
}///	
		
		
		
	


