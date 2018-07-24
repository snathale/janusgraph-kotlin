package demo.schema

import com.fasterxml.jackson.databind.ObjectMapper
import org.janusgraph.core.JanusGraphTransaction
import org.janusgraph.core.schema.JanusGraphManagement
import org.janusgraph.graphdb.database.StandardJanusGraph
import java.io.File


class JanusGraphImpoter(var graph: StandardJanusGraph) {

//    fun readFile(schemaFile: String) {
//        val mgmt: JanusGraphManagement = graph.openManagement()
//
//        try {
//            parse(schemaFile)
//                    .make(mgmt)
//            rollbackTxs(graph)
//        } catch (Exception e) {
//            print "parse GSON failed: ${e.getMessage()}"
//        }
//    }
//
//    /**
//     * Commit all running transactions upon the graph
//     * @param graph
//     * @return
//     */
//    public static boolean commitTxs(JanusGraph graph) {
//        StandardJanusGraph sgraph = graph
//                try {
//                    Set<JanusGraphTransaction>txs = sgraph.getOpenTransactions()
//                    //commit all running transactions
//                    Iterator<JanusGraphTransaction> iter = txs.iterator()
//                    while (iter.hasNext()) {
//                        iter.next().commit()
//                    }
//                } catch (Exception e) {
//                    //ignore
//                    return false
//                }
//        return true
//    }
//
//    /**
//     * Rollback back all running transaction upon the graph
//     * @param graph
//     * @return
//     */
//    public static boolean rollbackTxs(JanusGraph graph) {
//        StandardJanusGraph sgraph = graph
//                try {
//                    Set<JanusGraphTransaction>txs = sgraph.getOpenTransactions()
//                    //commit all running transactions
//                    Iterator<JanusGraphTransaction> iter = txs.iterator()
//                    while (iter.hasNext()) {
//                        iter.next().rollback()
//                    }
//                } catch (Exception e) {
//                    //ignore
//                    return false
//                }
//        return true
//    }
//
//    /**
//     * Parse the graph schema definition and return a GraphSchema object
//     * if parse successes
//     * @param gsonSchemaFile
//     * @return
//     */
//    fun parse(schemaFile: String): GraphSchema {
//        val gsonFile: File = File(schemaFile)
//
//        if (!gsonFile.exists()) {
//            throw Exception("file not found:" + schemaFile)
//        }
//
//        val mapper: ObjectMapper = ObjectMapper()
//        return mapper.readValue(gsonFile, GraphSchema.class)
//    }
//
//    void make(List<ObjectNode> nodes, String name, Closure check, Closure exist, Closure create) {
//        for (node in nodes) {
//            String nameStr = node.get(name).asText()
//            if (check.call(nameStr)) {
//                exist.call(nameStr)
//            } else {
//                create.call(nameStr, node)
//            }
//        }
//    }

}