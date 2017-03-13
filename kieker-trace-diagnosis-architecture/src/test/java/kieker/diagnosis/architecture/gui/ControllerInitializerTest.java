package kieker.diagnosis.architecture.gui;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ReflectionUtils;

import kieker.diagnosis.architecture.ArchitectureTestConfiguration;
import kieker.diagnosis.architecture.gui.ControllerInitializer;
import kieker.diagnosis.architecture.gui.testclasses.Controller1;
import kieker.diagnosis.architecture.gui.testclasses.Controller2;
import kieker.diagnosis.architecture.gui.testclasses.View1;
import kieker.diagnosis.architecture.gui.testclasses.View2;

@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = ArchitectureTestConfiguration.class )
public class ControllerInitializerTest {

	@Rule
	public ExpectedException ivExpectedException = ExpectedException.none( );

	@Autowired
	private ControllerInitializer ivControllerInitializer;

	@Test
	public void completeResourcesShouldNotWork( ) {
		final Controller1 controller = prepareController( new Controller1( ), new View1( ) );

		ivControllerInitializer.postProcessBeforeInitialization( controller, "controller" );

		assertThat( controller.getView( ).getNode1( ), is( notNullValue( ) ) );
		assertThat( controller.getResourceBundle( ), is( notNullValue( ) ) );
	}

	@Test
	public void incompleteResourcesShouldThrowException( ) {
		final Controller2 controller = prepareController( new Controller2( ), new View2( ) );

		ivExpectedException.expect( BeanInitializationException.class );
		ivControllerInitializer.postProcessBeforeInitialization( controller, "controller" );
	}

	private <T> T prepareController( final T aController, final Object aView ) {
		final Field field = ReflectionUtils.findField( aController.getClass( ), "ivView" );
		field.setAccessible( true );
		ReflectionUtils.setField( field, aController, aView );

		return aController;
	}

}
