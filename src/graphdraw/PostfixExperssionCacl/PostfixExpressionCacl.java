package graphdraw.PostfixExperssionCacl;

import graphdraw.ParsedExpressions;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javafx.scene.control.Alert;

/**
 *
 * @author havra
 */
public class PostfixExpressionCacl {

	String infixFunction;
	ArrayList<String> postfixFunctionArray = new ArrayList<>();
	char[] recognitionArray;
	String variable;
	boolean isExpressionCalculable = true;

	public PostfixExpressionCacl(String infixFunction, String variable) {
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
					case 3:
						if (temp.equals("phi")) {
							postfixFunctionArray.add("1.618033988749895");
						} else {
							stack.basicAdd(temp);
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
					while (j != infixFunction.length() - 1 && infixFunction.charAt(j + 1) != ')' && !stop) {
						if (!(Character.isDigit(infixFunction.charAt(j)) || infixFunction.charAt(j) == '.') || infixFunction.charAt(i) == variable.charAt(0)) {
							stop = true;
							stack.basicAdd("(");
						}
						j++;
					}
					if (stop == false) {
						String temp = infixFunction.substring(startingIndex, j + 1);
						i += temp.length() + 1; // z nejakeho duvodu bylo x +2
						postfixFunctionArray.add(temp);
					}
					break;
				case ')':
					System.out.println(postfixFunctionArray + "   " + i);
					List<String> toAdd = stack.rightBracket();
					if (toAdd == null) {
						isExpressionCalculable = false;
					} else {
						postfixFunctionArray.addAll(toAdd);
					}
					break;
				case ',':
					postfixFunctionArray.addAll(stack.commaFound());
					break;
				default:
					break;
			}

		}
		infixFunction = infixFunction.substring(0, infixFunction.length() - 1);
		List<String> toAdd = stack.emptyWholeStack();
		if (toAdd == null) {
			isExpressionCalculable = false;
		} else {
			postfixFunctionArray.addAll(toAdd);
		}
		recognitionArray = new char[postfixFunctionArray.size()];
		setUpRecognitionArray();
		System.out.println("Parsing took:\t\t" + ((System.nanoTime() - time) / 1000_000) + "ms");
	}

	// rychlejsi vypocet nedelam s ArrayListem a String u cisel a promenych
	private void setUpRecognitionArray() {
		for (int i = 0; i < recognitionArray.length; i++) {
			if (postfixFunctionArray.get(i).equals("-" + this.variable)) {
				recognitionArray[i] = 'n';
			} else if (postfixFunctionArray.get(i).equals(this.variable)) {
				recognitionArray[i] = 'p';
			} else if ((postfixFunctionArray.get(i).equals("-") && postfixFunctionArray.get(i).length() >= 2) || (Character.isDigit(postfixFunctionArray.get(i).charAt(0))) || postfixFunctionArray.get(i).charAt(0) == '.') {
				recognitionArray[i] = 'd';
			} else {
				recognitionArray[i] = 'o';
			}
		}
	}

	public double evaluateExpression(double variable) {
		if (isExpressionCalculable) {
			Stack<Double> stack = new Stack<>();
			double d1;
			double d2;
			for (int i = 0; i < postfixFunctionArray.size(); i++) {
				switch (recognitionArray[i]) {
					case 'n':
						stack.push(variable * -1);
						break;
					case 'p':
						stack.push(variable);
						break;
					case 'd':
						stack.push(Double.valueOf(postfixFunctionArray.get(i)));
						break;
					default:
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
							case "min":
								stack.push(Math.min(stack.pop(), stack.pop()));
								break;
							case "exp":
								stack.push(Math.exp(stack.pop()));
								break;
							case "log":
								stack.push(Math.log10(stack.pop()));
								break;
							case "ceil":
								stack.push(Math.ceil(stack.pop()));
								break;
							case "floor":
								stack.push(Math.floor(stack.pop()));
								break;
							default:
								isExpressionCalculable = false;
								errorMessage(postfixFunctionArray.get(i));
								return Double.NaN;
						}
						break;
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
		} else {
			return Double.NaN;
		}
	}

	public void errorMessage(String s) {
		Alert a = new Alert(Alert.AlertType.ERROR);
		a.setTitle("Error");
		a.setHeaderText("Unknown string: " + s);
		a.showAndWait();
	}

	public void setPostfixExpression(ArrayList<String> postfixFunctionArray, String variable) {
		this.variable = variable;
		this.postfixFunctionArray = postfixFunctionArray;
		isExpressionCalculable = true;
		recognitionArray = new char[this.postfixFunctionArray.size()];
		setUpRecognitionArray();
	}

	public ArrayList<String> getParsedExpression() {
		if (isExpressionCalculable) {
			return postfixFunctionArray;
		}
		return null;
	}
}
