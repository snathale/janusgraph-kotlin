package br.com.ntopus.accesscontrol

import br.com.ntopus.accesscontrol.helper.ApiControllerHelper
import br.com.ntopus.accesscontrol.helper.CreateAgentSuccess
import br.com.ntopus.accesscontrol.helper.CreateEdgeSuccess
import br.com.ntopus.accesscontrol.helper.IVertexTests
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
class ApiControllerGroupTests: ApiControllerHelper(), IVertexTests {
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
    override fun createVertex() {
        val gson = Gson()
        val properties:List<Property> = listOf(Property("code", "2"),
                Property("name", "RH"),
                Property("observation", "This is a RH Group"))
        val group = VertexData("group", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", group, String::class.java)
        val obj: CreateAgentSuccess = gson.fromJson(response.body, CreateAgentSuccess::class.java)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val finalDate = format.parse(obj.data.creationDate)
        Assert.assertEquals(200, response.statusCode.value())
        Assert.assertEquals("SUCCESS", obj.status)
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
    override fun createVertexWithExtraProperty() {
        val gson = Gson()
        val properties:List<Property> = listOf(Property("code", "2"),
                Property("name", "New Group"),
                Property("description", "This is a description"),
                Property("observation", "This is a observation"))
        val group = VertexData("group", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", group, String::class.java)
        val obj: CreateAgentSuccess = gson.fromJson(response.body, CreateAgentSuccess::class.java)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val finalDate = format.parse(obj.data.creationDate)
        Assert.assertNotNull(finalDate)
        Assert.assertEquals(200, response.statusCode.value())
        Assert.assertEquals("SUCCESS", obj.status)
        Assert.assertEquals("New Group", obj.data.name)
        Assert.assertEquals("2", obj.data.code)
        Assert.assertEquals("This is a observation", obj.data.observation)
        Assert.assertEquals(true, obj.data.enable)
        val g = GraphFactory.open().traversal()
        val userStorage = g.V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, "2")
        val values = AbstractMapper.parseMapVertex(userStorage)
        Assert.assertEquals("New Group", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("2", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a observation", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals("", AbstractMapper.parseMapValue(values["description"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    override fun cantCeateVertexThatExist() {
        val gson = Gson()
        val properties: List<Property> = listOf(Property("code", "1"), Property("name", "Test"))
        val group = VertexData("group", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", group, String::class.java)
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@GCVE-002 Adding this property for key [code] and value [1] violates a uniqueness constraint [vByGroupCode]", obj.data)
        Assert.assertEquals(404, response.statusCode.value())
        Assert.assertEquals("FAIL", obj.status)
        val g = GraphFactory.open().traversal()
        val groupStorage = g.V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, "1")
        val values = AbstractMapper.parseMapVertex(groupStorage)
        Assert.assertEquals(format.format(date), AbstractMapper.parseMapValueDate(values["creationDate"].toString()))
        Assert.assertEquals("Marketing", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("1", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a Marketing Group", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    override fun cantCreateVertexWithRequiredPropertyEmpty() {
        val gson = Gson()
        val code: List<Property> = listOf(Property("code", "2"))
        val group = VertexData("group", code)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", group, String::class.java)
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@GCVE-001 Empty Group properties", obj.data)
        Assert.assertEquals(404, response.statusCode.value())
        Assert.assertEquals("FAIL", obj.status)
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
    override fun cantCreateEdgeWithSourceThatNotExist() {
        val source = VertexInfo("group", "2")
        val target = VertexInfo("group", "1")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val response =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals(404, response.statusCode.value())
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@GCEE-002 Impossible find Group with code ${source.code}", obj.data)
        val g = GraphFactory.open().traversal()
        val group = g.V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, "2")
        val values = AbstractMapper.parseMapVertex(group)
        Assert.assertEquals(0, values.size)
    }

    @Test
    override fun cantCreateEdgeWithTargetThatNotExist() {
        val source = VertexInfo("group", "1")
        val target = VertexInfo("group", "2")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val response =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals(404, response.statusCode.value())
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@GCEE-003 Impossible find Group with code ${target.code}", obj.data)
        val g = GraphFactory.open().traversal()
        val group = g.V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, "2")
        val values = AbstractMapper.parseMapVertex(group)
        Assert.assertEquals(0, values.size)
    }

    @Test
    override fun cantCreateEdgeWithIncorrectTarget() {
        val source = VertexInfo("group", "1")
        val target = VertexInfo("user", "1")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val response =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals(404, response.statusCode.value())
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@GCEE-001 Impossible create edge with target code ${target.code}", obj.data)
        val g = GraphFactory.open().traversal()
        val group = g.V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, "2")
        Assert.assertFalse(group.both().hasNext())
    }

    @Test
    override fun createEdge() {
        this.createNewGroup()
        val source = VertexInfo("group", "1")
        val target = VertexInfo("group", "2")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val response =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj = gson.fromJson(response.body, CreateEdgeSuccess::class.java)
        Assert.assertEquals(200, response.statusCode.value())
        this.assertEdgeCreatedSuccess(source, target, obj, "has")
        val g = GraphFactory.open().traversal()
        val eSource = g.V().hasLabel("group").has("code", "1").next()
        Assert.assertTrue(eSource.edges(Direction.OUT, "has").hasNext())
        val eTarget = g.V().hasLabel("group").has("code", "2").next()
        Assert.assertTrue(eTarget.edges(Direction.IN, "has").hasNext())
    }

    @Test
    override fun updateProperty() {
        val properties : List<Property> = listOf(Property("name", "Group Test"), Property("observation", "Property updated"))
        val requestUpdate = HttpEntity(properties)
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/updateProperty/group/1", HttpMethod.PUT, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: CreateAgentSuccess = gson.fromJson(response.body, CreateAgentSuccess::class.java)
        Assert.assertNotNull(obj.data.creationDate)
        Assert.assertEquals(200, response.statusCode.value())
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
    override fun cantUpdateDefaultProperty() {
        val properties : List<Property> = listOf(Property("name", "Group Test"), Property("code", "2"))
        val requestUpdate = HttpEntity(properties)
        val response = restTemplate.exchange(
                "${this.createVertexBaseUrl(this.port)}/updateProperty/group/1", HttpMethod.PUT,
                requestUpdate, String::class.java
        )
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals(404, response.statusCode.value())
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@GUPE-002 Group property can be updated", obj.data)
    }

    @Test
    override fun cantUpdatePropertyFromVertexThatNotExist() {
        val properties : List<Property> = listOf(Property("name", "Group Test"), Property("enable", "false"))
        val requestUpdate = HttpEntity(properties)
        val response = restTemplate.exchange(
                "${this.createVertexBaseUrl(this.port)}/updateProperty/group/2", HttpMethod.PUT,
                requestUpdate, String::class.java
        )
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals(404, response.statusCode.value())
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@GUPE-001 Impossible find Group with code 2", obj.data)
    }

    @Test
    override fun deleteVertex() {
        val requestUpdate = HttpEntity("group")
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/delete/1", HttpMethod.DELETE, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: SUCCESSResponse = gson.fromJson(response.body, SUCCESSResponse::class.java)
        Assert.assertEquals(200, response.statusCode.value())
        Assert.assertEquals("SUCCESS", obj.status)
        Assert.assertEquals(null, obj.data)
        val g = GraphFactory.open().traversal()
        val organizationStorage = g.V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, "1")
        val values = AbstractMapper.parseMapVertex(organizationStorage)
        Assert.assertEquals(format.format(date), AbstractMapper.parseMapValueDate(values["creationDate"].toString()))
        Assert.assertEquals("Marketing", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("1", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a Marketing Group", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(false, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    override fun cantDeleteVertexThatNotExist() {
        val requestUpdate = HttpEntity("group")
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/delete/2", HttpMethod.DELETE, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals(404, response.statusCode.value())
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@GDE-001 Impossible find Group with code 2", obj.data)
    }

    fun createNewGroup() {
        val graph = GraphFactory.open()
        try {
            val group1 = graph.addVertex(VertexLabel.GROUP.label)
            group1.property(PropertyLabel.NAME.label, "RH")
            group1.property(PropertyLabel.CODE.label, 2)
            group1.property(PropertyLabel.OBSERVATION.label, "This is a RH Group")
            group1.property(PropertyLabel.CREATION_DATE.label, Date())
            group1.property(PropertyLabel.ENABLE.label, true)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
        }
    }
}