package br.com.ntopus.accesscontrol.helper

interface IVertexTests {
    fun getVertex()
    fun createVertex()
    fun createVertexWithExtraProperty()
    fun cantCreateVertexThatExist()
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