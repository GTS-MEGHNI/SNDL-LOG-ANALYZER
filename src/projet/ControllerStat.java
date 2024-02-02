package projet;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import projet.Compte;

public class ControllerStat {
	
	private Stage stage;
	private Compte compte;
	private JFXButton rech;
	private JFXButton source;
	private JFXButton tel;
	private JFXButton user;
	private Connection connection;
	@FXML
	private ScrollPane scroll;
	
	int total = 0;
	HashMap<String,Integer> map = new HashMap();
	
	public void quitter() {
		this.stage.close();
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	public void setCompte(Compte compte) {
		this.compte = compte;
	}
	
	public void setConnection() {
		try {
			this.connection = DriverManager.getConnection("jdbc:mysql://localhost/Extraction",
					compte.getUtilisateur(),compte.getMotDePasse());
		} catch (SQLException e) {	e.printStackTrace(); }
	}
	
	public void clickRech() throws Exception {	
		System.out.println("f");
		if(!this.tableVide("Extraction","event_recherche")) {
			map.clear();
			ArrayList<String> array = new ArrayList<String>();
			System.out.println(1);
			this.fillArray(array);
			System.out.println(2);
			ChoiceDialog<String> dialog = new ChoiceDialog<>(array.get(0), array);
			dialog.setHeaderText("Sélectionnez une date");
			Optional<String> result = dialog.showAndWait();
			result.ifPresent(date -> {
				
				TextInputDialog dialog1 = new TextInputDialog();
		        dialog1.setHeaderText("Sélectionnez le nombre de mots clés à afficher");
		        dialog1.setContentText("Nombre de mots clés");
				Optional<String> result1 = dialog1.showAndWait();
				result1.ifPresent(name -> {
					try {
						int i = 0;
						int k = 1;
						i = Integer.parseInt(name);
						total = i;
						//Remplir la hash
						this.fillHashS(date);
						//Trier la HashMap
						Object[] a = map.entrySet().toArray();
						this.sortHash(map, a);
						if(total <= map.size()) {
							ObservableList<ArrayList<String>> liste = FXCollections.observableArrayList();
							for (Object e : a) {
								if(k <= i) {
									liste.add(new ArrayList<String>(Arrays.asList(((Map.Entry<String, Integer>) e).getKey(),
										Integer.toString(((Map.Entry<String, Integer>) e).getValue()))));
								}
								k++;
							}
							//Calculer le pourcentage
							this.calculPourS(date, liste);
							
							TableView<ArrayList<String>> table = new TableView();
							TableColumn<ArrayList<String>, String> doc  = new TableColumn<>("Mots clés");
							TableColumn<ArrayList<String>, String> nombre = new TableColumn<>("Nombre");
							TableColumn<ArrayList<String>, String> pour = new TableColumn<>("%");
							doc.setMinWidth(1000);
							doc.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
							nombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
							pour.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
							Label label = new Label("Traffic du "+date);
							label.setFont(new Font("Verdana",20));
							label.setLayoutX(450);
							label.setLayoutY(0);
							DropShadow ds = new DropShadow();ds.setColor(Color.BLACK);
							ds.setOffsetX(0);ds.setOffsetY(0);
							label.setEffect(ds);
							table.setItems(liste);
							table.getColumns().addAll(doc,nombre,pour);
							table.setLayoutX(0);table.setLayoutY(25);table.setMinWidth(1155);table.setMinHeight(650);
							Pane pane = new Pane();
							pane.getChildren().addAll(label,table);
							scroll.setContent(pane);	 
						} else {
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("ERREUR");
							alert.setHeaderText("Nombre de mots clés maximal("+this.allDistKeyWords(date)+") a été dépassé");
							alert.showAndWait();
						}
					}catch (NumberFormatException e) { e.printStackTrace();}
				});
			});
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERREUR");
			alert.setHeaderText("ERREUR : aucunes données à visualiser");
			alert.showAndWait();
		}
	}
	
	public void clickTel() throws Exception {
		if(!this.tableVide("extraction","event_telechargement")) {
			ArrayList<String> array = new ArrayList<String>();
			this.fillArray(array);
			ChoiceDialog<String> dialog = new ChoiceDialog<>(array.get(0), array);
			dialog.setHeaderText("Sélectionnez une date");
			Optional<String> result = dialog.showAndWait();
			result.ifPresent(date -> {
				TextInputDialog dialog1 = new TextInputDialog();
		        dialog1.setHeaderText("Sélectionnez le nombre de mots clés à afficher");
		        dialog1.setContentText("Nombre de mots clés");
				Optional<String> result1 = dialog1.showAndWait();
				result1.ifPresent(name -> {
					try {
			    		int i = Integer.parseInt(name);
			    		int k=1;
			    		//Remplir la hash
			    		this.fillHashD(date);
						if(i <= map.size()) {
							//Trier la map
							Object[] a = map.entrySet().toArray();
							this.sortHash(map, a);
							ObservableList<ArrayList<String>> liste = FXCollections.observableArrayList();
							for (Object e : a) {
								if(k <= i) {
									liste.add(new ArrayList<String>(Arrays.asList(((Map.Entry<String, Integer>) e).getKey(),
										Integer.toString(((Map.Entry<String, Integer>) e).getValue()))));
								}
								k++;
							}
							this.calculPourD(date, liste);
							
							TableView<ArrayList<String>> table = new TableView();
							TableColumn<ArrayList<String>, String> doc  = new TableColumn<>("Document");
							doc.setMinWidth(1000);
							TableColumn<ArrayList<String>, String> nombre = new TableColumn<>("Nombre");
							TableColumn<ArrayList<String>, String> pour = new TableColumn<>("%");
							doc.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
							nombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
							pour.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
							table.setItems(liste);
							table.getColumns().addAll(doc,nombre,pour);
							table.setLayoutX(0);
							table.setLayoutY(0);
							table.setMinWidth(1155);
							table.setMinHeight(650);
							scroll.setContent(table);
						} else {
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("ERREUR");
							alert.setHeaderText("Le nombre maximum("+this.CalculAllDistDocs(date)+")+a été dépassé");
							alert.showAndWait();
						}
					}catch(NumberFormatException e) { e.printStackTrace();}
				});
			});
			} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERREUR");
			alert.setHeaderText("ERREUR : aucunes données à visualiser");
			alert.showAndWait();
			}
	}
	
