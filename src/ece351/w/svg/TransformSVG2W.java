package ece351.w.svg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.parboiled.common.ImmutableList;

import ece351.w.ast.WProgram;
import ece351.w.ast.Waveform;


public final class TransformSVG2W {
	
	/**
	 * Transforms an instance of WSVG to an instance of WProgram.
	 */
	public static final WProgram transform(final WSVG wsvg) {
		final List<Line> lines = new ArrayList<Line>(wsvg.segments);
		final List<Pin> pins = new ArrayList<Pin>(wsvg.pins);

		// sort the line segments by their Y position
		Collections.sort(lines, COMPARE_Y_X);

		// Place holder for the list of waveforms in the final WProgram result.
		ImmutableList<Waveform> waveforms = ImmutableList.of();

		// the set of Y values in use for the current waveform
		final Set<Integer> setY = new LinkedHashSet<Integer>();

		// the set of line segments for the current waveform
		final List<Line> extract = new ArrayList<Line>();
		
		// lines are taken off the global list and added to the extract list
		// then the extract list is used to construct a new waveform object 
		// finally, the extract list is cleared and the process repeats
		while(!lines.isEmpty()) {
			final Line line = lines.remove(0);

// TODO: 16 lines snipped
throw new ece351.util.Todo351Exception();
		}

		// the last waveform
		if(!extract.isEmpty()) {
			waveforms = waveforms.append(transformLinesToWaveform(extract, pins));
		}

		return new WProgram(waveforms);
	}

	/**
	 * Transform a list of Line to an instance of Waveform
	 */
	private static Waveform transformLinesToWaveform(final List<Line> lines, final List<Pin> pins) {
		if(lines.isEmpty()) return null;

		// Sort by the middle of two x-coordinates.
		Collections.sort(lines, COMPARE_X);

		// Place holder for the list of bits.
		ImmutableList<String> bits = ImmutableList.of();

		// The first line of the waveform.
		final Line first = lines.get(0);

		for(int i = 1; i < lines.size(); i++) {
			// If a dot, skip it.
// TODO: 10 lines snipped
throw new ece351.util.Todo351Exception();
		}

		// Get the corresponding id for this waveform.
		String id = "UNKNOWN";
// TODO: 7 lines snipped
throw new ece351.util.Todo351Exception();

		return new Waveform(bits, id);
	}

	public final static Comparator<Line> COMPARE_X = new Comparator<Line>() {
		@Override
		public int compare(final Line l1, final Line l2) {
			if(l1.x1 < l2.x1) return -1;
			if(l1.x1 > l2.x1) return 1;
			if(l1.x2 < l2.x2) return -1;
			if(l1.x2 > l2.x2) return 1;
			return 0;
		}
	};

	/**
	 * Sort lines according to their y position first, and then x position second.
	 */
	public final static Comparator<Line> COMPARE_Y_X = new Comparator<Line>() {
		@Override
		public int compare(final Line l1, final Line l2) {
			final double y_mid1 = (double) (l1.y1 + l1.y2) / 2.0f;
			final double y_mid2 = (double) (l2.y1 + l2.y2) / 2.0f;
			final double x_mid1 = (double) (l1.x1 + l1.x2) / 2.0f;
			final double x_mid2 = (double) (l2.x1 + l2.x2) / 2.0f;
			if (y_mid1 < y_mid2) return -1;
			if (y_mid1 > y_mid2) return 1;
			if (x_mid1 < x_mid2) return -1;
			if (x_mid1 > x_mid2) return 1;
			return 0;
		}
	};

}
