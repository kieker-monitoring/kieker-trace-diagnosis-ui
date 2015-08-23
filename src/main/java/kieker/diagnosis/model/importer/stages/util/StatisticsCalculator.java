package kieker.diagnosis.model.importer.stages.util;

import java.util.Collections;
import java.util.List;

public class StatisticsCalculator {

	public static Statistics calculateStatistics(final List<Long> durations) {
		Collections.sort(durations);

		long totalDuration = 0;
		for (final Long duration : durations) {
			totalDuration += duration;
		}

		final long minDuration = durations.get(0);
		final long maxDuration = durations.get(durations.size() - 1);
		final long meanDuration = totalDuration / durations.size();
		final long medianDuration = durations.get(durations.size() / 2);

		return new Statistics(totalDuration, meanDuration, medianDuration, minDuration, maxDuration);
	}

}