	public void clickSource() throws Exception {
		
		if(!this.tableVide("extraction","event_recherche") || !this.tableVide("extraction","event_telechargement")) {
			ArrayList<String> array = new ArrayList<String>();
			this.fillArray(array);
			ChoiceDialog<String> dialog = new ChoiceDialog<>(array.get(0), array);
			dialog.setHeaderText("Sélectionnez une date");
			Optional<String> result = dialog.showAndWait();
			result.ifPresent(date -> {
				System.out.println("Total ==>" + allEvent(date));
				PieChart pie = new PieChart();
				ObservableList<PieChart.Data> list = FXCollections.observableArrayList();
				//ACM
			    this.source("ACM", list, date);
			    //Aluka
				this.source("Aluka", list, date);
				//Annual Reviews
				this.source("Annual Reviews", list, date);
				//CAIRN
				this.source("CAIRN", list, date);
				//Elgar Online
				this.source("Elgar Online", list, date);
				//IEEE
				this.source("IEEE", list, date);
				//Science direct
				this.source("Science direct", list, date);
				//JSTOR
				this.source("JSTOR", list, date);
				//JSTOR Plants
				this.source("JSTOR Plants", list, date);
				//IOP Science
				this.source("IOP Science", list, date);
				//Springer Materiels
				this.source("Springer Materiels", list, date);
				//OCED Library
				this.source("OCED Library", list, date);
				//RSC Publishing Home
				this.source("RSC Publishing Home", list, date);
				//Springer Link
				this.source("Springer Link", list, date);
				//Springer Protocols
				this.source("Springer Protocols", list, date);
				//zbMATH
				this.source("zbMATH", list, date);
				//Clinical Key
				this.source("Clinical Key", list, date);
				pie.setData(list);
				pie.setTitle("Statistics on the number of users visiting each SNDL's sources in "+date);
				pie.setLegendSide(Side.BOTTOM);
				pie.setMinWidth(1000);
				pie.setMinHeight(750);
				pie.setLegendVisible(true);
			    scroll.setContent(pie);
			});
	
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERREUR");
			alert.setHeaderText("ERREUR : aucunes données à visualiser");
			alert.showAndWait();
		}
	}
	
