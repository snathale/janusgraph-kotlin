package br.com.ntopus.accesscontrol

import br.com.ntopus.accesscontrol.helper.ApiControllerHelper
import br.com.ntopus.accesscontrol.helper.CreateEdgeSuccess
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
class ApiControllerOrganizationTests: ApiControllerHelper() {
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
        this.createDefaultOrganization(date)
        this.createDefaultUnitOrganization(Date())
    }

    @Test
    fun createOrganization() {
        val gson = Gson()
        val initialDate = Date()
        val properties:List<Property> = listOf(Property("code", "2"),
                Property("name", "New Organization"),
                Property("observation", "This is a observation"))
        val organization = VertexData("organization", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", organization, String::class.java)
        val obj: CreateAgentSuccess = gson.fromJson(response.body, CreateAgentSuccess::class.java)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val finalDate = format.parse(obj.data.creationDate)
//        Assert.assertTrue(initialDate.before(finalDate))
        Assert.assertNotNull(finalDate)
        Assert.assertEquals("New Organization", obj.data.name)
        Assert.assertEquals("2", obj.data.code)
        Assert.assertEquals("This is a observation", obj.data.observation)
        Assert.assertEquals(true, obj.data.enable)
        val g = GraphFactory.open().traversal()
        val userStorage = g.V().hasLabel(VertexLabel.ORGANIZATION.label).has(PropertyLabel.CODE.label, "2")
        val values = AbstractMapper.parseMapVertex(userStorage)
        Assert.assertEquals("New Organization", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("2", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a observation", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    fun createOrganizationWithPropertyDuplicated() {
        val gson = Gson()
        val initialDate = Date()
        val properties:List<Property> = listOf(Property("code", "2"),
                Property("name", "New Organization"),
                Property("name", "New Organization 2"),
                Property("observation", "This is a observation"))
        val organization = VertexData("organization", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", organization, String::class.java)
        val obj: CreateAgentSuccess = gson.fromJson(response.body, CreateAgentSuccess::class.java)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val finalDate = format.parse(obj.data.creationDate)
//        Assert.assertTrue(initialDate.before(finalDate))
        Assert.assertNotNull(finalDate)
        Assert.assertEquals("New Organization 2", obj.data.name)
        Assert.assertEquals("2", obj.data.code)
        Assert.assertEquals("This is a observation", obj.data.observation)
        Assert.assertEquals(true, obj.data.enable)
        val g = GraphFactory.open().traversal()
        val userStorage = g.V().hasLabel(VertexLabel.ORGANIZATION.label).has(PropertyLabel.CODE.label, "2")
        val values = AbstractMapper.parseMapVertex(userStorage)
        Assert.assertEquals("New Organization 2", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("2", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a observation", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    fun cantCreateOrganizationWithRequiredPropertiesEmpty() {
        val gson = Gson()
        val code: List<Property> = listOf(Property("code", "2"))
        val organization = VertexData("organization", code)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", organization, String::class.java)
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@OCVE-001 Empty Organization properties", obj.data)
        val g = GraphFactory.open().traversal()
        val organizationStorage = g.V().hasLabel(VertexLabel.ORGANIZATION.label).has(PropertyLabel.CODE.label, "2")
        val values = AbstractMapper.parseMapVertex(organizationStorage)
        Assert.assertEquals(0, values.size)
        val name: List<Property> = listOf(Property("name", "test"))
        val organization1 = VertexData("organization", name)
        val response1 =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", organization1, String::class.java)
        val obj1 = gson.fromJson(response1.body, FAILResponse::class.java)
        Assert.assertEquals("@OCVE-001 Empty Organization properties", obj1.data)
        val organizationStorage1 = g.V().hasLabel(VertexLabel.ORGANIZATION.label).has(PropertyLabel.CODE.label, "2")
        val values1 = AbstractMapper.parseMapVertex(organizationStorage1)
        Assert.assertEquals(0, values1.size)
    }

    @Test
    fun cantCreateOrganizationThatExist() {
        val gson = Gson()
        val properties: List<Property> = listOf(Property("code", "1"), Property("name", "Test"))
        val organization = VertexData("organization", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", organization, String::class.java)
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@OCVE-002 Adding this property for key [code] and value [1] violates a uniqueness constraint [vByOrganizationCode]", obj.data)
        val g = GraphFactory.open().traversal()
        val organizationStorage = g.V().hasLabel(VertexLabel.ORGANIZATION.label).has(PropertyLabel.CODE.label, "1")
        val values = AbstractMapper.parseMapVertex(organizationStorage)
        Assert.assertEquals("Kofre", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("1", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a Organization", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(format.format(date), AbstractMapper.parseMapValueDate(values["creationDate"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    fun cantCreateEdgeWithTargetThatNotExist() {
        val source = VertexInfo("organization", "1")
        val target = VertexInfo("unitOrganization", "2")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val response =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@OCEE-003 Impossible find Unit Organization with code ${target.code}", obj.data)
        val g = GraphFactory.open().traversal()
        val unitOrganization = g.V().hasLabel(VertexLabel.ACCESS_RULE.label).has(PropertyLabel.CODE.label, "2").next()
        Assert.assertFalse(unitOrganization.edges(Direction.OUT, "has").hasNext())
    }

    @Test
    fun canCreateEdgeWithOrganizationThatNotExist() {
        val source = VertexInfo("organization", "2")
        val target = VertexInfo("unitOrganization", "1")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val response =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@OCEE-002 Impossible find Organization with code ${source.code}", obj.data)
        val g = GraphFactory.open().traversal()
        val organization = g.V().hasLabel(VertexLabel.ORGANIZATION.label).has(PropertyLabel.CODE.label, "2")
        val values = AbstractMapper.parseMapVertex(organization)
        Assert.assertEquals(0, values.size)
    }

    @Test
    fun createOrganizationEdge() {
        val source = VertexInfo("organization", "1")
        val target = VertexInfo("unitOrganization", "1")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val edgeResponse =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val edgeObj: CreateEdgeSuccess = gson.fromJson(edgeResponse.body, CreateEdgeSuccess::class.java)
        Assert.assertEquals("SUCCESS", edgeObj.status)
        Assert.assertEquals("organization", edgeObj.data.source.label)
        Assert.assertEquals("1", edgeObj.data.source.code)
        Assert.assertEquals("unitOrganization", edgeObj.data.target.label)
        Assert.assertEquals("1", edgeObj.data.target.code)
        Assert.assertEquals("has", edgeObj.data.edgeLabel)
        val g = GraphFactory.open().traversal()
        val organization = g.V().hasLabel(VertexLabel.ORGANIZATION.label).has(PropertyLabel.CODE.label, "1")
        val organizationValues = AbstractMapper.parseMapVertex(organization)
        Assert.assertTrue(organizationValues.size > 0)
        val unitOrganization = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, "1")
        val unitOrganizationValues = AbstractMapper.parseMapVertex(unitOrganization)
        Assert.assertTrue(unitOrganizationValues.size > 0)
        val edgeOrganization = g.V().hasLabel(VertexLabel.ORGANIZATION.label).has(PropertyLabel.CODE.label, "1").next()
        Assert.assertTrue(edgeOrganization.edges(Direction.OUT, "has").hasNext())
        val edgeUnitOrganization = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, "1").next()
        Assert.assertTrue(edgeUnitOrganization.edges(Direction.IN, "has").hasNext())
    }

    @Test
    fun updateOrganizationProperty() {
        val properties : List<Property> = listOf(Property("name", "Organization Test"), Property("observation", "Property updated"))
        val requestUpdate = HttpEntity(properties)
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/updateProperty/organization/1", HttpMethod.PUT, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: CreateAgentSuccess = gson.fromJson(response.body, CreateAgentSuccess::class.java)
        Assert.assertEquals("SUCCESS", obj.status)
        Assert.assertEquals("Organization Test", obj.data.name)
        Assert.assertEquals("1", obj.data.code)
        Assert.assertEquals("Property updated", obj.data.observation)
        Assert.assertEquals(true, obj.data.enable)
        val g = GraphFactory.open().traversal()
        val organizationStorage = g.V().hasLabel(VertexLabel.ORGANIZATION.label).has(PropertyLabel.CODE.label, "1")
        val values = AbstractMapper.parseMapVertex(organizationStorage)
        Assert.assertEquals("Organization Test", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("1", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("Property updated", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
        Assert.assertNotNull(AbstractMapper.parseMapValue(values["creationDate"].toString()))
    }

    @Test
    fun cantUpdateOrganizationDefaultProperty() {
        val properties : List<Property> = listOf(Property("name", "Organization Test"), Property("code", "2"))
        val requestUpdate = HttpEntity(properties)
        val response = restTemplate.exchange(
                "${this.createVertexBaseUrl(this.port)}/updateProperty/organization/1", HttpMethod.PUT,
                requestUpdate, String::class.java
        )
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@OUPE-002 Organization property can be updated", obj.data)
    }

    @Test
    fun deleteOrganization() {
        val requestUpdate = HttpEntity("organization")
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/delete/1", HttpMethod.DELETE, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: SUCCESSResponse = gson.fromJson(response.body, SUCCESSResponse::class.java)
        Assert.assertEquals("SUCCESS", obj.status)
        Assert.assertEquals(null, obj.data)
        val g = GraphFactory.open().traversal()
        val organizationStorage = g.V().hasLabel(VertexLabel.ORGANIZATION.label).has(PropertyLabel.CODE.label, "1")
        val values = AbstractMapper.parseMapVertex(organizationStorage)
        Assert.assertEquals("Kofre", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("1", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a Organization", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(format.format(date), AbstractMapper.parseMapValueDate(values["creationDate"].toString()))
        Assert.assertEquals(false, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    fun cantDeleteOrganizationThatNotExist() {
        val requestUpdate = HttpEntity("organization")
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/delete/2", HttpMethod.DELETE, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@ODE-001 Impossible find Organization with code 2", obj.data)
    }
}