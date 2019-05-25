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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author havra
 */
public class FXMLDocumentController {

	/**
	 * Class Resizable Canavas If you bind it with pane, Canvas will be resized
	 * together with window itself
	 *
	 */
	class ResizableCanvas extends Canvas {

		public ResizableCanvas() {
			widthProperty().addListener(e -> update());
			heightProperty().addListener(e -> update());
		}

		private void update() {
			infixTF.setMinWidth(borderPane.getWidth() - 497);
			calcForVarTF.setMinWidth(borderPane.getWidth());
			if (gc != null) {
				reDrawFunctions();
			}
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
	private TextField infixTF;
	@FXML
	private TextField zoomTF;
	@FXML
	private TextField calcForVarTF;
	@FXML
	private TextField variableTF;
	@FXML
	private VBox functionChoserBar;
	@FXML
	private BorderPane borderPane;
	@FXML
	private Button interBtn;

	private TextArea intersestionModeTA = new TextArea();
	private PostfixExpressionCacl pec;
	private ParsedExpressions p = new ParsedExpressions();
	private Image canvasCopy;

	private CustomColorDialog colorDialog;
	private GraphicsContext gc;
	public String function;
	public String variable;
	private Stage stage;
	private int zoom;
	private boolean amIInInterMode = false;
	private boolean didUserChangedColor = false;

	private final Color[] defaultColors = {Color.valueOf("0x1abc9cff"),
		Color.valueOf("0x2ecc71ff"),
		Color.valueOf("0x3498dbff"),
		Color.valueOf("0x9b59b6ff"),
		Color.valueOf("0x34495eff"),
		Color.valueOf("0xf39c12ff"),
		Color.valueOf("0xd35400ff"),
		Color.valueOf("0xc0392bff"),
		Color.valueOf("0x6d8764ff"),
		Color.valueOf("0xf472d0ff"),
		Color.valueOf("0xa0522dff"),
		Color.valueOf("0x2c3e50ff")};

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
		zoom = 20;
		zoomTF.setText("1");
		reset();
		drawScale();
		drawMouseBox();
	}

	/**
	 * Method for button switching between Canvas and TextArea for displaying
	 * Coordinates of intersections. Will also lock certain parts of UI if in
	 * intersection mode.
	 *
	 * @param event
	 */
	@FXML
	private void switchMode(ActionEvent event) {
		if (!amIInInterMode) {
			interBtn.setText("Back");
			borderPane.setCenter(intersestionModeTA);
			intersestionModeTA.setText("");
			intersestionModeTA.setEditable(false);
			amIInInterMode = true;
			infixTF.setEditable(false); // udelat pro vse
			calcForVarTF.setEditable(false);
		} else {
			interBtn.setText("Inter");
			borderPane.setCenter(pane);
			amIInInterMode = false;
			infixTF.setEditable(true);
			calcForVarTF.setEditable(true);
		}
	}

	/**
	 * Method for button showing help in Alert.
	 *
	 * @param event
	 */
	@FXML
	private void btnHelpPressed(ActionEvent event) { // remake to english
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText("Usage info");
		alert.setContentText("Enter variable to top left field, than write functions itself\n"
				+ "- variable can be any letter, except letter e (e \u2250 2,71)\n"
				+ "- supported constants are e, \u03C0, \uD835\uDF19 \n"
				+ "- if you want to enter function,  enter: function(it's arguments)\n"
				+ "- supported functions are: sin, tan, cos, asin, atan, acos, abs, ln, exp, floor, ceil, log (base 10), sqrt"
				+ "- min a max are also supported, these two take two arguments separeted by comma\n"
				+ "- for power enter: base^power\n"
				+ "- if you want to use root, write fraction in exponet of power");
		alert.getDialogPane().setMinSize(300, 300);
		alert.showAndWait();
	}

	/**
	 * Method for reset button, will set all fields, Canvas etc. to their
	 * default state.
	 *
	 * @param event
	 */
	@FXML
	private void btnResetPressed(ActionEvent event) {
		zoom = 20;
		zoomTF.setText("1");
		variable = "";
		function = "";
		calcForVarTF.setText("");
		infixTF.setText("");
		variableTF.setText("");
		gc.setStroke(Color.BLACK);
		p = new ParsedExpressions();
		pec = new PostfixExpressionCacl(function, variable);
		functionChoserBar.getChildren().clear();
		reset();
		drawScale();
		drawMouseBox();
	}

