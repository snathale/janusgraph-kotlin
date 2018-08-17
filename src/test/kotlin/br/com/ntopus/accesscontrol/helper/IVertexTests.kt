package br.com.ntopus.accesscontrol.helper

interface IVertexTests {
    fun createVertex()
    fun createVertexWithExtraProperty()
    fun cantCeateVertexThatExist()
    fun cantCreateVertexWithRequiredPropertyEmpty()
    fun cantCreateEdgeWithSourceThatNotExist()
    fun cantCreateEdgeWithTargetThatNotExist()
    fun cantCreateEdgeWithIncorrectTarget()
    fun createEdge()
    fun updateProperty()
    fun cantUpdateDefaultProperty()
    fun cantUpdatePropertyFromVertexThatNotExist()
    fun deleteVertex()
    fun cantDeleteVertexThatNotExist()
}