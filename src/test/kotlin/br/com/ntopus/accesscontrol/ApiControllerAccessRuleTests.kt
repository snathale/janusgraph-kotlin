package br.com.ntopus.accesscontrol

import br.com.ntopus.accesscontrol.helper.ApiControllerHelper
import br.com.ntopus.accesscontrol.helper.CreateAssociationSuccess
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
class ApiControllerAccessRuleTests: ApiControllerHelper(), IVertexTests {

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
        this.createDefaultOrganization(Date())
        this.createDefaultUnitOrganization(Date())
        this.createDefaultGroup(Date())
        this.createDefaultAccessGroup(Date())
        this.createDefaultAccessRule(date)
    }

    @Test
    override fun createVertex() {
        val gson = Gson()
        val expirationDate = this.addDays(Date(), 1)
        val properties:List<Property> = listOf(
                Property("code", "2"),
                Property("expirationDate", format.format(expirationDate)))
        val accessRule = VertexData("accessRule", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", accessRule, String::class.java)
        val obj: CreateAssociationSuccess = gson.fromJson(response.body, CreateAssociationSuccess::class.java)
        this.assertAccessRuleApiResponseSuccess("2", true, expirationDate, obj)
        this.assertAccessRuleMapper("2", true, expirationDate)
    }

    @Test
    fun createAccessRuleWithoutExpirationDate() {
        val gson = Gson()
        val properties:List<Property> = listOf(
                Property("code", "2"),
                Property("enable", "true"))
        val accessRule = VertexData("accessRule", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", accessRule, String::class.java)
        val obj: CreateAssociationSuccess = gson.fromJson(response.body, CreateAssociationSuccess::class.java)
        Assert.assertEquals(null, obj.data.expirationDate)
        Assert.assertEquals("2", obj.data.code)
        Assert.assertEquals(true, obj.data.enable)
        val g = GraphFactory.open().traversal()
        val userStorage = g.V().hasLabel(VertexLabel.ACCESS_RULE.label).has(PropertyLabel.CODE.label, "2")
        val values = AbstractMapper.parseMapVertex(userStorage)
        Assert.assertEquals("", AbstractMapper.parseMapValue(values["expirationDate"].toString()))
        Assert.assertEquals("2", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    override fun createVertexWithExtraProperty() {
        val gson = Gson()
        val properties:List<Property> = listOf(
                Property("code", "2"),
                Property("enable", "true"),
                Property("name", "Access Rule"))
        val accessRule = VertexData("accessRule", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", accessRule, String::class.java)
        val obj: CreateAssociationSuccess = gson.fromJson(response.body, CreateAssociationSuccess::class.java)
        Assert.assertEquals(null, obj.data.expirationDate)
        Assert.assertEquals("2", obj.data.code)
        Assert.assertEquals(true, obj.data.enable)
        val g = GraphFactory.open().traversal()
        val userStorage = g.V().hasLabel(VertexLabel.ACCESS_RULE.label).has(PropertyLabel.CODE.label, "2")
        val values = AbstractMapper.parseMapVertex(userStorage)
        Assert.assertEquals("", AbstractMapper.parseMapValue(values["expirationDate"].toString()))
        Assert.assertEquals("2", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
        Assert.assertEquals("", AbstractMapper.parseMapValue(values["name"].toString()))
    }

    @Test
    override fun cantCeateVertexThatExist() {
        val gson = Gson()
        val properties: List<Property> = listOf(Property("code", "1"), Property("expirationDate", format.format(this.date)))
        val accessRule = VertexData("accessRule", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", accessRule, String::class.java)
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@ARCVE-002 Adding this property for key [code] and value [1] violates a uniqueness constraint [vByAccessRuleCode]", obj.data)
        val g = GraphFactory.open().traversal()
        val accessRuleStorage = g.V().hasLabel("accessRule").has("code", "1")
        val values = AbstractMapper.parseMapVertex(accessRuleStorage)
        Assert.assertEquals("1", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals(format.format(this.date), AbstractMapper.parseMapValueDate(values["expirationDate"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    override fun cantCreateVertexWithRequiredPropertyEmpty() {
        val gson = Gson()
        val enable: List<Property> = listOf(Property("enable", "true"))
        val accessRule = VertexData("accessRule", enable)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", accessRule, String::class.java)
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@ARCVE-001 Empty Access Rule properties", obj.data)
        val g = GraphFactory.open().traversal()
        val accessRuleStorage = g.V().hasLabel("accessRule")
        val values = AbstractMapper.parseMapVertex(accessRuleStorage)
        Assert.assertEquals(3, values.size)
        Assert.assertEquals("1", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
        val expirationDate = this.addDays(Date(), 1)
        val property:List<Property> = listOf(
                Property("expirationDate", format.format(expirationDate)))
        val organization1 = VertexData("accessRule", property)
        val response1 =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", organization1, String::class.java)
        val obj1 = gson.fromJson(response1.body, FAILResponse::class.java)
        Assert.assertEquals("@ARCVE-001 Empty Access Rule properties", obj1.data)
        val accessRuleStorage1 = g.V().hasLabel("accessRule")
        val values1 = AbstractMapper.parseMapVertex(accessRuleStorage1)
        Assert.assertEquals(3, values1.size)
        Assert.assertEquals("1", AbstractMapper.parseMapValue(values1["code"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values1["enable"].toString()).toBoolean())
    }

    @Test
    override fun cantCreateEdgeWithSourceThatNotExist() {
        val source = VertexInfo("accessRule", "2")
        val target = VertexInfo("group", "1")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val response =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@ARCEE-002 Impossible find Access Rule with code 2", obj.data)
        val g = GraphFactory.open().traversal()
        val unitOrganization = g.V().hasLabel("accessRule").has("code", "1").next()
        Assert.assertFalse(unitOrganization.edges(Direction.OUT, "provide").hasNext())
    }

    @Test
    override fun cantCreateEdgeWithTargetThatNotExist() {
        val source = VertexInfo("accessRule", "1")
        val target = VertexInfo("accessGroup", "2")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val response =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@ARCEE-003 Impossible find AccessGroup with code ${target.code}", obj.data)
        val g = GraphFactory.open().traversal()
        val unitOrganization = g.V().hasLabel("accessRule").has("code", "1").next()
        Assert.assertFalse(unitOrganization.edges(Direction.OUT, "own").hasNext())
    }

    @Test
    fun cantCreateEdgeWithOrganizationThatNotExist() {
        val source = VertexInfo("accessRule", "1")
        val target = VertexInfo("organization", "2")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val response =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@ARCEE-003 Impossible find Organization with code ${target.code}", obj.data)
        val g = GraphFactory.open().traversal()
        val unitOrganization = g.V().hasLabel("accessRule").has("code", "1").next()
        Assert.assertFalse(unitOrganization.edges(Direction.OUT, "provide").hasNext())
    }

    @Test
    fun cantCreateEdgeWithUnitOrganizationThatNotExist() {
        val source = VertexInfo("accessRule", "1")
        val target = VertexInfo("unitOrganization", "2")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val response =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@ARCEE-003 Impossible find UnitOrganization with code ${target.code}", obj.data)
        val g = GraphFactory.open().traversal()
        val unitOrganization = g.V().hasLabel("accessRule").has("code", "1").next()
        Assert.assertFalse(unitOrganization.edges(Direction.OUT, "provide").hasNext())
    }

    @Test
    fun cantCreateEdgeWithGroupThatNotExist() {
        val source = VertexInfo("accessRule", "1")
        val target = VertexInfo("group", "2")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val response =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@ARCEE-003 Impossible find Group with code ${target.code}", obj.data)
        val g = GraphFactory.open().traversal()
        val unitOrganization = g.V().hasLabel("accessRule").has("code", "1").next()
        Assert.assertFalse(unitOrganization.edges(Direction.OUT, "provide").hasNext())
    }

    @Test
    override fun createEdge() {
        val source = VertexInfo("accessRule", "1")
        val target = VertexInfo("accessGroup", "1")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val edgeResponse =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val edgeObj: CreateEdgeSuccess = gson.fromJson(edgeResponse.body, CreateEdgeSuccess::class.java)
        this.assertEdgeCreatedSuccess(source, target, edgeObj, "own")
        val g = GraphFactory.open().traversal()
        val accessRule = g.V().hasLabel("accessRule").has("code", "1")
        val accessRuleValues = AbstractMapper.parseMapVertex(accessRule)
        Assert.assertTrue(accessRuleValues.size > 0)
        val accessGroup = g.V().hasLabel("accessGroup").has("code", "1")
        val accessGroupValues = AbstractMapper.parseMapVertex(accessGroup)
        Assert.assertTrue(accessGroupValues.size > 0)
        val edgeOrganization = g.V().hasLabel("accessRule").has("code", "1").next()
        Assert.assertTrue(edgeOrganization.edges(Direction.OUT, "own").hasNext())
        val edgeUnitOrganization = g.V().hasLabel("accessGroup").has("code", "1").next()
        Assert.assertTrue(edgeUnitOrganization.edges(Direction.IN, "own").hasNext())
    }

    @Test
    override fun updateProperty() {
        val expirationDate = this.addDays(date, 1)
        val properties : List<Property> = listOf(Property("expirationDate", format.format(expirationDate)))
        val requestUpdate = HttpEntity(properties)
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/updateProperty/accessRule/1", HttpMethod.PUT, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: CreateAssociationSuccess = gson.fromJson(response.body, CreateAssociationSuccess::class.java)
        this.assertAccessRuleApiResponseSuccess("1", true, expirationDate, obj)
        this.assertAccessRuleMapper("1", true, expirationDate)
    }

    @Test
    override fun cantUpdateDefaultProperty() {
        val properties : List<Property> = listOf(Property("code", "2"))
        val requestUpdate = HttpEntity(properties)
        val response = restTemplate.exchange(
                "${this.createVertexBaseUrl(this.port)}/updateProperty/accessRule/1", HttpMethod.PUT,
                requestUpdate, String::class.java
        )
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@ARUPE-002 Access Rule property can be updated", obj.data)
    }

    @Test
    override fun cantUpdatePropertyFromVertexThatNotExist() {
        val properties : List<Property> = listOf(Property("enable", "false"))
        val requestUpdate = HttpEntity(properties)
        val response = restTemplate.exchange(
                "${this.createVertexBaseUrl(this.port)}/updateProperty/accessRule/2", HttpMethod.PUT,
                requestUpdate, String::class.java
        )
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@ARUPE-001 Impossible find Access Rule with code 2", obj.data)
    }

    @Test
    override fun deleteVertex() {
        val requestUpdate = HttpEntity("accessRule")
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/delete/1", HttpMethod.DELETE, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: SUCCESSResponse = gson.fromJson(response.body, SUCCESSResponse::class.java)
        Assert.assertEquals("SUCCESS", obj.status)
        Assert.assertEquals(null, obj.data)
        val g = GraphFactory.open().traversal()
        this.assertAccessRuleMapper("1", false, date)
    }

    @Test
    override fun cantDeleteVertexThatNotExist() {
        val requestUpdate = HttpEntity("accessRule")
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/delete/2", HttpMethod.DELETE, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@ARDE-001 Impossible find Access Rule with code 2", obj.data)
    }

    @Test
    override fun cantCreateEdgeWithIncorrectTarget() {
        val source = VertexInfo("accessRule", "1")
        val target = VertexInfo("user", "1")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val response =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@ARCEE-001 Impossible create this edge with target code ${target.code}", obj.data)
        val g = GraphFactory.open().traversal()
        val v1 = g.V().hasLabel("accessRule").has("code", "1")
        val v2 = g.V().hasLabel("user").has("code", "1")
        Assert.assertFalse(v1.both().has("id", v2.id()).hasNext())
    }

    @Test
    fun createProvideEdge() {
        val source = VertexInfo("accessRule", "1")
        val targets = listOf<VertexInfo>(
                VertexInfo("organization", "1"),
                VertexInfo("unitOrganization", "1"),
                VertexInfo("group", "1")
        )
        for (target in targets) {
            val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
            val edgeResponse =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
            val gson = GsonBuilder().serializeNulls().create()
            val response: CreateEdgeSuccess = gson.fromJson(edgeResponse.body, CreateEdgeSuccess::class.java)
            this.assertEdgeCreatedSuccess(source, target, response, "provide")
            val g = GraphFactory.open().traversal()
            val vSource = g.V().hasLabel("accessRule").has("code", "1")
            val vSourceValues = AbstractMapper.parseMapVertex(vSource)
            Assert.assertTrue(vSourceValues.size > 0)
            val vTarget = g.V().hasLabel(target.label).has("code", target.code)
            val vTargetValues = AbstractMapper.parseMapVertex(vTarget)
            Assert.assertTrue(vTargetValues.size > 0)
            val eSource = g.V().hasLabel("accessRule").has("code", "1").next()
            Assert.assertTrue(eSource.edges(Direction.OUT, "provide").hasNext())
            val eTarget = g.V().hasLabel(target.label).has("code", target.code).next()
            Assert.assertTrue(eTarget.edges(Direction.IN, "provide").hasNext())
        }
    }
}