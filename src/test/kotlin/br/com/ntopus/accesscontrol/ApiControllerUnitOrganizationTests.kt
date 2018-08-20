package br.com.ntopus.accesscontrol

import br.com.ntopus.accesscontrol.helper.*
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
class ApiControllerUnitOrganizationTests: ApiControllerHelper(), IVertexTests {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @LocalServerPort
    private val port: Int = 0

    private val date: Date = Date()

    private val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")

    private var graph = GraphFactory.setInstance("janusgraph-inmemory.properties")

    private var unitId: Long = 0

    @Before
    fun setup() {
        JanusGraphSchemaImporter().writeGraphSONSchema(graph.open(), ClassPathResource("schema.json").file.absolutePath)
        this.unitId = this.createDefaultUnitOrganization(date)!!
        this.createDefaultGroup(Date())
    }

    @Test
    override fun getVertex() {
        val gson = Gson()
        val response =  restTemplate.getForEntity("${this.createVertexBaseUrl(this.port)}/?id=${this.unitId}",  String::class.java)
        val obj = gson.fromJson(response.body, VertexSuccess::class.java)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val creationDate = format.parse(obj.data["creationDate"].toString())
        Assert.assertEquals(200, response.statusCode.value())
        Assert.assertEquals(this.unitId.toString(), obj.data["id"])
        Assert.assertEquals("Bahia", obj.data["name"])
        Assert.assertEquals("1", obj.data["code"])
        Assert.assertEquals("This is a Unit Organization", obj.data["observation"])
        Assert.assertEquals(true, obj.data["enable"]!!.toBoolean())
        Assert.assertEquals(format.format(date), format.format(creationDate))
    }

