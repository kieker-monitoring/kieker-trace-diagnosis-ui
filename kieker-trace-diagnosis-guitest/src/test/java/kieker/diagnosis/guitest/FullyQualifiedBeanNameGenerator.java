package kieker.diagnosis.guitest;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;

public class FullyQualifiedBeanNameGenerator implements BeanNameGenerator {

	@Override
	public String generateBeanName( final BeanDefinition aDefinition, final BeanDefinitionRegistry aRegistry ) {
		return aDefinition.getBeanClassName( );
	}

}
