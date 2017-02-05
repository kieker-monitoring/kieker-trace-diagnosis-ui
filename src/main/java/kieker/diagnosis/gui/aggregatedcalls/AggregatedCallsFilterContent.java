package kieker.diagnosis.gui.aggregatedcalls;

class AggregatedCallsFilterContent {

	private boolean ivShowAllButton;
	private boolean ivShowJustSuccessful;
	private boolean ivShowJustFailedButton;

	private String ivFilterContainer;
	private String ivFilterComponent;
	private String ivFilterOperation;
	private String ivFilterException;

	public boolean isShowAllButton( ) {
		return ivShowAllButton;
	}

	public void setShowAllButton( final boolean showAllButton ) {
		ivShowAllButton = showAllButton;
	}

	public boolean isShowJustSuccessful( ) {
		return ivShowJustSuccessful;
	}

	public void setShowJustSuccessful( final boolean showJustSuccessful ) {
		ivShowJustSuccessful = showJustSuccessful;
	}

	public boolean isShowJustFailedButton( ) {
		return ivShowJustFailedButton;
	}

	public void setShowJustFailedButton( final boolean showJustFailedButton ) {
		ivShowJustFailedButton = showJustFailedButton;
	}

	public String getFilterContainer( ) {
		return ivFilterContainer;
	}

	public void setFilterContainer( final String filterContainer ) {
		ivFilterContainer = filterContainer;
	}

	public String getFilterComponent( ) {
		return ivFilterComponent;
	}

	public void setFilterComponent( final String filterComponent ) {
		ivFilterComponent = filterComponent;
	}

	public String getFilterOperation( ) {
		return ivFilterOperation;
	}

	public void setFilterOperation( final String filterOperation ) {
		ivFilterOperation = filterOperation;
	}

	public String getFilterException( ) {
		return ivFilterException;
	}

	public void setFilterException( final String filterException ) {
		ivFilterException = filterException;
	}

}