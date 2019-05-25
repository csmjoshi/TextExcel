public class TextCell extends Cell {
	public TextCell(String expression) {
		super.setExpression(expression);
	}
	
	
	public String toString() {
      return getExpression().substring(1, getExpression().length() - 1);
      

   }

}