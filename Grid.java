import java.io.*;
import java.util.*;

public class Grid extends GridBase {
	public int rows = 10;
	public int cols = 7;
	public int cellWidth = 9;
	public Cell[][] matrix;

	public Grid() {
		matrix = new Cell[rows][cols];
		intiailizeMatrix();
	}

	private void intiailizeMatrix() {
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				matrix[r][c] = new Cell();
			}
		}
	}

	public String processCommand(String input) {
		String[] tokens = input.split(" ");
		if (tokens[0].equalsIgnoreCase("rows") || tokens[0].equalsIgnoreCase("cols")
				|| tokens[0].equalsIgnoreCase("width")) {
			return handleSizingCommands(input, tokens);
		} else if (input.equalsIgnoreCase("print")) {
			return toString();
		} else if (input.equalsIgnoreCase("Clear")) {
			matrix = new Cell[rows][cols];
			intiailizeMatrix();
			return "done";
		} else if (input.contains("save")) {
			return saveFile(tokens[1]);
		} else if (input.contains("clear")) {
			// parse the second token
			this.matrix[Integer.parseInt(tokens[1].substring(1, 2)) - 1][tokens[1].charAt(0) - 97] = new Cell();
			return "Done!";
		} else if (input.contains("sorta")) {
			System.out.println(Arrays.toString(tokens));
			sortA(Integer.parseInt(tokens[1].substring(1, 2)) - 1, tokens[1].charAt(0) - 97,
					Integer.parseInt(tokens[3].substring(1, 2)) - 1, tokens[3].charAt(0) - 97);

			return "Done!";
		} else if (input.contains("sortd")) {
			sortD(Integer.parseInt(tokens[1].substring(1, 2)) - 1, tokens[1].charAt(0) - 97,
					Integer.parseInt(tokens[3].substring(1, 2)) - 1, tokens[3].charAt(0) - 97);
			
			return "Done!";
		}

		return handleCellCommands(input, tokens);

	}

	private String saveFile(String filename) {
		try {
			PrintStream printer = new PrintStream(new File(filename));
			printer.println("rows = " + rows);
			printer.println("cols = " + cols);
			printer.println("width = " + cellWidth);
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					Cell s = matrix[r][c];
					if (s instanceof TextCell || s instanceof DateCell || s instanceof NumberCell) {
						char d = (char) ('a' + c);
						printer.println((Character.toString(d)) + (r + 1) + " = " + s.getExpression());
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + filename);
		}

		return "Done!";
	}

	private String handleSizingCommands(String input, String[] tokens) {
		if (input.equalsIgnoreCase("Rows")) {
			return rows + "";
		} else if (input.equalsIgnoreCase("Cols")) {
			return cols + "";
		} else if (input.equalsIgnoreCase("Width")) {
			return cellWidth + "";
		} else if (input.contains("rows =")) {
			rows = Integer.parseInt(tokens[2]);
			matrix = new Cell[rows][cols];
			intiailizeMatrix();
			return rows + "";
		} else if (input.contains("cols =")) {
			cols = Integer.parseInt(tokens[2]);
			matrix = new Cell[rows][cols];
			intiailizeMatrix();
			return cols + "";
		} else if (input.contains("width =")) {
			cellWidth = Integer.parseInt(tokens[2]);
			return cellWidth + "";
		}
		return "";
	}

	private String handleCellCommands(String input, String[] tokens) {
		if (tokens.length == 1) {
			input = input.toLowerCase();
			return matrix[Integer.parseInt(input.substring(1)) - 1][input.charAt(0) - 97].getExpression();
		} else if (tokens.length == 2 && tokens[0].equalsIgnoreCase("value")) {
			tokens[1] = tokens[1].toLowerCase();
			return matrix[Integer.parseInt(tokens[1].substring(1, 2)) - 1][tokens[1].charAt(0) - 97].toString();
		} else if (input.contains("=")) {
			tokens[0] = tokens[0].toLowerCase();

			return setCell(Integer.parseInt(tokens[0].substring(1, 2)) - 1, tokens[0].charAt(0) - 97,
					input.substring(input.indexOf("=") + 1).trim());
		}

		return "That is not a legal command";
	}

	private String setCell(int row, int col, String expression) {
		if (expression.startsWith("\"") && expression.endsWith("\"")) {
			matrix[row][col] = new TextCell(expression);
			return "Done!";
		} else if (expression.matches("\\d{1,2}\\/\\d{1,2}\\/\\d{2,4}")) {
			System.out.println("It is a date");
			matrix[row][col] = new DateCell(expression);
			return "Done!";
		} else {
			matrix[row][col] = new NumberCell(expression);
			return "Done!";
		}

	}

	public String toString() {
		String result = makeHeadder();
		String str = "";
		for (int rows = 0; rows < matrix.length; rows++) {
			if (rows + 1 > 9) {
				result += " " + (rows + 1) + " ";
			} else {
				result += "  " + (rows + 1) + " ";
			}
			for (int cols = 0; cols < matrix[0].length; cols++) {
				str = matrix[rows][cols].toString();
				if (str.length() > cellWidth) {
					str = str.substring(0, cellWidth);
				}

				result += String.format("%" + (-1 * (cellWidth + 1 - str.length())) + "s", "|") + str;

			}
			result += "|\n" + Divider();
		}
		return result;
	}

	public String makeHeadder() {
		String result = "    |";
		String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for (int cols = 0; cols < this.cols; cols++) {
			for (int width = 1; width < cellWidth / 2 + 1; width++) {
				result += " ";
			}
			result += letters.charAt(cols);

			for (int w2 = 1; w2 <= cellWidth - (cellWidth / 2) - 1; w2++) {
				result += " ";
			}
			result += "|";
		}
		return result + "\n" + Divider();
	}

	public String Divider() {
		String result = "----";
		for (int times = 0; times < cellWidth * cols + cols + 1; times++) {
			if (times % (cellWidth + 1) == 0) {
				result += "+";
			} else {
				result += "-";
			}
		}

		return result + "\n";
	}

	private ArrayList<Double> getRange(int startRow, int startCol, int endRow, int endCol) {
		ArrayList<Double> list = new ArrayList<Double>();

		if (startRow == endRow) {
			for (int i = startCol; i <= endCol; i++) {
				list.add(matrix[startRow][i].getValue());
			}
		} else {
			for (int i = startRow; i <= endRow; i++) {
				list.add(matrix[i][startCol].getValue());
			}
		}

		return list;
	}

	private void sortA(int startRow, int startCol, int endRow, int endCol) {
		ArrayList<Double> list = getRange(startRow, startCol, endRow, endCol);
		Collections.sort(list);
		System.out.println(list);
		int index = 0;
		if (startRow == endRow) {
			for (int i = startCol; i <= endCol; i++) {
				matrix[startRow][i] = new NumberCell(list.get(index) + "");
				index++;
			}
		} else {
			for (int i = startRow; i <= endRow; i++) {
				matrix[i][startCol] = new NumberCell(list.get(index) + "");
				index++;
			}
		}
	}

	private void sortD(int startRow, int startCol, int endRow, int endCol) {
		ArrayList<Double> list = getRange(startRow, startCol, endRow, endCol);
		Collections.sort(list);
		int index = list.size() - 1;
		if (startRow == endRow) {
			for (int i = startCol; i <= endCol; i++) {
				matrix[startRow][i] = new NumberCell(list.get(index) + "");
				index--;
			}
		} else {
			for (int i = startRow; i <= endRow; i++) {
				matrix[i][startCol] = new NumberCell(list.get(index) + "");
				index--;
			}
		}
	}

}