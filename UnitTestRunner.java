import java.util.*;
import java.io.*;

public class UnitTestRunner {
   
   private static PrintStream testFile = null;
   
   /**
   *  Processes the command if it is a Test command
   *
   *  @param input The command string that the user entered to be processed.
   *  @return true if the command was a Test command. This means that the Excel engine
   *        will ignore the input. Flase means it will proceed to process the command.
   */
   public static boolean processCommand(String input) {
      if (generateTestCommand(input) || runTestCommand(input)) {
         return true;
      } else if (testFile != null) {        
         // check for the "quit" command.
         if (input.equalsIgnoreCase("quit")) {
            // close the test file
            // return as not processed
            testFile.flush();
            testFile.close();
            testFile = null;
            return false;
         }
         
         // output the command to the file
         testFile.println(input);
                  
         // get the output from the ExcelEngine
         String result = ExcelBase.engine.processCommand(input);
         if (result == null || result.length() == 0) {
            testFile.println("0");
         }
         else {
            // output the results to the file, but get line count first
            String[] answer = result.split("\\n");
            testFile.println(answer.length);
            // print one line at a time to make it look nicer in the file
            for (int line = 0; line < answer.length; line++) {
               testFile.println(answer[line]);
            }
            System.out.println(result);
         }
         
         return true;
      }
   
      // this was not a test command
      return false;
   }
   
   private static void runCheckpointTests(String checkpoint, boolean breakOnFail) {
      String file = "tests_checkpoint" + checkpoint + ".txt";
      runTests(file, breakOnFail); 
   }
      
   /**
   *  check if input is:
   *     test create #
   *  
   *  if so, it will redirect output to a file.
   *  When done with the tests, type:
   *     test off
   * @param input The command string that the user entered to be processed.
   * @return true if the command was processed
   */
   private static boolean generateTestCommand(String input) {
      // get the tokens
      Scanner parser = new Scanner(input);
      
      if ("test".equalsIgnoreCase(parser.next())) {
         if (parser.hasNext() && "create".equalsIgnoreCase(parser.next())) {
            if (parser.hasNext()) {
               String testName = parser.next();
               if (testFile != null && "off".equalsIgnoreCase(testName)) {
                  // creating test is done. Turn it off.
                  // flush file and close it up.
                  testFile.flush();
                  testFile.close();
                  testFile = null;
               } else {
                  try {
                     testFile = new PrintStream(new File("tests_checkpoint" + testName + ".txt"));
                  } 
                  catch (Exception e) {
                	  System.out.println(e.getMessage());
                	  e.printStackTrace();
                  }
               }
               return true;
            }
         }
      }
      
      return false;
   }
   
   /** 
   * runTestCommand
   *
   * Runs a processCommand UnitTestt.
   * The command is in the format:
   *  test {checkpoint name/#} [boolean:break_on_fail]
   *  Examples:   test 1
   *              test final
   *              test extra true
   *
   * The [boolean:break_on_fail] is defaulted to FALSE.
   *
   *
   * @param input The command that the user input.
   */
   private static boolean runTestCommand(String input) {
      // get the tokens
      Scanner parser = new Scanner(input);
      if ("test".equalsIgnoreCase(parser.next())) {
         String checkpoint;
         boolean breakOnFail = false;
            
         if (parser.hasNext()) {
            checkpoint = parser.next();
               
            // check for one optional boolean value
            if (parser.hasNextBoolean()) {
               breakOnFail = parser.nextBoolean();
            }
               
            // execute the tests
            runCheckpointTests(checkpoint, breakOnFail);
                  
            // tests were processed
            return true;
         }   
      }
            
      // not a test command
      return false;
   }
   
