import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author havra
 */
public class RegExParsing {

    public static String workFunction;
    public static char variable;

    public static void main(String[] args) throws ScriptException {
        double time = System.nanoTime();
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        String function = "f(x)=1^(-2)";
        setFunctionAndVariable(function);
        pahrseToJava();
        System.out.println(engine.eval(workFunction));
        System.out.println("Doba Trvani: " + (System.nanoTime() - time) / 1000000 + " ms");
    }
    
    public static void setFunctionAndVariable(String function) {
        workFunction = function.toLowerCase().trim();
        variable = workFunction.charAt(2);
        workFunction = "0.0+"+workFunction.substring(5, workFunction.length())+"+0";
    }

    public static void pahrseToJava() {
        workFunction = workFunction.replaceAll("pi", "Math.PI");
        workFunction = workFunction.replaceAll("e", "Math.E");
        workFunction = workFunction.replaceAll("ln", "Math.log");
        workFunction = workFunction.replaceAll("\\b(?:log)\\b", "Math.log10");
        workFunction = workFunction.replaceAll("abs", "Math.abs");
        workFunction = workFunction.replaceAll("atan", "Math.atan");
        workFunction = workFunction.replaceAll("asin", "Math.asin");
        workFunction = workFunction.replaceAll("acos", "Math.acos");
        workFunction = workFunction.replaceAll("\\b(?:sin)\\b", "Math.sin");
        workFunction = workFunction.replaceAll("\\b(?:cos)\\b", "Math.cos");
        workFunction = workFunction.replaceAll("\\b(?:tan)\\b", "Math.tan");
        if (workFunction.contains("^")) {
            power();
        }
        
    }

    public static void power() {
        String base;
        String exponent;

        if (workFunction.charAt(0) == '^' || workFunction.charAt(workFunction.length() - 3) == '^') {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText("Power found on begining or end of function");
        }
        for (int i = 1; i < workFunction.length() - 1; i++) {
            if (workFunction.charAt(i) == '^') {
                int correctionVariable = workFunction.length() - 1;
                base = baseOfPower(i);
                correctionVariable = correctionVariable - workFunction.length() - 1;
                exponent = exponentOfPower(i - correctionVariable);
                
                workFunction = workFunction.substring(0, i - correctionVariable - 2) + "Math.pow(" + base + "," + exponent + ")" + workFunction.substring(i - correctionVariable - 1, workFunction.length());
            }

        }
    }
    public static String baseOfPower(int i) {
        String temp;
        int bracketsCounter = 0;
        for (int j = i - 1; j >= 0; j--) {
            bracketsCounter += addOrSubtractBracketsCounter(j);
            if (bracketsCounter == 0) {
                if (workFunction.charAt(j) == '/' || workFunction.charAt(j) == '*' || workFunction.charAt(j) == '-' || workFunction.charAt(j) == '+') {
                    temp = workFunction.substring(j + 1, i);
                    workFunction = workFunction.substring(0, j + 1) + workFunction.substring(i, workFunction.length());
                    return temp;

                }
            }
        }
        temp = workFunction.substring(0, i - 1);
        workFunction = workFunction.substring(i, workFunction.length());
        return temp;
    }

    public static String exponentOfPower(int i) {
        String temp;
        int bracketsCounter = 0;
        for (int j = i-1; j <= workFunction.length(); j++) {
            bracketsCounter += addOrSubtractBracketsCounter(j);
            System.out.println(bracketsCounter);
            if (bracketsCounter == 0) {
                if (workFunction.charAt(j) == '/' || workFunction.charAt(j) == '*' || workFunction.charAt(j) == '-' || workFunction.charAt(j) == '+') {
                    temp = workFunction.substring(i - 1, j);
                    System.out.println("asd: "+temp);
                    workFunction = workFunction.substring(0, i - 1) + workFunction.substring(j, workFunction.length());
                    return temp;
                }
            }
        }
        temp = workFunction.substring(i + 1, workFunction.length() - 1);
        workFunction = workFunction.substring(0, i);
        return temp;
    }

    public static int addOrSubtractBracketsCounter(int j) {
        switch (workFunction.charAt(j)) {
            case '(':
                return 1;
            case ')':
                return -1;
            default:
                return 0;
        }
    }
    
    public static double calculateForSpecificVariable (double variableValue){
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        try {
            return (double) engine.eval(workFunction.replaceAll(String.valueOf(variable), String.valueOf(variableValue)));
        } catch (ScriptException ex) {
            Logger.getLogger(RegExParsing.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Double.NEGATIVE_INFINITY;
    }
}