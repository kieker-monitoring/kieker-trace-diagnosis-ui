package kieker.diagnosis.mainview.subview.util;

import java.lang.reflect.Method;

import javafx.beans.NamedArg;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.model.PropertiesModel.ComponentNames;

public class ComponentCellValueFactory implements Callback<CellDataFeatures<?, String>, ObservableValue<String>> {

	private final DataModel dataModel = DataModel.getInstance();
	private final PropertiesModel propertiesModel = PropertiesModel.getInstance();

	private final String property;

	public ComponentCellValueFactory(@NamedArg(value = "property") final String property) {
		this.property = property.substring(0, 1).toUpperCase() + property.substring(1);
	}

	@Override
	public ObservableValue<String> call(final CellDataFeatures<?, String> call) {
		try {
			this.dataModel.getTimeUnit();
			this.propertiesModel.getTimeUnit();

			final Method getter = call.getValue().getClass().getMethod("get" + this.property, new Class<?>[0]);
			String componentName = (String) getter.invoke(call.getValue(), new Object[0]);

			if (this.propertiesModel.getComponentNames() == ComponentNames.SHORT) {
				componentName = NameConverter.toShortComponentName(componentName);
			}

			return new ReadOnlyObjectWrapper<String>(componentName);
		} catch (final Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
