package kieker.diagnosis;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import org.junit.Test;

import com.google.inject.Singleton;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import kieker.diagnosis.backend.base.service.ServiceBase;
import kieker.diagnosis.backend.data.MonitoringLogService;
import kieker.diagnosis.backend.properties.PropertiesService;

public final class ArchitectureTest {

	@Test
	public void servicesShouldHaveNoState( ) {
		final JavaClasses importedClasses = new ClassFileImporter( ).importPackages( "kieker.diagnosis" );

		// Currently MonitoringLogService and PropertiesService are an exception from the rule. We should change this in the future though.
		final ArchRule rule = classes( ).that( )
				.areAssignableTo( ServiceBase.class ).and( ).areNotAssignableFrom( ServiceBase.class ).and( ).areNotAssignableFrom( MonitoringLogService.class ).and( ).areNotAssignableFrom( PropertiesService.class )
				.should( haveNoNonStaticFields( ) );

		rule.check( importedClasses );
	}

	@Test
	public void servicesShouldBeSingletons( ) {
		final JavaClasses importedClasses = new ClassFileImporter( ).importPackages( "kieker.diagnosis" );

		final ArchRule rule = classes( ).that( )
				.areAssignableTo( ServiceBase.class ).and( ).areNotAssignableFrom( ServiceBase.class )
				.should( ).beAnnotatedWith( Singleton.class );

		rule.check( importedClasses );
	}

	private ArchCondition<JavaClass> haveNoNonStaticFields( ) {
		return new ArchCondition<>( "have no non-static fields" ) {

			@Override
			public void check( final JavaClass item, final ConditionEvents events ) {
				item.getFields( )
						.stream( )
						.filter( javaField -> !javaField.getModifiers( ).contains( JavaModifier.STATIC ) )
						.map( javaField -> String.format( "Field %s is not static", javaField.getFullName( ) ) )
						.map( message -> SimpleConditionEvent.violated( item, message ) )
						.forEach( events::add );
			}
		};
	}

}
