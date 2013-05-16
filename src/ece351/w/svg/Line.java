package ece351.w.svg;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

final class Line {
	
	final static String LineX1 = "x1";
	final static String LineY1 = "y1";
	final static String LineX2 = "x2";
	final static String LineY2 = "y2";
	
	final int x1, x2, y1, y2;

	Line(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		assert repOk();
	}
	
	public boolean repOk() {
		// forum posts suggest that some code should be here
		return true;
	}

	@Override
	public String toString() {
		return "(" + x1 + "," + y1 + "," + x2 + "," + y2 + ")";
	}
	
	public String toSVG() {
		return "<line x1=\"" + x1 + "\" y1=\"" + y1 + "\" x2=\""
				+ x2 + "\" y2=\"" + y2 + "\" />";
	}

	public static String toSVG(final int x1, final int y1, final int x2, final int y2) {
		final Line line = new Line(x1, y1, x2, y2);
		return line.toSVG();
	}
	
	public static Line fromSVG(final Node node) {
		final String nodeName = node.getNodeName();
		if (nodeName.equals("line")) {
			final NamedNodeMap nnMap = node.getAttributes();
			if (nnMap.getNamedItem(LineX1) != null && nnMap.getNamedItem(LineY1) != null
					&& nnMap.getNamedItem(LineX2) != null && nnMap.getNamedItem(LineY2) != null) {
				final int x1, y1, x2, y2;
				x1 = Integer.parseInt(nnMap.getNamedItem(LineX1).getNodeValue());
				y1 = Integer.parseInt(nnMap.getNamedItem(LineY1).getNodeValue());
				x2 = Integer.parseInt(nnMap.getNamedItem(LineX2).getNodeValue());
				y2 = Integer.parseInt(nnMap.getNamedItem(LineY2).getNodeValue());
				return new Line(x1, y1, x2, y2);
			}
		}
		return null;
	}

}
