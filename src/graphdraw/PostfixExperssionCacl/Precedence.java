package graphdraw.PostfixExperssionCacl;

/**
 * Stores date of precedence of different operator
 * according to rules setup by shunting yard algorithm rules.
 * 
 * @author havra
 */
public enum Precedence {
	PLUS(1), MINUS(1), MULTIPLY(2), DIVIDE(2), POWER(3);
	private final int presedence;

	private Precedence(int presedence) {
		this.presedence = presedence;
	}

	public int getPresedence() {
		return presedence;
	}

	public int getPresedenceForString(String s) {
		switch (s) {
			case "+":
				return PLUS.getPresedence();
			case "-":
				return MINUS.getPresedence();
			case "*":
				return MULTIPLY.getPresedence();
			case "/":
				return DIVIDE.getPresedence();
			case "^":
				return POWER.getPresedence();
		}
		return 0;
	}
}