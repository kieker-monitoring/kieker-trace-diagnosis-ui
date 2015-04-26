package kieker.diagnosis.mainview.subview.util;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import javafx.beans.NamedArg;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;

public class DurationCellValueFactory implements Callback<CellDataFeatures<?, String>, ObservableValue<String>> {

	private final DataModel dataModel = DataModel.getInstance();
	private final PropertiesModel propertiesModel = PropertiesModel.getInstance();

	private final String property;

	public DurationCellValueFactory(@NamedArg(value = "property") final String property) {
		this.property = property.substring(0, 1).toUpperCase() + property.substring(1);
	}

	@Override
	public ObservableValue<String> call(final CellDataFeatures<?, String> call) {
		try {
			final TimeUnit srcTimeUnit = this.dataModel.getTimeUnit();
			final TimeUnit dstTimeUnit = this.propertiesModel.getTimeUnit();

			final Method getter = call.getValue().getClass().getMethod("get" + this.property, new Class<?>[0]);
			final long duration = (long) getter.invoke(call.getValue(), new Object[0]);

			final long newDuration = dstTimeUnit.convert(duration, srcTimeUnit);
			return new ReadOnlyObjectWrapper<String>(newDuration + " " + NameConverter.toShortTimeUnit(dstTimeUnit));
		} catch (final Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