	/**
	 * Increases value of zoom, value is offset by 19.
	 *
	 * @param event
	 */
	@FXML
	private void btnPlusPressed(ActionEvent event) {
		if (zoom < 119) {
			zoom++;
			reDrawFunctions();
		}
		zoomTF.setText(String.valueOf(zoom - 19));
	}

	/**
	 * Decreases value of zoom, value is offset by 19.
	 *
	 * @param event
	 */
	@FXML
	private void btnMinusPressed(ActionEvent event) {
		if (zoom >= 20) {
			zoom--;
			reDrawFunctions();
		}
		zoomTF.setText(String.valueOf(zoom - 19));
	}

	/**
	 * Increases or decreases value of zoom, value is offset by 19. Throws Alert
	 * if value is out of range.
	 *
	 * @param event
	 */
	@FXML
	private void changeZoomAction(KeyEvent event) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setHeaderText("Bad user input");
		alert.setTitle("Error dialog");
		alert.getDialogPane().setMinSize(330, 130);
		if (event.getCode().equals(KeyCode.ENTER)) {
			try {
				if (Integer.valueOf(zoomTF.getText()) >= 1 && Integer.valueOf(zoomTF.getText()) <= 100) {
					zoom = Integer.valueOf(zoomTF.getText()) + 19;
					reDrawFunctions();
				} else {
					zoomTF.setText(String.valueOf(zoom));
					alert.setTitle("Too big zoom or too small");
					alert.setContentText("Zoom value is out of range.\n"
							+ "Zoom can only have value between 1 and 100.");
					alert.showAndWait();

				}
			} catch (NumberFormatException e) {
				zoomTF.setText(String.valueOf(zoom));
				alert.setTitle("Not a natural number");
				alert.setContentText("Zoom value can only be natural nmber");
				alert.showAndWait();

			}

		}
	}

	/**
	 * Increases or decreases value of zoom, value is offset by 19. Throws Alert
	 * if value is out of range.
	 *
	 * @param event
	 */
	EventHandler<ScrollEvent> canvasScroll = event -> {
		if (event.getDeltaY() < 0 && zoom > 20) {
			zoom--;
			zoomTF.setText(String.valueOf(zoom - 19));
			reDrawFunctions();
		} else if (event.getDeltaY() > 0 && zoom < 119) {
			zoom++;
			zoomTF.setText(String.valueOf(zoom - 19));
			reDrawFunctions();
		}
	};

	@FXML
	private void specValueAction(KeyEvent event) {
		if (event.getCode().equals(KeyCode.ENTER)) {
			calcForVarTF.setText("For value: " + calcForVarTF.getText() + ", f(" + variableTF.getText() + ") = " + String.valueOf(pec.evaluateExpression(Double.valueOf(calcForVarTF.getText()))));
		}
	}

