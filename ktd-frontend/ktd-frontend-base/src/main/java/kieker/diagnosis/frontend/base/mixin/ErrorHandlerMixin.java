package kieker.diagnosis.frontend.base.mixin;

import kieker.diagnosis.frontend.base.common.ExceptionUtil;

public interface ErrorHandlerMixin {

	default void executeAction( final Action action ) {
		try {
			action.run( );
		} catch ( final Throwable t ) {
			final String loggerName = getClass( ).getName( );
			ExceptionUtil.handleException( t, loggerName );
		}
	}

	@FunctionalInterface
	public interface Action {

		void run( ) throws Exception;

	}

}
