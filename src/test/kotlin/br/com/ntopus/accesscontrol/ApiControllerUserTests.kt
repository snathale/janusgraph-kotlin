package br.com.ntopus.accesscontrol

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexData
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.mapper.EdgeCreated
import br.com.ntopus.accesscontrol.model.vertex.mapper.LocalUser
import br.com.ntopus.accesscontrol.model.vertex.mapper.VertexInfo
import br.com.ntopus.accesscontrol.schema.importer.JanusGraphSchemaImporter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.apache.tinkerpop.gremlin.structure.Vertex
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

    fun createDefaultUser() {
        val graph = GraphFactory.open()
        try {
            val user = graph.addVertex(VertexLabel.USER.label)
            user.property(PropertyLabel.NAME.label, "UserTest")
            user.property(PropertyLabel.CODE.label, "1")
            user.property(PropertyLabel.OBSERVATION.label, "This is UserTest")
            user.property(PropertyLabel.CREATION_DATE.label, Date())
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
        Assert.assertEquals("1", obj.data.code)
        Assert.assertEquals("null", obj.data.observation)
        Assert.assertEquals(true, obj.data.enable)
    }

    @Test
    fun cantCreateUserWithRequiredPropertiesEmpty() {
        val gson = Gson()
        val code: List<Property> = listOf(Property("code", "2"))
        val user = VertexData("user", code)
        val response =  restTemplate.postForEntity("${this.createBaseUrl()}/addVertex", user, String::class.java)
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@UCVE-001 Empty User properties", obj.data)
        val name: List<Property> = listOf(Property("name", "test"))
        val user1 = VertexData("user", name)
        val response1 =  restTemplate.postForEntity("${this.createBaseUrl()}/addVertex", user1, String::class.java)
        val obj1 = gson.fromJson(response1.body, FAILResponse::class.java)
        Assert.assertEquals("@UCVE-001 Empty User properties", obj1.data)
    }

    @Test
    fun cantCreateUserThatExist() {
        val gson = Gson()
        val properties: List<Property> = listOf(Property("code", "1"), Property("name", "UserTest"))
        val user = VertexData("user", properties)
        val response =  restTemplate.postForEntity("${this.createBaseUrl()}/addVertex", user, String::class.java)
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@UCVE-002 Adding this property for key [code] and value [1] violates a uniqueness constraint [vByUserCode]", obj.data)
    }

    @Test
    fun canCreateEdgeWithTargetThatNotExist() {
        val source = VertexInfo("user", "1")
        val target = VertexInfo("accessRule", "3")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val response =  restTemplate.postForEntity("${this.createBaseUrl()}/addEdge", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@UCEE-003 Impossible find Access Rule $target", obj.data)
    }

    @Test
    fun canCreateEdgeWithUserThatNotExist() {

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
    }


}