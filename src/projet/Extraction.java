package projet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.jfoenix.controls.JFXSpinner;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;

public class Extraction {
	

	public Task<Integer> extraire(Compte compte ,JFXSpinner bar, Label label1, Label label2) {
		
		Task<Integer> task = new Task<Integer>() {
			
			protected Integer call() throws Exception {
				partitionner(compte,bar,label1, label2);
				identifier(compte,bar,label1, label2);
				decoder(compte,bar,label1, label2);
				transf();
				//clearData(compte);
				return 1;
			}
			public HashMap<String,Integer> partitionner(Compte compte,JFXSpinner bar, Label label1, Label label2) {
				
				Connection connection1      = null;
				Connection connection2      = null;
				Statement statement1        = null;
				Statement statement2        = null;
				Statement statement3        = null;
				Statement statement4        = null;
				Statement statement5        = null;
				ResultSet res1              = null;
				ResultSet res2              = null;
				ResultSet res3              = null;
				ResultSet res4              = null;
				EventRecherche eventR       = null;
				EventTele eventT            = null;
				int set1                    = 0;
				int set2                    = 0;
				int total                   = 0;
				int compteur                = 1;
				HashMap<String,Integer> map = new HashMap();
				try {
					connection1 = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Extraction",
							compte.getUtilisateur(),compte.getMotDePasse());
					connection2 = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Nettoyage",
							compte.getUtilisateur(),compte.getMotDePasse());
					statement1 = connection1.createStatement();
					statement2 = connection2.createStatement();
					statement3 = connection2.createStatement();
					statement4 = connection2.createStatement();
					statement5 = connection1.createStatement();
					//Créer la table de recherche
					set1 = statement1.executeUpdate("CREATE TABLE IF NOT EXISTS Event_Recherche (" + 
								"ID BIGINT NOT NULL AUTO_INCREMENT," + 
								"session VARCHAR(50)," + 
								"nom VARCHAR(50)," + 
								"source VARCHAR(50)," + 
								"date VARCHAR(50)," +
								"heure VARCHAR(50)," + 
								"url longtext," + 
								"liste_mots longtext," + 
								"primary key(ID)" + 
								")");
					set1 = statement1.executeUpdate("CREATE TABLE IF NOT EXISTS Event_Recherche1 (" + 
								"ID BIGINT NOT NULL AUTO_INCREMENT," + 
								"session VARCHAR(50)," + 
								"nom VARCHAR(50)," + 
								"source VARCHAR(50)," + 
								"date VARCHAR(50)," + 
								"heure VARCHAR(50)," + 
								"url longtext," + 
								"liste_mots longtext," + 
								"primary key(ID)" + 
								")");
					//Créer la table de téléchargement
					set1 = statement1.executeUpdate("CREATE TABLE IF NOT EXISTS event_telechargement ("+ 
								"ID BIGINT NOT NULL AUTO_INCREMENT," + 
								"session VARCHAR(50)," + 
								"nom VARCHAR(50)," + 
								"source VARCHAR(50)," + 
								"date VARCHAR(50)," + 
								"heure VARCHAR(50)," + 
								"url LONGTEXT," + 
								"PRIMARY KEY(ID)" + 
								")");
					set1 = statement1.executeUpdate("CREATE TABLE IF NOT EXISTS event_telechargement1 ("+ 
								"ID BIGINT NOT NULL AUTO_INCREMENT," + 
								"session VARCHAR(50)," + 
								"nom VARCHAR(50)," + 
								"source VARCHAR(50)," + 
								"date VARCHAR(50)," + 
								"heure VARCHAR(50)," + 
								"url LONGTEXT," + 
								"PRIMARY KEY(ID)" + 
								")");
					//Calculer le nbr de ligne dans la table event qui est nettoyée
					res3 = statement3.executeQuery("SELECT COUNT(*) FROM event");
					if(res3.next()) {
						total = res3.getInt(1);
					}
						Platform.runLater(() -> {
						label1.setText("Paritionnement des données ...");
						label2.setText("Veuillez patienter s'il vous plaît");
					});
					res2 = statement2.executeQuery("SELECT * FROM EVENT ORDER BY SESSION");	
					while(res2.next()) {
						this.updateProgress((compteur*100)/total, 100); 
						eventR  = new EventRecherche();
						eventT  = new EventTele();
						res1 = statement4.executeQuery("SELECT * FROM type "
								+ "WHERE pos = "+Integer.parseInt(res2.getString("ID")));
						if(res1.next()) {
							if(res1.getString("type").equals("R")) {
								eventR.setSession(res2.getString("session"));
								eventR.setNom(res2.getString("nom"));
								eventR.setDate(res2.getString("date"));
								eventR.setHeure(res2.getString("heure"));
								eventR.setUrl(res2.getString("URL"));
								//Vérifier si l'événement n'existe pas dans les données extraites
								res4 = statement5.executeQuery("SELECT session,nom,date,heure,url "
										+ "FROM event_recherche "
										+ "WHERE session = "+"'"+eventR.getSession()+"' "
										+ "AND nom ="+"'"+eventR.getNom()+"' "
										+ "AND date ="+"'"+eventR.getDate()+"' "
										+ "AND heure ="+"'"+eventR.getHeure()+"' "
										+ "AND url ="+"'"+eventR.getUrl()+"'");
								if(!res4.next())
								set2 = statement1.executeUpdate("INSERT INTO event_recherche1(session,nom,date,heure,url) VALUES"
								+ "("+"'"+eventR.getSession()+"',"+"'"+eventR.getNom()+
								"',"+"'"+eventR.getDate()+"',"+"'"+eventR.getHeure()+"',"+"'"+eventR.getUrl()+"')");
							} else {
								eventT.setSession(res2.getString("session"));
								eventT.setNom(res2.getString("nom"));
								eventT.setDate(res2.getString("date"));
								eventT.setHeure(res2.getString("heure"));
								eventT.setUrl(res2.getString("URL"));
								res4 = statement5.executeQuery("SELECT session,nom,date,heure,url "
										+ "FROM event_telechargement "
										+ "WHERE session = "+"'"+eventT.getSession()+"' "
										+ "AND nom ="+"'"+eventT.getNom()+"' "
										+ "AND date ="+"'"+eventT.getDate()+"' "
										+ "AND heure ="+"'"+eventT.getHeure()+"' "
										+ "AND url ="+"'"+eventT.getUrl()+"'");
								if(!res4.next())
								set2 = statement1.executeUpdate("INSERT INTO event_telechargement1(session,nom,date,heure,url) VALUES"
										+ "("+"'"+eventT.getSession()+"',"+"'"+eventT.getNom()+
										"',"+"'"+eventT.getDate()+"',"+"'"+eventT.getHeure()+"',"+"'"+eventT.getUrl()+"')");
							}
						}
						compteur++;	
					}
					//Calculer le nbr de requête de recherche
					res1 = statement1.executeQuery("SELECT COUNT(*) FROM event_recherche1");
					if(res1.next()) {
						map.put("R",res1.getInt(1));
					}
					res1 = statement1.executeQuery("SELECT COUNT(*) FROM event_telechargement1");
					if(res1.next()) {
						map.put("T",res1.getInt(1));
					}
				} catch (SQLException e) { e.printStackTrace(); }
				return map;
			}
			
