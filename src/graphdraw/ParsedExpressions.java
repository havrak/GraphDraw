package graphdraw;

import java.util.ArrayList;
import javafx.scene.paint.Color;

/**
 * 
 *
 * @author havra
 */
public class ParsedExpressions {

	private final ArrayList<Color> colors = new ArrayList<>();
	private final ArrayList<ArrayList<String>> postfixExpressions = new ArrayList<>();
	private final ArrayList<String> variables = new ArrayList<String>();

	public Color getColor(int i) {
		return colors.get(i);
	}

	public ParsedExpressions getParsedExpressions() {
		return this;
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

	private void setColorForExisting(int i, Color newColor) {
		colors.set(i, newColor);
	}

	/**
	 * Used by FXMLDocumentControler to change color of PostfixExpressionCalc
	 * output
	 *
	 * @param paint
	 */
	public void ChangeColorAtIndex1(Color paint) {
		colors.set(0, paint);
	}

	/**
	 * Used for addNewEntry - combines values form postfixExpressionCalc
	 * and FXMLDocumentControler
	 *
	 * @param color
	 * @param expression
	 * @param variable
	 */
	public ParsedExpressions(Color color, ArrayList<String> expression, String variable) {
		colors.clear();
		postfixExpressions.clear();
		variables.clear();
		colors.add(color);
		postfixExpressions.add(expression);
		variables.add(variable);
	}

	public ParsedExpressions() {
	}

	
	/**
	 * Adds new entry, return true if there was only need to change color
	 * 
	 * @param parsed
	 * @return
	 */
	public boolean addNewEntry(ParsedExpressions parsed) {
		boolean wasThereOnlyNeedForChangeOfColor = false;
		for (int i = 0; i < getSize(); i++) {
			if (getPostfixExpression(i).equals(parsed.getPostfixExpression(0))) {
				setColorForExisting(i, parsed.getColor(0));
				return true;
			}
		}
		colors.add(parsed.getColor(0));
		postfixExpressions.add(parsed.getPostfixExpression(0));
		variables.add(parsed.getVariable(0));
		return false;
	}

	public String toString() {
		return "colors: " + colors + "\n" + "f(x): " + postfixExpressions + "\n" + "variables: " + variables;
	}
}
