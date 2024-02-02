package projet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Motif_Recherche extends Motif{
	
	private   ArrayList<String> liste_chemins;
	protected String source;
	protected ArrayList liste_params;	
	
	public Motif_Recherche() {
		this.domaine       = null;
		this.source        = null;
		this.liste_chemins = null;
		this.liste_params  = null;
	}
	
	public Motif_Recherche(String domaine,String source,ArrayList liste_chemins,ArrayList liste_params) {
		
		this.domaine       = domaine;
		this.source        = source;
		this.liste_chemins = new ArrayList(liste_chemins);
		this.liste_params  = new ArrayList(liste_params);
	}
	
	public String getSource() {
		return this.source;
	}
	
	public ArrayList getListeChemins() {
		return this.liste_chemins;
	}
	
	public ArrayList getListeParams() {
		return this.liste_params;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public void setListe_chemins(ArrayList liste) {
		this.liste_chemins = new ArrayList(liste);
	}
	
	public void setListe_params(ArrayList liste) {
		this.liste_params = new ArrayList(liste);
	}

	public void ajouter(Compte compte) {
		Connection connection;
		try {
			connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Nettoyage",
					compte.getUtilisateur(),compte.getMotDePasse());
			Statement statement = connection.createStatement();
			int set = statement.executeUpdate("INSERT INTO dico_recherche"
					+ "(domaine,source,liste_chemins,liste_params) "
					+ "VALUES("+"'"+this.domaine+"',"
					+"'"+this.source+"',"
					+"'"+String.join(";",this.liste_chemins)+"',"
					+"'"+String.join(";",this.liste_params)+"')");
			ResultSet res = statement.executeQuery("SELECT MAX(id) FROM dico_recherche"); 
			if(res.next()) this.setId(res.getInt(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public void modifier(Compte compte) {
		Connection connection;
		try {
			connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Nettoyage",
					compte.getUtilisateur(),compte.getMotDePasse());
			Statement statement = connection.createStatement();
			int set = statement.executeUpdate("UPDATE dico_recherche SET "
					+ "domaine ="+"'"+this.domaine+"', source = "+"'"+this.source+"',"
							+ "liste_chemins = "+"'"+String.join(";",this.liste_chemins)+"',"
									+ "liste_params = "+"'"+String.join(";",this.liste_params)+"' WHERE ID = "+this.id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	
	public void supprimer(Compte compte) {
		Connection connection;
		try {
			connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Nettoyage",
					compte.getUtilisateur(),compte.getMotDePasse());
			Statement statement = connection.createStatement();
			int set = statement.executeUpdate("DELETE FROM dico_recherche"
					+ " WHERE ID = "+this.id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
}















