import java.util.*;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class DateCell extends Cell {
	public DateCell(String date) {
		this.setExpression(date);
	}
	
	
	public String toString() { 
		SimpleDateFormat date = new SimpleDateFormat("MM/dd/yy");
		try {
			return DateFormat.getDateInstance().format(date.parse(this.getExpression()));
		} catch(ParseException e) {
			e.printStackTrace();
		}
		
		
		return "You have entered an invalid date";
	}
}
