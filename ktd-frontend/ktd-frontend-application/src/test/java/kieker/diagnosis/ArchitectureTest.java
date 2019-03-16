/***************************************************************************
 * Copyright 2015-2019 Kieker Project (http://kieker-monitoring.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package kieker.diagnosis;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import org.junit.jupiter.api.Test;

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
import com.tngtech.archunit.library.dependencies.SliceRule;

import kieker.diagnosis.backend.base.service.Service;
import kieker.diagnosis.backend.data.MonitoringLogService;
import kieker.diagnosis.backend.properties.ApplicationProperty;
import kieker.diagnosis.backend.properties.PropertiesService;
import kieker.diagnosis.frontend.base.common.DelegateException;

public final class ArchitectureTest {

	@Test
	public void servicesShouldHaveNoState( ) {
		final JavaClasses importedClasses = new ClassFileImporter( ).importPackages( "kieker.diagnosis" );

		// Currently MonitoringLogService and PropertiesService are an exception from the rule. We should change this in
		// the future though.
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
						.filter( javaField -> !javaField.getModifiers( ).contains( JavaModifier.FINAL ) )
						.filter( javaField -> !javaField.isAnnotatedWith( Inject.class ) )
						.map( javaField -> String.format( "Field %s is neither static, final, nor injected", javaField.getFullName( ) ) )
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
	public void servicesShouldNotBeFinal( ) {
		// Final classes cannot be proxied
		final JavaClasses importedClasses = new ClassFileImporter( ).importPackages( "kieker.diagnosis" );

		final ArchRule rule = classes( ).that( )
				.implement( Service.class )
				.should( )
				.notHaveModifier( JavaModifier.FINAL );

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
				.whereLayer( "Frontend" ).mayNotBeAccessedByAnyLayer( )
				.whereLayer( "Backend" ).mayOnlyBeAccessedByLayers( "Frontend" );

		rule.check( importedClasses );
	}

	@Test
	public void packagesShouldBeFreeOfCycles( ) {
		final JavaClasses importedClasses = new ClassFileImporter( ).importPackages( "kieker.diagnosis" );

		final SliceRule rule = slices( ).matching( "kieker.diagnosis.(*).." )
				.should( ).beFreeOfCycles( );

		rule.check( importedClasses );
	}

	@Test
	public void delegateExceptionShouldNotBeUsedInTheBackend( ) {
		final JavaClasses importedClasses = new ClassFileImporter( ).importPackages( "kieker.diagnosis" );

		final ArchRule rule = classes( ).that( )
				.resideInAnyPackage( "kieker.diagnosis.backend.." )
				.should( notUseDelegateExceptions( ) );

		rule.check( importedClasses );
	}

	private ArchCondition<JavaClass> notUseDelegateExceptions( ) {
		return new ArchCondition<>( "not use DelegateExceptions" ) {

			@Override
			public void check( final JavaClass item, final ConditionEvents events ) {
				item.getCallsFromSelf( )
						.stream( )
						.filter( access -> access.getTargetOwner( ).isAssignableTo( DelegateException.class ) )
						.map( access -> String.format( "Class %s uses a DelegateException", access.getOriginOwner( ).getName( ) ) )
						.map( message -> SimpleConditionEvent.violated( item, message ) )
						.forEach( events::add );
			}
		};
	}

}
