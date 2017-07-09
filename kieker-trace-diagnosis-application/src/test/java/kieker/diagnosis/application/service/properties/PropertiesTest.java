package kieker.diagnosis.application.service.properties;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIn.isIn;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import kieker.diagnosis.application.service.ServiceTestConfiguration;
import kieker.diagnosis.architecture.service.properties.ApplicationProperty;
import kieker.diagnosis.architecture.service.properties.SystemProperty;

@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = ServiceTestConfiguration.class )
public class PropertiesTest {

	@Autowired
	private List<ApplicationProperty<?>> ivApplicationProperties;

	@Autowired
	private List<SystemProperty<?>> ivSystemProperties;

	@Test
	public void allApplicationPropertiesShouldBeAvailable ( ) {
		assertThat ( ivApplicationProperties, hasSize ( 15 ) );
	}

	@Test
	public void allSystemPropertiesShouldBeAvailable ( ) {
		assertThat ( ivSystemProperties, hasSize ( 5 ) );
	}

	@Test
	public void applicationPropertyKeysShouldBeUnique ( ) {
		final Set<String> keys = new HashSet<> ( );

		for ( final ApplicationProperty<?> property : ivApplicationProperties ) {
			final String key = property.getKey ( );

			assertThat ( key, not ( isIn ( keys ) ) );
			keys.add ( key );
		}
	}

	@Test
	public void systemPropertyKeysShouldBeUnique ( ) {
		final Set<String> keys = new HashSet<> ( );

		for ( final SystemProperty<?> property : ivSystemProperties ) {
			final String key = property.getKey ( );

			assertThat ( key, not ( isIn ( keys ) ) );
			keys.add ( key );
		}
	}

}
