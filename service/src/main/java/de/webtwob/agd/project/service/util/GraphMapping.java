package de.webtwob.agd.project.service.util;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.eclipse.elk.graph.ElkBendPoint;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;

public class GraphMapping {

	Map<ElkNode, Pair<Rectangle2D.Double>> elkNodeMap = new HashMap<>();
	Map<ElkEdgeSection, Pair<Pair<Point2D.Double>>> elkSectionMap = new HashMap<>();
	Map<ElkBendPoint, Pair<Point2D.Double>> elkBendPointMap = new HashMap<>();

	@SuppressWarnings("exports")
	public Pair<Rectangle2D.Double> getMapping(ElkNode node) {
		return elkNodeMap.computeIfAbsent(node, key->new Pair<>(Rectangle2D.Double::new));
	}

	@SuppressWarnings("exports")
	public Pair<Pair<Point2D.Double>> getMapping(ElkEdgeSection sect) {
		return elkSectionMap.computeIfAbsent(sect,key -> new Pair<>(() -> {return new Pair<>(Double::new);}));
	}

	@SuppressWarnings("exports")
	public Pair<Point2D.Double> getMapping(ElkBendPoint bend) {
		return elkBendPointMap.computeIfAbsent(bend, key->new Pair<>(Double::new));
	}

	public static class Pair<A> {

		public Pair(Supplier<A> sup) {
			start = sup.get();
			end = sup.get();
		}
		
		public Pair() {}

		public A start;
		public A end;
	}

	public static class DummyMapping extends GraphMapping {

		@SuppressWarnings("exports")
		@Override
		public Pair<Point2D.Double> getMapping(ElkBendPoint bend) {
			Pair<Point2D.Double> pair = new Pair<>(Double::new);

			pair.start.setLocation(bend.getX(), bend.getY());
			pair.end = pair.start;

			return pair;
		}

		@SuppressWarnings("exports")
		@Override
		public Pair<Pair<Double>> getMapping(ElkEdgeSection sect) {
			Pair<Pair<Double>> pair = new Pair<>();
			pair.start = new Pair<>();
			pair.start.start = new Double(sect.getStartX(), sect.getStartY());
			pair.start.end = new Double(sect.getEndX(), sect.getEndY());
			pair.end = pair.start;
			return pair;
		}

		@SuppressWarnings("exports")
		@Override
		public Pair<Rectangle2D.Double> getMapping(ElkNode node) {
			Pair<Rectangle2D.Double> pair = new Pair<>();
			pair.start = new Rectangle2D.Double(node.getX(), node.getY(), node.getWidth(), node.getHeight());
			pair.end = pair.start;
			return pair;
		}

	}

}