	public void clickAutres() throws Exception {
		
		if(!this.tableVide("extraction","event_recherche") || !this.tableVide("extraction","event_telechargement")) {
			ArrayList<String> array = new ArrayList<String>();
			this.fillArray(array);
			ChoiceDialog<String> dialog = new ChoiceDialog<>(array.get(0), array);
			dialog.setHeaderText("Sélectionnez une date");
			Optional<String> result = dialog.showAndWait();
			
			result.ifPresent(date -> {
				ObservableList<ArrayList<String>> liste = FXCollections.observableArrayList();
				ArrayList<String> list = new ArrayList<String>();
				//Nombre total d'utilisateurs
				this.allUsers(date, liste);
				
				//Nombre total de visite
				this.allVisite(date, liste);
				
				//Nombre total de documents téléchargés
				this.allDocs(date,liste);
				
				//Nombre total de documents distincts téléchargés
				this.allDistDocs(date, liste);
				
				//Nombre total de requête de recherche
				this.allQuery(date, liste);
				
				//Nombre total de mots clés
				list = new ArrayList(Arrays.asList("Nombre total de mots clés",Integer.toString(this.allKeyWords(date))));
				liste.add(list);
				
				//Nombre total de mots clés distincts
				list = new ArrayList(Arrays.asList("Nombre total de mots clés distincts",Integer.toString(this.allDistKeyWords(date))));
				liste.add(list);
				
				Label label = new Label("Traffic du "+date);
				label.setFont(new Font("Verdana",20));
				label.setLayoutX(450);
				label.setLayoutY(0);
				DropShadow ds = new DropShadow();
				ds.setColor(Color.BLACK);
				ds.setOffsetX(0);
				ds.setOffsetY(0);
				label.setEffect(ds);
				TableView<ArrayList<String>> table = new TableView();
				TableColumn<ArrayList<String>, String> event  = new TableColumn<>("Evenemnt");
				TableColumn<ArrayList<String>, String> nombre = new TableColumn<>("Nombre");
				event.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
				nombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
				table.setItems(liste);
				table.getColumns().addAll(event,nombre);
				table.setLayoutX(0);
				table.setLayoutY(25);
				table.setMinWidth(1155);
				table.setMinHeight(650);
				Pane pane = new Pane();
				pane.getChildren().addAll(label,table);
				scroll.setContent(pane);
			});
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERREUR");
			alert.setHeaderText("ERREUR : aucunes données à visualiser");
			alert.showAndWait();
		}
	}
	
	///______________________________________________________________________________________________________
	
	public void allUsers(String date, ObservableList<ArrayList<String>> liste) {
		//Nombre total d'utilisateurs
		try {
			Statement statement = connection.createStatement();
			ResultSet res = statement.executeQuery("SELECT DISTINCT nom FROM event_recherche "
					+ "WHERE date = "+"'"+date+"'");
			ArrayList<String> list = new ArrayList<String>();
			ArrayList<String> rech = new ArrayList<String>();
			ArrayList<String> tele = new ArrayList<String>();
			ArrayList<String> total = new ArrayList<String>();
			while(res.next()) rech.add(res.getString("nom"));
			res = statement.executeQuery("SELECT DISTINCT nom FROM event_telechargement "
					+ "WHERE date ="+"'"+date+"'");
			while(res.next()) tele.add(res.getString("nom"));
			if(rech.size() < tele.size()) {
				for(int i = 0; i < rech.size(); i++) {
					total.add(rech.get(i));
				}
				for(int i = 0; i < tele.size(); i++) {
					if(!total.contains(tele.get(i))) total.add(tele.get(i));
				}
			} else {
				for(int i = 0; i < tele.size(); i++) {
					total.add(tele.get(i));
				}
				for(int i = 0; i < rech.size(); i++) {
					if(!total.contains(rech.get(i))) total.add(rech.get(i));
				}
			}
			list = new ArrayList(Arrays.asList("Nombre total d'utilisateurs",Integer.toString(total.size())));
			liste.add(list);
		} catch(SQLException e) { e.printStackTrace(); }
	}
	///______________________________________________________________________________________________________
	
