package graphdraw;

import graphdraw.PostfixExperssionCacl.PostfixExpressionCacl;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import org.json.*;

/**
 * Class for storing expression, you can add new with addNewEntry
 *
 * @author havra
 */
public class ParsedExpressions {

	private final List<Color> colors = new ArrayList<>();
	private final List<ArrayList<String>> postfixExpressions = new ArrayList<>();
	private final List<String> variables = new ArrayList<>();
	private final List<String> intfixExpressions = new ArrayList<>();

	public Color getColor(int i) {
		return colors.get(i);
	}

	public List<Color> getColors() {
		return colors;
	}

	public String getInfixExpression(int i) {
		return intfixExpressions.get(i);
	}

	public ArrayList<String> getPostfixExpression(int i) {
		return postfixExpressions.get(i);
	}

	public String getVariable(int i) {
		return variables.get(i);
	}

	public int getSize() {
		return colors.size();
	}

	public boolean isEmpty() {
		return colors.isEmpty();
	}

	public boolean containsInfix(String contains) {
		return intfixExpressions.contains(contains);
	}

	public int getIndexOfInfixFunction(String infix) {
		return intfixExpressions.indexOf(infix);
	}

	public ParsedExpressions() {
	}

	/**
	 * Adds new entry, return true if there was only need to change color
	 *
	 * @param parsed
	 * @return
	 */
	public boolean addNewEntry(ArrayList<String> postfixExpression, String infixExpression, String variable, Color color) {
		for (int i = 0; i < getSize(); i++) {
			if (getPostfixExpression(i).equals(postfixExpression)) {
				colors.set(i, color);
				return true;
			}
		}
		colors.add(color);
		postfixExpressions.add(postfixExpression);
		variables.add(variable);
		intfixExpressions.add(infixExpression);
		return false;
	}

	@Override
	public String toString() {
		return "colors:\t\t\t" + colors + "\n" + "functions (Postfix):\t" + postfixExpressions + "\n" + "functions (Infix):\t" + intfixExpressions + "\n" + "variables:\t\t" + variables;
	}

	/**
	 * Exports data stored in lists to given file.
	 *
	 * @param file
	 */
	public void exportToJSON(File file) {
		if (file != null) {
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {// predelat na FileChooser
				bw.append("{\n");
				bw.append("\t\"functions\":[\n");
				for (int i = 0; i < getSize(); i++) {
					String color = colors.get(i).toString();
					color = "#" + color.substring(2, color.length() - 2);
					bw.append("\t\t{\n");
					bw.append("\t\t\t\"infix\":\"" + intfixExpressions.get(i) + "\",\n");
					bw.append("\t\t\t\"postfix\":\"" + postfixExpressions.get(i).toString().substring(1, postfixExpressions.get(i).toString().length() - 1) + "\",\n");
					bw.append("\t\t\t\"color\":\"" + color + "\",\n");
					bw.append("\t\t\t\"variable\":\"" + variables.get(i) + "\"\n");
					if (i == getSize() - 1) {
						bw.append("\t\t}\n");
					} else {
						bw.append("\t\t},\n");
					}
				}
				bw.append("\t]\n");
				bw.append("}\n");
			} catch (IOException ex) {
				Logger.getLogger(ParsedExpressions.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	/**
	 * Imports functions written in JSON file to lists, return true if there was
	 * a change (functions were added).
	 *
	 * @param file
	 * @return
	 */
	public boolean importFromJSON(File file) {
		if (file != null) {
			boolean wasThereAChange = false;
			String fileContent = "";
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
				String line;
				while ((line = br.readLine()) != null) {
					fileContent += line;
				}
			} catch (IOException ex) {
				Logger.getLogger(ParsedExpressions.class.getName()).log(Level.SEVERE, null, ex);
			}
			JSONObject obj = new JSONObject(fileContent);
			JSONArray arr = obj.getJSONArray("functions");
			for (int i = 0; i < arr.length(); i++) {
				String infixToAdd = arr.getJSONObject(i).getString("infix");
				String postfixToAdd = arr.getJSONObject(i).getString("postfix");
				String variableToAdd = arr.getJSONObject(i).getString("variable");
				String colorToAdd = arr.getJSONObject(i).getString("color");
				if (infixToAdd != null && postfixToAdd != null && variableToAdd != null && colorToAdd != null) {
					ArrayList<String> postfix = new ArrayList<>();
					postfix.addAll(Arrays.asList(postfixToAdd.split(", ")));
					PostfixExpressionCacl temp = new PostfixExpressionCacl(postfix, variableToAdd);
					temp.evaluateExpression(2);
					if (temp.getParsedExpression() != null) {
						try {
							colorToAdd = "0x" + colorToAdd.substring(1, 7).toLowerCase() + "ff";
							Color c = Color.valueOf(colorToAdd);
							addNewEntry(postfix, infixToAdd, variableToAdd, c);
							wasThereAChange = true;
						} catch (IllegalArgumentException e) {
							alertForImporting(infixToAdd);
						}
					} else {
						alertForImporting(infixToAdd);
					}
				} else {
					alertForImporting(infixToAdd);
				}
			}
			return wasThereAChange;
		}
		return false;
	}

	private void alertForImporting(String infinx) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Wrong input");
		alert.setContentText("Function: " + infinx + " is invalid");
		alert.showAndWait();
	}
}
