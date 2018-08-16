package br.com.ntopus.accesscontrol

import br.com.ntopus.accesscontrol.helper.ApiControllerHelper
import br.com.ntopus.accesscontrol.helper.CreatePermissionSuccess
import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.VertexData
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.mapper.AbstractMapper
import br.com.ntopus.accesscontrol.schema.importer.JanusGraphSchemaImporter
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.junit4.SpringRunner
import java.text.SimpleDateFormat
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiControllerRuleTests: ApiControllerHelper() {
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
        this.createDefaultRules(date)
    }

    @Test
    fun createRule() {
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
        Assert.assertEquals("REMOVE_USER", obj.data.name)
        Assert.assertEquals("3", obj.data.code)
        Assert.assertEquals("This is a Rule Remove User", obj.data.description)
        Assert.assertEquals(true, obj.data.enable)
        val g = GraphFactory.open().traversal()
        val userStorage = g.V().hasLabel("rule").has("code", "3")
        val values = AbstractMapper.parseMapVertex(userStorage)
        Assert.assertEquals("REMOVE_USER", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("3", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a Rule Remove User", AbstractMapper.parseMapValue(values["description"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    fun createAccessGroupWithExtraProperty() {
        val gson = Gson()
        val properties:List<Property> = listOf(Property("code", "3"),
                Property("name", "REMOVE_USER"),
                Property("description", "This is a description"),
                Property("enable", "false"),
                Property("observation", "This is a test"))
        val rule = VertexData("rule", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", rule, String::class.java)
        val obj = gson.fromJson(response.body, CreatePermissionSuccess::class.java)
        Assert.assertEquals("REMOVE_USER", obj.data.name)
        Assert.assertEquals("3", obj.data.code)
        Assert.assertEquals("This is a description", obj.data.description)
        Assert.assertEquals(false, obj.data.enable)
        val g = GraphFactory.open().traversal()
        val vertex = g.V().hasLabel("rule").has("code", "3")
        val values = AbstractMapper.parseMapVertex(vertex)
        Assert.assertEquals("REMOVE_USER", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("3", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a description", AbstractMapper.parseMapValue(values["description"].toString()))
        Assert.assertEquals(false, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
        Assert.assertEquals("", AbstractMapper.parseMapValue(values["observation"].toString()))
    }

    @Test
    fun cantCreateRuleThatExist() {
        val gson = Gson()
        val properties: List<Property> = listOf(
                Property("code", "1"),
                Property("name", "REMOVE_USER"),
                Property("description", "Description Test"))
        val rule = VertexData("rule", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", rule, String::class.java)
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@RCVE-002 Adding this property for key [code] and value [1] violates a uniqueness constraint [vByRuleCode]", obj.data)
        this.assertRuleMapper("1", "ADD_USER", "This is a Rule Add User", date, true)
    }
}