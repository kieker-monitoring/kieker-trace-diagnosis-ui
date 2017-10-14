package kieker.diagnosis.architecture.ui;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Test class for {@link EnumStringConverter}.
 *
 * @author Nils Christian Ehmke
 */
public class EnumStringConverterTest {

	private final EnumStringConverter<TestEnum> ivConverter = new EnumStringConverter<>( TestEnum.class );

	@Test
	public void toStringShouldWork( ) {
		assertThat( ivConverter.toString( TestEnum.ENUM_VALUE_1 ), is( "Value 1" ) );
	}

	@Test
	public void toStringForNullValueShouldWork( ) {
		assertThat( ivConverter.toString( null ), is( "" ) );
	}

	@Test
	public void fromStringShouldWork( ) {
		assertThat( ivConverter.fromString( "Value 2" ), is( TestEnum.ENUM_VALUE_2 ) );
	}

	@Test
	public void fromStringForEmptyStringShouldWork( ) {
		assertThat( ivConverter.fromString( "" ), is( nullValue( ) ) );
	}

}

enum TestEnum {

	ENUM_VALUE_1, ENUM_VALUE_2

}