	public void allVisite(String date, ObservableList<ArrayList<String>> liste) {
		
		try {
			Statement statement = connection.createStatement();
			ArrayList<String> list = new ArrayList<String>();
			ArrayList<String> rech = new ArrayList<String>();
			ArrayList<String> tele = new ArrayList<String>();
			ArrayList<String> total = new ArrayList<String>();
			ResultSet res = statement.executeQuery("SELECT DISTINCT session FROM event_recherche "
					+ "WHERE date ="+"'"+date+"'");
			while(res.next()) rech.add(res.getString("session"));
			res = statement.executeQuery("SELECT DISTINCT session FROM event_telechargement "
					+ "WHERE date ="+"'"+date+"'");
			while(res.next()) tele.add(res.getString("session"));
			if(rech.size() < tele.size()) {
				for(int i = 0; i < rech.size(); i++) {
					total.add(rech.get(i));
				}
				for(int i = 0; i < tele.size(); i++) {
					if(!total.contains(tele.get(i))) total.add(tele.get(i));
				}
			} else {
				for(int i = 0; i < tele.size(); i++) {
					total.add(tele.get(i));
				}
				for(int i = 0; i < rech.size(); i++) {
					if(!total.contains(rech.get(i))) total.add(rech.get(i));
				}
			}
			list = new ArrayList(Arrays.asList("Nombre total de visites",Integer.toString(total.size())));
			liste.add(list);
		} catch (SQLException e) { e.printStackTrace(); }
	}
	///________________________________________________________________________________________________________

	public void allDocs(String date, ObservableList<ArrayList<String>> liste) {
		try {
			Statement statement = connection.createStatement();
			
			ResultSet res = statement.executeQuery("SELECT COUNT(*) FROM event_telechargement "
					+ "WHERE date ="+"'"+date+"'");
			if(res.next()) {
				ArrayList<String>list = new ArrayList<String>(Arrays.asList("Nombre total de documents téléchargés",Integer.toString(res.getInt(1))));
				liste.add(list);
			}
		} catch (SQLException e) {e.printStackTrace(); }
	}
	///_____________________________________________________________________________________________________________
	
	public void allDistDocs(String date, ObservableList<ArrayList<String>> liste) {
		try {
			Statement statement = connection.createStatement();
			ResultSet res = statement.executeQuery("SELECT COUNT(DISTINCT url) FROM event_telechargement "
					+ "WHERE date ="+"'"+date+"'");
			if(res.next()) {
				ArrayList<String> list = new ArrayList<String>(Arrays.asList("Nombre total de documents distincts téléchargés",Integer.toString(res.getInt(1))));
				liste.add(list);
			}
		} catch (SQLException e) {e.printStackTrace(); }
	}
	///______________________________________________________________________________________________________________
	
	public int CalculAllDistDocs(String date) {
		try {
			Statement statement = connection.createStatement();
			ResultSet res = statement.executeQuery("SELECT COUNT(DISTINCT url) FROM event_telechargement "
					+ "WHERE date ="+"'"+date+"'");
			if(res.next()) return res.getInt(1);
		} catch (SQLException e) {e.printStackTrace(); }
		return -1;
	}
	///________________________________________________________________________________________________________________
	
	public void allQuery(String date, ObservableList<ArrayList<String>> liste) {
		try {
			Statement statement = connection.createStatement();
			ResultSet res = statement.executeQuery("SELECT COUNT(url) FROM event_recherche "
					+ "WHERE date = "+"'"+date+"'");
			if(res.next()) {
				ArrayList<String> list = new ArrayList<String>(Arrays.asList("Nombre total de requête de recherche",Integer.toString(res.getInt(1))));
				liste.add(list);
			}
		} catch (SQLException e) {e.printStackTrace(); }
	}
	///______________________________________________________________________________________________________________
		
