package br.com.ntopus.accesscontrol

import br.com.ntopus.accesscontrol.helper.ApiControllerHerper
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
class ApiControllerAccessGroupTests: ApiControllerHerper() {
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
}