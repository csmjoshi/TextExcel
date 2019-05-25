import java.util.*;
import java.io.*;
import java.lang.reflect.*;

/**
   This class will establish the all required functionality
   and responsibilities of the Excel Engine.
   <p>
   Responsibilities of the Excel Engine are: <ul>
      <li> Create and connect the various objects to each/with other<ul>
         <li> Create the Grid</li>
         <li> Set Grid properties (e.g. row and col count)</li></ul></li>
      <li> Process user input (regardless of where it comes from)<ul>
         <li> distribute grid related commands to the Grid</li>
         <li> distribute test commands to the UnitTestRunner</li>
         <li> process other commands directly</li></ul></li>
      <li> Display console based output</li>
      <li> Commands the Engine will process are:<ul>
         <li> help</li>
         <li> read input from a file which contains a set of commands</li>
         <li> save all necessary commands necessary to populate the grid to a file</li></ul></li>
   </ul>
*/
public abstract class ExcelBase {

   // outputStream will enable ALL output to go to a file.
   // The programmer should use outputStream intead of System.out
   public static PrintStream outputStream = System.out;
   
   // This is the one and only console object to read from the console.
   public static Scanner console = new Scanner(System.in);
   
   // This allows any client code to leverage the single excel engine.
   public static ExcelBase engine = null;
   
   private Method launchUI = null;
   private Object mainUIObj = null;
   
   /**
    * Constructor which sets the engine value to this instance.
    */
   public ExcelBase() {
      engine = this;
      
      // Let's do some reflection to see if the MainUI class is available.
      // If it is, then grab the showUI method for later invocation.
      try {
         Class cls = Class.forName("MainUI");
         Object mainUIObj = cls.newInstance();
         Method[] methods =  cls.getDeclaredMethods();
         for( int i = 0 ; i < methods.length ; i++ ) {
            if ("showUI".equals(methods[i].getName())) {
               launchUI = methods[i];
               break;
            }
         }
      } 
      catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
         // no ability to launch GUI. Ignore and continue.
      }
   }

   /**
    * runInputLoop is a method to handle console based input.
    * The ExcelBase class will provide a test hook to the UnitTestRunner as well as
    * handle the quit command. If enabled, it will also open
    * a GUI for this application. The derived class will handle all other commands
    * via the processCommand() method.
    * <p>
	 * Until we're supposed to quit, get input from user.
	 * Do a forever loop and then break out of it when done.  
    */
   public void runInputLoop() {
      boolean done = false;
      while (!done) {
         outputStream.print("Enter: ");
         String input = console.nextLine();
      
         input = input.trim();
         
         if (input.length() > 0 && !UnitTestRunner.processCommand(input)) {
         
            // if we have the ability to launch a GUI, let's do it
            if (launchUI != null && input.equalsIgnoreCase("gui")) {
               try {
                  launchUI.invoke(mainUIObj);
                  return;
               } 
               catch (InvocationTargetException | IllegalAccessException e) {
                  // well, that failed. Stop trying in the future
                  launchUI = null;
                  mainUIObj = null;
               }
            } 
            else {
               if (input.equalsIgnoreCase("quit")) {
                  done = true;
               } 
               else {
                  // process the input
                  String s = processCommand(input);
                  if (s != null && s.length() > 0) {
                     outputStream.println(s);
                  }
               }
            }
         }   
      }
   
      console.close();
      outputStream.println("Thank you and goodbye.");
   }

	/**
	 * This will parse a line that contains a user provided command.
    * The Excel Engine should directly process commands it knows.
    * Other commands will be delegated to the Grid's processCommand().<p>
    * Required Commands to handle are: <ul>
    *    <li> help            "provides help to the user"</li>
    *    <li> load [filename] "opens the file and processes all commands in it"</li>
    *    <li>save [filename] "gets all properties and values from the grid and saves as commands in the file" </li>
	 * </ul>
	 * @param input a single command line to be processed
	 * @return The result of the command
	 */
   abstract public String processCommand(String input);
   
   
}