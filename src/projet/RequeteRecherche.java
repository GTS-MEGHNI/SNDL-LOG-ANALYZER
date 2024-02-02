package projet;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequeteRecherche extends Requete {
	
	private ArrayList liste_mots;
	
	public RequeteRecherche() {
		super();
		this.liste_mots = null;
	}
	
	public ArrayList getListe_mots() {
		return this.liste_mots;
	}
	
	public void setListe_mots(String liste) {
		
	}
	
	public ArrayList Decoder(String url, ArrayList<String> liste) {
		
		String a,b;
		ArrayList<String> result = new ArrayList<String>();
		Pattern pattern1 = Pattern.compile("http(s|)://(.+?):[0-9]+(/.*?)(\\?.*|)");
		Pattern pattern5 = Pattern.compile(".*/(.*)");
		Matcher matcher1;

		if(!this.domaine.equals("www.clinicalkey.com") && 
				!this.domaine.equals("www.clinicalkey.fr")) {
			for(int i = 0; i < liste.size(); i++) {
				a = liste.get(i);
				if(!a.isEmpty()) {
					try {
						b = java.net.URLDecoder.decode(a, "UTF-8");
						result.add(b);
					} catch (UnsupportedEncodingException e) { e.printStackTrace(); }
				}
			}
		} else {
			matcher1 = pattern1.matcher(url);
			matcher1.matches();
			this.setChemin(matcher1.group(3));
			matcher1 = pattern5.matcher(this.getChemin());
			matcher1.matches();
			try {
				b = java.net.URLDecoder.decode(matcher1.group(1), "UTF-8");
				result.add(b);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
			
		
	
		
		
		
		
		return result;
	}

}
