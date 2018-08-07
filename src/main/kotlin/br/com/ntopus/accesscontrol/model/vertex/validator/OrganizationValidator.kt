package br.com.ntopus.accesscontrol.model.vertex.validator

import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.interfaces.VertexInfo
import br.com.ntopus.accesscontrol.model.vertex.Organization
import br.com.ntopus.accesscontrol.model.vertex.base.ICommon

class OrganizationValidator: DefaultValilator() {

    override fun beforeUpdate(properties: List<Property>): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun beforeDelete(vvertex: VertexInfo): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}