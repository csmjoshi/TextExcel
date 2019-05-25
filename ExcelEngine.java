/**
 * Mehul Joshi
 * Period 1
 * AP CS A
 * Excel Engine
 **/
import java.io.*;
import java.util.*;

public class ExcelEngine extends ExcelBase {

   public static void main(String args[]) {

      GridBase.grid = new Grid();
      ExcelEngine engine = new ExcelEngine();

      engine.runInputLoop();

   }

   public String processCommand(String input) {
      String result = null;
      String[] tokens = input.split(" ");

      if (input.equalsIgnoreCase("help"))
         result = "This is your help";

      if (tokens[0].equalsIgnoreCase("load"))
         result = loadFromFile(tokens[1]);

      if (result == null && GridBase.grid != null)
         result = GridBase.grid.processCommand(input);

      if (result == null)
         result = "(" + input + ")" + " is Unhandled";

      return result;

   }

   // loads the files and runs process Command on each one of the lines in the file
   private String loadFromFile(String filename) {

      String result = "File loaded successfully";
      try {
         Scanner Sc = new Scanner(new File(filename));
         while (Sc.hasNextLine()) {
            String text = Sc.nextLine();
            
            processCommand(text);
         }
         Sc.close();

      } catch (FileNotFoundException e) {
         result = "Could not find file: " + filename;
      }

      return result;
   }

}
