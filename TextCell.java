public class TextCell extends Cell {
   //Mehul Joshi
   // AP CS A 
   // TextCell.java
   public boolean setValue (String exp) {
      formula = exp;
      value = getValue();
      return true;
   }
   
   public String toString() {
      return formula.substring(1, formula.length() - 1);
   }
}