   /**
   *  This will open a file and run the commands found inside of it.
   *  Subsequent lines will contain the expected output of running the command.
   *  The format is:<p>
   *     &lt;command&gt;
   *     &lt;# of lines in expected output&gt;
   *     &lt;expected output, if any&gt;
   *  Examples include:<p>
   *     a1 = 7
   *     0
   *     value a1
   *     1
   *     7.0
   *
   *  @param filename is the name of the test file
   */
   private static void runTests(String filename, boolean breakOnFail) {
   
      // all tests will be run against the ExcelBase engine
      ExcelBase engine = ExcelBase.engine;
      if (engine == null) {
         System.out.println("No Excel Engine available. No tests run.");
         return;
      }
      
      Scanner file = null;
      int points = 0;
      int total = 0;
      int subSectionPoints = 0;
      int subSectionTotal = 0;
      
      File f = null;
      try {
         // load the file of test cases
         f = new File(filename);
         file = new Scanner(f);
         
         while (file.hasNextLine()) {
         
            try {
               while (file.hasNextLine()) {
                  String input = file.nextLine();
                  if (input.equalsIgnoreCase("// subtotal")) {
                     // output the current sub total of points so far
                     System.out.printf("\tSection Sub-Total: %d / %d\t\tTOTAL: %d / %d\n", 
                           subSectionPoints, subSectionTotal, points, total);
                     subSectionPoints = 0;
                     subSectionTotal = 0;
                     continue;
                  } else if (input.startsWith("//")) {
                     // this line is a comment. Output the comment to the output stream.
                     System.out.println("\n\t"+ input.substring(3));
                     continue;
                  }
                  if (!file.hasNextLine()) {
                     System.out.println("ERROR in test file. Expected integer for count of lines.");
                     return;
                  }
                  
                  // to correctly parse this file, we need to read a line at a time.
                  String lineAndPts = file.nextLine();
                  String[] lineSplit = lineAndPts.split(" ");
                  int answerCount = Integer.parseInt(lineSplit[0]);
                  
                  // any answer of zero length is not worth any points
                  // because there is no validation that can be done
                  int pointsWorth = answerCount > 0 ? 1 : 0;
                  if (lineSplit.length > 1) {
                     pointsWorth = Integer.parseInt(lineSplit[1]);
                  }
                  String[] expected = new String[answerCount];
                  for (int line = 0; line < answerCount; line++) {
                     if (!file.hasNextLine()) {
                        System.out.println("ERROR in test file. Unexpected end of file.");
                        return;
                     }
                     expected[line] = file.nextLine();
                  }
                  total += pointsWorth;
                  subSectionTotal += pointsWorth;
                  
                  System.out.print("Running Test [" + input + "]");
                  String actualFull = engine.processCommand(input);
                  
                  if (answerCount == 0 || multiLineMatch(actualFull, expected)) {
                     System.out.printf(" passed  (+%d pts)\n", pointsWorth);
                     points += pointsWorth;
                     subSectionPoints += pointsWorth;
                  } else if (breakOnFail && answerCount != 0) {
                     // we have a failure. Just break out!
                     System.out.println("Set to break on fail");
                     return;
                  }
               }
            } 
            catch (Exception e) {
               System.out.println(" Failed with exception. Here are details:");
               e.printStackTrace();
               System.out.println(e.getMessage());
               if (breakOnFail) {
                  // we have a failure. Just break out!
                  System.out.println("Set to break on fail");
                  return;
               }
            }
         }
      } 
      catch (FileNotFoundException e) {
         System.out.println("Cannot find test file. Here are details:");
         if (f != null) {
            System.out.println(" path of file: " + f.getAbsolutePath());
         }
         System.out.println(e.getMessage());
      } 
      finally {
         System.out.printf("\nPoints: %d / %d\n", points, total);
         if (file != null) {
            file.close();
         }
      }
   }
   
  
   private static boolean multiLineMatch(String actualFull, String[] expected) {
      String[] actual = actualFull.split("\\n");
      
      for (int iexpected = 0; iexpected < expected.length; iexpected++) {
      
         // check for insufficient output in actual
         if (iexpected == actual.length) {
            System.out.printf(" failed: expected more output. After %d lines\n\tExpected: \"%s\"\n", iexpected, expected[iexpected]);
            return false;
         }
         
         if (!actual[iexpected].equalsIgnoreCase(expected[iexpected])) {
            System.out.printf(" failure after %d lines:\n\texpected: \"%s\"\n\t  Actual: \"%s\"\n", iexpected, expected[iexpected], actual[iexpected]);
            return false;
         }
      }
      
      if (actual.length > expected.length) {
         System.out.printf(" failed: actual output was too long. %d lines okay.\n\tUnexpected: \"%s\"\n", expected.length, actual[expected.length]);
         return false;
      }
      
      // All lines matched. Passed!
      return true;
   }
}