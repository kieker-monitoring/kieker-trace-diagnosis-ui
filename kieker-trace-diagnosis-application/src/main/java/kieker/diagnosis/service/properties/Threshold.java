package kieker.diagnosis.service.properties;

/**
 * @author Nils Christian Ehmke
 */
public enum Threshold {

	THRESHOLD_0_5( 0.5f ), THRESHOLD_1( 1f ), THRESHOLD_10( 10f ), THRESHOLD_20( 20f ), THRESHOLD_30( 30f ), THRESHOLD_40( 40f ), THRESHOLD_50( 50f );

	private final float ivPercent;

	private Threshold( final float aPercent ) {
		ivPercent = aPercent;
	}

	public float getPercent( ) {
		return ivPercent;
	}

}