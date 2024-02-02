package projet;

import com.jfoenix.controls.JFXSpinner;

import javafx.concurrent.Task;
import javafx.scene.control.Label;

public abstract class Nettoyage {
	
	public abstract Task nettoyer(Compte compte,JFXSpinner bar,Label label1, Label label2);

}
