package br.com.ntopus.accesscontrol

import br.com.ntopus.accesscontrol.helper.*
import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.VertexData
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.base.SUCCESSResponse
import br.com.ntopus.accesscontrol.model.vertex.mapper.AbstractMapper
import br.com.ntopus.accesscontrol.model.vertex.mapper.VertexInfo
import br.com.ntopus.accesscontrol.schema.importer.JanusGraphSchemaImporter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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
class ApiControllerRuleTests: ApiControllerHelper(), IVertexTests {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @LocalServerPort
    private val port: Int = 0

    private val date: Date = Date()

    private val graph = GraphFactory.setInstance("janusgraph-inmemory.properties")

    private var ruleId: Long = 0

    @Before
    fun setup() {
        JanusGraphSchemaImporter().writeGraphSONSchema(graph.open(), ClassPathResource("schema.json").file.absolutePath)
        this.ruleId = this.createDefaultRules(date)!!
    }

    @Test
    override fun getVertex() {
        val gson = Gson()
        val response =  restTemplate.getForEntity("${this.createVertexBaseUrl(this.port)}/?id=${this.ruleId}",  String::class.java)
        val obj = gson.fromJson(response.body, VertexSuccess::class.java)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val creationDate = format.parse(obj.data["creationDate"].toString())
        Assert.assertEquals(200, response.statusCode.value())
        Assert.assertEquals(this.ruleId.toString(), obj.data["id"])
        Assert.assertEquals("ADD_USER", obj.data["name"])
        Assert.assertEquals("1", obj.data["code"])
        Assert.assertEquals("This is a Rule Add User", obj.data["description"])
        Assert.assertEquals(true, obj.data["enable"]!!.toBoolean())
        Assert.assertEquals(format.format(date), format.format(creationDate))
    }

    @Test
    override fun createVertex() {
        val gson = Gson()
        val properties:List<Property> = listOf(Property("code", "3"),
                Property("name", "REMOVE_USER"),
                Property("description", "This is a Rule Remove User"),
                Property("enable", "true"))
        val rule = VertexData("rule", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", rule, String::class.java)
        val obj = gson.fromJson(response.body, CreatePermissionSuccess::class.java)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val finalDate = format.parse(obj.data.creationDate)
        Assert.assertNotNull(finalDate)
        Assert.assertEquals(200, response.statusCode.value())
        Assert.assertEquals("SUCCESS", obj.status)
        Assert.assertEquals("REMOVE_USER", obj.data.name)
        Assert.assertEquals("3", obj.data.code)
        Assert.assertEquals("This is a Rule Remove User", obj.data.description)
        Assert.assertEquals(true, obj.data.enable)
        val g = GraphFactory.open().traversal()
        val userStorage = g.V().hasLabel("rule").has("code", "3").next()
        val values = AbstractMapper.parseMapVertex(userStorage)
        Assert.assertEquals("REMOVE_USER", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("3", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a Rule Remove User", AbstractMapper.parseMapValue(values["description"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    override fun createVertexWithExtraProperty() {
        val gson = Gson()
        val properties:List<Property> = listOf(Property("code", "3"),
                Property("name", "REMOVE_USER"),
                Property("description", "This is a description"),
                Property("enable", "false"),
                Property("observation", "This is a test"))
        val rule = VertexData("rule", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", rule, String::class.java)
        val obj = gson.fromJson(response.body, CreatePermissionSuccess::class.java)
        Assert.assertEquals(200, response.statusCode.value())
        Assert.assertEquals("SUCCESS", obj.status)
        Assert.assertEquals("REMOVE_USER", obj.data.name)
        Assert.assertEquals("3", obj.data.code)
        Assert.assertEquals("This is a description", obj.data.description)
        Assert.assertEquals(false, obj.data.enable)
        val g = GraphFactory.open().traversal()
        val vertex = g.V().hasLabel("rule").has("code", "3").next()
        val values = AbstractMapper.parseMapVertex(vertex)
        Assert.assertEquals("REMOVE_USER", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("3", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a description", AbstractMapper.parseMapValue(values["description"].toString()))
        Assert.assertEquals(false, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
        Assert.assertEquals("", AbstractMapper.parseMapValue(values["observation"].toString()))
    }

    @Test
    override fun cantCreateVertexThatExist() {
        val gson = Gson()
        val properties: List<Property> = listOf(
                Property("code", "1"),
                Property("name", "REMOVE_USER"),
                Property("description", "Description Test"))
        val rule = VertexData("rule", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", rule, String::class.java)
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals(404, response.statusCode.value())
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@RCVE-002 Adding this property for key [code] and value [1] violates a uniqueness constraint [vByRuleCode]", obj.data)
    }

    @Test
    override fun cantCreateVertexWithRequiredPropertyEmpty() {
        val gson = Gson()
        val properties: List<Property> = listOf(
                Property("name", "REMOVE_USER"),
                Property("description", "Description Test"))
        val rule = VertexData("rule", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", rule, String::class.java)
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals(404, response.statusCode.value())
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@RCVE-001 Empty Rule properties", obj.data)
    }

    override fun cantCreateEdgeWithSourceThatNotExist() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cantCreateEdgeWithTargetThatNotExist() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cantCreateEdgeWithIncorrectTarget() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @Test
    override fun createEdge() {
        val source = VertexInfo("rule", "1")
        val target = VertexInfo("user", "1")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val response =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals(404, response.statusCode.value())
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@RCEE-001 Impossible create a edge with target code 1", obj.data)
    }

    @Test
    override fun updateProperty() {
        val properties : List<Property> = listOf(Property("name", "Test"), Property("description", "Property updated"))
        val requestUpdate = HttpEntity(properties)
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/updateProperty/rule/1", HttpMethod.PUT, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj = gson.fromJson(response.body, CreatePermissionSuccess::class.java)
        Assert.assertEquals(200, response.statusCode.value())
        println("-------->ID ${this.ruleId}")
        this.assertRuleApiResponseSuccess("1", "Test", "Property updated", true, date, obj)
        this.assertRuleMapper("1", "Test", "Property updated", date, true)
    }

    @Test
    override fun cantUpdateDefaultProperty() {
        val properties : List<Property> = listOf(Property("name", "Test"), Property("code", "2"))
        val requestUpdate = HttpEntity(properties)
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/updateProperty/rule/1", HttpMethod.PUT, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals(404, response.statusCode.value())
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@RUPE-002 Rule property can be updated", obj.data)
    }

    @Test
    override fun cantUpdatePropertyFromVertexThatNotExist() {
        val properties : List<Property> = listOf(Property("name", "Test"))
        val requestUpdate = HttpEntity(properties)
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/updateProperty/rule/3", HttpMethod.PUT, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals(404, response.statusCode.value())
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("RUPE-001 Impossible find Rule with code 3", obj.data)
    }

    @Test
    override fun deleteVertex() {
        val requestUpdate = HttpEntity("rule")
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/delete/1", HttpMethod.DELETE, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: SUCCESSResponse = gson.fromJson(response.body, SUCCESSResponse::class.java)
        Assert.assertEquals(200, response.statusCode.value())
        Assert.assertEquals("SUCCESS", obj.status)
        Assert.assertEquals(null, obj.data)
        this.assertRuleMapper("1", "ADD_USER", "This is a Rule Add User", date, false)
    }

    @Test
    override fun cantDeleteVertexThatNotExist() {
        val requestUpdate = HttpEntity("rule")
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/delete/3", HttpMethod.DELETE, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals(404, response.statusCode.value())
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@RDE-001 Impossible find Rule with code 3", obj.data)
    }
}