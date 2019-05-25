
import java.util.*;

public class NumberCell extends Cell {

	public NumberCell(String val) {
		this.setExpression(val);
	}

	public boolean setExpression(String expression) {
		super.setExpression(expression.trim());
		return true;
	}

	public String toString() {
		return new Double(this.getValue()).toString();
	}

	public double getValue() {
		String formula = getExpression();
		if (formula.contains("avg")) {

			formula = formula.replaceAll("\\( ", "").replaceAll(" \\)", "");
			
			String[] tokens = GridBase.smartSplit(formula);

			System.out.println(Arrays.toString(tokens));

			return avgRange(Integer.parseInt(tokens[1].substring(1, 2)) - 1, tokens[1].charAt(0) - 97,
					Integer.parseInt(tokens[3].substring(1, 2)) - 1, tokens[3].charAt(0) - 97,
					sumRange(Integer.parseInt(tokens[1].substring(1, 2)) - 1, tokens[1].charAt(0) - 97,
							Integer.parseInt(tokens[3].substring(1, 2)) - 1, tokens[3].charAt(0) - 97));
		} else if (formula.contains("sum")) {
			formula = formula.replaceAll("\\( ", "").replaceAll(" \\)", "");
			String[] tokens = GridBase.smartSplit(formula);

			return sumRange(Integer.parseInt(tokens[1].substring(1, 2)) - 1, tokens[1].charAt(0) - 97,
					Integer.parseInt(tokens[3].substring(1, 2)) - 1, tokens[3].charAt(0) - 97);
		}

		if (formula.contains("(")) {
			formula = formula.substring(formula.indexOf("(") + 1, formula.length() - 1).trim();
		}
		// The formula is not a simple numerical value like 1
		if (formula.length() > 1) {
			return clarifyExpression(GridBase.smartSplit(formula));
		} else {
			return Double.parseDouble(formula);
		}
	}

	private double clarifyExpression(String[] exp) {
		Grid z = (Grid) GridBase.grid;
		for (int i = 0; i < exp.length; i++) {
			String reference = exp[i];
			if (Character.isAlphabetic(exp[i].charAt(0))) {
				int row = Integer.parseInt(reference.substring(1, reference.length())) - 1;
				int col = reference.charAt(0) - 97;
				if (z.matrix[row][col].toString().equals("")) {
					exp[i] = "" + 0.0;
				} else {
					exp[i] = "" + z.matrix[row][col].toString();
				}
			}
		}

		return solveExpression(exp);
	}

	private double solveExpression(String[] exp) {
		double a = Double.parseDouble(exp[0]);
		for (int i = 1; i < exp.length - 1; i += 2) {
			a = calculate(a, exp[i], Double.parseDouble(exp[i + 1]));
		}
		return a;
	}

	private double calculate(double pre, String operator, double post) {

		if (operator.equals("+")) {
			return pre + post;
		} else if (operator.equals("-")) {
			return pre - post;
		} else if (operator.equals("*")) {
			return pre * post;
		} else if (operator.equals("/")) {
			return pre / post;
		}

		return 0.0;
	}

	private double sumRange(int startRow, int startCol, int endRow, int endCol) {
		double sum = 0.0;
		Grid abc = (Grid) GridBase.grid;
		if (startRow == endRow) {
			// it is a row swipe
			for (int i = startCol; i <= endCol; i++) {
				sum += abc.matrix[startRow][i].getValue();
			}

		} else {
			// it is a col shittt
			for (int i = startRow; i <= endRow; i++) {
				sum += abc.matrix[i][startCol].getValue();
			}

		}
		return sum;
	}

	private double avgRange(int startRow, int startCol, int endRow, int endCol, double sum) {
		if (startRow == endRow) {
			return sum / (endCol - startCol + 1);
		} else {
			return sum / (endRow - startRow + 1);
		}
	}

}