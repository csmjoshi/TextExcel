/* 
 * Mehul Joshi
 * Period 1
 * AP Computer Science A
 * Grid.java
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;

public class Grid extends GridBase {

   public int colCount = 7;
   public int rowCount = 10;
   public int cellWidth = 9;

   public Cell[][] matrix;

   public Grid() {
      createMatrix();
   }
   //Populates the original matrix
   private void createMatrix() {
      matrix = new Cell[rowCount][colCount];
      for (int rows = 0; rows < rowCount; rows++) {
         for (int cols = 0; cols < colCount; cols++) {
            matrix[rows][cols] = new Cell();
         }
      }
   }

   public String processCommand(String input) {
      String result = null;
      
      String[] tokens = input.split(" ");
      
      if (input.equalsIgnoreCase("print"))
         result = generateMatrix();

      else if (tokens[0].equalsIgnoreCase("clear"))
         result = handleClearcommands(tokens);

      else if (tokens[0].equalsIgnoreCase("Save"))
         result = saveGrid(tokens);
      
      else if (tokens[0].equalsIgnoreCase("SortA") || tokens[0].equalsIgnoreCase("SortD")) 
         result = sort(input);
         
      else if (result == null)
         result = handleSizecommands(tokens);
      
      if (result == null)
         result = handleCellcommands(input, tokens);

      return result;
   }
   // The following method sorts a List into ascending order and assigns the values corresponding to the command 
   private String sort(String input) {
      input = input.replace("-", " ").trim();
      String[]tokens = input.split(" ");
      
      ArrayList<Double> list = FormulaCell.makeList(getRow(tokens[1]), colsValue(tokens[1]), 
            getRow(tokens[tokens.length - 1]), colsValue(tokens[tokens.length - 1]));
      
      Collections.sort(list);
      
      assignVals(list,getRow(tokens[1]), colsValue(tokens[1]), 
            getRow(tokens[tokens.length - 1]), colsValue(tokens[tokens.length - 1]), tokens[0]);
      return "Done";
   }

   // The following Assigns the values of a list into the matrix
   private void assignVals (ArrayList<Double> list, int sr, int sc, int er, int ec, String sort) {
      int count = 0;
      if(sort.equalsIgnoreCase("sortD"))
         count = list.size() - 1;
      for(int rows = sr; rows <= er; rows++) {
         for(int cols = sc; cols <= ec; cols++) {
            matrix[rows][cols].setValue(list.get(count) + "");

            if(sort.equalsIgnoreCase("sortD"))
               count--;
            else
            count++;
         }
      }
   }

   /* This handles the save command which prints the rows, cols,
      and width. In addition it prints the value of any assigned cell into a file */ 
   private String saveGrid(String[] tokens) {
      try {
         String fileName = tokens[1];
         PrintStream infile = new PrintStream(new File(fileName));
         infile.println("rows = " + rowCount);
         infile.println("cols = " + colCount);
         infile.println("width = " + cellWidth);
         for (int rows = 0; rows < rowCount; rows++) {
            for (int cols = 0; cols < colCount; cols++) {
               if (matrix[rows][cols] instanceof TextCell || matrix[rows][cols] instanceof FormulaCell
                     || matrix[rows][cols] instanceof DateCell) {
                  infile.println(getLetter(cols) + (rows + 1) + " = " + matrix[rows][cols].formula);

               }
            }
         }
         infile.close();

         return "File saved";

      } catch (FileNotFoundException e) {
         return "File not found";
      }
   }

   private String getLetter(int cols) {
      return (char) ('a' + cols) + "";
   }

   // To clear the grid one must basically set each matrix value to a new Cell().
   private String handleClearcommands(String[] tokens) {
      if (tokens.length == 1)
         createMatrix();
      else {
         int x = getRow(tokens[1]);
         int y = colsValue(tokens[1]);
         matrix[x][y] = new Cell();
      }
      return "Done";
   }

   // The cell commands are as follows: [cell], value [cell] & [cell] = # || cell =
   // "String".
   private String handleCellcommands(String cmd, String[] tokens) {
      String result = null;
      // for [cell] formula only
      if (tokens.length == 1)
         result = getCellformula(cmd);
      // for setting the cell
      else if (tokens[1].equals("="))
         result = setCell(cmd, tokens);
      // for the value of the cell not formula
      else if (tokens[0].equalsIgnoreCase("value"))
         result = getCellvalue(tokens[1]);

      return result;
   }

   // setCell sets the type and formula of the cell
   // It does no calculations or concatenations
   private String setCell(String cmd, String[] tokens) {
      String result = null;

      int x = getRow(tokens[0]);
      int y = colsValue(cmd);
      //Checks if it is a TextCell
      if (isTextCell(tokens)) {
         TextCell textcell = new TextCell();
         textcell.setValue(cmd.substring(cmd.indexOf("\""), cmd.length()));
         result = checkCell(textcell, x, y);
         //Checks if it a DateCell
      } else if (tokens[2].contains("/")) {
         DateCell datecell = new DateCell();
         datecell.setValue(cmd.substring(cmd.indexOf("=") + 2, cmd.length()));
         result = checkCell(datecell, x, y);
         // If it's neither of those then it is a FormulaCell
      } else {
         FormulaCell formulaCell = new FormulaCell();
         String exp = cmd.substring(cmd.indexOf("=") + 1, cmd.length());

         if (!formulaCell.setValue(exp)) {
            return "Error: Fix Syntax";
         }

         result = checkCell(formulaCell, x, y);
      }
      return result;
   }
   // isTextCell returns whether or not a formula of a cell starts with and ends with a quotation mark
   private boolean isTextCell(String[] tokens) {
      return tokens[2].startsWith("\"") && tokens[tokens.length - 1].endsWith("\"");
   }
   
   //The following method takes a cell reference as a parameter and returns the row value
   public int getRow(String cell) {
      return Integer.parseInt(cell.substring(1, cell.length())) - 1; 
   }
   
   // checkCell is able to check if the cell is not 
   // null and then populate that cell with a formula or
   // date or text cell
   private String checkCell(Cell cell, int x, int y) {
      if (matrix != null) {
         matrix[x][y] = cell;
         return "Done";
      } else {
         createMatrix();
         return "Matrix is instanciated\nPlease re-enter your command";
      }
   }

   // Gets the String representation of a cell
   public String getCellvalue(String cell) {
      int x = getRow(cell);
      int y = colsValue(cell.substring(cell.indexOf(" ") + 1));

      return matrix[x][y].toString();
   }

   // Gets the formula of a cell reference
   public String getCellformula(String cell) {
      int x = getRow(cell);
      int y = colsValue(cell);

      return matrix[x][y].formula;
   }

   // colsValue returns the column value of a cell
   // For example, colsValue("a1") was passed then the colValue would be 0
   public int colsValue(String cell) {
      char one = Character.toUpperCase(cell.charAt(0));
      int colNum = -1;
      if (one >= 'A' && one <= 'Z')
         colNum = one - 'A';

      return colNum;
   }

   // handles the commands for rows, cols, width
   
   private String handleSizecommands(String[] tokens) {
      String result = null;
      if (tokens[0].equals("rows"))
         result = rowCommands(tokens);

      if (tokens[0].equals("cols"))
         result = colCommands(tokens);

      if (tokens[0].equals("width"))
         result = widthCommands(tokens);

      return result;

   }

   // Either assigns values or gets the value of the rows
   private String rowCommands(String[] tokens) {
      String result = null;
      if (tokens.length > 1) {
         if (tokens[1].equals("=")) {
            rowCount = Integer.parseInt(tokens[2]);
            createMatrix();
            result = rowCount + "";
         }
      } else
         result = rowCount + "";

      return result;
   }

   // Either assigns values or gets the value of the cols
   private String colCommands(String[] tokens) {
      String result = null;
      if (tokens.length > 1) {
         if (tokens[1].equals("=")) {
            colCount = Integer.parseInt(tokens[2]);
            createMatrix();
            result = colCount + "";
         }
      } else
         result = colCount + "";

      return result;
   }

   // Either assigns values or gets the value of the width
   private String widthCommands(String[] tokens) {
      String result = null;
      if (tokens.length > 1) {
         if (tokens[1].equals("=")) {
            cellWidth = Integer.parseInt(tokens[2]);
            result = cellWidth + "";
         }
      } else
         result = cellWidth + "";

      return result;
   }

   // Prints the table representation of the matrix
   private String generateMatrix() {
      String result = header();
      int indentSpace = 2;
      String s = "";
      int length = 0;
      for (int rows = 1; rows <= rowCount; rows++) {
         if (rows > 9)
            indentSpace = 1;

         result += String.format("%" + indentSpace + "s", " ") + rows + " ";
         for (int cols = 1; cols <= colCount; cols++) {
            if (matrix != null) {// checks if formula is set

               s = matrix[rows - 1][cols - 1].toString();
               length = s.length();
               if (length > cellWidth) {
                  s = s.substring(0, cellWidth);
                  length = cellWidth;
               }

            }
            result += String.format("%" + (-1 * ((cellWidth + 1) - length)) + "s", "|") + s;
         }
         result += "|\n" + lineDivider();
      }
      return result;
   }

   private String lineDivider() {// Divides each row
      String result = "----";
      for (int i = 0; i <= (cellWidth * colCount) + colCount; i++) {
         if (i % (cellWidth + 1) == 0)
            result += "+";
         else
            result += "-";
      }
      result += "\n";
      return result;
   }

   private String header() { // creates the header for the Matrix

      String result = String.format("%5s", "|");
      String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
      for (int cols = 0; cols < colCount; cols++) {
         result += (String.format("%" + (cellWidth / 2) + "s", " ") + letters.charAt(cols)
               + String.format("%" + (cellWidth - (cellWidth / 2) - 1) + "s", " ")) + "|";
      }
      return result + "\n" + lineDivider();
   }

}
