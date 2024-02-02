package projet;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

public class Fichier extends java.io.File {

	ArrayList<Integer> listeR = new ArrayList<Integer>();
	ArrayList<Integer> listeT = new ArrayList<Integer>();
	
	public Fichier(String chemin) {
		super(chemin);
	}
		
	public int calculerTaille() {
		int nbrLigne = 0;
		try {
			BufferedReader lire = new BufferedReader(new FileReader(this.getAbsolutePath()));
			String ligne;
			while((ligne = lire.readLine()) != null) if(!ligne.isEmpty()) nbrLigne++;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return nbrLigne;
	}
		
	public Task<Long> Transferer(Compte compte,ProgressIndicator bar, Label label)  {
		
		String name = this.getName();
		String path = this.getAbsolutePath();
		Task<Long> task = new Task<Long>() {

			protected Long call() throws Exception {
				
				Long begin = System.currentTimeMillis();
				//Se connecter à la base de données de Nettoyage
				Connection connection = null;
				Statement statement   = null;
				ResultSet res = null;
				int set = 0;
				String line;
				int i = 1;
				int nbrLigne = 0;
				int progress = 1;
				Event event = new Event();
				//Calculer le nombre de lignes du fichier
				nbrLigne = calculerTaille();
				try {
					connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Nettoyage",compte.getUtilisateur(),compte.getMotDePasse());
					statement = connection.createStatement();
					//Créer la table de nettoyage 
					set = statement.executeUpdate("CREATE TABLE IF NOT EXISTS Event1 ("
							+ "ID BIGINT unsigned auto_increment,"
							+ "IP VARCHAR(15) NOT NULL,"
							+ "session VARCHAR(50) NOT NULL,"
							+ "nom VARCHAR(50),"
							+ "date VARCHAR(50),"
							+ "heure VARCHAR(50),"
							+ "méthode VARCHAR(20),"
							+ "url LONGTEXT,"
							+ "protocole VARCHAR(20),"
							+ "statut INT unsigned,"
							+ "taille INT unsigned,"
							+ "PRIMARY KEY(id))");
					set = statement.executeUpdate("CREATE TABLE IF NOT EXISTS Event ("
							+ "ID BIGINT unsigned auto_increment,"
							+ "IP VARCHAR(15) NOT NULL,"
							+ "session VARCHAR(50) NOT NULL,"
							+ "nom VARCHAR(50),"
							+ "date VARCHAR(50),"
							+ "heure VARCHAR(50),"
							+ "méthode VARCHAR(20),"
							+ "url LONGTEXT,"
							+ "protocole VARCHAR(20),"
							+ "statut INT unsigned,"
							+ "taille INT unsigned,"
							+ "PRIMARY KEY(id))");
				} catch(SQLException e) { e.printStackTrace(); }
				try {
					///Etablir une regexp
					Pattern pattern = Pattern.compile("(([0-9\\.]+) (.*?|-) (.*?|-) \\[([0-9]{2}/[a-zA-Z]+/[0-9]{4}):(.*?)\\+[0-9]+\\]) \"(.*?) (.*?) "
							+ "(.*?)\" ([0-9]+) ([0-9]+)");
					Matcher matcher = null;
					//lire le fichier
					BufferedReader lire = new BufferedReader(new FileReader(path));
					//Récupérer les lignes du fichiers log
					while((line = lire.readLine()) != null) {
						if(!line.isEmpty()) {
							updateProgress((progress*100)/nbrLigne,100);
							matcher = pattern.matcher(line);
							matcher.matches();
							event.setIp(matcher.group(2));
							event.setSession(matcher.group(3));
							event.setNom(matcher.group(4));
							event.setDate(matcher.group(5));
							event.setHeure(matcher.group(6));
							event.setMethode(matcher.group(7));
							event.setUrl(matcher.group(8).replaceAll("'",""));
							event.setProtocole(matcher.group(9));
							event.setStatut(Integer.parseInt(matcher.group(10)));
							event.setTaille(Integer.parseInt(matcher.group(11)));
							try {
								set = statement.executeUpdate("INSERT INTO Event1"
								+ "(IP,session,nom,date,heure,méthode,url,protocole,statut,taille) "
										+ "Values("+"'"+event.getIp()+
										"',"+"'"+event.getSession()+"',"+"'"+event.getNom()+"',"+"'"+event.getDate()+"',"+"'"+event.getHeure()+"',"+
										"'"+event.getMethode()+"',"+"'"+event.getUrl()+"',"+"'"+event.getProtocole()+"',"
										+event.getStatut()+","+event.getTaille()+")");
							} catch (SQLException e) { e.printStackTrace();}
							i++; // i = ID de la table Event
							progress++;
						}
					}
					lire.close();
					set = statement.executeUpdate("INSERT INTO event(ip,session,nom,date,heure,méthode,url,protocole,statut,taille) "
							+ "SELECT ip,session,nom,date,heure,méthode,url,protocole,statut,taille FROM event1");
					set = statement.executeUpdate("TRUNCATE event1");
				} catch (IOException e) { e.printStackTrace(); }
				long end = System.currentTimeMillis();
				return (end - begin) / 1000;
			}
		
		};
		 Platform.runLater(()-> {
			 label.setText(this.getName());
			 bar.progressProperty().bind(task.progressProperty());
		 });
		return task;
	}
}
	
	

		
	
	

		
		
		
		
		

		
		
		











