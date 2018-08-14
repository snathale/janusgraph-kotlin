package br.com.ntopus.accesscontrol

import br.com.ntopus.accesscontrol.helper.ApiControllerHerper
import br.com.ntopus.accesscontrol.helper.CreateAgentSuccess
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
class ApiControllerGroupTests: ApiControllerHerper() {
    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @LocalServerPort
    private val port: Int = 0

    private val date: Date = Date()
    private val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")

    @Before
    fun setup() {
        GraphFactory.setInstance("janusgraph-inmemory.properties")
        val graph = GraphFactory.open()
        JanusGraphSchemaImporter().writeGraphSONSchema(graph, ClassPathResource("schema.json").file.absolutePath)
        this.createDefaultGroup(date)
    }

    @Test
    fun createGroup() {
        val gson = Gson()
        val initialDate = Date()
        val properties:List<Property> = listOf(Property("code", "2"),
                Property("name", "RH"),
                Property("observation", "This is a RH Group"))
        val group = VertexData("group", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", group, String::class.java)
        val obj: CreateAgentSuccess = gson.fromJson(response.body, CreateAgentSuccess::class.java)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val finalDate = format.parse(obj.data.creationDate)
//        Assert.assertTrue(initialDate.before(finalDate))
        Assert.assertNotNull(finalDate)
        Assert.assertEquals("RH", obj.data.name)
        Assert.assertEquals("2", obj.data.code)
        Assert.assertEquals("This is a RH Group", obj.data.observation)
        Assert.assertEquals(true, obj.data.enable)
        val g = GraphFactory.open().traversal()
        val userStorage = g.V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, "2")
        val values = AbstractMapper.parseMapVertex(userStorage)
        Assert.assertNotNull(AbstractMapper.parseMapValue(values["creationDate"].toString()))
        Assert.assertEquals("RH", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("2", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a RH Group", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    fun createGroupWithPropertyDuplicated() {
        val gson = Gson()
        val initialDate = Date()
        val properties:List<Property> = listOf(Property("code", "2"),
                Property("name", "New Group"),
                Property("name", "New Group 2"),
                Property("observation", "This is a observation"))
        val group = VertexData("group", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", group, String::class.java)
        val obj: CreateAgentSuccess = gson.fromJson(response.body, CreateAgentSuccess::class.java)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val finalDate = format.parse(obj.data.creationDate)
//        Assert.assertTrue(initialDate.before(finalDate))
        Assert.assertNotNull(finalDate)
        Assert.assertEquals("New Group 2", obj.data.name)
        Assert.assertEquals("2", obj.data.code)
        Assert.assertEquals("This is a observation", obj.data.observation)
        Assert.assertEquals(true, obj.data.enable)
        val g = GraphFactory.open().traversal()
        val userStorage = g.V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, "2")
        val values = AbstractMapper.parseMapVertex(userStorage)
        Assert.assertEquals("New Group 2", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("2", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a observation", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    fun cantCreateOrganizationWithRequiredPropertiesEmpty() {
        val gson = Gson()
        val code: List<Property> = listOf(Property("code", "2"))
        val group = VertexData("group", code)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", group, String::class.java)
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@GCVE-001 Empty Group properties", obj.data)
        val g = GraphFactory.open().traversal()
        val groupStorage = g.V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, "2")
        val values = AbstractMapper.parseMapVertex(groupStorage)
        Assert.assertEquals(0, values.size)
        val name: List<Property> = listOf(Property("name", "test"))
        val group1 = VertexData("group", name)
        val response1 =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", group1, String::class.java)
        val obj1 = gson.fromJson(response1.body, FAILResponse::class.java)
        Assert.assertEquals("@GCVE-001 Empty Group properties", obj1.data)
        val groupStorage1 = g.V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, "2")
        val values1 = AbstractMapper.parseMapVertex(groupStorage1)
        Assert.assertEquals(0, values1.size)
    }

    @Test
    fun cantCreateGroupThatExist() {
        val gson = Gson()
        val properties: List<Property> = listOf(Property("code", "1"), Property("name", "Test"))
        val group = VertexData("group", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", group, String::class.java)
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@GCVE-002 Adding this property for key [code] and value [1] violates a uniqueness constraint [vByGroupCode]", obj.data)
        val g = GraphFactory.open().traversal()
        val groupStorage = g.V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, "1")
        val values = AbstractMapper.parseMapVertex(groupStorage)
        Assert.assertEquals(format.format(date), AbstractMapper.formatDate(values["creationDate"].toString()))
        Assert.assertEquals("Marketing", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("1", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a Marketing Group", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    fun createEdgeGroup() {
        val source = VertexInfo("group", "1")
        val target = VertexInfo("test", "2")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val response =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@GCEE-001 Impossible create a edge from this vertex", obj.data)
        val g = GraphFactory.open().traversal()
        val group = g.V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, "1").next()
        Assert.assertFalse(group.edges(Direction.OUT).hasNext())
    }

    @Test
    fun updateGroupProperty() {
        val properties : List<Property> = listOf(Property("name", "Group Test"), Property("observation", "Property updated"))
        val requestUpdate = HttpEntity(properties)
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/updateProperty/group/1", HttpMethod.PUT, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: CreateAgentSuccess = gson.fromJson(response.body, CreateAgentSuccess::class.java)
        Assert.assertNotNull(obj.data.creationDate)
        Assert.assertEquals("SUCCESS", obj.status)
        Assert.assertEquals("Group Test", obj.data.name)
        Assert.assertEquals("1", obj.data.code)
        Assert.assertEquals("Property updated", obj.data.observation)
        Assert.assertEquals(true, obj.data.enable)
        val g = GraphFactory.open().traversal()
        val organizationStorage = g.V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, "1")
        val values = AbstractMapper.parseMapVertex(organizationStorage)
        Assert.assertNotNull(AbstractMapper.parseMapValue(values["creationDate"].toString()))
        Assert.assertEquals("Group Test", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("1", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("Property updated", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    fun canUpdateGroupDefaultProperty() {
        val properties : List<Property> = listOf(Property("name", "Group Test"), Property("code", "2"))
        val requestUpdate = HttpEntity(properties)
        val response = restTemplate.exchange(
                "${this.createVertexBaseUrl(this.port)}/updateProperty/group/1", HttpMethod.PUT,
                requestUpdate, String::class.java
        )
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@GUPE-002 Group property can be updated", obj.data)
    }

    @Test
    fun deleteGroup() {
        val requestUpdate = HttpEntity("group")
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/delete/1", HttpMethod.DELETE, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: SUCCESSResponse = gson.fromJson(response.body, SUCCESSResponse::class.java)
        Assert.assertEquals("SUCCESS", obj.status)
        Assert.assertEquals(null, obj.data)
        val g = GraphFactory.open().traversal()
        val organizationStorage = g.V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, "1")
        val values = AbstractMapper.parseMapVertex(organizationStorage)
        Assert.assertEquals(format.format(date), AbstractMapper.formatDate(values["creationDate"].toString()))
        Assert.assertEquals("Marketing", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("1", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a Marketing Group", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(false, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    fun cantDeleteOrganizationThatNotExist() {
        val requestUpdate = HttpEntity("group")
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/delete/2", HttpMethod.DELETE, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@GDE-001 Impossible find Group with code 2", obj.data)
    }
}