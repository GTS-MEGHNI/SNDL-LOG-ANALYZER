package projet;

import java.util.ArrayList;

public class Requete  {
	
	protected String domaine;
	protected String chemin;
	private ArrayList<String> liste_params;
	
	public Requete() {
		this.domaine = null;
		this.chemin  = null; 
	}
	
	public String getDomaine() {
		return this.domaine;
	}
	
	public String getChemin() {
		return this.chemin;
	}
	
	public ArrayList<String> getListeParams() {
		return this.liste_params;
	}
	public void setDomaine(String domaine) {
		this.domaine = domaine;
	}
	
	public void setChemin(String chemin) {
		this.chemin = chemin;
	}
	
	public void setListe_params(ArrayList liste) {
		this.liste_params = new ArrayList<String>(liste);
	}
	
	
	

}
