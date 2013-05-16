package ece351.w.svg;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

final class Pin {
	
	final static String TextX = "x";
	final static String TextY = "y";

	final String id;
	final int x, y;

	Pin(final String id, final int x, final int y) {
		this.id = id;
		this.x = x;
		this.y = y;
		assert repOk();
	}
	
	public boolean repOk() {
		assert id != null : "id is null";
		return true;
	}

	@Override
	public String toString() {
		return id + " (" + x + "," + y + ")";
	}
	
	public String toSVG() {
		return "<text x=\"" + x + "\" y=\"" + y + "\">" + id + "</text>";
	}
	
	public static String toSVG(final String id, final int x, final int y) {
		final Pin p = new Pin(id, x, y);
		return p.toSVG();
	}
	
	public static Pin fromSVG(final Node node) {
		final String nodeName = node.getNodeName();
		if (nodeName.equals("text") && node.hasAttributes() && node.getChildNodes().getLength() == 1) {
			final NamedNodeMap nnMap = node.getAttributes();
			if (nnMap.getNamedItem(TextX) != null && nnMap.getNamedItem(TextY) != null && node.getFirstChild().getNodeValue() != null) {
				final int x = Integer.parseInt(nnMap.getNamedItem(TextX).getNodeValue());
				final int y = Integer.parseInt(nnMap.getNamedItem(TextY).getNodeValue());
				return new Pin(node.getFirstChild().getNodeValue(), x, y);
			}
		}
		return null;
	}

}