	public void source(String source,ObservableList<PieChart.Data> list,String date) {
		
		try {
			Statement statement = connection.createStatement();
			ResultSet res;
			int total = 0;
			res = statement.executeQuery("SELECT COUNT(*) FROM event_recherche "
					+ "WHERE date ="+"'"+date+"' AND source ="+"'"+source+"'");
			if(res.next()) total += res.getInt(1);
			res = statement.executeQuery("SELECT COUNT(*) FROM event_telechargement "
					+ "WHERE date ="+"'"+date+"' AND source ="+"'"+source+"'");
			if(res.next()) total += res.getInt(1);
			if(total != 0) {
				Double a = (double) total;
				Double b = (double) allEvent(date);
				Double c = (double) total / allEvent(date)*100;
				DecimalFormat df = new DecimalFormat("##");
				if(c < 1)
				list.add(new PieChart.Data(source + " < 1 %",total));
				else list.add(new PieChart.Data(source + " " + df.format(c) +"%",total));
			}
		} catch (SQLException e) {e.printStackTrace(); }
	}
	///___________________________________________________________________________________________________________________
	
	public void fillArray(ArrayList<String> array) {
		try {
			Statement statement = connection.createStatement();
			ResultSet res1 = statement.executeQuery("SELECT DISTINCT date FROM event_recherche");
			while(res1.next()) array.add(res1.getString("date"));
		}catch(SQLException e ) {e.printStackTrace();}
	}
	///______________________________________________________________________________________________
	
