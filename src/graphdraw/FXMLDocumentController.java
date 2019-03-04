package graphdraw;

import com.sun.javafx.scene.control.skin.CustomColorDialog;
import graphdraw.PostfixExperssionCacl.PostfixExperssionCacl;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author havra
 */
public class FXMLDocumentController implements Initializable {

	@FXML
	private Canvas Canvas;
	@FXML
	private TextField TextField;
	@FXML
	private TextField ZoomDisplay;
	@FXML
	private TextField VariableText;
	@FXML
	private TextField Variable;

	private PostfixExperssionCacl pec;
	private HashMap< ArrayList<String>, String> parsedExpression = new HashMap<>();
	private ArrayList<Color> parsedExpresionColor = new ArrayList<Color>(); // muze se rozejit s hashmapou
	private CustomColorDialog colorDialog;
	private GraphicsContext gc;
	public String function;
	public String variable;
	private Stage stage;
	private int zoom;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		gc = Canvas.getGraphicsContext2D();
		reset();
		zoom = 10;
		ZoomDisplay.setText("10");

	}

	public void setStage(Stage stage) {
		this.stage = stage;
		this.colorDialog = new CustomColorDialog(this.stage);
	}

	@FXML
	private void btnHelpPressed(ActionEvent event) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Informace o použití");
		alert.setHeaderText("Informace o požití");
		alert.setContentText("Zadejte promenou do policka v levem hornim rohu a funkci do vedlejsiho policka, pro zobrazení grafu stiskněte ENTER\n"
				+ "- proměná může být jakékoliv písmeno až na písmeno e, neboť to náleží kontantě e (e \u2250 2,71)\n"
				+ "- pro sin zadejte:sin(VÝRAZ)\n"
				+ "- pro asin zadejte: asin(VÝRAZ)\n"
				+ "- pro cos zadejte: cos(VÝRAZ)\n"
				+ "- pro acos zadejte: acos(VÝRAZ)\n"
				+ "- pro tan zadejte: tan(VÝRAZ)\n"
				+ "- pro atan zadejte: atan(VÝRAZ)\n"
				+ "- pro ln zadejte: ln(VÝRAZ)\n"
				+ "- pro konstantu pi zadejte: pi\n"
				+ "- pro konstantu e zadejte: e\n"
				+ "- pro mocninu zadejte: ZAKLAD^EXPONENT\n"
				+ "- pro odmocninu zadejte: ZAKLAD^(1/ODMOCNITEL)\n");
		alert.getDialogPane().setMinSize(640, 400);
		alert.showAndWait();
	}

	@FXML
	private void btnResetPressed(ActionEvent event) {
		reset();
		zoom = 10;
		ZoomDisplay.setText("10");
		variable = "";
		function = "";
		VariableText.setText("");
	}

	@FXML
	private void btnPlusPressed(ActionEvent event) {
		if (zoom < 100) {
			zoom++;
			reset();
			reDrawFunctions();
		}
		ZoomDisplay.setText(String.valueOf(zoom));
	}

	@FXML
	private void btnMinusPressed(ActionEvent event) {
		if (zoom >= 10) {
			zoom--;
			reset();
			reDrawFunctions();
		}
		ZoomDisplay.setText(String.valueOf(zoom));
	}

	@FXML
	private void changeZoomAction(KeyEvent event) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setHeaderText("Bad user input");
		alert.setTitle("Error dialog");
		alert.getDialogPane().setMinSize(330, 130);
		if (event.getCode().equals(KeyCode.ENTER)) {
			try {
				if (Integer.valueOf(ZoomDisplay.getText()) >= 1 && Integer.valueOf(ZoomDisplay.getText()) <= 100) {
					zoom = Integer.valueOf(ZoomDisplay.getText());
					reset();
					reDrawFunctions();
				} else {
					ZoomDisplay.setText(String.valueOf(zoom));
					alert.setTitle("Too big zoom");
					alert.setContentText("Zoom value is out of range.\n"
							+ "Zoom can only have value between 1 and 100.");
					alert.showAndWait();

				}
			} catch (NumberFormatException e) {
				ZoomDisplay.setText(String.valueOf(zoom));
				alert.setTitle("Not a integer");
				alert.setContentText("Zoom value can only be integer writen in decimal form");
				alert.showAndWait();

			}

		}
	}

	@FXML
	private void specValueAction(KeyEvent event) {
		if (event.getCode().equals(KeyCode.ENTER)) {
			VariableText.setText("For value: " + VariableText.getText() + ", f(" + Variable.getText() + ") = " + String.valueOf(pec.evaluateExpression(Double.valueOf(VariableText.getText()))));
		}
	}

	@FXML
	private void drawGraphAction(KeyEvent event) throws IOException {
		if (event.getCode().equals(KeyCode.ENTER)) {
			int sizeOfpe = parsedExpression.size();
			System.out.println("started calculation zoom: " + zoom);
			double time = System.nanoTime();
			PointsCoordinates coordinates = new PointsCoordinates(new ArrayList<>(), new ArrayList<>());

			function = TextField.getText().toLowerCase().trim();
			variable = Variable.getText();

			pec = new PostfixExperssionCacl(function, variable);
			for (double i = -(Canvas.getWidth() / (2 * zoom)); i < (Canvas.getWidth() / (2 * zoom)); i += (0.1 / (double) zoom)) {
				Double d = pec.evaluateExpression(i) * zoom;
				if (d.isNaN()) {
					i = Double.POSITIVE_INFINITY;
				} else {
					coordinates.AddToMap(i * zoom, d);
				}
			}
			pec.getPostfixFunctionArray();
			parsedExpression.putAll(pec.getPostfixFunctionArray());
			if (sizeOfpe == parsedExpression.size() - 1) {
				parsedExpresionColor.add((Color) gc.getStroke());
			} else { // je potreba zmenit barvu pro funkci - aby se ArrayList A HashMap nerozesly
				int i = 0;
				for (Entry<ArrayList<String>, String> e : parsedExpression.entrySet()) {
					if(pec.getPostfixFunctionArray().containsKey(e.getKey())){
						ArrayList<Color> temp = new ArrayList<Color>(parsedExpresionColor.subList(0,i));
						temp.add((Color) gc.getStroke());
						temp.addAll(parsedExpresionColor.subList(i+1,parsedExpresionColor.size()));
						parsedExpresionColor = temp;
					}
					i++;
				}
			}
			System.out.println("saved expressions: "+parsedExpression + ", theirs colors: "+parsedExpresionColor);
			System.out.println("Time: " + (System.nanoTime() - time) / 1000_000 + "ms");
			drawToCanvas(coordinates);

		}

	}

	@FXML
	private void btnColorAction(Event event) {
		colorDialog.getDialog().showAndWait();
		gc.setStroke(colorDialog.getCustomColor());
	}

	@FXML
	private void btnSaveAction(Event event) {
		WritableImage image = Canvas.snapshot(new SnapshotParameters(), null);
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Image");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("File type: PNG", "*.png");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showSaveDialog(this.stage);
		if (file != null) {
			if (!file.getPath().endsWith(".png")) {
				file = new File(file.getPath() + ".png");
			}
			try {
				ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
			} catch (IOException e) {

			}
		}
	}

	@FXML
	public void keyTypedInVariable(KeyEvent event) {
		if (Variable.getText().length() > 1) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("Variable can be only one character");
		}
	}

	@FXML
	public void canvasMouseMoved(MouseEvent event) {
		if (pec != null) {
			double x = (event.getX() - Canvas.getWidth() / 2) / zoom;
			Double y = pec.evaluateExpression(x);
			Paint c = gc.getStroke();
			gc.fillRect(Canvas.getWidth() - 95, Canvas.getHeight() - 60, Canvas.getWidth(), 34);
			gc.setStroke(Color.BLACK);
			String stx = "X: " + String.valueOf((double) ((int) (x * 10000)) / 10000);
			String sty;
			if (!y.isNaN()) {
				sty = "Y: " + String.valueOf((double) ((int) (y * 10000)) / 10000);
			} else {
				sty = "Y: Error";
			}

			if (sty.length() > 13) {
				sty = "Y: TooLarge";
			}
			if (stx.length() > 13) {
				stx = "X: TooLarge";
			}
			gc.strokeText(stx, Canvas.getWidth() - 90, Canvas.getHeight() - 45);
			gc.strokeText(sty, Canvas.getWidth() - 90, Canvas.getHeight() - 30);
			gc.setStroke(c);
		}

	}

	public void drawToCanvas(PointsCoordinates coordinates) {
		int distanceBetweenPoints;
		Point2D point1;
		Point2D point2;
		for (int i = 0; i < coordinates.getArrayListLenght() - 1; i++) {
			point1 = new Point2D(coordinates.getAxisXForI(i) + Canvas.getWidth() / 2, coordinates.getAxisYForI(i) + Canvas.getHeight() / 2);
			point2 = new Point2D(coordinates.getAxisXForI(i + 1) + Canvas.getWidth() / 2, coordinates.getAxisYForI(i + 1) + Canvas.getHeight() / 2);

			if (point1.distance(point2) < zoom * 25) {
				gc.strokeLine(point1.getX(), point1.getY(), point2.getX(), point2.getY());
			}
		}
		gc.fillRect(Canvas.getWidth() - 95, Canvas.getHeight() - 60, Canvas.getWidth(), 34);
		gc.strokeText("X: ", Canvas.getWidth() - 90, Canvas.getHeight() - 45);
		gc.strokeText("Y: ", Canvas.getWidth() - 90, Canvas.getHeight() - 30);
	}

	public void reDrawFunctions() {
		int j = parsedExpresionColor.size() - 1;
		for (Entry<ArrayList<String>, String> e : parsedExpression.entrySet()) {
			gc.setStroke(parsedExpresionColor.get(j));
			pec.setPostfixExpression(e);
			PointsCoordinates coordinates = new PointsCoordinates(new ArrayList<>(), new ArrayList<>());
			for (double i = -(Canvas.getWidth() / (2 * zoom)); i < (Canvas.getWidth() / (2 * zoom)); i += (0.1 / (double) zoom)) {
				Double d = pec.evaluateExpression(i) * zoom;
				if (d.isNaN()) {
					i = Double.POSITIVE_INFINITY;
				} else {
					coordinates.AddToMap(i * zoom, d);
				}
			}
			j--;
			drawToCanvas(coordinates);
		}

	}

	public void reset() {
		gc.setFill(Color.WHITE);
		Color stroke = (Color) gc.getStroke();
		gc.setStroke(Color.BLACK);
		gc.fillRect(0, 0, Canvas.getWidth(), Canvas.getHeight());
		gc.strokeLine(0, Canvas.getHeight() / 2, Canvas.getWidth(), Canvas.getHeight() / 2);
		gc.strokeLine(Canvas.getWidth() / 2, 0, Canvas.getWidth() / 2, Canvas.getHeight());
		gc.strokeText("0", Canvas.getWidth() / 2 + 2, Canvas.getHeight() / 2 + 12);
		gc.strokeRect(Canvas.getWidth() - 96, Canvas.getHeight() - 61, Canvas.getWidth(), 36);
		gc.strokeText("X: ", Canvas.getWidth() - 90, Canvas.getHeight() - 45);
		gc.strokeText("Y: ", Canvas.getWidth() - 90, Canvas.getHeight() - 30);
		gc.setStroke(stroke);
	}
}
