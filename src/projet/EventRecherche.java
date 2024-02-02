package projet;

import java.util.ArrayList;

public class EventRecherche extends Event {
	
	private ArrayList<String> liste_mots;
	private String source;
	
	public EventRecherche() {
		super();
		this.liste_mots = null;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public ArrayList<String> getListe_mots() {
		return liste_mots;
	}

	public void setListe_mots(ArrayList<String> liste_mots) {
		this.liste_mots = new ArrayList<String>(liste_mots);
	}
}



