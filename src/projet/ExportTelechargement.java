package projet;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.jfoenix.controls.JFXSpinner;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;

public class ExportTelechargement extends Export{

	public Task<Integer> export(JFXSpinner bar, Label label1, Label label2, Compte compte, String path) {
		
		Task<Integer> task = new Task<Integer>() {
			protected Integer call() throws Exception {
				Platform.runLater(() -> {
					label1.setText("Exportation en cours ...");
					label2.setText("Veuillez patienter s'il vous plaît");
				});
				Connection connection = null;
				Statement statement = null;
				ResultSet res = null;
				int i = 1;
				int total = 0;
				try {
					connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Extraction",
							compte.getUtilisateur(),compte.getMotDePasse());
					statement = connection.createStatement();
					HSSFWorkbook workbook = new HSSFWorkbook();
					HSSFSheet sheet = workbook.createSheet("Data Sheet");
					HSSFCellStyle style = workbook.createCellStyle();
					HSSFFont font = workbook.createFont();
					font.setBold(true);
					font.setFontHeightInPoints((short)16);
					style.setFont(font);
					HSSFRow row = sheet.createRow(0);
					row.setHeight((short) 500);
					HSSFCell cell;
					cell = row.createCell(0);
					cell.setCellValue("Session");
					cell = row.createCell(1);
					cell.setCellValue("Nom");
					cell = row.createCell(2);
					cell.setCellValue("Source");
					cell = row.createCell(3);
					cell.setCellValue("Date");
					cell = row.createCell(4);
					cell.setCellValue("Heure");
					cell = row.createCell(5);
					cell.setCellValue("URL");
					for(int o = 0; o < 6; o++) {
						row.getCell(o).setCellStyle(style);
					}
					row = sheet.createRow(i);
					for(int k = 0; k < 5; k++) {
						sheet.setColumnWidth(k, 5000);
					}
					sheet.setColumnWidth(5, 25000);
					
					//Calculer le nbr de ligne de la table
					res = statement.executeQuery("SELECT COUNT(*) FROM event_telechargement");
					if(res.next()) {
						total = res.getInt(1);
					}					
					res = statement.executeQuery("SELECT * FROM event_telechargement");
					while(res.next()) {
						
						this.updateProgress((i * 100) / total,100);
						cell = row.createCell(0);
						cell.setCellValue(res.getString("session"));
						cell = row.createCell(1);
						cell.setCellValue(res.getString("nom"));
						cell = row.createCell(2);
						cell.setCellValue(res.getString("source"));
						cell = row.createCell(3);
						cell.setCellValue(res.getString("date"));
						cell = row.createCell(4);
						cell.setCellValue(res.getString("heure"));
						cell = row.createCell(5);
						cell.setCellValue(res.getString("url"));
						i++;
						row = sheet.createRow(i);
					}
					workbook.write(new FileOutputStream(path+".xls"));
					workbook.close();	
					Platform.runLater(() -> {
						label1.setText("Exportation terminé !");
					});
				} catch (Exception e) { e.printStackTrace(); }
				return 1;
			}
		};
		Platform.runLater(() -> {
			bar.progressProperty().bind(task.progressProperty());
		});
		return task;
	}
}


