package graphdraw.PostfixExperssionCacl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Stack;
import javafx.scene.control.Alert;

/**
 *
 * @author havra
 */
public class PostfixExperssionCacl {

	String infixFunction;
	ArrayList<String> postfixFunctionArray = new ArrayList<>();
	String variable;
	boolean isExpressionCalculable = true;

	public PostfixExperssionCacl(String infixFunction, String variable) {
		this.infixFunction = infixFunction + " "; // diky zpusobu jakym je algoritmus zapsany nebral posledni string -> nejlehci zpusob jak to vyresit
		this.variable = variable;
		parse();

	}

	// Shunting-yard algorithm
	private void parse() {
		double time = System.nanoTime();
		OperatorStack stack = new OperatorStack();
		boolean firstLetter = true;
		boolean wasItALetter;
		boolean firstDigit = true;
		boolean wasItADigit;
		int startingIndexOfDigit = 0;
		int startingIndexOfLetter = 0;
		boolean numberInBrackets = false;
		if (infixFunction.charAt(0) == '-') {
			postfixFunctionArray.add("0");
		}
		for (int i = 0; i < infixFunction.length(); i++) {
			wasItALetter = false;
			wasItADigit = false;
			char c = infixFunction.charAt(i);
			if ('a' <= c && c <= 'z') {
				if (firstLetter) {
					startingIndexOfLetter = i;
					firstLetter = false;
				}
				wasItALetter = true;

			} else if (Character.isDigit(infixFunction.charAt(i)) || c == '.') {
				if (firstDigit) {
					startingIndexOfDigit = i;
					firstDigit = false;
				}
				wasItADigit = true;
			}
			if (wasItADigit == false && firstDigit == false) {
				String temp = infixFunction.substring(startingIndexOfDigit, i);
				postfixFunctionArray.add(temp);
				firstDigit = true;
			}
			if (wasItALetter == false && firstLetter == false) {
				String temp = infixFunction.substring(startingIndexOfLetter, i);
				firstLetter = true;
				switch (temp.length()) {
					case 1:
						if (temp.equals(variable)) {
							postfixFunctionArray.add(variable);
						} else if (temp.equals("e")) {
							postfixFunctionArray.add(String.valueOf(Math.E));
						} else {
							System.out.println(temp);
							errorMessage(temp);
						}
						break;
					case 2:
						if (temp.equals("pi")) {
							postfixFunctionArray.add(String.valueOf(Math.PI));
						} else if (temp.equals("ln")) {
							stack.basicAdd(temp);
						} else {
							errorMessage(temp);
						}
						break;
					default:
						stack.basicAdd(temp);
				}
				firstLetter = true;
			}

			switch (c) {
				case '*':
				case '+':
				case '-':
				case '/':
				case '^':
					postfixFunctionArray.addAll(stack.addToStack(String.valueOf(c)));
					break;
				case '(':
					int startingIndex = i + 1;
					int j = i + 1;
					boolean stop = false;
					if (infixFunction.charAt(startingIndex) == '-') {
						j++;
					}
					while (infixFunction.charAt(j + 1) != ')' && !stop) { // out of bounds
						if (!(Character.isDigit(infixFunction.charAt(j)) || infixFunction.charAt(j) == '.') || infixFunction.charAt(i) == variable.charAt(0)) {
							stop = true;
							stack.leftBracket();
						}
						j++;
					}
					if (stop == false) {
						String temp = infixFunction.substring(startingIndex, j + 1);
						i += temp.length() + 2;
						postfixFunctionArray.add(temp);
					}
					break;
				case ')':
					if (!numberInBrackets) {
						postfixFunctionArray.addAll(stack.rightBracket());
					}
					numberInBrackets = false;
					break;
				default:
					break;
			}
			System.out.println("postfix: " + postfixFunctionArray + ", oprator stack: " + stack.getStack());

		}
		infixFunction = infixFunction.substring(0, infixFunction.length() - 1);
		postfixFunctionArray.addAll(stack.emptyWholeStack());
		System.out.println("Infix expression is: " + infixFunction + ", Postfix expression is: " + postfixFunctionArray + ", parsing took: " + ((System.nanoTime() - time) / 1000_000) + "ms");
	}

	public double evaluateExpression(double variable) {
		if (isExpressionCalculable) {
			Stack<Double> stack = new Stack<>();
			for (int i = 0; i < postfixFunctionArray.size(); i++) {
				if (postfixFunctionArray.get(i).equals("-" + this.variable)) {
					stack.push(variable * -1);
				} else if (postfixFunctionArray.get(i).equals(this.variable)) {
					stack.push(variable);
				} else if ((postfixFunctionArray.get(i).equals("-") && postfixFunctionArray.get(i).length() >= 2) || (Character.isDigit(postfixFunctionArray.get(i).charAt(0))) || postfixFunctionArray.get(i).charAt(0) == '.') {
					stack.push(Double.valueOf(postfixFunctionArray.get(i)));
				} else {
					double d1;
					double d2;
					switch (postfixFunctionArray.get(i)) {
						case "tan":
							stack.push(Math.tan(stack.pop()));
							break;
						case "atan":
							stack.push(Math.atan(stack.pop()));
							break;
						case "sin":
							stack.push(Math.sin(stack.pop()));
							break;
						case "asin":
							stack.push(Math.asin(stack.pop()));
							break;
						case "cos":
							stack.push(Math.cos(stack.pop()));
							break;
						case "acos":
							stack.push(Math.acos(stack.pop()));
							break;
						case "abs":
							stack.push(Math.abs(stack.pop()));
							break;
						case "^":
							d1 = stack.pop();
							d2 = stack.pop();
							stack.push(Math.pow(d2, d1));
							break;
						case "/":
							d1 = stack.pop();
							d2 = stack.pop();
							stack.push(d2 / d1);
							break;
						case "*":
							stack.push(stack.pop() * stack.pop());
							break;
						case "+":
							stack.push(stack.pop() + stack.pop());
							break;
						case "-":
							d1 = stack.pop();
							d2 = stack.pop();
							stack.push(d2 - d1);
							break;
						case "max":
							stack.push(Math.max(stack.pop(), stack.pop()));
							break;
						default:
							isExpressionCalculable = false;
							errorMessage(postfixFunctionArray.get(i));
							return Double.NaN;
					}
				}
			}
			if (!stack.isEmpty()) {
				return stack.pop();
			} else {
				Alert a = new Alert(Alert.AlertType.ERROR);
				a.setTitle("Error");
				a.setHeaderText("Error occured during calculation, wrong input");
				isExpressionCalculable = false;
				return Double.NaN;
			}
		}else{
			return Double.NaN;
		}
	}

	public void errorMessage(String s) {
		Alert a = new Alert(Alert.AlertType.ERROR);
		a.setTitle("Error");
		a.setHeaderText("Unknown string: " + s);
		a.showAndWait();

	}

	public void setPostfixExpression(Entry<ArrayList<String>, String> h) {
		this.variable = h.getValue();
		this.postfixFunctionArray = h.getKey();
		isExpressionCalculable = true;

	}

	public HashMap<ArrayList<String>, String> getPostfixFunctionArray() {
		HashMap<ArrayList<String>, String> h = new HashMap();
		h.put(postfixFunctionArray, variable);
		return h;
	}
}
