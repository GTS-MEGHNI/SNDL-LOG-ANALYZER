package projet;


public class Event {
	
	protected String url;
	protected String session;
	protected String nom;
	protected String date;
	protected String heure;
	private String ip;
	private String methode;
	private String protocole;
	private int statut;
	private int taille;
	
	public Event() {
		this.ip        = null;
		this.session   = null;
		this.nom       = null;
		this.date      = null;
		this.heure     = null;
		this.methode   = null;
		this.url       = null;
		this.protocole = null;
		this.statut    = -1;
		this.taille    = -1;
	}
	
	public String getIp() {
		return this.ip;
	}
	
	public String getSession() {
		return this.session;
	}
	
	public String getNom() {
		return this.nom;
	}
	
	public String getDate() {
		return this.date;
	}
	
	public String getHeure() {
		return this.heure;
	}
	
	public String getMethode() {
		return this.methode;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public String getProtocole() {
		return this.protocole;
	}
	
	public int getStatut() {
		return this.statut;
	}
	
	public int getTaille() {
		return this.taille;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public void setSession(String session) {
		this.session = session;
	}
	
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public void setHeure(String heure) {
		this.heure = heure;
	}
	
	public void setMethode(String methode) {
		this.methode = methode;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setProtocole(String protocole) {
		this.protocole = protocole;
	}
	
	public void setStatut(int statut) {
		this.statut = statut;
	}
	
	public void setTaille(int taille) {
		this.taille = taille;
	}
	
	public void print() {
		System.out.println("IP : "+this.ip);
		System.out.println("Session : "+this.session);
		System.out.println("Nom : "+this.nom);
		System.out.println("Date : "+this.date);
		System.out.println("Heure : "+this.heure);
		System.out.println("Méthode : "+this.methode);
		System.out.println("URL : "+this.url);
		System.out.println("Protocole : "+this.protocole);
		System.out.println("Taille : "+this.taille);
		System.out.println("Statut : "+this.statut);
	}
	
	
	
}
		
	
	
	
	
	
	
	
	
		
	
	


