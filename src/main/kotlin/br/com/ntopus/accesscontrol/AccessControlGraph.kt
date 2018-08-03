package br.com.ntopus.accesscontrol

import br.com.ntopus.accesscontrol.model.vertex.*
import br.com.ntopus.accesscontrol.model.vertex.base.Permission
import br.com.ntopus.accesscontrol.model.vertex.base.CommonAgent
import com.syncleus.ferma.DelegatingFramedGraph
import org.janusgraph.core.JanusGraph
import org.springframework.stereotype.Component

@Component
class AccessControlGraph(graph: JanusGraph): DelegatingFramedGraph<JanusGraph>(
        graph,
        true,
        setOf(
                CommonAgent::class.java,
                Permission::class.java,
                AccessGroup::class.java,
                AccessRule::class.java,
                Group::class.java,
                Organization::class.java,
                Rule::class.java,
                UnitOrganization::class.java,
                User::class.java)
) {
}