	/**
	 * Method for InfixField, if ENTER is pressed graph will be draw. Also will
	 * add new function to VBox with functions or chage color of already drawn
	 * function.
	 *
	 * @param event
	 */
	@FXML
	private void drawGraphAction(KeyEvent event) {
		if (event.getCode().equals(KeyCode.ENTER)) {
			System.out.println("-----------------");
			double time = System.nanoTime();

			function = infixTF.getText().toLowerCase().trim();
			variable = variableTF.getText();
			if (!(variable.charAt(0) >= 'a' && variable.charAt(0) <= 'z')) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setHeaderText("Alert can be only letter");
				alert.showAndWait();
			} else {
				pec = new PostfixExpressionCacl(function, variable);
				pec.evaluateExpression(2);
				if (pec.isCalculable()) {
					ArrayList<String> temp = (ArrayList<String>) pec.getParsedExpression().clone();
					if (!didUserChangedColor) {
						boolean change = false;
						for (Color defaultColor : defaultColors) {
							if (!p.getColors().contains(defaultColor)) {
								gc.setStroke(defaultColor);
								change = true;
							}
						}
						if (!change && p.getSize() == 0) {
						} else if (!change) {
							gc.setStroke(new Color(0, 0, 0, 1));
						}
					}
					if(p.containsInfix(function)){
						if(didUserChangedColor == true){
							p.addNewEntry(temp, function, variable, (Color) gc.getStroke());
							reDrawFunctions();
						}

					}else{
						p.addNewEntry(temp, function, variable, (Color) gc.getStroke());
						reDrawFunctions();
						Button btn = new Button();
						btn.setAlignment(Pos.CENTER_LEFT);
						btn.setMaxWidth(functionChoserBar.getPrefWidth());
						btn.setPrefWidth(functionChoserBar.getPrefWidth());
						btn.setText("f(" + variable + "):" + function);
						btn.setOnAction(btnChoseFunctionPressed);
						functionChoserBar.getChildren().add(btn);
					}
					didUserChangedColor = false;
				}
				System.out.println(p.toString());
				System.out.println("Time:\t" + (System.nanoTime() - time) / 1000_000 + "ms");
			}
			System.out.println("-----------------");
		}
	}

	/**
	 * Shows Color dialog to choose graph color
	 *
	 * @param event
	 */
	@FXML
	private void btnColorAction(Event event) {
		colorDialog.getDialog().showAndWait();
		gc.setStroke(colorDialog.getCustomColor());
		didUserChangedColor = true;
	}

	/**
	 * Export Canvas screen shot to picture, will not contain mouse coordinates.
	 *
	 * @param event
	 */
	@FXML
	private void menuSaveAction(Event event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Image");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("File type: PNG", "*.png");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showSaveDialog(this.stage);
		if (!file.getName().endsWith(".png")) {
			file = new File(file.getAbsolutePath() + ".png");
		}
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(canvasCopy, null), "png", file); // null pointer
		} catch (IOException ex) {
		}
	}

	/**
	 * Calls method in Parsed Expression to export functions to file.
	 *
	 * @param event
	 */
	@FXML
	private void menuExportAction(Event event) { // co kdyz jsou ve file data, pridat?????
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select File");
		File file = fileChooser.showSaveDialog(this.stage);
		p.exportToJSON(file);
	}

	/**
	 * Calls method in Parsed Expression to import functions from file. If some
	 * functions were added will redraw canvas.
	 *
	 * @param event
	 */
	@FXML
	private void menuImportAction(Event event) {
		int size = p.getSize();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select File");
		if (p.importFromJSON(fileChooser.showOpenDialog(this.stage))) {
			for (int i = size; i < p.getSize(); i++) {
				Button btn = new Button();
				btn.setAlignment(Pos.CENTER_LEFT);
				btn.setMaxWidth(functionChoserBar.getPrefWidth());
				btn.setPrefWidth(functionChoserBar.getPrefWidth());
				btn.setText("f(" + p.getVariable(i) + "):" + p.getInfixExpression(i));
				btn.setOnAction(btnChoseFunctionPressed);
				functionChoserBar.getChildren().add(btn);
			}
			reDrawFunctions();
		}
		if (size != p.getSize()) {
			infixTF.setText(p.getInfixExpression(p.getSize() - 1));
			variableTF.setText(p.getVariable(p.getSize() - 1));
			function = p.getInfixExpression(p.getSize() - 1);
			variable = p.getVariable(p.getSize() - 1);
		}
	}

	/**
	 * Prevents Variable field to contain more than 1 character.
	 *
	 * @param event
	 */
	@FXML
	public void keyTypedInVariable(KeyEvent event) {
		if (variableTF.getText().length() > 0) {
			variableTF.setText("");
		}
	}

	/**
	 * Calculates values of function for current mouse position. Draw blue dot
	 * on selected graph in place where X position of mouse is same.
	 * evaluateExpression
	 */
	EventHandler<MouseEvent> mouseMovedInCanvas = event -> {
		if (!p.isEmpty()) { // cheme aby nece bylo ulozeneho ?? pec == null
			reset();
			gc.drawImage(canvasCopy, 0, 0);
			double x = (event.getX() - Canvas.getWidth() / 2) / zoom; /// nepocitat p
			Double y = pec.evaluateExpression(x);
			Paint c = gc.getStroke();
			gc.setFill(Color.BLUE);
			gc.fillOval(event.getX() - 3, -y * zoom + Canvas.getHeight() / 2 - 3, 6, 6);
			gc.setFill(Color.WHITE);
			gc.fillRect(Canvas.getWidth() - 95, Canvas.getHeight() - 60, Canvas.getWidth(), 34);
			gc.setStroke(Color.BLACK);
			drawMouseBox();
			String stx = String.valueOf((double) ((int) (x * 10000)) / 10000);
			String sty;
			if (!y.isNaN()) {
				sty = String.valueOf((double) ((int) (y * 10000)) / 10000);
			} else {
				sty = "Error"; // kdy to hazi, lze to vubec hodit -- pocitat pro neco spatneho, nevratit se k predchozi hodnote pec ?????
			}
			if (sty.length() > 13) {
				sty = "TooLarge";
			}
			if (stx.length() > 13) {
				stx = "TooLarge";
			}
			gc.strokeText(stx, Canvas.getWidth() - 78, Canvas.getHeight() - 45);
			gc.strokeText(sty, Canvas.getWidth() - 78, Canvas.getHeight() - 30);
			gc.setStroke(c);
		}
	};

	/**
	 * If in normal mode: Sets function for which to calculate value from mouse
	 * X. If in intersection mode: Will find intersection and display them.
	 */
	EventHandler<ActionEvent> btnChoseFunctionPressed = event -> {
		Button temp = (Button) event.getSource();
		String name = temp.getText().split(":")[1];
		int index = p.getIndexOfInfixFunction(name);
		if (!amIInInterMode) {
			pec.setPostfixExpression(p.getPostfixExpression(index), p.getVariable(index));
			gc.setStroke(p.getColor(index));
			function = name;
			variable = p.getVariable(index);
			infixTF.setText(name);
			variableTF.setText(p.getVariable(index));
		} else {
			intersestionModeTA.setEditable(true);
			intersestionModeTA.setText("");
			if (name.equals(infixTF.getText())) {
				List<Double> points = pec.bisectionMethod(new ArrayList<String>(), pec.getVariable(), Canvas.getWidth() / zoom, zoom);
				intersestionModeTA.appendText("You chose two same functions,\n");
				intersestionModeTA.appendText("showing point where function intersects axis X:\n");
				points.forEach((point) -> {
					intersestionModeTA.appendText("[" + String.valueOf((double) ((int) (point * 10000)) / 10000) + " ; 0 ]\n");
				});
			} else {
				List<Double> points = pec.bisectionMethod((ArrayList<String>) p.getPostfixExpression(index).clone(), p.getVariable(index), Canvas.getWidth() / zoom, zoom);
				if (points == null) {
					intersestionModeTA.appendText("Requested functions don't have any intersections\n");
				} else {
					intersestionModeTA.appendText("Function: " + function + " and function: " + name + ", have intersection/s in:\n");
					points.forEach((point) -> {
						intersestionModeTA.appendText("[ " + String.valueOf((double) ((int) (point * 10000)) / 10000) + " ; " + String.valueOf((double) ((int) (pec.evaluateExpression(point) * 10000)) / 10000) + " ]\n");
					});
				}
			}
			intersestionModeTA.setEditable(false);
		}
	};

	/**
	 * Draws points for List in succession.
	 *
	 * @param coordinates
	 */
	public void drawToCanvas(List<Point2D> coordinates) {
		Point2D point1;
		Point2D point2;
		for (int i = 0; i < coordinates.size() - 1; i++) {
			point1 = new Point2D(coordinates.get(i).getX() + Canvas.getWidth() / 2, coordinates.get(i).getY() + Canvas.getHeight() / 2);
			point2 = new Point2D(coordinates.get(i + 1).getX() + Canvas.getWidth() / 2, coordinates.get(i + 1).getY() + Canvas.getHeight() / 2);

			if (point1.distance(point2) < zoom * 25) {
				gc.strokeLine(point1.getX(), point1.getY(), point2.getX(), point2.getY());
			}
		}
	}

	/**
	 * Redraws all graphs saved in parsedExpression
	 */
	public synchronized void reDrawFunctions() {
		reset();
		boolean needToReturn = false;
		ArrayList<String> originalPostfix = null;
		String originalVariable = null;
		if (pec != null && pec.isCalculable()) {
			originalPostfix = pec.getParsedExpression();
			originalVariable = pec.getVariable();
			needToReturn = true;
		}
		for (int i = 0; i < p.getSize(); i++) {
			gc.setStroke(p.getColor(i));
			if (pec == null) {
				pec = new PostfixExpressionCacl(p.getPostfixExpression(i), p.getVariable(i));
			} else {
				pec.setPostfixExpression(p.getPostfixExpression(i), p.getVariable(i));
			}
			List<Point2D> coordinates = new ArrayList<>();
			for (double x = -(Canvas.getWidth() / (2 * zoom)); x < (Canvas.getWidth() / (2 * zoom)); x += (0.1 / (double) zoom)) {
				Double d = pec.evaluateExpression(x) * zoom;
				coordinates.add(new Point2D(x * zoom, -d));
			}
			drawToCanvas(coordinates);
		}
		if (needToReturn) {
			pec.setPostfixExpression(originalPostfix, originalVariable);
		}
		drawScale();
		drawMouseBox();
	}

	/**
	 * Draws scale to canvas according to current zoom value.
	 */
	public void drawScale() {
		double[] pointsToDisplay = {0.5, 1, 2, 5, 10, 20, 30, 40, 50, 70, 100};
		Paint paintToRestore = gc.getStroke();
		gc.setStroke(Color.BLACK);
		gc.strokeLine(0, Canvas.getHeight() / 2, Canvas.getWidth(), Canvas.getHeight() / 2);
		gc.strokeLine(Canvas.getWidth() / 2, 0, Canvas.getWidth() / 2, Canvas.getHeight());
		gc.strokeText("0", Canvas.getWidth() / 2 + 2, Canvas.getHeight() / 2 + 14);
		for (int i = 0; i < pointsToDisplay.length; i++) {
			double distanceFromZero = Math.abs(pointsToDisplay[i] * zoom);
			if (distanceFromZero > 25) {
				gc.strokeText(String.valueOf(pointsToDisplay[i]), pointsToDisplay[i] * zoom + Canvas.getWidth() / 2, Canvas.getHeight() / 2 + 14);
				gc.strokeLine(pointsToDisplay[i] * zoom + Canvas.getWidth() / 2, Canvas.getHeight() / 2 + 5, pointsToDisplay[i] * zoom + Canvas.getWidth() / 2, Canvas.getHeight() / 2 - 5);
				gc.strokeText("-" + pointsToDisplay[i], Canvas.getWidth() - (pointsToDisplay[i] * zoom + Canvas.getWidth() / 2), Canvas.getHeight() / 2 + 14);
				gc.strokeLine(Canvas.getWidth() - (pointsToDisplay[i] * zoom + Canvas.getWidth() / 2), Canvas.getHeight() / 2 + 5, Canvas.getWidth() - (pointsToDisplay[i] * zoom + Canvas.getWidth() / 2), Canvas.getHeight() / 2 - 5);

				gc.strokeText("-" + String.valueOf(pointsToDisplay[i]), Canvas.getWidth() / 2 - 34, pointsToDisplay[i] * zoom + Canvas.getHeight() / 2);
				gc.strokeLine(Canvas.getWidth() / 2 + 5, pointsToDisplay[i] * zoom + Canvas.getHeight() / 2, Canvas.getWidth() / 2 - 5, pointsToDisplay[i] * zoom + Canvas.getHeight() / 2);
				gc.strokeText(String.valueOf(pointsToDisplay[i]), Canvas.getWidth() / 2 - 34, Canvas.getHeight() - (pointsToDisplay[i] * zoom + Canvas.getHeight() / 2));
				gc.strokeLine(Canvas.getWidth() / 2 + 5, Canvas.getHeight() - (pointsToDisplay[i] * zoom + Canvas.getHeight() / 2), Canvas.getWidth() / 2 - 5, Canvas.getHeight() - (pointsToDisplay[i] * zoom + Canvas.getHeight() / 2));
			}
		}
		gc.setStroke(paintToRestore);
		try {
			canvasCopy = Canvas.snapshot(new SnapshotParameters(), null);
		} catch (NullPointerException e) {
		}
	}

	private void reset() {
		if (gc != null) {
			gc.setFill(Color.WHITE);
			Color stroke = (Color) gc.getStroke();
			gc.setStroke(Color.BLACK);
			gc.fillRect(0, 0, Canvas.getWidth(), Canvas.getHeight());
			gc.setStroke(stroke);
		}
	}

	private void drawMouseBox() {
		if (gc != null) {
			gc.setStroke(Color.BLACK);
			gc.fillRect(Canvas.getWidth() - 95, Canvas.getHeight() - 60, Canvas.getWidth(), 34);
			gc.strokeRect(Canvas.getWidth() - 96, Canvas.getHeight() - 61, Canvas.getWidth(), 36);
			gc.strokeText("X: ", Canvas.getWidth() - 90, Canvas.getHeight() - 45);
			gc.strokeText("Y: ", Canvas.getWidth() - 90, Canvas.getHeight() - 30);
			Color temp = (Color) gc.getStroke();
			gc.setStroke(temp);
		}
	}
}

