package projet;

public class EventTele extends Event {
	
	private String source;
	
	public EventTele() {
		super();
		this.source = null;
	}
	
	public EventTele(String session,String nom, String date, String url) {
		this.session = session;
		this.nom = nom;
		this.date = date;
		this.url = url;
	}
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}


