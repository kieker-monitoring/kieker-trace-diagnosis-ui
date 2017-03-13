package kieker.diagnosis.architecture.service.properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import kieker.diagnosis.architecture.ArchitectureTestConfiguration;
import kieker.diagnosis.architecture.service.properties.AbstractLongSystemProperty;
import kieker.diagnosis.architecture.service.properties.testclasses.LongSystemProperty;

/**
 * @author Nils Christian Ehmke
 */
@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = ArchitectureTestConfiguration.class )
public final class LongSystemPropertyTest {

	@Test
	public void deserializationShouldWork( ) {
		final AbstractLongSystemProperty property = new LongSystemProperty( );

		assertThat( property.deserialize( "42" ), is( Long.valueOf( 42L ) ) );
		assertThat( property.deserialize( "-10" ), is( Long.valueOf( -10L ) ) );
	}

}
