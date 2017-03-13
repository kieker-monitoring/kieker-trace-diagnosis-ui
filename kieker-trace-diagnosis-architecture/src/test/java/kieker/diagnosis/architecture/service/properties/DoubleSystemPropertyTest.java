package kieker.diagnosis.architecture.service.properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import kieker.diagnosis.architecture.ArchitectureTestConfiguration;
import kieker.diagnosis.architecture.service.properties.AbstractDoubleSystemProperty;
import kieker.diagnosis.architecture.service.properties.testclasses.DoubleSystemProperty;

/**
 * @author Nils Christian Ehmke
 */
@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = ArchitectureTestConfiguration.class )
public final class DoubleSystemPropertyTest {

	@Test
	public void deserializationShouldWork( ) {
		final AbstractDoubleSystemProperty property = new DoubleSystemProperty( );

		assertThat( property.deserialize( "42.0" ), is( Double.valueOf( 42 ) ) );
		assertThat( property.deserialize( "-10." ), is( Double.valueOf( -10 ) ) );
	}

}
