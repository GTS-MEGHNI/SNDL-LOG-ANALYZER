package projet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXListView;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import projet.Compte;
import projet.Motif_Recherche;
import projet.Motif_Telechargement;

public class ControllerAgent {

	private Compte compte;
	private JFXButton rech;
	private JFXButton tel;
	private Stage stage;
	@FXML
	private Pane pane2;
	
	ArrayList<String> lst;
	ChangeListener change;
	
	
	public void quitter() {
		this.stage.close();
	}
	
	public void clickRech() throws Exception {
		
		Connection connection  = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Nettoyage",
				compte.getUtilisateur(),compte.getMotDePasse());
		Statement statement = connection.createStatement();
		ResultSet res = statement.executeQuery("SELECT * FROM dico_recherche ORDER BY id");
		ObservableList<ArrayList<String>> liste  = FXCollections.observableArrayList();
		ArrayList<String> list;
		while(res.next()) {
			list = new ArrayList<String>();
			list.add(res.getString("ID"));
			list.add(res.getString("domaine"));
			list.add(res.getString("source"));
			list.add(res.getString("liste_chemins"));
			list.add(res.getString("liste_params"));
			liste.add(list);
		}
		TableView<ArrayList<String>> table                  = new TableView();
		TableColumn<ArrayList<String>, String> id           = new TableColumn<>("ID");
		TableColumn<ArrayList<String>, String> domaine      = new TableColumn<>("domaine");
		TableColumn<ArrayList<String>, String> source       = new TableColumn<>("source");
		TableColumn<ArrayList<String>, String> liste_chemins = new TableColumn<>("liste des chemins");
		TableColumn<ArrayList<String>, String> liste_params = new TableColumn<>("liste des paramètres");
		id.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
		domaine.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
		source.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
		liste_chemins.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
		liste_params.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4)));
		table.setItems(liste);
		table.getColumns().addAll(id,domaine,source,liste_chemins,liste_params);
		table.setLayoutX(0);
		table.setLayoutY(0);
		table.setMinWidth(1155);
		table.setMinHeight(500);
		
		TextField idField      = new TextField();
		TextField domaineField = new TextField();
		TextField sourceField  = new TextField();
		TextField cheminField  = new TextField();
		TextField paramField   = new TextField();
		domaineField.setDisable(true);
		domaineField.setLayoutX(25);
		domaineField.setLayoutY(530);
		domaineField.setPromptText("Domaine");
		domaineField.setMaxWidth(200);
		sourceField.setDisable(true);
		sourceField.setLayoutX(250);
		sourceField.setLayoutY(530);
		sourceField.setPromptText("Source");
		sourceField.setMaxWidth(200);
		cheminField.setDisable(true);
		cheminField.setLayoutX(450);
		cheminField.setLayoutY(530);
		cheminField.setPromptText("Chemins");
		cheminField.setMaxWidth(200);
		paramField.setDisable(true);
		paramField.setLayoutX(650);
		paramField.setLayoutY(530);
		paramField.setPromptText("Paramètres");
		paramField.setMaxWidth(200);
		
		JFXButton apply = new JFXButton("Appliquer");
		JFXButton annuler = new JFXButton("Annuler");
		apply.setLayoutX(500);
		apply.setLayoutY(625);
		apply.setOpacity(0.5);
		apply.setDisable(true);
		annuler.setLayoutX(650);
		annuler.setLayoutY(625);
		
		JFXComboBox<String> combo = new JFXComboBox();
		combo.setLayoutX(850);
		combo.setLayoutY(530);
		combo.setMinWidth(200);
		combo.setPromptText("Sélectionnez une opération");
		combo.getItems().addAll("Ajouter","Modifier","Supprimer");
		pane2.getChildren().clear();
		pane2.getChildren().addAll(table,domaineField,sourceField,cheminField,paramField,combo,apply,annuler);
		
		change = new ChangeListener() {
			public void changed(ObservableValue a, Object b, Object c) {
				if(c != null) {
					ArrayList<String> lit = new ArrayList<String>((ArrayList<String>) c);
					idField.setText(lit.get(0));
					domaineField.setText(lit.get(1));
					sourceField.setText(lit.get(2));
					cheminField.setText(lit.get(3));
					paramField.setText(lit.get(4));							
					apply.setOnAction(e1 -> {
						if(combo.getSelectionModel().getSelectedItem().equals("Modifier")) {
							Alert alert;
							if(domaineField.getText().isEmpty() || sourceField.getText().isEmpty() 
									|| cheminField.getText().isEmpty()) {
								alert = new Alert(AlertType.ERROR);
								alert.setHeaderText("ARGUMENTS MANQUANTS.");
								alert.setTitle("ERREUR");
								alert.showAndWait();
							} else {
								Motif_Recherche motif = new Motif_Recherche();
								motif.setId(Integer.parseInt(idField.getText()));
								motif.setDomaine(domaineField.getText());
								motif.setSource(sourceField.getText());
								String[] tableau = cheminField.getText().split(";");
								motif.setListe_chemins(new ArrayList<String>(Arrays.asList(tableau)));
								tableau = paramField.getText().split(";");
								motif.setListe_params(new ArrayList<String>(Arrays.asList(tableau)));
								motif.modifier(compte);
								alert = new Alert(AlertType.INFORMATION);
								alert.setHeaderText("Motif modifié avec succès, veuillez rafraîchir la table pour voir "
										+ "les données modifiées");
								alert.showAndWait();
							}
						}
					});
				}
			}
		};
		
		annuler.setOnAction(e2 -> {
			combo.valueProperty().set(null);
			domaineField.setText("");
			sourceField.setText("");
			cheminField.setText("");
			paramField.setText("");
			domaineField.setDisable(true);
			sourceField.setDisable(true);
			cheminField.setDisable(true);
			paramField.setDisable(true);
			table.getSelectionModel().selectedItemProperty().removeListener(change);
			apply.setDisable(true);
		});
		
		combo.setOnAction(e -> {
			if(!combo.getSelectionModel().isSelected(-1))
			if(combo.getSelectionModel().getSelectedItem().equals("Ajouter")) {
				domaineField.setDisable(false);
				sourceField.setDisable(false);
				cheminField.setDisable(false);
				paramField.setDisable(false);
				domaineField.setText("");
				sourceField.setText("");
				cheminField.setText("");
				paramField.setText("");
				apply.setDisable(false);
				apply.setOpacity(1);
				table.getSelectionModel().selectedItemProperty().removeListener(change);
				apply.setOnAction(e1 -> {
					
					Alert alert;
					if(domaineField.getText().isEmpty() || sourceField.getText().isEmpty() 
							|| cheminField.getText().isEmpty()) {
						alert = new Alert(AlertType.ERROR);
						alert.setHeaderText("ARGUMENTS MANQUANTS.");
						alert.setTitle("ERREUR");
						alert.showAndWait();
					} else {
						Motif_Recherche motif = new Motif_Recherche();
						String[] tableau;
						motif.setDomaine(domaineField.getText());
						motif.setSource(sourceField.getText());
						tableau = cheminField.getText().split(";");
						motif.setListe_chemins(new ArrayList<String>(Arrays.asList(tableau)));
						tableau = paramField.getText().split(";");
						motif.setListe_params(new ArrayList<String>(Arrays.asList(tableau)));
							motif.ajouter(this.compte);
							alert = new Alert(AlertType.INFORMATION);
							alert.setHeaderText("Motif ajouté avec succès");
							alert.showAndWait();
							liste.add(new ArrayList<String>(Arrays.asList(Integer.toString(motif.getId()),
									motif.getDomaine(),motif.getSource(),
									String.join(";",motif.getListeChemins()),
									String.join(";",motif.getListeParams()))));
							table.setItems(liste);
					}
				});
			} else if(combo.getSelectionModel().getSelectedItem().equals("Modifier")) {
				table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
				domaineField.setDisable(false);
				sourceField.setDisable(false);
				cheminField.setDisable(false);
				paramField.setDisable(false);
				apply.setDisable(false);
				apply.setOpacity(1);
				
				table.getSelectionModel().selectedItemProperty().addListener(change);

			} else if(combo.getSelectionModel().getSelectedItem().equals("Supprimer")) {
				domaineField.setText("");
				sourceField.setText("");
				cheminField.setText("");
				paramField.setText("");
				apply.setDisable(false);
				apply.setOpacity(1);
				table.getSelectionModel().selectedItemProperty().removeListener(change);
				table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
				apply.setDisable(false);
				apply.setOpacity(1);
				domaineField.setDisable(true);
				sourceField.setDisable(true);
				cheminField.setDisable(true);
				paramField.setDisable(true);
				apply.setOnAction(e1 -> {
						ObservableList<ArrayList<String>> all = table.getItems();
						ObservableList<ArrayList<String>> remove = table.getSelectionModel().getSelectedItems();
						Motif_Recherche motif = new Motif_Recherche();
						for(int i = 0; i < remove.size(); i++) {
							motif.setId(Integer.parseInt(remove.get(i).get(0)));
							motif.supprimer(this.compte);
						}
						remove.forEach(all::remove);
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setContentText("");
						if(remove.size() == 1) alert.setHeaderText("Motif supprimé avec succès");
						alert.showAndWait();
				});
			}
		});
	}
	public void clickTel() throws Exception {
		
		Connection connection  = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Nettoyage",
				compte.getUtilisateur(),compte.getMotDePasse());
		Statement statement = connection.createStatement();
		ResultSet res = statement.executeQuery("SELECT * FROM dico_telechargement ORDER BY id");
		ObservableList<ArrayList<String>> liste  = FXCollections.observableArrayList();
		ArrayList<String> list;
		while(res.next()) {
			list = new ArrayList<String>();
			list.add(res.getString("ID"));
			list.add(res.getString("domaine"));
			list.add(res.getString("source"));
			list.add(res.getString("liste_marks"));
			liste.add(list);
		}
			TableView<ArrayList<String>> table                  = new TableView();
			TableColumn<ArrayList<String>, String> id           = new TableColumn<>("ID");
			TableColumn<ArrayList<String>, String> domaine      = new TableColumn<>("domaine");
			TableColumn<ArrayList<String>, String> source       = new TableColumn<>("source");
			TableColumn<ArrayList<String>, String> liste_marks  = new TableColumn<>("liste des marqueurs");
			
			id.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
			domaine.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
			source.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
			liste_marks.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
			table.setItems(liste);
			table.getColumns().addAll(id,domaine,source,liste_marks);
			table.setLayoutX(0);
			table.setLayoutY(0);
			table.setMinWidth(1155);
			table.setMinHeight(500);
			TextField idField      = new TextField();
			TextField domaineField = new TextField();
			TextField sourceField  = new TextField();
			TextField markField    = new TextField();
			
			domaineField.setDisable(true);
			domaineField.setLayoutX(25);
			domaineField.setLayoutY(530);
			domaineField.setPromptText("Domaine");
			domaineField.setMaxWidth(200);
			sourceField.setDisable(true);
			sourceField.setLayoutX(250);
			sourceField.setLayoutY(530);
			sourceField.setPromptText("Source");
			sourceField.setMaxWidth(200);
			markField.setDisable(true);
			markField.setLayoutX(475);
			markField.setLayoutY(530);
			markField.setPromptText("Liste des marqueurs");
			markField.setMaxWidth(200);
			JFXButton apply = new JFXButton("Appliquer");
			JFXButton annuler = new JFXButton("Annuler");
			apply.setLayoutX(500);
			apply.setLayoutY(625);
			apply.setOpacity(0.5);
			apply.setDisable(true);
			annuler.setLayoutX(650);
			annuler.setLayoutY(625);
			JFXComboBox<String> combo = new JFXComboBox();
			combo.setLayoutX(850);
			combo.setLayoutY(530);
			combo.setMinWidth(200);
			combo.setPromptText("Sélectionnez une opération");
			combo.getItems().addAll("Ajouter","Modifier","Supprimer");
			pane2.getChildren().clear();
			pane2.getChildren().addAll(table,domaineField,sourceField,markField,combo,apply,annuler);
			
			change = new ChangeListener() {
				public void changed(ObservableValue a, Object b, Object c) {
					if(c != null) {
						ArrayList<String> lit = new ArrayList<String>((ArrayList<String>) c);
						idField.setText(lit.get(0));
						domaineField.setText(lit.get(1));
						sourceField.setText(lit.get(2));
						markField.setText(lit.get(3));			
						apply.setOnAction(e1 -> {
							if(combo.getSelectionModel().getSelectedItem().equals("Modifier")) {
								Alert alert;
								if(domaineField.getText().isEmpty() || sourceField.getText().isEmpty() 
										|| markField.getText().isEmpty()) {
									alert = new Alert(AlertType.ERROR);
									alert.setHeaderText("ARGUMENTS MANQUANTS.");
									alert.setTitle("ERREUR");
									alert.showAndWait();
								} else {
									Motif_Telechargement motif = new Motif_Telechargement();
									motif.setId(Integer.parseInt(idField.getText()));
									motif.setDomaine(domaineField.getText());
									motif.setSource(sourceField.getText());
									String[] tableau = markField.getText().split(";");
									motif.setListe_marks(new ArrayList<String>(Arrays.asList(tableau)));
									motif.modifier(compte);
									alert = new Alert(AlertType.INFORMATION);
									alert.setHeaderText("Motif modifié avec succès, veuillez rafraîchir la table pour voir "
											+ "les données modifiées");
									alert.showAndWait();
								}
							}
						});
					}
				}
			};
			annuler.setOnAction(e2 -> {
				combo.getSelectionModel().select(-1);
				domaineField.setText("");
				sourceField.setText("");
				markField.setText("");
				domaineField.setDisable(true);
				sourceField.setDisable(true);
				markField.setDisable(true);
				table.getSelectionModel().selectedItemProperty().removeListener(change);
				table.getSelectionModel().clearSelection();
				apply.setDisable(true);
				apply.setOpacity(0.5);
			});
			
			combo.setOnAction(e -> {
				if(!combo.getSelectionModel().isSelected(-1))
				if(combo.getSelectionModel().getSelectedItem().equals("Ajouter")) {
					domaineField.setDisable(false);
					sourceField.setDisable(false);
					markField.setDisable(false);
					domaineField.setText("");
					sourceField.setText("");
					markField.setText("");
					apply.setDisable(false);
					apply.setOpacity(1);
				
					table.getSelectionModel().selectedItemProperty().removeListener(change);
					apply.setOnAction(e1 -> {
						Alert alert;
						if(domaineField.getText().isEmpty() || sourceField.getText().isEmpty() 
								|| markField.getText().isEmpty()) {
							alert = new Alert(AlertType.ERROR);
							alert.setHeaderText("ARGUMENTS MANQUANTS.");
							alert.setTitle("ERREUR");
							alert.showAndWait();
						} else {
							Motif_Telechargement motif = new Motif_Telechargement();
							String[] tableau;
							motif.setDomaine(domaineField.getText());
							motif.setSource(sourceField.getText());
							tableau = markField.getText().split(";");
							motif.setListe_marks(new ArrayList<String>(Arrays.asList(tableau)));
							motif.ajouter(this.compte);
							alert = new Alert(AlertType.INFORMATION);
							alert.setHeaderText("Motif ajouté avec succès");
							alert.showAndWait();
							liste.add(new ArrayList<String>(Arrays.asList(Integer.toString(motif.getId()),
									motif.getDomaine(),motif.getSource(),
									String.join(";",motif.getListe_marks()))));
							table.setItems(liste);
						}
					});
				} else if (combo.getSelectionModel().getSelectedItem().equals("Modifier")) {
					
					domaineField.setDisable(false);
					sourceField.setDisable(false);
					markField.setDisable(false);
					apply.setDisable(false);
					apply.setOpacity(1);
					table.getSelectionModel().selectedItemProperty().addListener(change);
					
				} else if(combo.getSelectionModel().getSelectedItem().equals("Supprimer")) {
					domaineField.setText("");
					sourceField.setText("");
					markField.setText("");
					apply.setDisable(false);
					apply.setOpacity(1);
					table.getSelectionModel().selectedItemProperty().removeListener(change);
					table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
					apply.setDisable(false);
					apply.setOpacity(1);
					domaineField.setDisable(true);
					sourceField.setDisable(true);
					markField.setDisable(true);
					apply.setOnAction(e1 -> {
							ObservableList<ArrayList<String>> all = table.getItems();
							ObservableList<ArrayList<String>> remove = table.getSelectionModel().getSelectedItems();
							Motif_Telechargement motif = new Motif_Telechargement();
							for(int i = 0; i < remove.size(); i++) {
								motif.setId(Integer.parseInt(remove.get(i).get(0)));
								motif.supprimer(this.compte);
							}
							remove.forEach(all::remove);
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setContentText("");
							if(remove.size() == 1) alert.setHeaderText("Motif supprimé avec succès");
							alert.showAndWait();
					});
				}
			});
	}
	
	public void setCompte(Compte compte) {
		this.compte = compte;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	
}
