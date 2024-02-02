package projet;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jfoenix.controls.JFXSpinner;
import com.mysql.jdbc.DatabaseMetaData;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;

public class NettoyageApprofondit extends Nettoyage {
	
	ArrayList<Integer> listeR = new ArrayList<Integer>();
	ArrayList<Integer> listeT = new ArrayList<Integer>();
	public HashMap<String,Integer> map = new HashMap<String,Integer>();
	public Task nettoyer(Compte compte,JFXSpinner bar, Label label1, Label label2) {
		
		Task<Integer> task = new Task<Integer>() {
			protected Integer call() throws Exception {
				
				///---------->V A R I A B L E S
				Connection connection                   = null;
				Connection connection1                  = null;
				Statement statement                     = null;
				Statement statement1                    = null;
				Statement statement2                    = null;
				ResultSet res                           = null;
				ResultSet rs                            = null;
				ResultSet res1                          = null;
				int set                                 = 0;
				int set1                                = 0;
				int rowDeleted                          = 0;
				int compteur                            = 1;
				int i                                   = 0;
				Boolean isClinicalKey                   = false;
				Boolean foundInR						= false;
				Boolean foundInT						= false;
				Boolean pathFound						= false;
				Boolean markFound						= false;
				Boolean paramFound						= false;
				String a  								= null;
				String b 								= null;
				Requete query                           = new Requete();
				Motif_Recherche motif			        = new Motif_Recherche();
				Motif_Telechargement motif1             = new Motif_Telechargement();
				ArrayList<String> liste 		        = null;
				String[] tableau 				        = null;
				ArrayList<Integer> corbeille            = new ArrayList<Integer>();
				ArrayList<Motif_Recherche> motifR       = new ArrayList<Motif_Recherche>();
				ArrayList<Motif_Telechargement> motifT  = new ArrayList<Motif_Telechargement>();
				Pattern pattern1 						= Pattern.compile("http(s|)://(.+?):[0-9]+(.*)");
				Pattern pattern2 						= Pattern.compile("(\\?|&)(.+?)=");
				Pattern pattern3 						= Pattern.compile("http(s|)://(.+?):[0-9]+(/.*?)(\\?.*|)");
				Pattern pattern4 						= null;
				Matcher matcher1 						= null;
				PrintWriter ecrire 						= null;
				int noSource                            = 0;
				///
				
				Platform.runLater(() -> {
				label1.setText("Nettoyage approfondit en cours ...");
				label2.setText("Veuillez patienter s'il vous plaît...");
				});
				
				try {
					connection  = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Nettoyage",
							compte.getUtilisateur(),compte.getMotDePasse());
					connection1 = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Extraction",
							compte.getUtilisateur(),compte.getMotDePasse());
					statement   = connection.createStatement();
					statement1  = connection.createStatement();
					statement2  = connection1.createStatement();
				} catch (SQLException e1) { e1.printStackTrace(); }
				
				//Récupérer les motifs de recherche
				try {
					res = statement.executeQuery("SELECT * FROM Dico_recherche");
					while(res.next()) {
						motif = new Motif_Recherche();
						//Domaine
						motif.setDomaine(res.getString("domaine"));
						//source
						motif.setSource(res.getString("source"));
						//liste des chemins
						tableau = res.getString("liste_chemins").split(";");
						liste = new ArrayList<String>(Arrays.asList(tableau));
						motif.setListe_chemins(liste);
						//liste des paramètres
						tableau = res.getString("liste_params").split(";");
						liste = new ArrayList<String>(Arrays.asList(tableau));
						motif.setListe_params(liste);
						motifR.add(motif);
					}
				} catch (SQLException e) { e.printStackTrace(); }
				//
				
				//Récupérer les motifs de téléchargement
				try {
					res = statement.executeQuery("SELECT * FROM dico_telechargement");
					while(res.next()) {
						motif1 = new Motif_Telechargement();
						//domaine
						motif1.setDomaine(res.getString("domaine"));
						//source
						motif1.setSource(res.getString("source"));
						///liste des marqueurs
						tableau = res.getString("liste_marks").split(";");
						liste = new ArrayList<String>(Arrays.asList(tableau));
						motif1.setListe_marks(liste);
						motifT.add(motif1);
					}
				} catch (SQLException e) { e.printStackTrace(); }
				//
				
				try {
					ecrire = new PrintWriter("test.txt");
				} catch (FileNotFoundException e) { e.printStackTrace(); }
				
				try {
					res1 = statement1.executeQuery("SELECT COUNT(*) FROM event");
					if(res1.next());
				} catch (SQLException e) { e.printStackTrace(); }
			
				Platform.runLater(() -> {
				label1.setText("Détection des éléments à supprimer ...");
				});
				
				try {
					res = statement.executeQuery("SELECT ID,URL FROM Event");
					
					while(res.next()) {
						updateProgress((compteur*100)/res1.getInt(1),100);
						
						//Initialiser les variables
						query         = new Requete();
						motif	      = new Motif_Recherche();
					    motif1        = new Motif_Telechargement();
						foundInR      = false;
						foundInT      = false;
						markFound     = false;
						paramFound    = false;
						isClinicalKey = false;
						liste.clear();
						//
						
						//Récupérer le domaine de la requête
						matcher1 = pattern1.matcher(res.getString("URL"));
						matcher1.matches();
						query.setDomaine(matcher1.group(2));
						if(query.getDomaine().equals("www.clinicalkey.com") 
								|| query.getDomaine().equals("www.clinicalkey.fr")) {
							isClinicalKey = true;
						}
						//
						
						//Récupérer le chemin de la requête
						matcher1 = pattern3.matcher(res.getString("URL"));
						matcher1.matches();
						query.setChemin(matcher1.group(3));
						//
						
						//Récupérer la liste des paramètres
						matcher1 = pattern2.matcher(res.getString("URL"));
						while(matcher1.find()) {
							liste.add(matcher1.group(2));
						}
						query.setListe_params(liste);
						//
						
						//Sauvegarder l'ID de la ligne où la requête est destinée à aucune source
						
						//Vérifier le dicitonnaire de recherche
						for(int j = 0; j < motifR.size() && !foundInR; j++) {
							a = motifR.get(j).getDomaine();
							b = query.getDomaine();
							if(a.equals(b)) {
								foundInR = true;
								motif = motifR.get(j);
							}
						}
						//
						
						//Vérifier le dictionnaire de téléchargement
						for(int j = 0; j < motifT.size() && !foundInT; j++) {
							a = motifT.get(j).getDomaine();
							b = query.getDomaine();
							if(a.equals(b)) {
								foundInT = true;
								motif1 = motifT.get(j);
							}
						}
						//
						
						if(!foundInR && !foundInT) {
							corbeille.add(Integer.parseInt(res.getString("ID")));
							noSource++;
						}
						else if (query.getChemin().equals("/") && query.getListeParams().isEmpty()) {
							corbeille.add(Integer.parseInt(res.getString("ID")));
						} else {
							if(foundInT) { //Vérifier si c'est une requête de télé
								liste = new ArrayList(motif1.getListe_marks());
								for(int k = 0; k < liste.size() && !markFound; k++) {
									if(query.getChemin().contains(liste.get(k))) {
										markFound = true;
									}
								}
							}
							if(!markFound) { //Vérifier si c'est une requête de rech
								if(isClinicalKey) {
									liste = new ArrayList(motif.getListeChemins());					
									pathFound = false;
									//Vérifier le chemin
									for(int k = 0; k < liste.size() && !pathFound; k++) {
										//Vérifier le chemin
										a = liste.get(k).replaceAll(" ","");
										if(query.getChemin().contains(a)) {
											pathFound = true;
										}
									}
									if(!pathFound) {
										corbeille.add(Integer.parseInt(res.getString("ID")));
									}
								} else if(foundInR){
									liste = new ArrayList(motif.getListeChemins());	
									pathFound = false;
									for(int k = 0; k < liste.size() && !pathFound; k++) {
										a = liste.get(k).replaceAll(" ","");
										if(query.getChemin().equals(a)) {
											pathFound = true;
											liste = new ArrayList(motif.getListeParams());
											for(int m = 0; m < liste.size() && !paramFound; m++) {
												if(liste.get(m).endsWith("#")) {
													a = liste.get(m).replaceAll("#","");
													pattern4 = Pattern.compile(a+"[0-9]*");
													for(int n = 0; n < query.getListeParams().size(); n++) {
														b = query.getListeParams().get(n);
														matcher1 = pattern4.matcher(b);
														if(matcher1.matches()) paramFound = true;
													}
												} else {
													a = liste.get(m);
													if(query.getListeParams().contains(a)) paramFound = true;
												}
											}
										}
									}
								}
							}//if(!markFound)
							
							if(markFound) {
								listeT.add(Integer.parseInt(res.getString("ID")));
							}
							else if(pathFound && paramFound) {
								listeR.add(Integer.parseInt(res.getString("ID")));
							}
							else if(isClinicalKey && pathFound) {
								listeR.add(Integer.parseInt(res.getString("ID")));
							}
							else {
								corbeille.add(Integer.parseInt(res.getString("ID")));
							}
						}//else général
						
						compteur++;
					}//---->While loop
					
					ecrire.close();
					map.put("noSource",noSource);
					
					///supprimer les lignes
					Platform.runLater(() -> {
						label1.setText("Suppression des éléments ...");
					});
					
					for(int m = 0; m < corbeille.size(); m++) {
						updateProgress(((m+1)*100)/corbeille.size(),100);
						set = statement.executeUpdate("DELETE FROM Event WHERE ID = "+ corbeille.get(m));
					}
					
					identifier(compte);
					
				} catch (SQLException e) { e.printStackTrace(); }
				
				rowDeleted = corbeille.size();	
				map.put("total", rowDeleted);
				return rowDeleted;
			}
		};
		Platform.runLater(() -> {
			bar.progressProperty().bind(task.progressProperty());
			label2.setText("");
			});
		return task;
	}
	
	
	public void identifier(Compte compte) {
		
		Connection connection = null;
		Statement statement = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost/Nettoyage",
					compte.getUtilisateur(),compte.getMotDePasse());
			statement = connection.createStatement();
			
			int set = statement.executeUpdate("CREATE TABLE IF NOT EXISTS type (" + 
					"id BIGINT NOT NULL AUTO_INCREMENT," +
					"pos BIGINT NOT NULL," + 
					"type CHAR NOT NULL," + 
					"PRIMARY KEY(id)" + 
					")");
			set = statement.executeUpdate("TRUNCATE type");
			
			for(int s = 0; s < listeR.size(); s++) {
				set = statement.executeUpdate("INSERT INTO type(pos,type) "
						+ "VALUES("+listeR.get(s)+",'R')");
			}
			for(int s = 0; s < listeT.size(); s++) {
				set = statement.executeUpdate("INSERT INTO type(pos,type) "
						+ "VALUES("+listeT.get(s)+",'T')");
			}
		} catch (SQLException e) { e.printStackTrace(); }
		
		
	}
}




































