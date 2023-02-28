package graphdraw.PostfixExperssionCacl;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;

/**
 * Representation of operator stack for Shunting yard
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
					&& !endOfStack.equals("^")) && !endOfStack.equals("(") && !stop) {
				toReturn.add(takeFromStack());
				if (stack.isEmpty()) {
					stop = true;
				} else {
					endOfStack = stack.get(stack.size() - 1);
				}
			}
			stack.add(s);
		}
		return toReturn;
	}

	public void basicAdd(String s) {
		stack.add(s);
	}

	public ArrayList<String> rightBracket() {
		ArrayList<String> toReturn = new ArrayList<>();
		String endOfStack = stack.get(stack.size() - 1);
		while (!endOfStack.equals("(") && !stack.isEmpty()) {
			toReturn.add(takeFromStack());
			if (!stack.isEmpty()) {
				endOfStack = stack.get(stack.size() - 1);
			}
		}
		if (stack.isEmpty()) {
			errorMismatchedParenthesis();
			return null;
		} else {
			takeFromStack();
		}
		return toReturn;
	}

	private String takeFromStack() {
		if (!stack.isEmpty()) {
			String tmp = stack.get(stack.size() - 1);
			stack.remove(stack.size() - 1);
			return tmp;
		}
		return null;
	}

	public List<String> commaFound() {
		List<String> toReturn = new ArrayList<>();
		if (!stack.isEmpty()) {
			String endOfStack = stack.get(stack.size() - 1);
			while (!stack.isEmpty() && !endOfStack.equals("(")) {
				toReturn.add(takeFromStack());
				endOfStack = stack.get(stack.size() - 1);
			}
		}
		return toReturn;
	}

	public ArrayList<String> emptyWholeStack() {
		if (stack.contains("(")) {
			errorMismatchedParenthesis();
			return null;
		}
		ArrayList<String> toReturn = new ArrayList<>();
		while (!stack.isEmpty()) {
			toReturn.add(takeFromStack());
		}
		return toReturn;
	}

	private void errorMismatchedParenthesis() {
		Alert a = new Alert(Alert.AlertType.ERROR);
		a.setTitle("Error");
		a.setHeaderText("Mismatched parenthesies");
		a.showAndWait();
	}

	public String getStack() {
		return stack.toString();
	}

}
