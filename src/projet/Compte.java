package projet;

public class Compte {
	
	private String utilisateur;
	private String motDePasse;
	
	public Compte() {
		this.utilisateur = null;
		this.motDePasse  = null; 
	}
	
	public Compte(String user, String password) {
		this.utilisateur = user;
		this.motDePasse = password;
	}

	public String getUtilisateur() {
		return this.utilisateur;
	}

	public void setUtilisateur(String utilisateur) {
		this.utilisateur = utilisateur;
	}

	public String getMotDePasse() {
		return this.motDePasse;
	}

	public void setMotDePasse(String motDePasse) {
		this.motDePasse = motDePasse;
	}
}
	
	


