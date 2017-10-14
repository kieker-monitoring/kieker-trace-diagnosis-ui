package kieker.diagnosis.service.export;

public final class CSVData {

	private String[] header;
	private String[][] values;

	public String[] getHeader( ) {
		return header;
	}

	public void setHeader( final String[] aHeader ) {
		header = aHeader;
	}

	public String[][] getValues( ) {
		return values;
	}

	public void setValues( final String[][] aValues ) {
		values = aValues;
	}

}