	public void fillHashS(String date) {
		try {
			Statement statement = connection.createStatement();
			ResultSet res = statement.executeQuery("SELECT liste_mots FROM event_recherche "
					+ "WHERE date = "+"'"+date+"'"); 
			String[] tableau;
			ArrayList<String> list;
			String s;
			int n;
			while(res.next()) {
				tableau = res.getString("liste_mots").split(";");
				list = new ArrayList<String>(Arrays.asList(tableau));
				for(int i1 = 0; i1 < list.size(); i1++) {
					s = list.get(i1);
					if(!s.isEmpty()) if(!map.containsKey(s)) map.put(s,1);
					else {
						n = map.get(s) + 1;
						map.remove(s);
						map.put(s,n);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	///___________________________________________________________________________________________
	
	public void fillHashD(String date) {
		try {
			Statement statement = connection.createStatement();
			ResultSet res = statement.executeQuery("SELECT url FROM event_telechargement "
					+ "WHERE date = "+"'"+date+"'");
			int n = 0;
			while(res.next()) {
				if(!map.containsKey(res.getString("url"))) {
					map.put(res.getString("url"),1);
				} else {
					n = map.get(res.getString("url")) + 1;
					map.remove(res.getString("url"));
					map.put(res.getString("URL"),n);
				}
			}
		}catch(SQLException e) {e.printStackTrace();}
	}
	///________________________________________________________________________________________________
	
	public void sortHash(HashMap<String,Integer> map,Object[] a) {
		Arrays.sort(a,new Comparator<Object>() {
			public int compare(Object arg0, Object arg1) {
				return ((Map.Entry<String, Integer>) arg1).getValue()
				                   .compareTo(((Map.Entry<String, Integer>) arg0).getValue());
			}
		});
	}
	///____________________________________________________________________________________________________
	
	public void calculPourS(String date,ObservableList<ArrayList<String>> liste) {
		try {
			Statement statement = connection.createStatement();
			int total = this.allKeyWords(date);
			int n = 0;
			ResultSet res = statement.executeQuery("SELECT liste_mots FROM event_recherche WHERE date ="+"'"+date+"'");
			String[] tableau;
			ArrayList<String> rech = new ArrayList<String>();
			Double a = (double) total;
			Double b;
			DecimalFormat df = new DecimalFormat("##");
			for(int i = 0; i < liste.size(); i++) {
				String word = liste.get(i).get(0);
				n = 0;
				while(res.next()) {
					tableau = res.getString("liste_mots").split(";");
					rech = new ArrayList<String>(Arrays.asList(tableau));
					if(rech.contains(word)) n++;
				}
				a = (double) total;
				b = (double) (n / a) * 100;
				if(b < 1)
					liste.get(i).add("< 1 %");
				else liste.get(i).add( df.format(b)+ "%");
			}
		}catch(SQLException e ) {e.printStackTrace();}
	}
	///________________________________________________________________________________________________
	
	public void calculPourD(String date,ObservableList<ArrayList<String>> liste) {
		try {
			Statement statement = connection.createStatement();
			//Calculer le %
			for(int i = 0; i < liste.size(); i++) {
				String doc = liste.get(i).get(0);
				DecimalFormat df = new DecimalFormat("##");
				ResultSet res2 = statement.executeQuery("SELECT COUNT(*) "
						+ "FROM event_telechargement "
						+ "WHERE url = "+"'"+doc+"' "
						+ "AND date = "+"'"+date+"'");
				if(res2.next()) {
					Double b = (double) res2.getInt(1);
					Double c = (double) allDoc(date);
					Double d = ((b / c) * 100);
					if(d < 1) 
						liste.get(i).add("< 1 %");
					else liste.get(i).add(df.format(d) + "%");
				}
			}
		} catch(SQLException e ) {e.printStackTrace();}
	}
	///______________________________________________________________________________________________________
	
	public int allDoc(String date) {
   	 try {
			Statement statement = connection.createStatement();
			ResultSet res;
			int total = 0;
			res = statement.executeQuery("SELECT COUNT(*) FROM event_telechargement WHERE date ="+"'"+date+"'");
			if(res.next()) total += res.getInt(1);
			return total;
		} catch (SQLException e) {e.printStackTrace(); }
		return 0;    	 
    }
	///_________________________________________________________________________________________________________
	
	public int allKeyWords(String date) {
		try {
			Statement statement = connection.createStatement();
			ResultSet res = statement.executeQuery("SELECT liste_mots FROM event_recherche WHERE date ="+"'"+date+"'");
			ArrayList<String> rech = new ArrayList<String>();
			ArrayList<String> total = new ArrayList<String>();
			String[] tableau;
			int n = 0;
			while(res.next()) {
				tableau = res.getString("liste_mots").split(";");
				n += tableau.length;
			}
			return n;
		}catch(SQLException e ) {e.printStackTrace();}
		return -1;
	}
	///____________________________________________________________________________________________________________
	
	public int allEvent(String date) {
		try {
			Statement statement = connection.createStatement();
			ResultSet res;
			int total = 0;
			res = statement.executeQuery("SELECT COUNT(*) FROM event_recherche WHERE date ="+"'"+date+"'");
			if(res.next()) total += res.getInt(1);
			res = statement.executeQuery("SELECT COUNT(*) FROM event_telechargement WHERE date ="+"'"+date+"'");
			if(res.next()) total += res.getInt(1);
			return total;
		} catch (SQLException e) {e.printStackTrace(); }
		return 0;
	}
	///_______________________________________________________________________________________________________________
	
	public int allDistKeyWords(String date) {
		try {
			Statement statement = connection.createStatement();
			ResultSet res = statement.executeQuery("SELECT liste_mots FROM event_recherche WHERE date ="+"'"+date+"'");
			String[] tableau;
			ArrayList<String> rech = new ArrayList<String>();
			ArrayList<String> total = new ArrayList<String>();
			while(res.next()) {
				tableau = res.getString("liste_mots").split(";");
				rech = new ArrayList<String>(Arrays.asList(tableau));
				for(int i = 0; i < rech.size(); i++) {
					if(!total.contains(rech.get(i))) total.add(rech.get(i));
				}
			}
			return total.size();
		} catch (SQLException e) {e.printStackTrace(); }
		return -1;
	}
	///________________________________________________________________________________________________________________
	
	public Boolean tableExiste(String base,String table) {
		Statement statement    = null;
		DatabaseMetaData meta  = null;
		ResultSet res          = null;
		try {
			statement = connection.createStatement();
			meta = connection.getMetaData();
			res = meta.getTables(null, null,table,new String[] {"TABLE"}); 
			if(res.next()) return true;
			else return false;
		} catch (SQLException e) { e.printStackTrace(); }
		return null;
	}
	///______________________________________________________________________________________________________________
	
	public Boolean tableVide(String base, String table) {
		if(this.tableExiste(base, table)) {
			try {
				Statement statement = connection.createStatement();
				ResultSet res = statement.executeQuery("SELECT COUNT(*) FROM "+table);
				if(res.next()) if(res.getInt(1) != 0)  return false;
				else return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else return true;
		return null;
	}
}
	

	

