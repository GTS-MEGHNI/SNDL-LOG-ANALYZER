package projet;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXSpinner;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import projet.Compte;
import projet.ExportRecherche;
import projet.ExportTelechargement;
import projet.Extraction;
import projet.Fichier;
import projet.NettoyageApprofondit;
import projet.NettoyageClassique;

public class Controller implements Initializable{

	@FXML
	private JFXButton importer;
	@FXML
	private JFXButton nettoyer;
	@FXML
	private JFXButton extraire;
	@FXML
	private JFXButton gestion;
	@FXML
	private JFXButton stat;
	@FXML
	private Label user;
	@FXML
	private ScrollPane scroll;
	private Stage stage;
	private Compte compte;

	
	Boolean abort = false;
	
	Thread threadImport    = null;
	Thread threadNettoyage = null;
	Thread threadExtraire  = null;
	Thread threadExport    = null;
	
	int classique = 0; //stocker le nbr de lignes supprim�es par le nettoyage classique
	int approfondit = 0; //stocker le nbr de lignes supprim�es par le nettoyage approfondit
	int total = 0; // stocker le nbr total de donn�es transmisent avant le nettoyage
	int restant = 0; // stocker le nbr restant apr�s le nettoyage
	
	Long time = (long) 0;
	Long size = (long) 0;
	
	
	public void test() {
		if(this.compte.getUtilisateur().equals("user")) {
			importer.setDisable(true);
			nettoyer.setDisable(true);
			extraire.setDisable(true);
			gestion.setDisable(true);
			stat.setDisable(true);
			user.setText("Utilisateur");
		}
		else if(this.compte.getUtilisateur().equals("mainteneur")) {
			importer.setDisable(true);
			nettoyer.setDisable(true);
			extraire.setDisable(true);
			stat.setDisable(true);
			extraire.setDisable(true);
			user.setText("Mainteneur");
		}
		else if(this.compte.getUtilisateur().equals("admin")) {
			gestion.setDisable(true);
			user.setText("Administrateur");
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERREUR");
			alert.setHeaderText("Cet utilisateur n'a pas acc�s � l'application");
			alert.showAndWait();
			this.stage.close();
			System.exit(0);
		}
	}
	
