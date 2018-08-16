package br.com.ntopus.accesscontrol

import br.com.ntopus.accesscontrol.helper.ApiControllerHelper
import br.com.ntopus.accesscontrol.helper.CreateEdgeSuccess
import br.com.ntopus.accesscontrol.helper.CreatePermissionSuccess
import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexData
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.base.SUCCESSResponse
import br.com.ntopus.accesscontrol.model.vertex.mapper.AbstractMapper
import br.com.ntopus.accesscontrol.model.vertex.mapper.VertexInfo
import br.com.ntopus.accesscontrol.schema.importer.JanusGraphSchemaImporter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.apache.tinkerpop.gremlin.structure.Direction
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.test.context.junit4.SpringRunner
import java.text.SimpleDateFormat
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiControllerAccessGroupTests: ApiControllerHelper() {
    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @LocalServerPort
    private val port: Int = 0

    private val date: Date = Date()

    @Before
    fun setup() {
        GraphFactory.setInstance("janusgraph-inmemory.properties")
        val graph = GraphFactory.open()
        JanusGraphSchemaImporter().writeGraphSONSchema(graph, ClassPathResource("schema.json").file.absolutePath)
        this.createDefaultAccessGroup(date)
        this.createDefaultRules(Date())
    }

    @Test
    fun createAccessGroup() {
        val gson = Gson()
        val properties:List<Property> = listOf(Property("code", "2"),
                Property("name", "New Access Group"),
                Property("description", "This is a description"),
                Property("enable", "false"))
        val accessGroup = VertexData("accessGroup", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", accessGroup, String::class.java)
        val obj = gson.fromJson(response.body, CreatePermissionSuccess::class.java)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val finalDate = format.parse(obj.data.creationDate)
        Assert.assertNotNull(finalDate)
        Assert.assertEquals("New Access Group", obj.data.name)
        Assert.assertEquals("2", obj.data.code)
        Assert.assertEquals("This is a description", obj.data.description)
        Assert.assertEquals(false, obj.data.enable)
        val g = GraphFactory.open().traversal()
        val userStorage = g.V().hasLabel("accessGroup").has("code", "2")
        val values = AbstractMapper.parseMapVertex(userStorage)
        Assert.assertEquals("New Access Group", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("2", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a description", AbstractMapper.parseMapValue(values["description"].toString()))
        Assert.assertEquals(false, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    fun createAccessGroupWithExtraProperty() {
        val gson = Gson()
        val properties:List<Property> = listOf(Property("code", "2"),
                Property("name", "New Access Group"),
                Property("description", "This is a description"),
                Property("enable", "false"),
                Property("observation", "This is a test"))
        val accessGroup = VertexData("accessGroup", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", accessGroup, String::class.java)
        val obj = gson.fromJson(response.body, CreatePermissionSuccess::class.java)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val finalDate = format.parse(obj.data.creationDate)
        Assert.assertNotNull(finalDate)
        Assert.assertEquals("New Access Group", obj.data.name)
        Assert.assertEquals("2", obj.data.code)
        Assert.assertEquals("This is a description", obj.data.description)
        Assert.assertEquals(false, obj.data.enable)
        val g = GraphFactory.open().traversal()
        val userStorage = g.V().hasLabel("accessGroup").has("code", "2")
        val values = AbstractMapper.parseMapVertex(userStorage)
        Assert.assertEquals("New Access Group", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("2", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a description", AbstractMapper.parseMapValue(values["description"].toString()))
        Assert.assertEquals(false, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
        Assert.assertEquals("", AbstractMapper.parseMapValue(values["observation"].toString()))
    }

    @Test
    fun cantCreateAccessGroupThatExist() {
        val gson = Gson()
        val properties: List<Property> = listOf(
                Property("code", "1"),
                Property("name", "Access Group Name"),
                Property("description", "Description Test"))
        val accessGroup = VertexData("accessGroup", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", accessGroup, String::class.java)
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@AGCVE-002 Adding this property for key [code] and value [1] violates a uniqueness constraint [vByAccessGroupCode]", obj.data)
        this.assertAccessGroupMapper("1", "Operator", "This is a Operator Access Group", date, true)
    }

    @Test
    fun cantCreateAccessGroupWithRequiredPropertiesEmpty() {
        val gson = Gson()
        val code: List<Property> = listOf(Property("code", "2"))
        val accessGroup = VertexData("accessGroup", code)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", accessGroup, String::class.java)
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@AGCVE-001 Empty Access Group properties", obj.data)
        val g = GraphFactory.open().traversal()
        val accessGroupStorage = g.V().hasLabel("accessGroup").has("code", "2")
        val values = AbstractMapper.parseMapVertex(accessGroupStorage)
        Assert.assertEquals(0, values.size)
        val name: List<Property> = listOf(Property("name", "test"))
        val accessGroup1 = VertexData("accessGroup", name)
        val response1 =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", accessGroup1, String::class.java)
        val obj1 = gson.fromJson(response1.body, FAILResponse::class.java)
        Assert.assertEquals("@AGCVE-001 Empty Access Group properties", obj1.data)
        val accessGroupStorage1 = g.V().hasLabel("accessGroup").has("code", "2")
        val values1 = AbstractMapper.parseMapVertex(accessGroupStorage1)
        Assert.assertEquals(0, values1.size)
    }

    @Test
    fun cantCreateEdgeWithRuleThatNotExist() {
        val source = VertexInfo("accessGroup", "1")
        val target = VertexInfo("rule", "3")
        val params: Map<String, Any> = hashMapOf("source" to source, "target" to target, "edgeLabel" to "inherit")
        val response =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@AGCEE-003 Impossible find Rule with code ${target.code}", obj.data)
        val g = GraphFactory.open().traversal()
        val v1 = g.V().hasLabel("accessGroup").has("code", "1")
        val v2 = g.V().hasLabel("rule").has("code", "3")
        Assert.assertFalse(v1.both().hasNext())
    }

    @Test
    fun cantCreateEdgeWithAccessGroupThatNotExist() {
        val source = VertexInfo("accessGroup", "1")
        val target = VertexInfo("accessGroup", "2")
        val params: Map<String, Any> = hashMapOf("source" to source, "target" to target, "edgeLabel" to "inherit")
        val response =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@AGCEE-003 Impossible find AccessGroup with code ${target.code}", obj.data)
        val g = GraphFactory.open().traversal()
        val v1 = g.V().hasLabel("accessGroup").has("code", "1")
        val v2 = g.V().hasLabel("accessGroup").has("code", "2")
        Assert.assertFalse(v1.both().hasNext())
    }

    @Test
    fun cantCreateEdgeWithIncorrectTarget() {
        val source = VertexInfo("accessGroup", "1")
        val target = VertexInfo("user", "1")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val response =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@AGCEE-001 Impossible create this edge with target code ${target.code}", obj.data)
        val g = GraphFactory.open().traversal()
        val v1 = g.V().hasLabel("accessRule").has("code", "1")
        val v2 = g.V().hasLabel("user").has("code", "1")
        Assert.assertFalse(v1.out().hasNext())
        Assert.assertFalse(v2.`in`().hasNext())
    }

    @Test
    fun createAddEdge() {
        val source = VertexInfo("accessGroup", "1")
        val target1 = VertexInfo("rule", "1")
        val params1: Map<String, Any> = hashMapOf("source" to source, "target" to target1, "edgeLabel" to "add")
        val edgeResponse1 =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params1, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val edgeObj1: CreateEdgeSuccess = gson.fromJson(edgeResponse1.body, CreateEdgeSuccess::class.java)
        this.assertEdgeCreatedSuccess(source, target1, edgeObj1, "add")
        val g = GraphFactory.open().traversal()
        val eSource1 = g.V().hasLabel("accessGroup").has("code", "1").next()
        Assert.assertTrue(eSource1.edges(Direction.OUT, "add").hasNext())
        val eTarget1 = g.V().hasLabel("rule").has("code", "1").next()
        Assert.assertTrue(eTarget1.edges(Direction.IN, "add").hasNext())
    }

    @Test
    fun createAddEdgeWithoutSetEdgeLabel() {
        val source = VertexInfo("accessGroup", "1")
        val target2 = VertexInfo("rule", "2")
        val params2: Map<String, Any> = hashMapOf("source" to source, "target" to target2)
        val edgeResponse2 =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params2, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val edgeObj2: CreateEdgeSuccess = gson.fromJson(edgeResponse2.body, CreateEdgeSuccess::class.java)
        this.assertEdgeCreatedSuccess(source, target2, edgeObj2, "add")
        val g = GraphFactory.open().traversal()
        val eSource2 = g.V().hasLabel("accessGroup").has("code", "1").next()
        Assert.assertTrue(eSource2.edges(Direction.OUT, "add").hasNext())
        val eTarget2 = g.V().hasLabel("rule").has("code", "2").next()
        Assert.assertTrue(eTarget2.edges(Direction.IN, "add").hasNext())
    }

    @Test
    fun createRemoveEdge() {
        val source = VertexInfo("accessGroup", "1")
        val target = VertexInfo("rule", "1")
        val params: Map<String, Any> = hashMapOf("source" to source, "target" to target, "edgeLabel" to "remove")
        val edgeResponse =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val edgeObj: CreateEdgeSuccess = gson.fromJson(edgeResponse.body, CreateEdgeSuccess::class.java)
        this.assertEdgeCreatedSuccess(source, target, edgeObj, "remove")
        val g = GraphFactory.open().traversal()
        val edgeOrganization = g.V().hasLabel("accessGroup").has("code", "1").next()
        Assert.assertTrue(edgeOrganization.edges(Direction.OUT, "remove").hasNext())
        val edgeUnitOrganization = g.V().hasLabel("rule").has("code", "1").next()
        Assert.assertTrue(edgeUnitOrganization.edges(Direction.IN, "remove").hasNext())
    }

    fun createNewAccessGroup() {
        val graph = GraphFactory.open()
        try {
            val unitOrganization = graph.addVertex(VertexLabel.ACCESS_GROUP.label)
            unitOrganization.property(PropertyLabel.NAME.label, "Administrator")
            unitOrganization.property(PropertyLabel.CODE.label, 2)
            unitOrganization.property(PropertyLabel.DESCRIPTION.label, "This is a Admin Access Group")
            unitOrganization.property(PropertyLabel.CREATION_DATE.label, Date())
            unitOrganization.property(PropertyLabel.ENABLE.label, true)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
        }
    }

    @Test
    fun createInheritEdge() {
        this.createNewAccessGroup()
        val source = VertexInfo("accessGroup", "1")
        val target = VertexInfo("accessGroup", "2")
        val params: Map<String, Any> = hashMapOf("source" to source, "target" to target)
        val edgeResponse =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val edgeObj: CreateEdgeSuccess = gson.fromJson(edgeResponse.body, CreateEdgeSuccess::class.java)
        this.assertEdgeCreatedSuccess(source, target, edgeObj, "inherit")
        val g = GraphFactory.open().traversal()
        val eSource = g.V().hasLabel("accessGroup").has("code", "1").next()
        Assert.assertTrue(eSource.edges(Direction.OUT, "inherit").hasNext())
        val eTarget = g.V().hasLabel("accessGroup").has("code", "2").next()
        Assert.assertTrue(eTarget.edges(Direction.IN, "inherit").hasNext())
    }

    @Test
    fun updateProperty() {
        val properties : List<Property> = listOf(Property("name", "Operator Updated"), Property("description", "Property updated"))
        val requestUpdate = HttpEntity(properties)
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/updateProperty/accessGroup/1", HttpMethod.PUT, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val objResponse = gson.fromJson(response.body, CreatePermissionSuccess::class.java)
        this.assertAccessGroupResponseSuccess("1", "Operator Updated",true, date, "Property updated", objResponse)
        this.assertAccessGroupMapper("1", "Operator Updated", "Property updated", date, true)
    }

    @Test
    fun cantUpdateAccessGroupDefaultProperty() {
        val properties : List<Property> = listOf(Property("name", "Operator Updated"), Property("code", "2"))
        val requestUpdate = HttpEntity(properties)
        val response = restTemplate.exchange(
                "${this.createVertexBaseUrl(this.port)}/updateProperty/accessGroup/1", HttpMethod.PUT,
                requestUpdate, String::class.java
        )
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@AGUPE-002 Access Group property can be updated", obj.data)
    }

    @Test
    fun cantUpdateAccessGroupThatNotExist() {
        val properties : List<Property> = listOf(Property("name", "Operator Updated"), Property("description", "Property updated"))
        val requestUpdate = HttpEntity(properties)
        val response = restTemplate.exchange(
                "${this.createVertexBaseUrl(this.port)}/updateProperty/accessGroup/2", HttpMethod.PUT,
                requestUpdate, String::class.java
        )
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("AGUPE-001 Impossible find Access Group with code 2", obj.data)
    }

    @Test
    fun deleteAccessGroup() {
        val requestUpdate = HttpEntity("accessGroup")
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/delete/1", HttpMethod.DELETE, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: SUCCESSResponse = gson.fromJson(response.body, SUCCESSResponse::class.java)
        Assert.assertEquals("SUCCESS", obj.status)
        Assert.assertEquals(null, obj.data)
        this.assertAccessGroupMapper("1", "Operator", "This is a Operator Access Group", date, false)
    }

    @Test
    fun cantDeleteOrganizationThatNotExist() {
        val requestUpdate = HttpEntity("accessGroup")
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/delete/2", HttpMethod.DELETE, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@AGDE-001 Impossible find Access Group with code 2", obj.data)
    }
}
