package com.example.ccm.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

	/**
	 * An array of sample (dummy) items.
	 */
	public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

	static {
		// Add 3 sample items.
		addItem(new DummyItem("1", "�lgebra, Combinatoria, Toer�a N�meros"));
		addItem(new DummyItem("2", "An�lisis y Ecuaciones Diferenciales"));
		addItem(new DummyItem("3", "Educaci�n, Historia, Filosofia de las Matem�ticas"));
		addItem(new DummyItem("4", "Geometr�a y Topolog�a"));
		addItem(new DummyItem("5", "L�gica"));
		addItem(new DummyItem("6", "Geometr�a y Topolog�a"));
		addItem(new DummyItem("7", "Matem�ticas Aplicadas"));
	}

	private static void addItem(DummyItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}
	
	
	

	/**
	 * A dummy item representing a piece of content.
	 */
	public static class DummyItem {
		public String id;
		public String content;

		public DummyItem(String id, String content) {
			this.id = id;
			this.content = content;
		}

		@Override
		public String toString() {
			return content;
		}
	}
}
