package br.com.ntopus.accesscontrol

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexData
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.base.SUCCESSResponse
import br.com.ntopus.accesscontrol.model.vertex.mapper.AbstractMapper
import br.com.ntopus.accesscontrol.model.vertex.mapper.EdgeCreated
import br.com.ntopus.accesscontrol.model.vertex.mapper.LocalUser
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
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.io.ClassPathResource
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod


data class CreateSuccess(val status: String, val data: LocalUser)
data class CreateEdgeSuccess(val status: String, val data: EdgeCreated)
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiControllerTests {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @LocalServerPort
    private val port: Int = 0

    fun createBaseUrl(): String {
        return "http://localhost:$port/api/v1"
    }

    private val date: Date = Date()
    private val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")

    fun createDefaultUser() {
        val graph = GraphFactory.open()
        try {
            val user = graph.addVertex(VertexLabel.USER.label)
            user.property(PropertyLabel.NAME.label, "UserTest")
            user.property(PropertyLabel.CODE.label, "1")
            user.property(PropertyLabel.OBSERVATION.label, "This is UserTest")
            user.property(PropertyLabel.CREATION_DATE.label, date)
            user.property(PropertyLabel.ENABLE.label, true)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
        }
    }

    fun createDefaultAccessRule() {
        val graph = GraphFactory.open()
        try {
            val accessRule = graph.addVertex(VertexLabel.ACCESS_RULE.label)
            accessRule.property(PropertyLabel.CODE.label, "1")
            accessRule.property(PropertyLabel.ENABLE.label, true)
            accessRule.property(PropertyLabel.EXPIRATION_DATE.label, Date())
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
        }
    }

    @Before
    fun setup() {
        GraphFactory.setInstance("janusgraph-inmemory.properties")
        val graph = GraphFactory.open()
        JanusGraphSchemaImporter().writeGraphSONSchema(graph, ClassPathResource("schema.json").file.absolutePath)
        this.createDefaultUser()
        this.createDefaultAccessRule()
    }

    @Test
    fun createUser() {
        val gson = Gson()
        val initialDate = Date().time
        val properties:List<Property> = listOf(Property("code", "2"), Property("name", "test"))
        val user = VertexData("user", properties)
        val response =  restTemplate.postForEntity("${this.createBaseUrl()}/addVertex", user, String::class.java)
        val obj: CreateSuccess = gson.fromJson(response.body, CreateSuccess::class.java)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val finalDate = format.parse(obj.data.creationDate).time
        val condition = finalDate > initialDate
        Assert.assertFalse(condition)
        Assert.assertEquals("test", obj.data.name)
        Assert.assertEquals("2", obj.data.code)
        Assert.assertEquals("", obj.data.observation)
        Assert.assertEquals(true, obj.data.enable)
        val g = GraphFactory.open().traversal()
        val userStorage = g.V().hasLabel(VertexLabel.USER.label).has(PropertyLabel.CODE.label, "2")
        val values = AbstractMapper.parseMapVertex(userStorage)
        Assert.assertEquals("test", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("2", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    fun cantCreateUserWithRequiredPropertiesEmpty() {
        val gson = Gson()
        val code: List<Property> = listOf(Property("code", "2"))
        val user = VertexData("user", code)
        val response =  restTemplate.postForEntity("${this.createBaseUrl()}/addVertex", user, String::class.java)
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@UCVE-001 Empty User properties", obj.data)
        val g = GraphFactory.open().traversal()
        val userStorage = g.V().hasLabel(VertexLabel.USER.label).has(PropertyLabel.CODE.label, "2")
        val values = AbstractMapper.parseMapVertex(userStorage)
        Assert.assertEquals(0, values.size)
        val name: List<Property> = listOf(Property("name", "test"))
        val user1 = VertexData("user", name)
        val response1 =  restTemplate.postForEntity("${this.createBaseUrl()}/addVertex", user1, String::class.java)
        val obj1 = gson.fromJson(response1.body, FAILResponse::class.java)
        Assert.assertEquals("@UCVE-001 Empty User properties", obj1.data)
        val userStorage1 = g.V().hasLabel(VertexLabel.USER.label).has(PropertyLabel.CODE.label, "2")
        val values1 = AbstractMapper.parseMapVertex(userStorage1)
        Assert.assertEquals(0, values1.size)
    }

    @Test
    fun cantCreateUserThatExist() {
        val gson = Gson()
        val properties: List<Property> = listOf(Property("code", "1"), Property("name", "Test"))
        val user = VertexData("user", properties)
        val response =  restTemplate.postForEntity("${this.createBaseUrl()}/addVertex", user, String::class.java)
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@UCVE-002 Adding this property for key [code] and value [1] violates a uniqueness constraint [vByUserCode]", obj.data)
        val g = GraphFactory.open().traversal()
        val userStorage = g.V().hasLabel(VertexLabel.USER.label).has(PropertyLabel.CODE.label, "1")
        val values = AbstractMapper.parseMapVertex(userStorage)
        Assert.assertEquals("UserTest", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("1", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is UserTest", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(format.format(date), AbstractMapper.formatDate(values["creationDate"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    fun canCreateEdgeWithTargetThatNotExist() {
        val source = VertexInfo("user", "1")
        val target = VertexInfo("accessRule", "2")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val response =  restTemplate.postForEntity("${this.createBaseUrl()}/addEdge", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@UCEE-003 Impossible find Access Rule with code ${target.code}", obj.data)
        val g = GraphFactory.open().traversal()
        val accessRule = g.V().hasLabel(VertexLabel.ACCESS_RULE.label).has(PropertyLabel.CODE.label, "2")
        val values = AbstractMapper.parseMapVertex(accessRule)
        Assert.assertEquals(0, values.size)
    }

    @Test
    fun canCreateEdgeWithUserThatNotExist() {
        val source = VertexInfo("user", "2")
        val target = VertexInfo("accessRule", "1")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val response =  restTemplate.postForEntity("${this.createBaseUrl()}/addEdge", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@UCEE-002 Impossible find User with code ${source.code}", obj.data)
        val g = GraphFactory.open().traversal()
        val user = g.V().hasLabel(VertexLabel.USER.label).has(PropertyLabel.CODE.label, "2")
        val values = AbstractMapper.parseMapVertex(user)
        Assert.assertEquals(0, values.size)
    }

    @Test
    fun createUserEdge() {
        val source = VertexInfo("user", "1")
        val target = VertexInfo("accessRule", "1")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val edgeResponse =  restTemplate.postForEntity("${this.createBaseUrl()}/addEdge", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val edgeObj: CreateEdgeSuccess = gson.fromJson(edgeResponse.body, CreateEdgeSuccess::class.java)
        Assert.assertEquals("SUCCESS", edgeObj.status)
        Assert.assertEquals("user", edgeObj.data.source.label)
        Assert.assertEquals("1", edgeObj.data.source.code)
        Assert.assertEquals("accessRule", edgeObj.data.target.label)
        Assert.assertEquals("1", edgeObj.data.target.code)
        Assert.assertEquals("associated", edgeObj.data.edgeLabel)
        val g = GraphFactory.open().traversal()
        val user = g.V().hasLabel(VertexLabel.USER.label).has(PropertyLabel.CODE.label, "1")
        val userValues = AbstractMapper.parseMapVertex(user)
        Assert.assertTrue(userValues.size > 0)
        val accessRule = g.V().hasLabel(VertexLabel.ACCESS_RULE.label).has(PropertyLabel.CODE.label, "1")
        val accessRuleValues = AbstractMapper.parseMapVertex(accessRule)
        Assert.assertTrue(accessRuleValues.size > 0)
        val edgeUser = g.V().hasLabel(VertexLabel.USER.label).has(PropertyLabel.CODE.label, "1").next()
        Assert.assertTrue(edgeUser.edges(Direction.OUT, "associated").hasNext())
        val edgeAccessRule = g.V().hasLabel(VertexLabel.ACCESS_RULE.label).has(PropertyLabel.CODE.label, "1").next()
        Assert.assertTrue(edgeAccessRule.edges(Direction.IN, "associated").hasNext())
    }

    @Test
    fun updateUserProperty() {
        val user = VertexInfo("user", "1")
        val properties : List<Property> = listOf(Property("name", "Test"), Property("observation", "Property updated"))
        val params: HashMap<String, Any> = hashMapOf("vertex" to user, "properties" to properties)
        val requestUpdate = HttpEntity(params)
        val response = restTemplate.exchange("${this.createBaseUrl()}/updateVertexProperty/1", HttpMethod.PUT, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: CreateSuccess = gson.fromJson(response.body, CreateSuccess::class.java)
        Assert.assertEquals("SUCCESS", obj.status)
        Assert.assertEquals("Test", obj.data.name)
        Assert.assertEquals("1", obj.data.code)
        Assert.assertEquals("Property updated", obj.data.observation)
        Assert.assertEquals(true, obj.data.enable)
        val g = GraphFactory.open().traversal()
        val userStorage = g.V().hasLabel(VertexLabel.USER.label).has(PropertyLabel.CODE.label, "1")
        val values = AbstractMapper.parseMapVertex(userStorage)
        Assert.assertEquals("Test", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("1", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("Property updated", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    fun canUpdateUserDefaultProperty() {
        val user = VertexInfo("user", "1")
        val properties : List<Property> = listOf(Property("name", "Test"), Property("code", "2"))
        val params: HashMap<String, Any> = hashMapOf("vertex" to user, "properties" to properties)
        val requestUpdate = HttpEntity(params)
        val response = restTemplate.exchange("${this.createBaseUrl()}/updateVertexProperty/1", HttpMethod.PUT, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@UUPE-002 User property can be updated", obj.data)
    }

    @Test
    fun deleteUser() {
        val requestUpdate = HttpEntity("user")
        val response = restTemplate.exchange("${this.createBaseUrl()}/deleteVertex/1", HttpMethod.DELETE, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: SUCCESSResponse = gson.fromJson(response.body, SUCCESSResponse::class.java)
        Assert.assertEquals("SUCCESS", obj.status)
        Assert.assertEquals(null, obj.data)
        val g = GraphFactory.open().traversal()
        val userStorage = g.V().hasLabel(VertexLabel.USER.label).has(PropertyLabel.CODE.label, "1")
        val values = AbstractMapper.parseMapVertex(userStorage)
        Assert.assertEquals("UserTest", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("1", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is UserTest", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(format.format(date), AbstractMapper.formatDate(values["creationDate"].toString()))
        Assert.assertEquals(false, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    fun cantDeleteUserThatNotExist() {
        val requestUpdate = HttpEntity("user")
        val response = restTemplate.exchange("${this.createBaseUrl()}/deleteVertex/2", HttpMethod.DELETE, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@UDE-001 Impossible find User with code 2", obj.data)
    }

}