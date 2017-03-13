package kieker.diagnosis.architecture.service.properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import kieker.diagnosis.architecture.ArchitectureTestConfiguration;
import kieker.diagnosis.architecture.service.properties.AbstractCharacterApplicationProperty;
import kieker.diagnosis.architecture.service.properties.testclasses.CharacterApplicationProperty;

/**
 * @author Nils Christian Ehmke
 */
@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = ArchitectureTestConfiguration.class )
public final class CharacterApplicationPropertyTest {

	@Test
	public void serializationShouldWork( ) {
		final AbstractCharacterApplicationProperty property = new CharacterApplicationProperty( );

		assertThat( property.serialize( 'A' ), is( "A" ) );
		assertThat( property.serialize( 'Z' ), is( "Z" ) );
	}

	@Test
	public void deserializationShouldWork( ) {
		final AbstractCharacterApplicationProperty property = new CharacterApplicationProperty( );

		assertThat( property.deserialize( "A" ), is( Character.valueOf( 'A' ) ) );
		assertThat( property.deserialize( "Z" ), is( Character.valueOf( 'Z' ) ) );
	}

}
