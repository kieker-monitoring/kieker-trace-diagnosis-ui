package kieker.diagnosis.czi;

import java.util.ArrayList;
import java.util.List;

import kieker.diagnosis.domain.DatabaseOperationCall;

/**
 * Helper methods for formatting statements and other text
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
		formattedString = formattedString.replaceAll("VALUES", "\r\nVALUES");

		return formattedString;
	}

	public static String insertParametersIntoPreparedStatment(
			DatabaseOperationCall call) {

		final String statement = call.getStringClassArgs().toUpperCase();
		StringBuilder sb = new StringBuilder(statement);
		List<Integer> parameterSubstitutionPositions = new ArrayList<Integer>();
		List<String> parameterValues = new ArrayList<String>();
		final char substitutedChar = '?';

		// count parameters within statement
		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) == substitutedChar) {
				parameterSubstitutionPositions.add(i);
				parameterValues.add(null);
			}
		}

		// TODO add validation checks
		// creates key-value-pairs consisting parameterIndex and value
		for (DatabaseOperationCall childCall : call.getChildren()) {
			String[] keyValuePair = childCall.getStringClassArgs().split(",");

			// check if key-value-pair exists
			// TODO check if (>= 1 value) is possible
			if (keyValuePair.length == 2) {
				int parameterIndex = Integer.parseInt(keyValuePair[0]) - 1;
				if ((parameterIndex >= 0)
						&& (parameterIndex <= parameterSubstitutionPositions
								.size())) {
					final String value = keyValuePair[1];
					parameterValues.set(parameterIndex, value);
				}
			}
		}

		// injects parameters into statement string
		for (int i = 0; i < parameterValues.size(); i++) {
			int nextSubstitutePostion = sb.toString().indexOf(substitutedChar);
			final String value = parameterValues.get(i);

			if (nextSubstitutePostion > -1) {
				sb.replace(nextSubstitutePostion, nextSubstitutePostion
						+ String.valueOf(substitutedChar).length(), value);
			}
		}

		return sb.toString();
	}
}
