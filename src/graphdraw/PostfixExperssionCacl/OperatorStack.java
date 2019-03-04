package graphdraw.PostfixExperssionCacl;

import java.util.ArrayList;
import javafx.scene.control.Alert;

/**
 *
 * @author havra
 */
public class OperatorStack {

	private final ArrayList<String> stack = new ArrayList<>();

	public ArrayList<String> addToStack(String s) {
		ArrayList<String> toReturn = new ArrayList<>();
		if (stack.isEmpty()) {
			stack.add(s);
		} else {
			Precedence p = Precedence.DIVIDE;
			String endOfStack = stack.get(stack.size() - 1);
			boolean stop = false;
			while ((endOfStack.length() >= 2 || p.getPresedenceForString(s) <= p.getPresedenceForString(endOfStack)
					&& !endOfStack.equals("^")) && !endOfStack.equals("(") && !stop) { // chyba v podmince
				toReturn.add(takeFromStack());
				if (stack.isEmpty()) { // z nejakeho neznameho duvodu musi byt boolean
					stop = true;
				} else {
					endOfStack = stack.get(stack.size() - 1); // muze hodit chybu
				}
			}
			stack.add(s);
		}
		return toReturn;
	}
	public void basicAdd(String s){
		stack.add(s);
	}

	public ArrayList<String> rightBracket() {
		ArrayList<String> toReturn = new ArrayList<>();
		String endOfStack = stack.get(stack.size() - 1);
		while (!endOfStack.equals("(")) {
			toReturn.add(takeFromStack());
			if (!stack.isEmpty()) {
				endOfStack = stack.get(stack.size() - 1);
			}
		}
		if (stack.isEmpty()) {
			Alert a = new Alert(Alert.AlertType.ERROR);
			a.setTitle("Error");
			a.setHeaderText("Mismatched parenthesies");
		} else {
			takeFromStack();
		}
		return toReturn;
	}

	public void leftBracket() {
		stack.add("(");
	}

	public String takeFromStack() {
		if (!stack.isEmpty()) {
			String tmp = stack.get(stack.size() - 1);
			stack.remove(stack.size() - 1);
			return tmp;
		}
		return null;
	}

	public ArrayList<String> emptyWholeStack() {
		ArrayList<String> toReturn = new ArrayList<>();
		while (!stack.isEmpty()) {
			toReturn.add(takeFromStack());
		}
		return toReturn;
	}
	public String getStack(){
		return stack.toString();
	}

}
