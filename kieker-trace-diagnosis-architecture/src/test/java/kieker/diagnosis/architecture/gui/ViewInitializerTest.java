package kieker.diagnosis.architecture.gui;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import javafx.scene.Node;

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
import kieker.diagnosis.architecture.gui.ViewInitializer;
import kieker.diagnosis.architecture.gui.testclasses.View1;
import kieker.diagnosis.architecture.gui.testclasses.View2;

@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = ArchitectureTestConfiguration.class )
public class ViewInitializerTest {

	@Rule
	public ExpectedException ivExpectedException = ExpectedException.none( );

	@Autowired
	private ViewInitializer ivViewInitializer;

	@Test
	public void autowiredFieldsShouldBeFilled( ) {
		final View1 view = prepareView( new View1( ) );
		ivViewInitializer.initialize( view );
		assertThat( view.getNode1( ), is( notNullValue( ) ) );
	}

	@Test
	public void notAutowiredFieldsShouldNotBeFilled( ) {
		final View1 view = prepareView( new View1( ) );
		ivViewInitializer.initialize( view );
		assertThat( view.getNode2( ), is( nullValue( ) ) );
	}

	@Test
	public void notFindableFieldShouldThrowException( ) {
		final View2 view = prepareView( new View2( ) );
		ivExpectedException.expect( BeanInitializationException.class );
		ivViewInitializer.initialize( view );
	}

	private <T> T prepareView( final T aView ) {
		final Node node = mock( Node.class );
		when( node.lookup( "#node1" ) ).thenReturn( mock( Node.class ) );
		when( node.lookup( "#node2" ) ).thenReturn( mock( Node.class ) );
		when( node.lookup( "#node3" ) ).thenReturn( null );

		final Field field = ReflectionUtils.findField( aView.getClass( ), "ivNode" );
		field.setAccessible( true );
		ReflectionUtils.setField( field, aView, node );

		return aView;
	}

}
