package kieker.diagnosis;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import org.junit.Test;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.library.Architectures.LayeredArchitecture;

import kieker.diagnosis.backend.base.service.Service;
import kieker.diagnosis.backend.data.MonitoringLogService;
import kieker.diagnosis.backend.properties.ApplicationProperty;
import kieker.diagnosis.backend.properties.PropertiesService;
import kieker.diagnosis.frontend.base.ui.ControllerBase;
import kieker.diagnosis.frontend.base.ui.ViewBase;
import kieker.diagnosis.frontend.base.ui.ViewModelBase;

public final class ArchitectureTest {

	@Test
	public void controllersShouldBeSingletons( ) {
		final JavaClasses importedClasses = new ClassFileImporter( ).importPackages( "kieker.diagnosis" );

		final ArchRule rule = classes( ).that( )
				.areAssignableTo( ControllerBase.class ).and( ).dontHaveModifier( JavaModifier.ABSTRACT )
				.should( ).beAnnotatedWith( Singleton.class );

		rule.check( importedClasses );
	}

	@Test
	public void viewsShouldBeSingletons( ) {
		final JavaClasses importedClasses = new ClassFileImporter( ).importPackages( "kieker.diagnosis" );

		final ArchRule rule = classes( ).that( )
				.areAssignableTo( ViewBase.class ).and( ).dontHaveModifier( JavaModifier.ABSTRACT )
				.should( ).beAnnotatedWith( Singleton.class );

		rule.check( importedClasses );
	}

	@Test
	public void viewModelsShouldBeSingletons( ) {
		final JavaClasses importedClasses = new ClassFileImporter( ).importPackages( "kieker.diagnosis" );

		final ArchRule rule = classes( ).that( )
				.areAssignableTo( ViewModelBase.class ).and( ).dontHaveModifier( JavaModifier.ABSTRACT )
				.should( ).beAnnotatedWith( Singleton.class );

		rule.check( importedClasses );
	}

	@Test
	public void servicesShouldHaveNoState( ) {
		final JavaClasses importedClasses = new ClassFileImporter( ).importPackages( "kieker.diagnosis" );

		// Currently MonitoringLogService and PropertiesService are an exception from the rule. We should change this in the future though.
		final ArchRule rule = classes( ).that( )
				.implement( Service.class ).and( ).areNotAssignableFrom( MonitoringLogService.class ).and( ).areNotAssignableFrom( PropertiesService.class )
				.should( haveNoStatefulFields( ) );

		rule.check( importedClasses );
	}

	private ArchCondition<JavaClass> haveNoStatefulFields( ) {
		return new ArchCondition<>( "have no stateful fields" ) {

			@Override
			public void check( final JavaClass item, final ConditionEvents events ) {
				item.getFields( )
						.stream( )
						.filter( javaField -> !javaField.getModifiers( ).contains( JavaModifier.STATIC ) )
						.filter( javaField -> !javaField.isAnnotatedWith( Inject.class ) )
						.map( javaField -> String.format( "Field %s is neither static nor injected", javaField.getFullName( ) ) )
						.map( message -> SimpleConditionEvent.violated( item, message ) )
						.forEach( events::add );
			}
		};
	}

	@Test
	public void servicesShouldBeSingletons( ) {
		final JavaClasses importedClasses = new ClassFileImporter( ).importPackages( "kieker.diagnosis" );

		final ArchRule rule = classes( ).that( )
				.implement( Service.class ).and( ).dontHaveModifier( JavaModifier.ABSTRACT )
				.should( ).beAnnotatedWith( Singleton.class );

		rule.check( importedClasses );
	}

	@Test
	public void applicationPropertiesShouldBeSingletons( ) {
		final JavaClasses importedClasses = new ClassFileImporter( ).importPackages( "kieker.diagnosis" );

		final ArchRule rule = classes( ).that( )
				.areAssignableTo( ApplicationProperty.class ).and( ).dontHaveModifier( JavaModifier.ABSTRACT )
				.should( ).beAnnotatedWith( Singleton.class );

		rule.check( importedClasses );
	}

	@Test
	public void backendShouldNotUseFrontend( ) {
		final JavaClasses importedClasses = new ClassFileImporter( ).importPackages( "kieker.diagnosis" );

		final LayeredArchitecture rule = layeredArchitecture( )
				.layer( "Backend" ).definedBy( "kieker.diagnosis.backend.." )
				.layer( "Frontend" ).definedBy( "kieker.diagnosis.frontend.." )
				.layer( "Frontend-Old" ).definedBy( "kieker.diagnosis.ui.." )
				.whereLayer( "Frontend" ).mayOnlyBeAccessedByLayers( "Frontend-Old" )
				.whereLayer( "Frontend-Old" ).mayOnlyBeAccessedByLayers( "Frontend" )
				.whereLayer( "Backend" ).mayOnlyBeAccessedByLayers( "Frontend", "Frontend-Old" );

		rule.check( importedClasses );
	}

}