			public void identifier(Compte compte,JFXSpinner bar, Label label1, Label label2) {
				
				Connection connection1      = null;
				Connection connection2      = null;
				Statement statement1        = null;
				Statement statement2        = null;
				Statement statement3        = null;
				ResultSet res1              = null;
				ResultSet res2              = null;
				EventRecherche eventR       = null;
				EventTele eventT            = null;
				RequeteRecherche query 	    = null;
				RequeteTelechargement reqT  = null;
				Pattern pattern1            = Pattern.compile("http(s|)://(.+?):[0-9]+(/.*?)(\\?.*|)");
				Matcher matcher1            = null;
				int compteur                = 1;
				int total                   = 0;
				int set1                    = 0;
				try {
					connection1 = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Extraction",
							compte.getUtilisateur(),compte.getMotDePasse());
					connection2 = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Nettoyage",
							compte.getUtilisateur(),compte.getMotDePasse());
					statement1 = connection1.createStatement();
					statement2 = connection2.createStatement();
					statement3 = connection1.createStatement();
					res1 = statement1.executeQuery("SELECT COUNT(*) FROM event_recherche1");
					if(res1.next()) {
						total = res1.getInt(1);
					}
					res1 = statement1.executeQuery("SELECT COUNT(*) FROM event_telechargement1");
					if(res1.next()) {
						total += res1.getInt(1);
					}
					Platform.runLater(() -> {
					label1.setText("Identification des sources ...");
					label2.setText("Veuillez patienter s'il vous plaît");
					});
					res1 = statement1.executeQuery("SELECT ID,URL FROM event_recherche1");
					while(res1.next()) {
						eventR  = new EventRecherche();
						query    = new RequeteRecherche();
						eventR.setUrl(res1.getString("URL"));
						matcher1 = pattern1.matcher(eventR.getUrl());
						matcher1.matches();
						query.setDomaine(matcher1.group(2));
						res2 = statement2.executeQuery("SELECT source FROM dico_recherche "
								+ "WHERE domaine = "+"'"+query.getDomaine()+"'");
						if(res2.next()) {
							set1 = statement3.executeUpdate("UPDATE event_recherche1 SET "
							+ "source ="+"'"+res2.getString("source")+"'"+"WHERE ID = "+Integer.parseInt(res1.getString("ID")));
						}
						updateProgress((compteur*100)/total,100);
						compteur++;
					}
					res1 = statement1.executeQuery("SELECT ID,URL FROM event_telechargement1");
					while(res1.next()) {
						eventT = new EventTele();
						reqT   = new RequeteTelechargement();
						eventT.setUrl(res1.getString("URL"));
						matcher1 = pattern1.matcher(eventT.getUrl());
						matcher1.matches();
						reqT.setDomaine(matcher1.group(2));
						res2 = statement2.executeQuery("SELECT source FROM dico_telechargement "
								+ "WHERE domaine = "+"'"+reqT.getDomaine()+"'");
						if(res2.next()) {
							set1 = statement3.executeUpdate("UPDATE event_telechargement1 SET "
							+ "source ="+"'"+res2.getString("source")+"'"+"WHERE ID = "+Integer.parseInt(res1.getString("ID")));
						}
						updateProgress((compteur*100)/total,100);
						compteur++;
					}
				} catch (SQLException e) { e.printStackTrace(); }
			}
				
			public void decoder(Compte compte,JFXSpinner bar, Label label1, Label label2) {
				
				Connection connection1      = null;
				Connection connection2      = null;
				Statement statement1        = null;
				Statement statement2        = null;
				Statement statement3        = null;
				ResultSet res1              = null;
				ResultSet res2              = null;
				RequeteRecherche query 	    = null;
				ArrayList<String> liste     = new ArrayList<String>();
				ArrayList<String> listeCles = new ArrayList<String>();
				ArrayList<String> listeB    = new ArrayList<String>();
				String[] tableau            = null;
				Map<String,String> map      = new HashMap<String, String>();
				Boolean isClinicalKey       = false;
				Pattern pattern1            = Pattern.compile("http(s|)://(.+?):[0-9]+(/.*?)(\\?.*|)");
				Pattern pattern2            = Pattern.compile("[^&?]*=[^&?]*");	
				Pattern pattern3            = Pattern.compile("(.*?)=(.*)");	
				Pattern pattern4            = null;
				Matcher matcher1            = null;
				Matcher matcher2            = null;
				int compteur                = 1;
				int total                   = 0;
				int set                     = 0;
				String a;
				String b;
				try {
					connection1 = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Extraction",
							compte.getUtilisateur(),compte.getMotDePasse());
					connection2 = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Nettoyage",
							compte.getUtilisateur(),compte.getMotDePasse());
					statement1 = connection1.createStatement();
					statement2 = connection2.createStatement();
					statement3 = connection1.createStatement();
					Platform.runLater(() -> {
						label1.setText("Extraction des mots clés ...");
						label2.setText("Veuillez patienter s'il vous plaît");
					});					
					res1 = statement1.executeQuery("SELECT COUNT(*) FROM event_recherche1");
					if(res1.next()) {
						total = res1.getInt(1);
					}
					compteur = 1;
					res1 = statement1.executeQuery("SELECT ID,URL from event_recherche1");
					while(res1.next()) {						
						updateProgress((compteur*100)/total,100);
						query = new RequeteRecherche();
						liste.clear();
						listeCles.clear();
						listeB.clear();
						map.clear();
						isClinicalKey = false;
						matcher1 = pattern1.matcher(res1.getString("URL"));
						matcher1.matches();
						//Récupérer le domaine
						query.setDomaine(matcher1.group(2));
						if(query.getDomaine().equals("www.clinicalkey.com") 
								|| query.getDomaine().equals("www.clinicalkey.fr")) {
							isClinicalKey = true;
						}
						//
						//Récupérer les parmètres
						matcher1 = pattern2.matcher(res1.getString("URL"));
						while(matcher1.find()) {
							matcher2 = pattern3.matcher(matcher1.group(0));
							matcher2.matches();
							//Récupérer les params et leur valeur dans une hashmap
							map.put(matcher2.group(1), matcher2.group(2));
							liste.add(matcher2.group(1));
						}
						query.setListe_params(liste);
						//
						if(!isClinicalKey) {							
							//Récupérer les params du motif
							res2 = statement2.executeQuery("SELECT liste_params FROM dico_recherche "
									+ "WHERE domaine="+"'"+query.getDomaine()+"'");
							if(res2.next()) {
								tableau = res2.getString("liste_params").split(";");
								liste = new ArrayList<String>(Arrays.asList(tableau));
							}
							//							
							//2-Vérifier quel param du motif existe dans la requête
							for(int j = 0; j < liste.size(); j++) {
								a = liste.get(j);
								//avec le #
								if(a.endsWith("#")) {
									a = liste.get(j).replaceAll("#","");
									pattern4 = Pattern.compile(a+"[0-9]*");
									for(int n = 0; n < query.getListeParams().size(); n++) {
										b = query.getListeParams().get(n);
										matcher1 = pattern4.matcher(b);
										if(matcher1.matches()) {
											listeCles.add(map.get(matcher1.group(0)));
										}
									}
								} else { //sans le #
									if(query.getListeParams().contains(a)) listeCles.add(map.get(a));
								}
							}// for
							//------------------------>D E C O D A G E __ D E S__ D O N N E E S
							listeB = new ArrayList<String>(query.Decoder(res1.getString("URL"),listeCles));
						} else { //Clinical Key
							listeB = new ArrayList<String>(query.Decoder(res1.getString("URL"),listeCles));
						}
						//Sauvegarde des données
						a = String.join(";",listeB);
						a = a.replaceAll("'","");
						set = statement3.executeUpdate("UPDATE event_recherche1 "
								+ "SET liste_mots = "+"'"+a+"'"+
								" WHERE ID ="+Integer.parseInt(res1.getString("ID")));				
						compteur++;	
					}//----> while
				} catch (SQLException e) { e.printStackTrace(); }
				Platform.runLater(() -> {
					label1.setText("Extraction des mots clés terminé");
					this.updateProgress(100,100);
				});
			}
			
			public void clearData(Compte compte) {
				
				Connection connection;
				try {
					connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Nettoyage",
							compte.getUtilisateur(),compte.getMotDePasse());
					Statement statement = connection.createStatement();
					//Vider la table event
					int set = statement.executeUpdate("DROP TABLE event");
					set = statement.executeUpdate("TRUNCATE type");
				} catch (SQLException e) { e.printStackTrace(); }
			}
	
			public void transf() {
				
				try {
					Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Extraction",
							compte.getUtilisateur(),compte.getMotDePasse());
					Statement statement = connection.createStatement();
					int set = statement.executeUpdate("INSERT INTO "
							+ "event_recherche(session,nom,source,date,heure,url,liste_mots) "
							+ "SELECT session,nom,source,date,heure,url,liste_mots FROM event_recherche1");
					set = statement.executeUpdate("TRUNCATE event_recherche1");
					set = statement.executeUpdate("INSERT INTO "
							+ "event_telechargement(session,nom,source,date,heure,url) "
							+ "SELECT session,nom,source,date,heure,url FROM event_telechargement1");
					set = statement.executeUpdate("TRUNCATE event_telechargement1");
					
				} catch (SQLException e) { e.printStackTrace(); }
			}
		};
		bar.progressProperty().bind(task.progressProperty());
		return task;
	}
}
		
		
	
	
	
	
	
	
	
	


