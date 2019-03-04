import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
public class OneWayToParse {

    public static String workFunction;
    public static char variable;

    public static void main(String[] args) throws IOException {
        double time = System.nanoTime();

        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        String function = "f(x)=4E3";

        File file = new File("file");
        FileWriter fw = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("sdsd");

        if (bw != null) {
            bw.close();
        }

        if (fw != null) {
            fw.close();
        }
        
        setFunctionAndVariable(function);
        pahrseToJava();
        System.out.println(workFunction);

        try {
            System.out.println("vysledek je " + engine.eval(workFunction));
        } catch (ScriptException ex) {
            Logger.getLogger(OneWayToParse.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Doba Trvani: " + (System.nanoTime() - time) / 1000000 + " ms");
    }

    public static void setFunctionAndVariable(String function) {
        workFunction = function.toLowerCase().trim();
        variable = workFunction.charAt(2);
        workFunction = "0+" + workFunction.substring(5, workFunction.length()) + "+0";

    }

    public static void pahrseToJava() {
        if (workFunction.contains("pi")) {
            replace("pi", "Math.PI");
        }
        //if (workFunction.contains("e")) {
        //    replace("e", "Math.E");
        //}
        if (workFunction.contains("ln")) {
            replace("ln", "Math.log");
        }
        if (workFunction.contains("log")) {
            replace("log", "Math.log10");
        }
        if (workFunction.contains("acos")) {
            replace("acos", "Math.acos");
        }
        if (workFunction.contains("cos")) {
            replace("cos", "Math.cos");
        }
        if (workFunction.contains("asin")) {
            replace("asin", "Math.asin");
        }
        if (workFunction.contains("sin")) {
            replace("sin", "Math.sin");
        }
        if (workFunction.contains("atan")) {
            replace("atan", "Math.atan");
        }
        if (workFunction.contains("tan")) {
            replace("tan", "Math.tan");
        }
        if (workFunction.contains("abs")) {
            replace("abs", "Math.abs");
        }
        if (workFunction.contains("^")) {
            power();
        }

    }

    public static void replace(String original, String toReplaceWith) {
        for (int i = 2; i < workFunction.length() - original.length() + 1; i++) {
            if (workFunction.substring(i, i + original.length()).equals(original) && !workFunction.substring(i, workFunction.length()).contains("." + original) && !workFunction.substring(i - 1, i).contains("a")) {
                workFunction = workFunction.substring(0, i) + toReplaceWith + workFunction.substring(i + original.length(), workFunction.length());
                i += 5;
            }
        }
        System.out.println(workFunction);
    }

    public static void power() {
        String base;
        String exponent;

        if (workFunction.charAt(2) == '^' || workFunction.charAt(workFunction.length() - 3) == '^') {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText("Power found on begining or end of function");
        }
        for (int i = 1; i < workFunction.length() - 1; i++) {
            if (workFunction.charAt(i) == '^') {
                int correctionVariable = workFunction.length() - 1;
                base = baseOfPower(i);
                System.out.println("zaklad: " + base);
                correctionVariable = correctionVariable - workFunction.length() - 1;
                exponent = exponentOfPower(i - correctionVariable);
                System.out.println("exponent: " + exponent);
                workFunction = workFunction.substring(0, i - correctionVariable - 2) + "Math.pow(" + base + "," + exponent + ")" + workFunction.substring(i - correctionVariable - 1, workFunction.length());
            }

        }
    }

    // i je pozice ^ a j je pozice znacky
    public static String baseOfPower(int i) {
        String temp;
        String operations = "+-*/(";
        int bracketsCounter = 0;
        for (int j = i - 1; j >= 0; j--) {
            bracketsCounter += addOrSubtractBracketsCounter(j);
            if (bracketsCounter == 0 || bracketsCounter == 1) {
                for (int k = 0; k < operations.length(); k++) {
                    if (workFunction.charAt(j) == operations.charAt(k)) {
                        temp = workFunction.substring(j + 1, i);
                        workFunction = workFunction.substring(0, j + 1) + workFunction.substring(i, workFunction.length());
                        return temp;
                    }
                }
            }
        }
        temp = workFunction.substring(0, i - 1);
        workFunction = workFunction.substring(i, workFunction.length());
        return temp;
    }

    public static String exponentOfPower(int i) {
        String temp;
        String operations = "+-*/)";
        int bracketsCounter = 0;
        for (int j = i; j <= workFunction.length(); j++) {
            bracketsCounter += addOrSubtractBracketsCounter(j);
            if (bracketsCounter == 0 || bracketsCounter == -1) {
                for (int k = 0; k < operations.length(); k++) {
                    if (workFunction.charAt(j) == operations.charAt(k)) {
                        temp = workFunction.substring(i - 1, j);
                        workFunction = workFunction.substring(0, i - 1) + workFunction.substring(j, workFunction.length());
                        return temp;
                    }
                }
            }
        }
        temp = workFunction.substring(i + 1, workFunction.length());
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
}
