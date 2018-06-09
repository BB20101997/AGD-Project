import de.webtwob.agd.project.file.JSONGraphLoader;

/**
 * Created by BB20101997 on 31. Mai. 2018.
 */
module de.webtwob.agd.project.file {
    requires org.eclipse.elk.graph;
    requires org.eclipse.elk.graph.json;
    requires gson;
    
    requires transitive de.webtwob.agd.project.api;

    exports de.webtwob.agd.project.file;
    
    provides de.webtwob.agd.project.api.IGraphLoader with JSONGraphLoader;
}
