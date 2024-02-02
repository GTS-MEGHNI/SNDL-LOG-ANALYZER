package projet;

import java.awt.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Motif_Telechargement extends Motif{
	
	private String source;
	private ArrayList liste_marks;
	
	
	public Motif_Telechargement() {
		this.domaine     = null;
		this.source      = null;
		this.liste_marks = null;
	}
	
	private Motif_Telechargement(String domaine,String source,ArrayList liste) {
		
		this.domaine     = domaine;
		this.source      = source;
		this.liste_marks = new ArrayList(liste);
	}
	
	
	
	public String getSource() {
		return this.source;
	}
	
	public ArrayList getListe_marks() {
		return this.liste_marks;
	}
	
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public void setListe_marks(ArrayList liste) {
		this.liste_marks = new ArrayList(liste);
	}

	@Override
	public void ajouter(Compte compte) {
		Connection connection;
		try {
			connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Nettoyage",
					compte.getUtilisateur(),compte.getMotDePasse());
			Statement statement = connection.createStatement();
			int set = statement.executeUpdate("INSERT INTO dico_telechargement"
					+ "(domaine,source,liste_marks) VALUES"
					+ "("+"'"+this.domaine+"',"
					+"'"+this.source+"',"
					+"'"+String.join(";",this.liste_marks)+"')"
					);
			ResultSet res = statement.executeQuery("SELECT MAX(id) FROM dico_telechargement"); 
			if(res.next()) this.setId(res.getInt(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void modifier(Compte compte) {
		Connection connection;
		try {
			connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Nettoyage",
					compte.getUtilisateur(),compte.getMotDePasse());
			Statement statement = connection.createStatement();
			int set = statement.executeUpdate("UPDATE dico_telechargement SET "
					+ "domaine ="+"'"+this.domaine+"', source = "+"'"+this.source+"',"
							+ "liste_marks = "+"'"+String.join(";",this.liste_marks)+"' WHERE ID = "+this.id);
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
			int set = statement.executeUpdate("DELETE FROM dico_telechargement"
					+ " WHERE ID = "+this.id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
		
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
