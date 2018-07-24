package demo.schema

import org.janusgraph.core.schema.JanusGraphManagement

class GraphSchema {
    private val propertyKeys: MutableList<PropertyKeyBean> = mutableListOf<PropertyKeyBean>()
    private val vertexLabels: MutableList<VertexLabelBean> =  mutableListOf<VertexLabelBean>()
    private val edgeLabels: MutableList<EdgeLabelBean> = mutableListOf<EdgeLabelBean>()
    private val vertexIndexes: MutableList<IndexBean> = mutableListOf<IndexBean>()
    private val edgeIndexes: MutableList<IndexBean> = mutableListOf<IndexBean>()
    private val vertexCentricIndexes: MutableList<VertexCentricIndexBean> = mutableListOf<VertexCentricIndexBean>()

    /**
     * use the {@code mgmt} to create the schema
     * @param mgmt
     */
    fun make(mgmt: JanusGraphManagement) {
        //create properties
        for (property in propertyKeys) {
            property.make(mgmt)
        }

        //create vertex labels
        for (vertex in vertexLabels) {
            vertex.make(mgmt)
        }

        //create edge labels
        for (edge in edgeLabels) {
            edge.make(mgmt)
        }

        //create v indexes
        for (vindex in vertexIndexes) {
            vindex.make(mgmt, true)
        }

        //create e indexes
        for (eindex in edgeIndexes) {
            eindex.make(mgmt, false)
        }

        //create vc indexes
        for (vcindex in vertexCentricIndexes) {
            vcindex.make(mgmt)
        }

        mgmt.commit()
    }
}