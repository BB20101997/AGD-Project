package de.webtwob.agd.project.model;

import org.eclipse.elk.graph.ElkConnectableShape;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;

//import de.webtwob.agd.s4.layouts.options.LayerBasedMetaDataProvider;

@SuppressWarnings("exports")
public class Util {
    
//    public static final int UNASSIGNED = -1;

//    public static final Comparator<ElkNode> COMPARE_POS_IN_LAYER = Comparator
//            .<ElkNode> comparingInt((ElkNode n) -> n.getProperty(LayerBasedMetaDataProvider.OUTPUTS_POS_IN_LAYER));

    /**
     * @param edge
     *            the edge to reverse
     * 
     *            This method expects a simple Edge and will swap source and target
     */
    public static void reverseEdge(ElkEdge edge) {
        replaceEnds(edge, getTarget(edge), getSource(edge));

        //TODO Metadata import 
//        edge.setProperty(LayerBasedLayoutMetadata.OUTPUTS_EDGE_REVERSED,
//                !edge.getProperty(LayerBasedLayoutMetadata.OUTPUTS_EDGE_REVERSED));
    }

    
   
   

    

    

    

    /**
     * A version of ElkGraphUtil.getTargetNode which doesn't throw if more than one Target is present
     */
    public static ElkNode getTarget(ElkEdge edge) {
        if (edge.getTargets().size() < 1) {
            throw new IllegalArgumentException("Passed Egde does not have any Targets!");
        }

        return ElkGraphUtil.connectableShapeToNode(edge.getTargets().get(0));

    }

    /**
     * A version of ElkGraphUtil.getSourceNode which doesn't throw if more than one Target is present
     */
	public static ElkNode getSource(ElkEdge edge) {
        if (edge.getSources().size() < 1) {
            throw new IllegalArgumentException("Passed Egde does not have any Sources!");
        }

        return ElkGraphUtil.connectableShapeToNode(edge.getSources().get(0));

    }

    public static boolean isSimpleEdge(ElkEdge edge) {
        return edge.getTargets().size() == 1 && edge.getSources().size() == 1;
    }

    

   
    public static void replaceEnds(ElkEdge edge, ElkConnectableShape start, ElkConnectableShape end) {

        edge.getSources().clear();
        edge.getTargets().clear();
        edge.getSources().add(start);
        edge.getTargets().add(end);
    }

}
