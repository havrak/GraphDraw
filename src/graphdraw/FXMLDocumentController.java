package graphdraw;

import com.sun.javafx.scene.control.skin.CustomColorDialog;
import graphdraw.PostfixExperssionCacl.PostfixExpressionCacl;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author havra
 */
public class FXMLDocumentController {

	class ResizableCanvas extends Canvas {

		public ResizableCanvas() {
			widthProperty().addListener(evt -> draw());
			heightProperty().addListener(evt -> draw());
		}

		private void draw() {
			reset(true);
			reDrawFunctions(true);
		}

		@Override
		public boolean isResizable() {
			return true;
		}

		@Override
		public double prefWidth(double height) {
			return getWidth();
		}

		@Override
		public double prefHeight(double width) {
			return getHeight();
		}

		public void bindWIthParent(Pane parent) {
			parent.getChildren().add(this);
			this.widthProperty().bind(parent.widthProperty());
			this.heightProperty().bind(parent.heightProperty());
		}
	}

	@FXML
	private Pane pane;
	private ResizableCanvas Canvas;
	@FXML
	private TextField TextField;
	@FXML
	private TextField ZoomDisplay;
	@FXML
	private TextField VariableText;
	@FXML
	private TextField Variable;
	@FXML
	private VBox functionChoserBar;
	
	private PostfixExpressionCacl pec;
	private ParsedExpressions p = new ParsedExpressions();

	private CustomColorDialog colorDialog;
	private GraphicsContext gc;
	public String function;
	public String variable;
	private Stage stage;
	private int zoom;

	public void setStage(Stage stage) {
		ResizableCanvas resizableCanvas = new ResizableCanvas();
		resizableCanvas.bindWIthParent(pane);
		resizableCanvas.setOnMouseMoved(mouseMovedInCanvas);
		resizableCanvas.setOnScroll(canvasScroll);
		this.Canvas = resizableCanvas;
		this.stage = stage;
		this.colorDialog = new CustomColorDialog(this.stage);
		gc = Canvas.getGraphicsContext2D();
		gc.setFont(new Font(10));
		reset(true);
		zoom = 10;
		ZoomDisplay.setText("10");
		drawScale();
	}

	@FXML
	private void btnHelpPressed(ActionEvent event) { // remake
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Informace o použití");
		alert.setHeaderText("Informace o požití");
		alert.setContentText("Zadejte proměnou do políčka v levém horném rohu a funkci do vedlejšího políčka, pro zobrazení grafu stiskněte ENTER\n"
				+ "- proměná může být jakékoliv písmeno až na písmeno e, neboť to náleží kontantě e (e \u2250 2,71)\n"
				+ "- podporované konstsnty jsou e, \u03C0, \uD835\uDF19 \n"
				+ "- pro zadání funkce zadejte FUNKCE(VÝRAZ)\n"
				+ "- podporavené funkce jsou: sin, tan, cos, asin, atan, acos, abs, ln, exp, floor, ceil, log (base 10)"
				+ "- min a max jsou také podoporované, ty berou dva argumenty oddělené čárkou\n"
				+ "- pro mocninu zadejte: ZÁKLAD^EXPONENT\n"
				+ "- pro odmocninu zadejte: ZÁKLAD^(1/ODMOCNITEL)");
		alert.getDialogPane().setMinSize(300, 300);
		alert.showAndWait();
	}

	@FXML
	private void btnResetPressed(ActionEvent event) {
		reset(true);
		zoom = 10;
		ZoomDisplay.setText("10");
		variable = "";
		function = "";
		VariableText.setText("");
		gc.setStroke(Color.BLACK);
		p = new ParsedExpressions();
		pec = new PostfixExpressionCacl(function, variable);
		drawScale();
	}

	@FXML
	private void btnPlusPressed(ActionEvent event) {
		if (zoom < 100) {
			zoom++;
			reset(true);
			reDrawFunctions(true);
		}
		ZoomDisplay.setText(String.valueOf(zoom));
	}

