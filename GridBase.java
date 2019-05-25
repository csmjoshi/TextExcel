import java.io.*;
import java.util.*;

/** 
 * This class is intended to establish the required functionality
 * and responsibilities of the Grid.<p>
 *
 * The grid HAS:<ul>
 *    <li> matrix: a 2D matrix of cells</li>
 * </ul>
 * <p>
 * Responsibilities include:<ul>
 *
 *    <li>processCommand specific to the grid</li>
 *    <li>hold all the Cell values</li>
 *    <li>enable cell values/formulas to be set and retrieved</li>
 *    <li>properties are:<ul>
 *       <li>row and column count</li>
 *       <li>cell display width</li></ul></li>
 *    </ul>
 * <p>   
 * The following are things the grid will NOT do:<ul>
 *    <li>Do NOT calculate cell values. This is the Cell's responsibility</li>
 * </ul>
 */
public abstract class GridBase {
   
   /**
    * Have the one and only Grid
    */
   public static GridBase grid = null;
   
   private static final String mathOperators = "+-/*%^";
   
   private static final String[] functionList = { "avg", "sum", "sqrt", "log", "sorta", "sortd" };
   
   // constructor which sets the grid singleton to this instance
   public GridBase() {
      GridBase.grid = this;
   }
   
   /**
    * This processes a user command.<p>
    * Required commands are:<ul>
    *       <li>[cell] = [expression]    "set the cell expression, which may be simplay a value"</li>
    *       <li> [cell]                "get the cell expression, NOT the cell's value"</li>
    *       <li> value [cell]          "get the cell value"</li>
    *       <li> print                 "render a text based version of the matrix"</li>
    *       <li> clear                 "empty out the entire matrix"</li>
    *       <li> clear [cell]          "empty out a single cell"</li>
    *       <li> sorta [range]         "sort the range in ascending order"</li>
    *       <li> sortd [range]         "sort the range in descending order" </li>
    *       <li> width = [value]       "set the cell width"</li>
    *       <li> width                 "get the cell width"</li>
    *       <li> rows = [value]        "set the row count"</li>
    *       <li> cols = [value]        "set the column count"</li>
    *       <li> rows                  "get the row count"</li>
    *       <li> cols                  "get the column count"</li>
    * </ul>
    * @param input The command to be processed.
    * @return the results of a command as a string
    */
   abstract public String processCommand(String input);
   
   /**
   * This will convert an expression with parentheses and operators into tokens.
   * It will convert the following into an array of string tokens.
   * a2-(34 / sqrt 7)+( (c7-c8) /9)
   * <p>
   * Normally, this would go into the NumberCell, but it may be nice to share this with students
   * as a helper method.
   */
   public static String[] smartSplit(String exp) {
      ArrayList<String> tokens = new ArrayList<String>();

      for (int index = 0; index < exp.length(); ) {
         // start of a new token
         // let's figure out the type
         char ch = exp.charAt(index);
         if (Character.isAlphabetic(ch)) {
            String remainder = exp.substring(index);
            // we are a cell location or function
            int funcLength = getFunctionTokenLength(remainder);
            if (funcLength > 0) {
               // it is a function name!
               tokens.add(exp.substring(index, funcLength + index));
               index += funcLength;
               continue;
            }
            String cellName = parseCellNameFrom(remainder);
            if (cellName == null) {
               // we are a malformed expression
               return null;
            }
            tokens.add(cellName);
            index += cellName.length();
            continue;
         }
         if (Character.isDigit(ch) || ch == '.' || 
            (ch == '-' && index+1 < exp.length() && Character.isDigit(exp.charAt(index+1)))) {
            // we are a number. Must end with an operator, ), or space.
            int tokenLength = 1;
            // if our current character is '-', then we know we have a token length of at least 2.
            if (ch == '-') {
               tokenLength = 2;
               ch = exp.charAt(index+1);
            }
            while ((Character.isDigit(ch) || ch == '.') && tokenLength + index < exp.length()) {
               ch = exp.charAt(index + tokenLength);
               if (Character.isDigit(ch) || ch == '.')
                  tokenLength++;
            }
            // if the exp ends with a number, or next char is a space, parenthesis, or operator, then add the token.
            if (tokenLength + index == exp.length() || " ()".contains("" + ch) || mathOperators.contains("" + ch)) {
               tokens.add(exp.substring(index, index + tokenLength));
               index += tokenLength;
               continue;
            }
            // we are a malformed expression
            return null;
         }
         if (mathOperators.contains("" + ch) || "():".contains("" + ch)) {
            tokens.add("" + ch);
            index++;
            continue;
         }
         if (ch != ' ') {
            // a malformed expression
            return null;
         }
         index++;
      }
      
      return tokens.toArray(new String[0]);
   }
   
   /**
   * This will see if the start of this string is a cell location or cellName. 
   * It may NOT be a valid location in the current matrix depending on the matrix size.
   * @param s The string to check.
   * @return null if not a cell location, otherwise the cell location. 
   */
   private static String parseCellNameFrom(String s) {
      if (!Character.isAlphabetic(s.charAt(0)))
         return null;
      if (s.length() < 2 || !Character.isDigit(s.charAt(1))) {
         // second digit is not a number!
         return null;
      }
      if (s.length() == 2) {
         // it is exactly a cellName
         return s;
      }
      // check the 3rd character for operator, parenthesis, or space
      String ch = s.substring(2, 3);
      if (mathOperators.contains(ch) || "():".contains(ch) || ch.equals(" ")) {
         return s.substring(0, 2);
      }
      // check the 3rd character is digit AND
      // that the 4th character is operator, parenthesis, or space, or end of string
      if (s.length() > 2 && Character.isDigit(s.charAt(2))) {
         if (s.length() == 3) {
            // it is exactly a cellName of length 3
            return s;
         }
         ch = s.substring(3, 4);
         if (mathOperators.contains(ch) || "():".contains(ch) || ch.equals(" ")) {
            return s.substring(0, 3);
         }
      }
      // our 3rd or 4th character failed
      return null;
   }
   
   /**
   * This checks the start of this input string and determines if
   * it is a function name. This currently assumes that the expression cell supports
   * exactly 2 functions: sqrt and log.
   * @param s The string to check
   * @return 0 if the input is not a function name, or the number of
   *  characters in the function name.
   */
   private static int getFunctionTokenLength(String s) {
      s = s.toLowerCase();
      int ich = 0;
      for (String func : GridBase.functionList) {
    	  if (s.startsWith(func)) {
    		  ich = func.length();
    	  }
      }
      if (ich == 0)
         return 0;
      // we need to see if we have a cellName, (, or space
      if (s.charAt(ich) == ' ' || s.charAt(ich) == '(' || null != parseCellNameFrom(s.substring(ich)))
         return ich;

      return  0;
   }   
}