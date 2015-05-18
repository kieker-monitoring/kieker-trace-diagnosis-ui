package kieker.diagnosis.czi;

/**
 * Helper methods for formatting text
 * 
 * @author Christian Zirkelbach
 *
 */
public class Utils {
	public static String formatSQLStatement(String s) {
		StringBuilder sb = new StringBuilder(s);
		int i = 0;
		int wrapAtPosition = 80;

		// wrap after a full word around wrapAtPosition
		while (i + wrapAtPosition < sb.length()
				&& (i = sb.lastIndexOf(" ", i + wrapAtPosition)) != -1) {
			sb.replace(i, i + 1, "\n");
		}
		
		// wrap after FROM and WHERE
		String formattedString = sb.toString();
		formattedString = formattedString.replaceAll("FROM", "\r\nFROM");
		formattedString = formattedString.replaceAll("WHERE", "\r\nWHERE");

		return formattedString;
	}
}
