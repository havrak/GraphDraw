package graphdraw;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;

/**
 * 
 *
 * @author havra
 */
public class ParsedExpressions {

	private final List<Color> colors = new ArrayList<>();
	private final List<ArrayList<String>> postfixExpressions = new ArrayList<>();
	private final List<String> variables = new ArrayList<String>();
	private final List<String> intfixExpressions = new ArrayList<>();
	
	public Color getColor(int i) {
		return colors.get(i);
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
	public int getIndexOfInfixFunction(String infix){
		return intfixExpressions.indexOf(infix);
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
		return "colors:\t\t\t" + colors + "\n" + "functions (Postfix):\t" + postfixExpressions +"\n" + "functions (Infix):\t" + intfixExpressions + "\n" + "variables:\t\t" + variables;
	}
}