	public void logOff() throws Exception {
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Fermeture de session");
		alert.setHeaderText("Confirmation requise");
		Optional<ButtonType> result = alert.showAndWait();
		
		if(result.get() == ButtonType.OK) {
			this.stage.close();
			File file = new File("config.xml");
			file.delete();
			Stage stage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("log.fxml"));
			Parent root = loader.load();
			ControllerLogIn controllerLog = loader.getController();
			controllerLog.setStage(stage); 
			stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
			stage.setScene(new Scene(root,419,367));
			stage.setResizable(false);
			stage.setTitle("Connexion au serveur de BDD Mysql");
			//Effets limuneux
			DropShadow ds = new DropShadow();
			ds.setColor(Color.WHITE);
			ds.setOffsetX(0);
			ds.setOffsetY(0);
			controllerLog.getUser().setEffect(ds);
			controllerLog.getPassword().setEffect(ds);
			stage.show();
		}
	}
	
	public void hintImport() {
		Stage stage = new Stage();
		StackPane pane = new StackPane();
		Label label = new Label();
		label.setFont(new Font("Verdana",16));
		label.setText("Ce module permet de s�lectionner des fichiers logs "
				+ "cibles qui seront envoy�s � la base de donn�es pour une analyse (nettoyage, extraction, ...).\n"
				+ "La s�lection des fichiers est filtr�e par le type de donn�es (*.log), "
				+ "ainsi que des fichiers logs pourront �tre s�lectionn�s.\n"
				+ "A la s�lection de(s) fichier(s), un processus est d�clench�, "
				+ "et commence � transf�rer les fichiers logs s�lectionn�s.\n"
				+ "A l�annulation de transfert, le syst�me interrompt le processus "
				+ "et d�clenche la suppression des donn�es envoy�es.\n"
				+ "A la terminaison de processus, le syst�me affiche la quantit� de "
				+ "donn�es import�es ainsi que le temps d�ex�cution");
		JFXDialog dialog = new JFXDialog(pane,label,JFXDialog.DialogTransition.CENTER);
		stage.setScene(new Scene(pane,1280,115));
		dialog.show();
		stage.setResizable(false);
		stage.show();
		dialog.setOnDialogClosed(e -> stage.close());
	}
	
	public void hintStat() {
		
		Stage stage = new Stage();
		StackPane pane = new StackPane();
		Label label = new Label();
		label.setFont(new Font("Verdana",16));
		label.setText("Ce module permet de visualiser les diff�rents statistiques "
				+ "sur les donn�es extraites depuis les donn�es nettoy�es qui sont :\n"
				+ " � Les mots cl�s les plus recherch�s (c�est � l�utilisateur de sp�cifier le nombre de mots cl�s il veut afficher).\n"
				+ " � Le nombre des visites des utilisateurs sur chacune des sources SNDL.\n"
				+ " � Les documents les plus t�l�charg�s recherch�s (c�est � l�utilisateur de sp�cifier le nombre de documents qu�il veut afficher).\n"
				+ " � Nombre total d�utilisateurs.\n"
				+ " � Nombre total de visites.\n" 
				+ " � Nombre total de documents t�l�charg�s\n" 
				+ " � Nombre total de requ�te de recherches.\n" 
				+ " � Nombre total de mots cl�s.\n"
				+ " � Nombre total de documents distincts t�l�charg�s\n" 
				+ " � Nombre total de mots cl�s distincts.");
		JFXDialog dialog = new JFXDialog(pane,label,JFXDialog.DialogTransition.CENTER);
		stage.setScene(new Scene(pane,1025,225));
		dialog.show();
		stage.setResizable(false);
		stage.show();
		dialog.setOnDialogClosed(e -> stage.close());
	}
	
	public void hintGestion() {
		
		Stage stage = new Stage();
		StackPane pane = new StackPane();
		Label label = new Label();
		label.setFont(new Font("Verdana",16));
		label.setText("Ce module permet de g�rer les sources de la plateforme SNDL utilis�es lors de la phase de nettoyage.\n");
		JFXDialog dialog = new JFXDialog(pane,label,JFXDialog.DialogTransition.CENTER);
		stage.setScene(new Scene(pane,850,50));
		dialog.show();
		stage.setResizable(false);
		stage.show();
		dialog.setOnDialogClosed(e -> stage.close());
	}
	
	public void hintExport() {
		
		Stage stage = new Stage();
		StackPane pane = new StackPane();
		Label label = new Label();
		label.setFont(new Font("Verdana",16));
		label.setText("Ce module permet � l�utilisateur de transf�rer les donn�es pr�sentes "
				+ "dans la base de donn�es dans un fichier Excel.\n"
				+ "L�utilisateur pourra choisir quel type de donn�es "
				+ "(recherche, t�l�chargement) pourra t�l�charger de la base pour une utilisation future.\n"
				+ "Si aucune donn�e � exporter n�existe, un message d�erreur sera affich� � l�utilisateur. ");
		JFXDialog dialog = new JFXDialog(pane,label,JFXDialog.DialogTransition.CENTER);
		stage.setScene(new Scene(pane,1100,60));
		dialog.show();
		stage.setResizable(false);
		stage.show();
		dialog.setOnDialogClosed(e -> stage.close());
	}
	
	public void hintExtraire() {
		
		Stage stage = new Stage();
		StackPane pane = new StackPane();
		Label label = new Label();
		label.setFont(new Font("Verdana",16));
		label.setText("Ce module permet d�extraire les donn�es nettoy�es dans le "
				+ "et de les stocker dans une base de donn�es.\n"
				+ "Les donn�es extraites sont des informations concernant les documents t�l�charg�s et les mots cl�s "
				+ "recherch�s par les utilisateurs de la plateforme SNDL.\n"
				+ "Si aucunes donn�es n�ont �t� import�es, ou les donn�es n�ont pas �t� nettoy�es, "
				+ "un message d�erreur s�affichera.\n"
				+ "A la fin de l�extraction, des r�sultats statistiques seront affich�es, "
				+ " d�crivant la quantit� de donn�es extraites par chaque "
				+ "type de donn�es.");
		JFXDialog dialog = new JFXDialog(pane,label,JFXDialog.DialogTransition.CENTER);
		stage.setScene(new Scene(pane,1250,100));
		dialog.show();
		stage.setResizable(false);
		stage.show();
		dialog.setOnDialogClosed(e -> stage.close());
	}
	
	public void hintNettoyer() {
		
		Stage stage = new Stage();
		StackPane pane = new StackPane();
		Label label = new Label();
		label.setFont(new Font("Verdana",16));
		label.setText("Ce module permet de supprimer toutes les donn�es inutiles "
				+ "import�es.\n"
				+ "Si les donn�es ont d�j� �t� nettoy�es ou aucunes donn�es n�existent, "
				+ "un message d�erreur s�affichera\n"
				+ "A la fin du nettoyage, une fen�tre s�affichera contenant, "
				+ "les r�sultats graphiques (graphe circulaire) et statistiques du processus de nettoyages.");
		
		JFXDialog dialog = new JFXDialog(pane,label,JFXDialog.DialogTransition.CENTER);
		stage.setScene(new Scene(pane,1250,100));
		dialog.show();
		stage.setResizable(false);
		stage.show();
		dialog.setOnDialogClosed(e -> stage.close());
	}
	
	public void clickImport() {
		
		Stage stage1 = new Stage();
        Pane layout = new Pane();
		JFXSpinner bar = new JFXSpinner();
		Label label1 = new Label("Transfert en cours du fichier ");
		Label label2 = new Label();
		
		bar.setProgress(0);
		bar.setEffect(new Reflection());
		bar.setLayoutX(170); 
		bar.setLayoutY(125); 
		bar.setPrefHeight(200); 
		bar.setPrefWidth(150);
		bar.setProgress(0);
		
		label1.setFont(Font.font ("Serif", 18));
		label1.setLayoutX(50); 
		label1.setLayoutY(300);
		
		label2.setFont(Font.font ("Serif", 18));
		label2.setLayoutX(275); 
		label2.setLayoutY(300);
		
		layout.getChildren().addAll(bar,label1,label2);
		stage1.setScene(new Scene(layout,500,500));
		stage1.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
		stage1.setResizable(false);
		stage1.setTitle("Import en cours ...");
		stage1.initModality(Modality.APPLICATION_MODAL);
		
		abort = false;
		Alert alert;
		Boolean canProcess = false;
		Boolean cancel = false;
		if(!this.tableVide("nettoyage", "type") && this.siTableEstNettoyee()) {
			alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("INFORMATION");
			alert.setHeaderText("Des donn�es nettoy�es ne sont pas encore extraites");
			alert.setContentText("Voulez-vous �craser les donn�es nettoy�es par les fichiers s�lectionn�s ?");
			Optional<ButtonType> result = alert.showAndWait();
			if(result.get() == ButtonType.OK) canProcess = true;
			if(result.get() == ButtonType.CANCEL) cancel = true;
		}
		
		if(canProcess) {
			this.dropTable("nettoyage","event");
			this.truncateTable("nettoyage", "type");
		}
		
		if(!cancel) {
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Log files (*.log)", "*.log");
			fileChooser.getExtensionFilters().add(extFilter);
			List<File> liste = fileChooser.showOpenMultipleDialog(this.stage);
			if(liste != null) {
				int s = this.fichierLog(liste);
				int z = this.fichierVide(liste);
				if(z != -1) {
					alert = new Alert(AlertType.ERROR);
					alert.setTitle("ERREUR");
					alert.setHeaderText("Le fichier "+liste.get(z).getName()+" est vide");
					alert.showAndWait();
				}
				else if(s != -1) {
					alert = new Alert(AlertType.ERROR);
					alert.setTitle("ERREUR");
					alert.setHeaderText("Le fichier [" + liste.get(s).getName() + "] n'est pas un fichier log");
					alert.showAndWait();
				}  else if(!this.tableVide("nettoyage", "event")) {
					 	int t = this.fichierExiste(liste);
					 	if(t != -1) {
					 		alert = new Alert(AlertType.ERROR);
					 		alert.setTitle("ERREUR");
					 		alert.setHeaderText("Le fichier "+liste.get(t).getName()+" a d�j� �t� import�");
					 		alert.showAndWait();
					 	}
				} else {
					threadImport = new Thread(new Runnable() {
						public void run() {
							Fichier a;
							Thread t1;
							size = (long) 0;
							for(int i = 0; i < liste.size() && !abort; i++) {
								 a = new Fichier(liste.get(i).getAbsolutePath());
								 size += a.length();
								 Task<Long> task = a.Transferer(compte,bar,label2);
								 Platform.runLater(() -> {
									 task.setOnSucceeded(e -> time += task.getValue());
								 });
								 t1 = new Thread(task);
								 t1.setDaemon(true);
								 t1.start();
								 while(t1.isAlive()) {
									 if(abort) { t1.stop(); }
								 }
								 
							}
							Platform.runLater(() -> {
								Alert alert1 = new Alert(AlertType.INFORMATION);
								alert1.setTitle("Op�ration termin�e");
								if(liste.size() == 1) {
									if(size/1024/1024 != 0) {
										alert1.setHeaderText("Quantit� de donn�es import�es "
											+size/1024/1024+ "MB,"+
											"Temps du transfert  : "+time+" sec");
									}
									else {
										alert1.setHeaderText("Quantit� de donn�es import�es "
												+size+ "Ko,"+
												"Temps du transfert < 1 sec");
									}
								} else {
									if(size/1024/1024 != 0) {
										alert1.setHeaderText("Quantit� de donn�es import�es "
											+size/1024/1024+ "MB,"+
											"Temps du transfert  : "+time+" sec");
									}
									else {
										alert1.setHeaderText("Quantit� de donn�es import�es "
												+size+ "Ko,"+
												"Temps du transfert < 1 sec");
									}
								}
								Alert alert2 = new Alert(AlertType.ERROR);
								alert2.setTitle("Op�ration abandonn�e");
								if(liste.size() == 1) {
									alert2.setHeaderText("Le transfert du fichier a �t� abandonn�");
								} else {
									alert2.setHeaderText("Le transfert des fichiers a �t� abandonn�");
								}
								if(abort) alert2.showAndWait();
								else {
									alert1.showAndWait();
								}
								
								stage1.close();
							});
						}
					});
					
					threadImport.setDaemon(true);
					
					threadImport.start();
					
					//quitter le transfert 
					stage1.setOnCloseRequest(e -> {
						e.consume();
						Alert alert1 = new Alert(AlertType.CONFIRMATION);
						alert1.setTitle("Avertissement");
						alert1.setHeaderText("Abandon du transfert");
						alert1.setContentText("Voulez-vous vraiment quitter le transfert ? "
								+ "toutes les donn�es seront perdues");
						Optional<ButtonType> result = alert1.showAndWait();
						if (result.get() == ButtonType.OK){
						    stage1.close();
						    abort = true;
						    threadImport.interrupt();
						    this.truncateTable("nettoyage","event1");
						}
					});
					
					stage1.showAndWait();
					
				}// if
			}
		}
	}
		
	///Button nettoyer
	
	public void clickNettoyer() {
		
		Stage stage = new Stage();
        Pane layout = new Pane();
        Boolean canClean = true;
        abort = false;

        //Barre de progression
		JFXSpinner bar = new JFXSpinner();
		bar.setEffect(new Reflection());
		bar.setLayoutX(170);
		bar.setLayoutY(125);
		bar.setPrefHeight(200);
		bar.setPrefWidth(150);
		///
		
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
		
		layout.getChildren().addAll(bar,label1,label2);
		stage.setScene(new Scene(layout,500,500));
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Nettoyage en cours ...");
		stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
		stage.setResizable(false);
		
		//
		if(!this.tableExiste("nettoyage","event") ||this.tableVide("nettoyage","event")) {///////////////////////////
			Alert alert1 = new Alert(AlertType.ERROR);
			alert1.setTitle("ERREUR");
			alert1.setHeaderText("ERREUR : aucun fichier import�");
			alert1.showAndWait();
		}
		else {
			///Alert box
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirmation");
			alert.setHeaderText("D�but du processus de nettoyage");
			alert.setContentText("Voulez-vous commencer le processus de nettoyage ?");
			Optional<ButtonType> result = alert.showAndWait();
			
			if(result.get() == ButtonType.OK && canClean) {
				
				//R�cup�rer le nbr de lignes avant de commencer le nettoyage
				try {
					total = this.nbrLignes();
				} catch (SQLException e) { e.printStackTrace(); }
				
				//V�rifier que la table n'a pas encore �t� nettoy�e 
				threadNettoyage = new Thread(new Runnable() {
					public void run() {
						/// N E T T O Y A G E __ C L A S S I Q U E
						NettoyageClassique nettoyageClassique     = new NettoyageClassique();
						NettoyageApprofondit nettoyageApprofondit = new NettoyageApprofondit();
						Task<Integer> taskClassique = nettoyageClassique.nettoyer(compte, bar, label1, label2);
						Task<Integer> taskApprofondit = nettoyageApprofondit.nettoyer(compte, bar, label1, label2);
						Thread t = new Thread(taskClassique);
						t.setDaemon(true);
						t.start();
						while(t.isAlive()) {
							if(abort) t.stop();
						}
						if(abort) threadNettoyage.interrupt();
						Platform.runLater(() -> {
							if(!abort)
							classique = taskClassique.getValue();
							
						});
						/// N E T T O Y A G E __ A P P R O F O N D I T
						t = new Thread(taskApprofondit);
						t.setDaemon(true);
						t.start();
						while(t.isAlive()) {
							if(abort) t.stop();
						}
						if(abort) threadNettoyage.interrupt();
						// F I N __ D E __ N E T T O Y A G E
						Platform.runLater(() -> {
							if(!abort) {
								approfondit = taskApprofondit.getValue();
								Alert alert1 = new Alert(AlertType.INFORMATION);
								alert1.setTitle("Op�ration termin�e");
								alert1.setHeaderText("Le processus de nettoyage est termin�");
								alert1.showAndWait();
								HashMap<String,Integer>map = nettoyageClassique.map;
								//Table
								int cc=0;
								ObservableList<ArrayList<String>> liste = FXCollections.observableArrayList();
								liste.add(new ArrayList<String>(Arrays.asList("Donn�es avant le processus du nettoyage",Integer.toString(total))));
								liste.add(new ArrayList<String>(Arrays.asList("Erreurs 404 d�tect�es",Integer.toString(map.get("404")))));
								liste.add(new ArrayList<String>(Arrays.asList("Fichiers images d�tect�s",Integer.toString(map.get("images")))));
								liste.add(new ArrayList<String>(Arrays.asList("Fichiers JavaScript d�tect�s",Integer.toString(map.get("js")))));
								liste.add(new ArrayList<String>(Arrays.asList("Fichiers CSS d�tect�s",Integer.toString(map.get("css")))));
								liste.add(new ArrayList<String>(Arrays.asList("Logs g�n�r�s par des robots web",Integer.toString(map.get("robots")))));
								liste.add(new ArrayList<String>(Arrays.asList("Logs g�n�r�s par des utilisateurs anonymes",Integer.toString(map.get("user")))));
								liste.add(new ArrayList<String>(Arrays.asList("Donn�es supprim�es par le nettoyage classique",Integer.toString(map.get("total")))));
								cc = map.get("total");
								map = nettoyageApprofondit.map;
								liste.add(new ArrayList<String>(Arrays.asList("Logs non destin�es aux sources",Integer.toString(map.get("noSource")))));
								liste.add(new ArrayList<String>(Arrays.asList("Donn�es supprim�es par le nettoyage approfondit",Integer.toString(map.get("total")))));
								liste.add(new ArrayList<String>(Arrays.asList("Quantit� total de donn�es supprim�es",Integer.toString(cc+map.get("total")))));
								liste.add(new ArrayList<String>(Arrays.asList("Quantit� de donn�es restantes",Integer.toString(total - classique - approfondit))));
								TableView<ArrayList<String>> table = new TableView();
								TableColumn<ArrayList<String>, String> event  = new TableColumn<>("R�sultat");
								TableColumn<ArrayList<String>, String> nombre = new TableColumn<>("Nbr d'�v�nements");
								event.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
								nombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
								table.setItems(liste);
								table.getColumns().addAll(event,nombre);
								table.setLayoutX(600);
								table.setLayoutY(30);
								table.setMinWidth(800);
								Label label = new Label("R�sultats statistiques du nettoyage");
								DropShadow ds = new DropShadow();
								ds.setColor(Color.BLACK);
								ds.setOffsetX(0);
								ds.setOffsetY(0);
								label.setEffect(ds);
								label.setLayoutX(800);
								label.setFont(new Font("Verdana",20));
								//Etablir le PieChart
								restant = total - classique - approfondit;
								Pane pane1 = new Pane();
								Double a = (double) classique;
								Double b = (double) approfondit;
								Double c = (double) restant;
								Double d = (double) total;
								DecimalFormat df2 = new DecimalFormat("##");
								ObservableList<PieChart.Data> list = FXCollections.observableArrayList();
								list.addAll(new PieChart.Data("Classique "   + df2.format((a / d) * 100) + "%",classique),
											new PieChart.Data("Approfondit " + df2.format((b / d) * 100) + "%",approfondit),
											new PieChart.Data("Restant "     + df2.format((c / d) * 100) + "%",restant)
											);
								PieChart pie = new PieChart();
								pie.setData(list);
								pie.setLayoutX(0);
								pie.setTitle("R�sultat graphique du nettoyage");
								pie.setLegendSide(Side.BOTTOM);
								pie.setLegendVisible(true);
								pie.setStartAngle(90);
								pane1.getChildren().addAll(pie,table,label);
								stage.setScene(new Scene(pane1,1366,700));
								stage.setTitle("R�sultats du nettoyage");
								stage.show();
								stage.setOnCloseRequest(e -> stage.close());
							}
						});
					}
				});
						
				///L A N C E R __ L E __ N E T T O Y A G E
				threadNettoyage.start();
				///A R R E T __ D U __ N E T T O Y A G E
				stage.setOnCloseRequest(e -> {
					e.consume();
					Alert alert1 = new Alert(AlertType.CONFIRMATION);
					alert1.setTitle("AVERTISSEMENT");
					alert1.setHeaderText("Arr�t le processus de nettoyage");
					Optional<ButtonType> result1 = alert1.showAndWait();
					if (result.get() == ButtonType.OK) {
						abort = true;
						stage.close();
					}
				});
				///
				if(abort) {
					threadNettoyage.interrupt();
					stage.close();
				}
				stage.showAndWait();	
				
			}
		}
	}
	
	public void clickExtraire() {
		
		if(this.tableVide("nettoyage", "event")) {////////////////////////////////////////
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("ERREUR : aucune donn�es import�es");
			alert.showAndWait();
		}
		else if(this.tableVide("nettoyage","type")) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("ERREUR : les donn�es ne sont pas encore nettoy�es");
			alert.showAndWait();
		}
		else {
			
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setHeaderText("D�but de processus d'�xtraction");
			alert.setContentText("Voulez-vous commencer le processus d'extraction vers une base de donn�es ?");
			Optional<ButtonType> result1 = alert.showAndWait();
			
			Stage stage = new Stage();
	        Pane layout = new Pane();
	        abort = false;
	        int R = this.calculType("R");
	        int T = this.calculType("T");
	        int total = this.calculTaille("nettoyage", "event");
	        //Barre de progression
			JFXSpinner bar = new JFXSpinner();
			bar.setEffect(new Reflection());
			bar.setLayoutX(170);
			bar.setLayoutY(125);
			bar.setPrefHeight(200);
			bar.setPrefWidth(150);
			bar.setProgress(0);
			///
			
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
			stage.setScene(new Scene(layout,500,500));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Extraction en cours ...");
			stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
			stage.setResizable(false);
			///
			
			if(result1.get() == ButtonType.OK) {
				threadExtraire = new Thread(new Runnable() {
					public void run() {
						Extraction extraire = new Extraction();
						Task<Integer> task = extraire.extraire(compte,bar,label1,label2);
						Thread t = new Thread(task);
						t.setDaemon(true);
						t.start();
						while(t.isAlive()) {
							if(abort) t.interrupt();
						}
						Platform.runLater(() -> {
							stage.close();
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setHeaderText("Extraction termin�");
							alert.showAndWait();
							stage.close();
						});
					}
				});
				threadExtraire.setDaemon(true);
				threadExtraire.start();
				//Quitter le processus 
				stage.setOnCloseRequest(e -> {
					e.consume();
					Alert alert1 = new Alert(AlertType.WARNING);
					alert1.setTitle("AVERTISSEMENT");
					alert1.setHeaderText("Arr�t du processus d'extraction");
					alert.setContentText("Voulez-vous arr�ter le processus d'extraction ? toutes les donn�es extraites seront perdues");
					Optional<ButtonType> result = alert1.showAndWait();
					if (result.get() == ButtonType.OK) {
						abort = true;
						stage.close();
					}
				});
				if(abort) {
					threadExtraire.interrupt();
					stage.close();
				}
				stage.showAndWait();
			}
		}
	}
	
	public void clickExporter() throws Exception{
		
		Stage stage = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("choixExport.fxml"));
		Parent root = loader.load();
		ControllerExport controller = loader.getController();
		controller.setCompte(this.compte);
		controller.setStage(stage);
		stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
		stage.setTitle("Exporter les donn�es vers Excel");
		stage.setScene(new Scene(root,300,123));
		stage.setResizable(false);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
	}
	
	
	public void clickGestion() throws Exception {
		Stage stage = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("homeAgent.fxml"));
		Parent root = loader.load();
		ControllerAgent controller = loader.getController();
		controller.setCompte(this.compte);
		controller.setStage(stage);
		stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
		stage.setTitle("Maintenance des sources SNDL");
		stage.setScene(new Scene(root,1366,700));
		stage.showAndWait();
	}
	
	public void clickStat() throws Exception {
		Stage stage = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("stat.fxml"));
		Parent root = loader.load();
		ControllerStat controller = loader.getController();
		controller.setCompte(this.compte);
		controller.setConnection();
		controller.setStage(stage);
		stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
		stage.setTitle("Interface des statistiques");
		stage.setScene(new Scene(root,1366,700));
		stage.showAndWait();
	}

	
	public void setCompte(Compte compte) {
		this.compte = compte;
	}
	
		

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	

	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	
	///M�thode dropper la table event si le transfert est abondonn�
	public void truncateTable(String base,String table) {
		Connection connection = null;
		Statement statement   = null;
		try {
			connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/"+base,compte.getUtilisateur(),compte.getMotDePasse());
			statement = connection.createStatement();
			int set = statement.executeUpdate("TRUNCATE "+table);
		} catch(SQLException e) { e.printStackTrace(); }
	}
	

	//V�rifier si la table est nettoy�e avant de commencer le transfert
	public boolean siTableEstNettoyee() {
		
		if(!this.tableVide("nettoyage", "type")) return true;
		else return false;
	}
	
	
	///R�cup�rer le nbr de ligne avant de nettoyer
	public int nbrLignes() throws SQLException {
		Connection connection = null;
		Statement statement   = null;
		ResultSet res = null;
		int n = 0;
		try {
			connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Nettoyage",compte.getUtilisateur(),compte.getMotDePasse());
			DatabaseMetaData meta = connection.getMetaData();
		    ResultSet res1 = meta.getTables(null, null, "event",new String[] {"TABLE"});  
			statement = connection.createStatement();
			if(res1.next()) {
				res = statement.executeQuery("SELECT COUNT(*) FROM event");
				if(res.next())	n = res.getInt(1);
			}
		} catch (SQLException e) { e.printStackTrace(); }
		
		return n;
	}
	///
	
	//V�rifier les fichiers import�s avant de commender le transfert
	public int fichierLog(List<File> liste) {
		
		for(int i = 0; i < liste.size(); i++) {
			if(!liste.get(i).getName().endsWith(".log")) {
				return i;
			} 
		}
		return -1;
	}
	
	public int fichierExiste(List<File> liste) {
		
		Connection connection = null;
		Statement statement = null;
		try {
			connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Nettoyage",
					compte.getUtilisateur(),compte.getMotDePasse());
			statement = connection.createStatement();
			ResultSet res;
			BufferedReader lire = null;
			String line;
			for(int i = 0; i < liste.size(); i++) {
				lire = new BufferedReader(new FileReader(liste.get(i).getAbsolutePath()));
				line = lire.readLine();
				Pattern pattern = Pattern.compile("(([0-9\\.]+) (.*?|-) (.*?|-) \\[([0-9]{2}/[a-zA-Z]+/[0-9]{4}):(.*?)\\+[0-9]+\\]) \"(.*?) (.*?) "
						+ "(.*?)\" ([0-9]+) ([0-9]+)");
				Matcher matcher = null;
				matcher = pattern.matcher(line);
				matcher.matches();
				res = statement.executeQuery("SELECT ip,session,nom,date,heure,url "
						+ "FROM event WHERE "
						+ "ip ="+"'"+matcher.group(2)+"' "
						+ "AND session ="+"'"+matcher.group(3)+"' "
						+ "AND nom ="+"'"+matcher.group(4)+"' "
						+ "AND date ="+"'"+matcher.group(5)+"' "
						+ "AND heure ="+"'"+matcher.group(6)+"' "
						+ "AND url ="+"'"+matcher.group(8)+"'");
				if(res.next()) return i;
			}
		} catch (SQLException e) { e.printStackTrace(); } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) { e.printStackTrace(); }
		return -1;
	}
	
	public int fichierVide(List<File> liste) {
		
		for(int i = 0; i < liste.size(); i++) if(liste.get(i).length() == 0) return i;
		return -1;
	}
	
	public Boolean tableVide(String base, String table) {
		if(this.tableExiste(base, table)) {
			Connection connection;
			try {
				connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/"+base,
						compte.getUtilisateur(),compte.getMotDePasse());
				Statement statement = connection.createStatement();
				ResultSet res = statement.executeQuery("SELECT COUNT(*) FROM "+table);
				if(res.next()) {
					if(res.getInt(1) != 0) {
						return false;
					} else {
						return true;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else return true;
		
		return null;
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
	
	public int calculType(String type) {
		
		Connection connection  = null;
		Statement statement    = null;
		ResultSet res          = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/nettoyage",
					compte.getUtilisateur(),compte.getMotDePasse());
			statement = connection.createStatement();
			res = statement.executeQuery("SELECT COUNT(*) FROM type "
					+ "WHERE type = "+"'"+type+"'");
			if(res.next()) {
				return res.getInt(1);
			}
		} catch (SQLException e) { e.printStackTrace(); }
		
		return -1;
	}
	
	public int calculTaille(String base, String table) {
		Connection connection  = null;
		Statement statement    = null;
		ResultSet res          = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/"+base,
					compte.getUtilisateur(),compte.getMotDePasse());
			statement = connection.createStatement();
			res = statement.executeQuery("SELECT COUNT(*) FROM "+table);
			if(res.next()) {
				return res.getInt(1);
			}
		} catch (SQLException e) { e.printStackTrace(); }
		return -1;
	}
	
	public void dropTable(String base, String table) {
		Connection connection  = null;
		Statement statement    = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/"+base,
					compte.getUtilisateur(),compte.getMotDePasse());
			statement = connection.createStatement();
			int set = statement.executeUpdate("DROP TABLE IF EXISTS "+table);
		} catch (SQLException e) { e.printStackTrace(); }
	}
	

	
	
	
	
	
	
	
}
	
	
	
	
	

		
		
		

		
		
		
	
	
	
	

		
		
		


		
	
	
	
	
	
	
	

