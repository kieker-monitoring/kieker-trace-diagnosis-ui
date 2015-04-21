package kieker.diagnosis.mainview.subview.util;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import kieker.diagnosis.domain.AbstractOperationCall;

public final class LazyOperationCallTreeItem<T extends AbstractOperationCall<T>> extends TreeItem<T> {

	private boolean childrenInitialized = false;

	public LazyOperationCallTreeItem(final T value) {
		super(value);
	}

	@Override
	public ObservableList<TreeItem<T>> getChildren() {
		if (!this.childrenInitialized) {
			this.initializeChildren();
		}

		return super.getChildren();
	}

	@Override
	public boolean isLeaf() {
		return super.getValue().getChildren().isEmpty();
	}

	private void initializeChildren() {
		this.childrenInitialized = true;

		final List<TreeItem<T>> result = new ArrayList<>();

		for (final T child : super.getValue().getChildren()) {
			result.add(new LazyOperationCallTreeItem<T>(child));
		}

		super.getChildren().setAll(result);
	}
}
