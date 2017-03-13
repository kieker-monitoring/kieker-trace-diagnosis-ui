package kieker.diagnosis.architecture.service.properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import kieker.diagnosis.architecture.ArchitectureTestConfiguration;
import kieker.diagnosis.architecture.service.properties.AbstractShortSystemProperty;
import kieker.diagnosis.architecture.service.properties.testclasses.ShortSystemProperty;

/**
 * @author Nils Christian Ehmke
 */
@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = ArchitectureTestConfiguration.class )
public final class ShortSystemPropertyTest {

	@Test
	public void deserializationShouldWork( ) {
		final AbstractShortSystemProperty property = new ShortSystemProperty( );

		assertThat( property.deserialize( "42" ), is( Short.valueOf( (short) 42 ) ) );
		assertThat( property.deserialize( "-10" ), is( Short.valueOf( (short) -10 ) ) );
	}

}
