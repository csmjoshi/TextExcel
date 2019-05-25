import java.util.ArrayList;

/* 
 * Mehul Joshi
 * Period 1
 * AP Computer Science A
 * FormulaCell.java
 */
public class FormulaCell extends Cell {
   private String[] tokens;
   private final String mathOperators = "+-/*%^";

   public boolean setValue(String exp) {
      if (validateFormula(exp)) {
         formula = exp.trim();
         return true;
      }
      return false;
   }
   
   private boolean validateFormula(String exp) {
      tokens = GridBase.smartSplit(exp);
      return tokens != null;
   }

   public String toString() {
      return new Double(getValue()).toString();
   }

   public double getValue() {
      if (tokens.length > 1) {//Checks if the formula is an elaborate expression rather than a number
         if (tokens[1].equalsIgnoreCase("AVG") || tokens[1].equalsIgnoreCase("SUM")) {
            Grid g = (Grid) GridBase.grid;

            ArrayList<Double> list = makeList(g.getRow(tokens[2]), g.colsValue(tokens[2]), g.getRow(tokens[4]),
                  g.colsValue(tokens[4]));
            if (tokens[1].equalsIgnoreCase("SUM"))
               return sum(list);
            else
               return avg(list);
            
         } else if (tokens.length >= 3) {
            
            return simplifyExpression(formula);

         }
      }

      return Double.parseDouble(formula);

   }
   //Makes an arrayList of Doubles given a particular range
   public static ArrayList<Double> makeList(int sr, int sc, int er, int ec) {
      ArrayList<Double> list = new ArrayList<Double>();

      Grid g = (Grid) GridBase.grid;
      //Loops through the range
      for (int rows = sr; rows <= er; rows++) {
         for (int cols = sc; cols <= ec; cols++) {
            if(g.matrix[rows][cols].toString().equals("")) 
               list.add(0.0);
            else 
            list.add(Double.parseDouble(g.matrix[rows][cols].toString()));
         }
      }

      return list;
   }
//Simplifies Expressions that contains cell references
   private double simplifyExpression(String exp) {
      String[] tokens = exp.split(" ");
      Grid g = (Grid) GridBase.grid;
      for (int i = 0; i < tokens.length; i++) {
         if (Character.isAlphabetic(tokens[i].charAt(0))) {
            if (g.getCellvalue(tokens[i]).equals(""))
               tokens[i] = 0.0 + "";
            else {
               tokens[i] = g.getCellvalue(tokens[i]);
            }
         }
      }
      String result = "";
      for (int i = 0; i < tokens.length; i++) {
         result += tokens[i] + " ";
      }
      exp = result.trim();

      return evaluateExpression(exp);

   }
   //Does all the math by calling the calc method
   private double evaluateExpression(String exp) {

      String[] tokens = exp.split(" ");
      double Ans = Double.parseDouble(tokens[1]);
      for (int i = 2; i < tokens.length - 1; i++) {
         if (mathOperators.contains(tokens[i])) {
            double right = Double.parseDouble(tokens[i + 1]);
            Ans = calc(Ans, tokens[i], right);

         }
      }
      return Ans;
   }
   //Sums up all the values of an arrayList
   private double sum(ArrayList<Double> list) {
      double sum = 0;
      for (Double n : list) {
         sum += n;
      }
      return sum;
   }
   //Averages all the values in an arrayList
   private double avg(ArrayList<Double> list) {
      return sum(list) / list.size();
   }

   private double calc(double left, String operator, double right) {
      if (operator.equals("+"))
         return left + right;

      else if (operator.equals("-"))
         return left - right;

      else if (operator.equals("*"))
         return left * right;

      else if (operator.equals("/"))
         return left / right;
      else if (operator.equals("^"))
         return Math.pow(left, right);
      return 0;
   }

}
