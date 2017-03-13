package kieker.diagnosis.architecture.service.properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import kieker.diagnosis.architecture.ArchitectureTestConfiguration;
import kieker.diagnosis.architecture.service.properties.AbstractCharacterSystemProperty;
import kieker.diagnosis.architecture.service.properties.testclasses.CharacterSystemProperty;

/**
 * @author Nils Christian Ehmke
 */
@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = ArchitectureTestConfiguration.class )
public final class CharacterSystemPropertyTest {

	@Test
	public void deserializationShouldWork( ) {
		final AbstractCharacterSystemProperty property = new CharacterSystemProperty( );

		assertThat( property.deserialize( "A" ), is( Character.valueOf( 'A' ) ) );
		assertThat( property.deserialize( "Z" ), is( Character.valueOf( 'Z' ) ) );
	}

}