    @Test
    override fun createVertex() {
        val gson = Gson()
        val properties:List<Property> = listOf(Property("code", "2"),
                Property("name", "Minas Gerais"),
                Property("observation", "This is a Unit Organization from Minas Gerais"))
        val unitOrganization = VertexData("unitOrganization", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", unitOrganization, String::class.java)
        val obj: CreateAgentSuccess = gson.fromJson(response.body, CreateAgentSuccess::class.java)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val finalDate = format.parse(obj.data.creationDate)
        Assert.assertEquals(200, response.statusCode.value())
        Assert.assertNotNull(finalDate)
        Assert.assertEquals("Minas Gerais", obj.data.name)
        Assert.assertEquals("2", obj.data.code)
        Assert.assertEquals("This is a Unit Organization from Minas Gerais", obj.data.observation)
        Assert.assertEquals(true, obj.data.enable)
        val g = GraphFactory.open().traversal()
        val userStorage = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, "2").next()
        val values = AbstractMapper.parseMapVertex(userStorage)
        Assert.assertNotNull(AbstractMapper.parseMapValue(values["creationDate"].toString()))
        Assert.assertEquals("Minas Gerais", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("2", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a Unit Organization from Minas Gerais", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    override fun createVertexWithExtraProperty() {
        val gson = Gson()
        val properties:List<Property> = listOf(Property("code", "2"),
                Property("name", "New Unit Organization"),
                Property("description", "This is a description"),
                Property("observation", "This is a observation"))
        val unitOrganization = VertexData("unitOrganization", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", unitOrganization, String::class.java)
        val obj: CreateAgentSuccess = gson.fromJson(response.body, CreateAgentSuccess::class.java)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val finalDate = format.parse(obj.data.creationDate)
        Assert.assertEquals(200, response.statusCode.value())
        Assert.assertNotNull(finalDate)
        Assert.assertEquals("New Unit Organization", obj.data.name)
        Assert.assertEquals("2", obj.data.code)
        Assert.assertEquals("This is a observation", obj.data.observation)
        Assert.assertEquals(true, obj.data.enable)
        val g = GraphFactory.open().traversal()
        val userStorage = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, "2").next()
        val values = AbstractMapper.parseMapVertex(userStorage)
        Assert.assertEquals("New Unit Organization", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("2", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a observation", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals("", AbstractMapper.parseMapValue(values["description"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    override fun cantCreateVertexThatExist() {
        val gson = Gson()
        val properties: List<Property> = listOf(Property("code", "1"), Property("name", "Test"))
        val unitOrganization = VertexData("unitOrganization", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", unitOrganization, String::class.java)
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals(404, response.statusCode.value())
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@UOCVE-002 Adding this property for key [code] and value [1] violates a uniqueness constraint [vByUnitOrganizationCode]", obj.data)
        val g = GraphFactory.open().traversal()
        val organizationStorage = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, "1").next()
        val values = AbstractMapper.parseMapVertex(organizationStorage)
        Assert.assertEquals("Bahia", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("1", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a Unit Organization", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(format.format(date), AbstractMapper.parseMapValueDate(values["creationDate"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    override fun cantCreateVertexWithRequiredPropertyEmpty() {
        val gson = Gson()
        val code: List<Property> = listOf(Property("code", "2"))
        val unitOrganization = VertexData("unitOrganization", code)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", unitOrganization, String::class.java)
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals(404, response.statusCode.value())
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@UOCVE-001 Empty Unit Organization properties", obj.data)
        val g = GraphFactory.open().traversal()
        val userStorage = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, "2").next()
        val values = AbstractMapper.parseMapVertex(userStorage)
        Assert.assertEquals(0, values.size)
        val name: List<Property> = listOf(Property("name", "test"))
        val unitOrganization1 = VertexData("unitOrganization", name)
        val response1 =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", unitOrganization1, String::class.java)
        val obj1 = gson.fromJson(response1.body, FAILResponse::class.java)
        Assert.assertEquals("@UOCVE-001 Empty Unit Organization properties", obj1.data)
        val unitOrganizationStorage1 = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, "2").next()
        val values1 = AbstractMapper.parseMapVertex(unitOrganizationStorage1)
        Assert.assertEquals(0, values1.size)
    }

    @Test
    override fun cantCreateEdgeWithSourceThatNotExist() {
        val source = VertexInfo("unitOrganization", "2")
        val target = VertexInfo("group", "1")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val response =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals(404, response.statusCode.value())
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@UOCEE-002 Impossible find Unit Organization with code ${source.code}", obj.data)
        val g = GraphFactory.open().traversal()
        Assert.assertFalse(g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, "2").hasNext())
    }

    @Test
    override fun cantCreateEdgeWithTargetThatNotExist() {
        val source = VertexInfo("unitOrganization", "1")
        val target = VertexInfo("group", "3")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val response =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals(404, response.statusCode.value())
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@UOCEE-003 Impossible find Group with code ${target.code}", obj.data)
        val g = GraphFactory.open().traversal()
        Assert.assertFalse(g.V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, "3").hasNext())
    }

    @Test
    override fun cantCreateEdgeWithIncorrectTarget() {
        val source = VertexInfo("unitOrganization", "1")
        val target = VertexInfo("organization", "1")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val response =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals(404, response.statusCode.value())
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@UOCEE-001 Impossible create edge with target code ${target.code}", obj.data)
        val g = GraphFactory.open().traversal()
        val v1 = g.V().hasLabel("unitOrganization").has("code", "1")
        Assert.assertFalse(v1.both().hasNext())
    }

    @Test
    override fun createEdge() {
        val source = VertexInfo("unitOrganization", "1")
        val target = VertexInfo("group", "1")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val edgeResponse =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val edgeObj: CreateEdgeSuccess = gson.fromJson(edgeResponse.body, CreateEdgeSuccess::class.java)
        Assert.assertEquals(200, edgeResponse.statusCode.value())
        Assert.assertEquals("SUCCESS", edgeObj.status)
        Assert.assertEquals("unitOrganization", edgeObj.data.source.label)
        Assert.assertEquals("1", edgeObj.data.source.code)
        Assert.assertEquals("group", edgeObj.data.target.label)
        Assert.assertEquals("1", edgeObj.data.target.code)
        Assert.assertEquals("has", edgeObj.data.edgeLabel)
        val g = GraphFactory.open().traversal()
        val organization = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, "1").next()
        val organizationValues = AbstractMapper.parseMapVertex(organization)
        Assert.assertTrue(organizationValues.size > 0)
        val unitOrganization = g.V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, "1").next()
        val unitOrganizationValues = AbstractMapper.parseMapVertex(unitOrganization)
        Assert.assertTrue(unitOrganizationValues.size > 0)
        val edgeOrganization = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, "1").next()
        Assert.assertTrue(edgeOrganization.edges(Direction.OUT, "has").hasNext())
        val edgeUnitOrganization = g.V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, "1").next()
        Assert.assertTrue(edgeUnitOrganization.edges(Direction.IN, "has").hasNext())
    }

    @Test
    override fun updateProperty() {
        val properties : List<Property> = listOf(Property("name", "Unit Organization Test"), Property("observation", "Property updated"))
        val requestUpdate = HttpEntity(properties)
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/updateProperty/unitOrganization/1", HttpMethod.PUT, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: CreateAgentSuccess = gson.fromJson(response.body, CreateAgentSuccess::class.java)
        Assert.assertEquals(200, response.statusCode.value())
        Assert.assertEquals("SUCCESS", obj.status)
        Assert.assertEquals("Unit Organization Test", obj.data.name)
        Assert.assertEquals("1", obj.data.code)
        Assert.assertEquals("Property updated", obj.data.observation)
        Assert.assertEquals(true, obj.data.enable)
        val g = GraphFactory.open().traversal()
        val organizationStorage = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, "1").next()
        val values = AbstractMapper.parseMapVertex(organizationStorage)
        Assert.assertEquals("Unit Organization Test", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("1", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("Property updated", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
        Assert.assertNotNull(AbstractMapper.parseMapValue(values["creationDate"].toString()))
    }

    @Test
    override fun cantUpdateDefaultProperty() {
        val properties : List<Property> = listOf(Property("name", "Unit Organization Test"), Property("code", "2"))
        val requestUpdate = HttpEntity(properties)
        val response = restTemplate.exchange(
                "${this.createVertexBaseUrl(this.port)}/updateProperty/unitOrganization/1", HttpMethod.PUT,
                requestUpdate, String::class.java
        )
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals(404, response.statusCode.value())
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@UOUPE-002 Unit Organization property can be updated", obj.data)
    }

    @Test
    override fun cantUpdatePropertyFromVertexThatNotExist() {
        val properties : List<Property> = listOf(
                Property("name", "Unit Organization Test"),
                Property("description", "New Description")
        )
        val requestUpdate = HttpEntity(properties)
        val response = restTemplate.exchange(
                "${this.createVertexBaseUrl(this.port)}/updateProperty/unitOrganization/2", HttpMethod.PUT,
                requestUpdate, String::class.java
        )
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals(404, response.statusCode.value())
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@UOCEE-001 Impossible find Unit Organization with code 2", obj.data)
    }

    @Test
    override fun deleteVertex() {
        val requestUpdate = HttpEntity("unitOrganization")
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/delete/1", HttpMethod.DELETE, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: SUCCESSResponse = gson.fromJson(response.body, SUCCESSResponse::class.java)
        Assert.assertEquals(200, response.statusCode.value())
        Assert.assertEquals("SUCCESS", obj.status)
        Assert.assertEquals(null, obj.data)
        val g = GraphFactory.open().traversal()
        val organizationStorage = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, "1").next()
        val values = AbstractMapper.parseMapVertex(organizationStorage)
        Assert.assertEquals("Bahia", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("1", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a Unit Organization", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(format.format(date), AbstractMapper.parseMapValueDate(values["creationDate"].toString()))
        Assert.assertEquals(false, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    override fun cantDeleteVertexThatNotExist() {
        val requestUpdate = HttpEntity("unitOrganization")
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/delete/2", HttpMethod.DELETE, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals(404, response.statusCode.value())
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@UODE-001 Impossible find Unit Organization with code 2", obj.data)
    }
}