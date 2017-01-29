package kieker.diagnosis.gui.settings;

import java.util.concurrent.TimeUnit;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import kieker.diagnosis.gui.AbstractView;
import kieker.diagnosis.model.PropertiesModel.ComponentNames;
import kieker.diagnosis.model.PropertiesModel.OperationNames;
import kieker.diagnosis.model.PropertiesModel.Threshold;
import kieker.diagnosis.model.PropertiesModel.TimestampTypes;

public class SettingsDialogView extends AbstractView {

	private ComboBox<OperationNames> ivOperationNames;
	private ComboBox<ComponentNames> ivComponentNames;
	private ComboBox<Threshold> ivThresholds;
	private ComboBox<TimeUnit> ivTimeunits;
	private ComboBox<TimestampTypes> ivTimestamps;
	private CheckBox ivAdditionalLogChecks;
	private CheckBox ivActivateRegularExpressions;
	private CheckBox ivAggregateMethodCalls;
	private CheckBox ivCaseSensitive;
	private CheckBox ivPercentageCalculation;
	private CheckBox ivCacheViews;
	private CheckBox ivSearchInEntireTrace;

	public ComboBox<OperationNames> getOperationNames( ) {
		return ivOperationNames;
	}

	public void setOperationNames( final ComboBox<OperationNames> aOperationNames ) {
		ivOperationNames = aOperationNames;
	}

	public ComboBox<ComponentNames> getComponentNames( ) {
		return ivComponentNames;
	}

	public void setComponentNames( final ComboBox<ComponentNames> aComponentNames ) {
		ivComponentNames = aComponentNames;
	}

	public ComboBox<Threshold> getThresholds( ) {
		return ivThresholds;
	}

	public void setThresholds( final ComboBox<Threshold> aThresholds ) {
		ivThresholds = aThresholds;
	}

	public ComboBox<TimeUnit> getTimeunits( ) {
		return ivTimeunits;
	}

	public void setTimeunits( final ComboBox<TimeUnit> aTimeunits ) {
		ivTimeunits = aTimeunits;
	}

	public ComboBox<TimestampTypes> getTimestamps( ) {
		return ivTimestamps;
	}

	public void setTimestamps( final ComboBox<TimestampTypes> aTimestamps ) {
		ivTimestamps = aTimestamps;
	}

	public CheckBox getAdditionalLogChecks( ) {
		return ivAdditionalLogChecks;
	}

	public void setAdditionalLogChecks( final CheckBox aAdditionalLogChecks ) {
		ivAdditionalLogChecks = aAdditionalLogChecks;
	}

	public CheckBox getActivateRegularExpressions( ) {
		return ivActivateRegularExpressions;
	}

	public void setActivateRegularExpressions( final CheckBox aActivateRegularExpressions ) {
		ivActivateRegularExpressions = aActivateRegularExpressions;
	}

	public CheckBox getAggregateMethodCalls( ) {
		return ivAggregateMethodCalls;
	}

	public void setAggregateMethodCalls( final CheckBox aAggregateMethodCalls ) {
		ivAggregateMethodCalls = aAggregateMethodCalls;
	}

	public CheckBox getCaseSensitive( ) {
		return ivCaseSensitive;
	}

	public void setCaseSensitive( final CheckBox aCaseSensitive ) {
		ivCaseSensitive = aCaseSensitive;
	}

	public CheckBox getPercentageCalculation( ) {
		return ivPercentageCalculation;
	}

	public void setPercentageCalculation( final CheckBox aPercentageCalculation ) {
		ivPercentageCalculation = aPercentageCalculation;
	}

	public CheckBox getCacheViews( ) {
		return ivCacheViews;
	}

	public void setCacheViews( final CheckBox aCacheViews ) {
		ivCacheViews = aCacheViews;
	}

	public CheckBox getSearchInEntireTrace( ) {
		return ivSearchInEntireTrace;
	}

	public void setSearchInEntireTrace( final CheckBox aSearchInEntireTrace ) {
		ivSearchInEntireTrace = aSearchInEntireTrace;
	}

}
