import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
//Mehul Joshi
//AP CS A
//DateCell.java
public class DateCell extends Cell {
   //This class handles everything to do with dates
   
   public boolean setValue(String exp) {
      formula = exp;
      value = getValue();
      
      return true;
      
      
   }
   
   public String toString() {
      //Using simpleDateFormat parsing of a date in the format "MM/dd/yy" is made easy
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
      
      try { //Parses the Date using the DateFormat Object
         return DateFormat.getDateInstance().format(sdf.parse(formula));
      } catch (ParseException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return "Invalid Date: " + formula;
   }


}
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
//Mehul Joshi
//AP CS A
//DateCell.java
public class DateCell extends Cell {
   //This class handles everything to do with dates
 
   public boolean setValue(String exp) {
      formula = exp;
      value = getValue();
 
      return true;
 
 
   }
 
   public String toString() {
      //Using simpleDateFormat parsing of a date in the format "MM/dd/yy" is made easy
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
 
      try { //Parses the Date using the DateFormat Object
         return DateFormat.getDateInstance().format(sdf.parse(formula));
      } catch (ParseException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return "Invalid Date: " + formula;
   }
 
 
}
 
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
//Mehul Joshi
//AP CS A
//DateCell.java
public class DateCell extends Cell {
   //This class handles everything to do with dates
   
   public boolean setValue(String exp) {
      formula = exp;
      value = getValue();
      
      return true;
      
      
   }
   
   public String toString() {
      //Using simpleDateFormat parsing of a date in the format "MM/dd/yy" is made easy
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
      
      try { //Parses the Date using the DateFormat Object
         return DateFormat.getDateInstance().format(sdf.parse(formula));
      } catch (ParseException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return "Invalid Date: " + formula;
   }


}
