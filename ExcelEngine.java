import java.util.*;
import java.io.*;
public class ExcelEngine extends ExcelBase {
   public static void main (String[] args) {
      GridBase.grid = new Grid();
      ExcelEngine engine = new ExcelEngine();
      
      engine.runInputLoop();
   
      
   
   }
   
   public String processCommand(String input) {
	   if(input.contains("load"))
		   return loadFile(input.split(" ")[1]);
	   
      return GridBase.grid.processCommand(input);
   
   }
   
   public String help() {
	return"";   
   }
   
   public String loadFile(String filename) {
	   try {
		   System.out.println(filename);
		   Scanner fileScanner = new Scanner(new File(filename));
		   String input = "";
		   while(fileScanner.hasNextLine()) {
			   input = fileScanner.nextLine();
			   GridBase.grid.processCommand(input);
		   }
		   
		   return "File loaded successfully";
		   
		   
	   } catch(FileNotFoundException e) {
		   return "That file cannot be found";
	   }
	   
   }
   
   
   
   

}