/**
* Base class that also acts as the Empty cell type.<p>
* Author: Mr. Stride
*/
public class Cell implements Comparable<Cell>{
	
   /**
   * Every cell class has an expression.
   */
	private String expression;
	
   /**
   * Constructor which sets expression to empty.
   */
	public Cell() {
		this.expression = "";
	}		
	
	/**
	* Accessor to the expression for this cell.
	*/
	public String getExpression() {
		return this.expression;
	}
	
   /**
   * Sets the expression of the cell.
   * @param expression is a string that tells the cell how to calculate its value.
   * A value can be either a number, date, or text value (label). 
   * @return true if we are successful at setting the cell's expression
   */
	public boolean setExpression(String expression) {
		this.expression = expression;
		return true;
	}
	
	/**
	* An empty cell has a zero value.
	* 
	* @return 0.0 because we are an empty cell.
	*/
	public double getValue() {
		
		return 0.0;
	}
	
   /**
   * Returns the presentation of the cell's value.
   * @return "", and empty string.
   */
	@Override
	public String toString() {
		
		return "";
	}

   /**
   * This method is the implementation of the interface Comparable<T> which allows
   * the cells to be compared to one another even if they are different types. As 
   * implemented, empty cells are "less than" TextCell, DateCell and NumberCell.<p>
   * 
   * One should override this implementation in the derived classes to get proper
   * sorting. Use "public int compareTo(Cell other)" when overriding.<p>
   * 
   * Collections.sort(arr) will sort arr of type ArrayList<Cell>. <p>
   * Collections.sort(arr, Collections.reverseOrder()) will sort it in reverse.
   *
   *  @param other is the cell to which this cell is being compared to.
   *  @return -1 if this cell is less than the other cell. 0 if equal. 1 if greater than.
   */
	@Override
	public int compareTo(Cell other) {
		if (other instanceof TextCell ||
			other instanceof DateCell ||
			other instanceof NumberCell) {

			return -1;
		}
		
		// empty cell == empty cell
		return 0;
	}
}

