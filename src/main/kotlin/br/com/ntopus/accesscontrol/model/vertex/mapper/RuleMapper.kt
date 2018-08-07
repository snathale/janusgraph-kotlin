package br.com.ntopus.accesscontrol.model.vertex.mapper

import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.interfaces.VertexInfo
import br.com.ntopus.accesscontrol.model.vertex.Rule
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import br.com.ntopus.accesscontrol.model.vertex.base.SUCCESSResponse
import br.com.ntopus.accesscontrol.model.vertex.validator.RuleValidator
import org.janusgraph.core.JanusGraph

class RuleMapper (val properties: Map<String, String>, val graph: JanusGraph): IMapper {
    private val rule = Rule(properties)
    override fun insert(): JSONResponse {
        try {
            if (!RuleValidator().beforeInsert(this.rule)) {
                throw Exception("Empty Rule properties")
            }
            val rule = graph.addVertex(VertexLabel.RULE.label)
            rule.property(PropertyLabel.NAME.label, this.rule.name)
            rule.property(PropertyLabel.CODE.label, this.rule.code)
            rule.property(PropertyLabel.CREATION_DATE.label, this.rule.creationDate)
            rule.property(PropertyLabel.DESCRIPTION.label, this.rule.description)
            rule.property(PropertyLabel.ENABLE.label, true)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = e.message.toString())
        }
        return SUCCESSResponse(data = this)
    }

    override fun updateProperty(vertex: VertexInfo, property: Property): JSONResponse {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createEdge(target: VertexInfo): JSONResponse {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(vertex: VertexInfo, code: String): JSONResponse {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}