package projet;

public abstract class Motif {
	
	protected String domaine;
	protected int id;

	
	public String getDomaine() {
		return this.domaine;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDomaine(String domaine) {
		this.domaine = domaine;
	}
	
	public abstract void ajouter(Compte compte);
		
	public abstract void modifier(Compte compte);
		
	public abstract void supprimer(Compte compte);
		
	

	
	

		
	
	
	
	
	
	
	
}
