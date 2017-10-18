package kieker.diagnosis.service.aggregatedmethods;

/**
 * This is a data transfer object holding the filter for the aggregated methods service.
 *
 * @author Nils Christian Ehmke
 */
public final class AggregatedMethodsFilter {

	private String ivHost;
	private String ivClazz;
	private String ivMethod;
	private String ivException;
	private boolean ivUseRegExpr;
	private SearchType ivSearchType = SearchType.ALL;

	public String getHost( ) {
		return ivHost;
	}

	public void setHost( final String aHost ) {
		ivHost = aHost;
	}

	public String getClazz( ) {
		return ivClazz;
	}

	public void setClazz( final String aClazz ) {
		ivClazz = aClazz;
	}

	public String getMethod( ) {
		return ivMethod;
	}

	public void setMethod( final String aMethod ) {
		ivMethod = aMethod;
	}

	public String getException( ) {
		return ivException;
	}

	public void setException( final String aException ) {
		ivException = aException;
	}

	public boolean isUseRegExpr( ) {
		return ivUseRegExpr;
	}

	public void setUseRegExpr( final boolean aUseRegExpr ) {
		ivUseRegExpr = aUseRegExpr;
	}

	public SearchType getSearchType( ) {
		return ivSearchType;
	}

	public void setSearchType( final SearchType aSearchType ) {
		ivSearchType = aSearchType;
	}

}