	@FXML
	private void btnMinusPressed(ActionEvent event) {
		if (zoom >= 10) {
			zoom--;
			reset(true);
			reDrawFunctions(true);
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
				if (Integer.valueOf(ZoomDisplay.getText()) >= 10 && Integer.valueOf(ZoomDisplay.getText()) <= 100) {
					zoom = Integer.valueOf(ZoomDisplay.getText());
					reset(true);
					reDrawFunctions(true);
				} else {
					ZoomDisplay.setText(String.valueOf(zoom));
					alert.setTitle("Too big zoom or too small");
					alert.setContentText("Zoom value is out of range.\n"
							+ "Zoom can only have value between 10 and 100.");
					alert.showAndWait();

				}
			} catch (NumberFormatException e) {
				ZoomDisplay.setText(String.valueOf(zoom));
				alert.setTitle("Not a natural number");
				alert.setContentText("Zoom value can only be natural nmber");
				alert.showAndWait();

			}

		}
	}

	EventHandler<ScrollEvent> canvasScroll = event -> {
		if (event.getDeltaY() < 0 && zoom > 10) {
			zoom--;
			ZoomDisplay.setText(String.valueOf(zoom));
			reset(true);
			reDrawFunctions(true);
		} else if (event.getDeltaY() > 0 && zoom < 100) {
			zoom++;
			ZoomDisplay.setText(String.valueOf(zoom));
			reset(true);
			reDrawFunctions(true);
		}
	};

	@FXML
	private void specValueAction(KeyEvent event) {
		if (event.getCode().equals(KeyCode.ENTER)) {
			VariableText.setText("For value: " + VariableText.getText() + ", f(" + Variable.getText() + ") = " + String.valueOf(pec.evaluateExpression(Double.valueOf(VariableText.getText()))));
		}
	}

	@FXML
	private void drawGraphAction(KeyEvent event) throws IOException {
		if (event.getCode().equals(KeyCode.ENTER)) {
			System.out.println("-----------------");
			double time = System.nanoTime();
			PointsCoordinates coordinates = new PointsCoordinates(new ArrayList<>(), new ArrayList<>());

			function = TextField.getText().toLowerCase().trim();
			variable = Variable.getText();

			pec = new PostfixExpressionCacl(function, variable);
			for (double i = -(Canvas.getWidth() / (2 * zoom)); i < (Canvas.getWidth() / (2 * zoom)); i += (0.1 / (double) zoom)) {
				Double d = pec.evaluateExpression(i) * zoom;
				if (d.isNaN()) {
					i = Double.POSITIVE_INFINITY;
				} else {
					coordinates.AddToMap(i * zoom, d);
				}
			}
			ArrayList<String> temp = pec.getParsedExpression();
			if (temp != null) {
				if (p.addNewEntry(temp, function, variable, (Color) gc.getStroke())) { // diky antiAnalysing zmena barvy je nutne vykreslit nove a ne pres sebe
					reDrawFunctions(true);
					drawScale();
				} else {
					drawToCanvas(coordinates, true);
					Button btn = new Button();
					btn.setAlignment(Pos.CENTER_LEFT);
					btn.setMaxWidth(functionChoserBar.getPrefWidth());
					btn.setPrefWidth(functionChoserBar.getPrefWidth());
					btn.setText("f("+variable+"):"+function);
					btn.setOnAction(btnChoseFunctionPressed);
					functionChoserBar.getChildren().add(btn);
				}
			}
			System.out.println(p.toString());
			System.out.println("Time:\t" + (System.nanoTime() - time) / 1000_000 + "ms");
			System.out.println("-----------------");
		}
	}

	@FXML
	private void btnColorAction(Event event) {
		colorDialog.getDialog().showAndWait();
		gc.setStroke(colorDialog.getCustomColor());
	}

	@FXML
	private void btnSaveAction(Event event) {
		reset(false);
		reDrawFunctions(false);
		drawScale();
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
		reset(true);
		reDrawFunctions(true);
		drawScale();
	}

	@FXML
	public void keyTypedInVariable(KeyEvent event) { //nefunguje
		System.out.println(Variable.getText());
		if (Variable.getText().length() > 0) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("Variable can be only one character");
			alert.showAndWait();
			Variable.setText("");
		}
	}

	EventHandler<MouseEvent> mouseMovedInCanvas = event -> {
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
	};
	// nastavit barvu, TextArea, Variable, Postfix
	EventHandler<ActionEvent> btnChoseFunctionPressed = event -> {
		Button temp = (Button) event.getSource();
		String name = temp.getText().split(":")[1];
		int index = p.getIndexOfInfixFunction(name);
		pec.setPostfixExpression(p.getPostfixExpression(index), p.getVariable(index));
		gc.setStroke(p.getColor(index));
		TextField.setText(name);
		Variable.setText(p.getVariable(index));
	};
	
	
	public void drawToCanvas(PointsCoordinates coordinates, boolean b) {
		Point2D point1;
		Point2D point2;
		for (int i = 0; i < coordinates.getArrayListLenght() - 1; i++) {
			point1 = new Point2D(coordinates.getAxisXForI(i) + Canvas.getWidth() / 2, coordinates.getAxisYForI(i) + Canvas.getHeight() / 2);
			point2 = new Point2D(coordinates.getAxisXForI(i + 1) + Canvas.getWidth() / 2, coordinates.getAxisYForI(i + 1) + Canvas.getHeight() / 2);

			if (point1.distance(point2) < zoom * 25) {
				gc.strokeLine(point1.getX(), point1.getY(), point2.getX(), point2.getY());
			}
		}
		if (b) {
			gc.fillRect(Canvas.getWidth() - 95, Canvas.getHeight() - 60, Canvas.getWidth(), 34);
			gc.strokeText("X: ", Canvas.getWidth() - 90, Canvas.getHeight() - 45);
			gc.strokeText("Y: ", Canvas.getWidth() - 90, Canvas.getHeight() - 30);
		}
	}

	public void reDrawFunctions(boolean b) {
		for (int i = 0; i < p.getSize(); i++) {
			gc.setStroke(p.getColor(i));
			pec.setPostfixExpression(p.getPostfixExpression(i), p.getVariable(i));
			PointsCoordinates coordinates = new PointsCoordinates(new ArrayList<>(), new ArrayList<>());
			for (double x = -(Canvas.getWidth() / (2 * zoom)); x < (Canvas.getWidth() / (2 * zoom)); x += (0.1 / (double) zoom)) {
				Double d = pec.evaluateExpression(x) * zoom;
				if (d.isNaN()) {
					x = Double.POSITIVE_INFINITY;
				} else {
					coordinates.AddToMap(x * zoom, d);
				}
			}
			drawToCanvas(coordinates, b);
		}
		drawScale();
	}

	public void drawScale() {
		if (gc != null) {
			Paint p = gc.getStroke();
			gc.setStroke(Color.BLACK);
			double realX = 0;
			for (double x = -(Canvas.getWidth() / (2 * zoom)); x < (Canvas.getWidth() / (2 * zoom)); x += (0.1 / (double) zoom)) {
				double distanceFromZero = Math.abs(Math.round(realX) - Canvas.getWidth() / 2);
				double tempX = Math.abs(Math.round(x * 1000));
				if (distanceFromZero > 25 && (tempX == 500 || tempX == 1000 || tempX == 2000 || tempX == 5000 || tempX == 10000 || tempX == 20000 || tempX == 30000 || tempX == 50000 || tempX == 70000 || tempX == 100000)) {
					gc.strokeText(String.valueOf(tempX / 1000.), realX, Canvas.getHeight() / 2 + 14);
					gc.strokeLine(realX, Canvas.getHeight() / 2 + 5, realX, Canvas.getHeight() / 2 - 5);
				}
				realX += 0.1;
			}
			double realY = 0;
			for (double y = -(Canvas.getHeight() / (2 * zoom)); y < (Canvas.getHeight() / (2 * zoom)); y += (0.1 / (double) zoom)) {
				double distanceFromZero = Math.abs(Math.round(realY) - Canvas.getHeight() / 2);
				double tempY = Math.abs(Math.round(y * 1000));
				if (distanceFromZero > 25 && (tempY == 500 || tempY == 1000 || tempY == 2000 || tempY == 5000 || tempY == 10000 || tempY == 20000 || tempY == 30000 || tempY == 50000 || tempY == 70000 || tempY == 100000)) {
					gc.strokeText(String.valueOf(tempY / 1000.), Canvas.getWidth() / 2 - 34, realY);
					gc.strokeLine(Canvas.getWidth() / 2 + 5, realY, Canvas.getWidth() / 2 - 5, realY);
				}
				realY += 0.1;
			}
			gc.setStroke(p);
		}
	}

	public void reset(boolean b) {
		if (gc != null) {// zavola se driv nez se prida gc, mozna neni problem na windows
			gc.setFill(Color.WHITE);
			Color stroke = (Color) gc.getStroke();
			gc.setStroke(Color.BLACK);
			gc.fillRect(0, 0, Canvas.getWidth(), Canvas.getHeight());
			gc.strokeLine(0, Canvas.getHeight() / 2, Canvas.getWidth(), Canvas.getHeight() / 2);
			gc.strokeLine(Canvas.getWidth() / 2, 0, Canvas.getWidth() / 2, Canvas.getHeight());
			if (b) {
				gc.strokeText("0", Canvas.getWidth() / 2 + 2, Canvas.getHeight() / 2 + 12);
				gc.strokeRect(Canvas.getWidth() - 96, Canvas.getHeight() - 61, Canvas.getWidth(), 36);
				gc.strokeText("X: ", Canvas.getWidth() - 90, Canvas.getHeight() - 45);
				gc.strokeText("Y: ", Canvas.getWidth() - 90, Canvas.getHeight() - 30);
			}
			gc.setStroke(stroke);
		}
	}
}
