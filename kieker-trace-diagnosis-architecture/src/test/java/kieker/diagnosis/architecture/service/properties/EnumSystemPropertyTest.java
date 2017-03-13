package kieker.diagnosis.architecture.service.properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import kieker.diagnosis.architecture.ArchitectureTestConfiguration;
import kieker.diagnosis.architecture.service.properties.AbstractEnumSystemProperty;
import kieker.diagnosis.architecture.service.properties.testclasses.EnumSystemProperty;

/**
 * @author Nils Christian Ehmke
 */
@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = ArchitectureTestConfiguration.class )
public final class EnumSystemPropertyTest {

	@Test
	public void deserializationShouldWork( ) {
		final AbstractEnumSystemProperty<TimeUnit> property = new EnumSystemProperty( );

		assertThat( property.deserialize( "DAYS" ), is( TimeUnit.DAYS ) );
		assertThat( property.deserialize( "MILLISECONDS" ), is( TimeUnit.MILLISECONDS ) );
	}

}
