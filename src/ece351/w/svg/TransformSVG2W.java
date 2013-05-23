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
import java.io.PrintWriter;

public final class TransformSVG2W {
	
	/**
	 * Transforms an instance of WSVG to an instance of WProgram.
	 */
	public static final WProgram transform(final WSVG wsvg) {
		final List<Line> lines = new ArrayList<Line>(wsvg.segments);
		final List<Pin> pins = new ArrayList<Pin>(wsvg.pins);

		// sort the line segments by their Y position
		Collections.sort(lines, COMPARE_Y_X);
		Collections.sort(pins,COMPARE_Y);
		// Place holder for the list of waveforms in the final WProgram result.
		ImmutableList<Waveform> waveforms = ImmutableList.of();

		// the set of line segments for the current waveform
		final List<Line> extract = new ArrayList<Line>();
		
		// lines are taken off the global list and added to the extract list
		// then the extract list is used to construct a new waveform object 
		// finally, the extract list is cleared and the process repeats
		int midpoint_y = 0;
		while(!lines.isEmpty()) {
			final Line line = lines.remove(0);
			if(midpoint_y == 0){
				//should always start from the midpoint of the line regardless of value
				midpoint_y = line.y1;
			}else if(line.y1 >= midpoint_y+150){
				waveforms = waveforms.append(transformLinesToWaveform(extract,pins.remove(0)));
				extract.clear();
				midpoint_y = line.y1;
				//new line just started
			}
			extract.add(line);
		}
		if(!extract.isEmpty()) {
			waveforms = waveforms.append(transformLinesToWaveform(extract,pins.remove(0)));
		}

		return new WProgram(waveforms);
	}

	/**
	 * Transform a list of Line to an instance of Waveform
	 */
	private static boolean is_vertical(Line line){
		if(line.x1 == line.x2) return true;
		return false;
	}
	private static int length_in_bits(Line line){
		return (int)Math.ceil((line.x2 - line.x1)/100);
	}
	
	private static Waveform transformLinesToWaveform(final List<Line> lines, final Pin pin) {
		if(lines.isEmpty()) return null;

		// Sort by the middle of two x-coordinates.
		Collections.sort(lines, COMPARE_X);
		// Place holder for the list of bits.
		ImmutableList<String> bits = ImmutableList.of();
		// The first line of the waveform.
		// Get the corresponding id for this waveform.
		final Line first = lines.get(0);
		int midpoint = first.y1;
		String id = pin.id;
		for(int i = 1; i < lines.size(); i++) {
			final Line line = lines.get(i);
			if(is_vertical(line)){
				continue;
				//line was vertical skip it
			}else{
				if(line.y1 < midpoint){
					int num_bits = length_in_bits(line);
					for(int x = 0; x < num_bits; x++){
						bits = bits.append("1");
					}
				}
				else{
					int num_bits = length_in_bits(line);
					for(int x = 0; x < num_bits; x++){
						bits = bits.append("0");
					}
				}
			}
		}
		return new Waveform(bits, id);
	}
	public final static Comparator<Pin> COMPARE_Y = new Comparator<Pin>() {
		@Override
		public int compare(final Pin P1, final Pin P2) {
			if(P1.y < P2.y) return -1;
			if(P1.y > P2.y) return 1;
			return 0;
		}
	};
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
