package projet;

import com.jfoenix.controls.JFXSpinner;
import javafx.concurrent.Task;
import javafx.scene.control.Label;

public abstract class Export {
	public abstract Task<Integer> export(JFXSpinner bar, Label label1, Label label2, Compte compte,String path);
}


