package br.com.ntopus.accesscontrol

import br.com.ntopus.accesscontrol.helper.ApiControllerHerper
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
class ApiControllerUnitOrganizationTests: ApiControllerHerper() {

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
        this.createDefaultUnitOrganization(date)
        this.createDefaultGroup(Date())
    }

    @Test
    fun createUnitOrganization() {
        val gson = Gson()
        val initialDate = Date()
        val properties:List<Property> = listOf(Property("code", "2"),
                Property("name", "Minas Gerais"),
                Property("observation", "This is a Unit Organization from Minas Gerais"))
        val unitOrganization = VertexData("unitOrganization", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", unitOrganization, String::class.java)
        val obj: CreateAgentSuccess = gson.fromJson(response.body, CreateAgentSuccess::class.java)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val finalDate = format.parse(obj.data.creationDate)
//        Assert.assertTrue(initialDate.before(finalDate))
        Assert.assertNotNull(finalDate)
        Assert.assertEquals("Minas Gerais", obj.data.name)
        Assert.assertEquals("2", obj.data.code)
        Assert.assertEquals("This is a Unit Organization from Minas Gerais", obj.data.observation)
        Assert.assertEquals(true, obj.data.enable)
        val g = GraphFactory.open().traversal()
        val userStorage = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, "2")
        val values = AbstractMapper.parseMapVertex(userStorage)
        Assert.assertNotNull(AbstractMapper.parseMapValue(values["creationDate"].toString()))
        Assert.assertEquals("Minas Gerais", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("2", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a Unit Organization from Minas Gerais", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    fun createUnitOrganizationWithPropertyDuplicated() {
        val gson = Gson()
        val initialDate = Date()
        val properties:List<Property> = listOf(Property("code", "2"),
                Property("name", "New Unit Organization"),
                Property("name", "New Unit Organization 2"),
                Property("observation", "This is a observation"))
        val unitOrganization = VertexData("unitOrganization", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", unitOrganization, String::class.java)
        val obj: CreateAgentSuccess = gson.fromJson(response.body, CreateAgentSuccess::class.java)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val finalDate = format.parse(obj.data.creationDate)
//        Assert.assertTrue(initialDate.before(finalDate))
        Assert.assertNotNull(finalDate)
        Assert.assertEquals("New Unit Organization 2", obj.data.name)
        Assert.assertEquals("2", obj.data.code)
        Assert.assertEquals("This is a observation", obj.data.observation)
        Assert.assertEquals(true, obj.data.enable)
        val g = GraphFactory.open().traversal()
        val userStorage = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, "2")
        val values = AbstractMapper.parseMapVertex(userStorage)
        Assert.assertEquals("New Unit Organization 2", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("2", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a observation", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }


    @Test
    fun cantCreateUnitOrganizationWithRequiredPropertiesEmpty() {
        val gson = Gson()
        val code: List<Property> = listOf(Property("code", "2"))
        val unitOrganization = VertexData("unitOrganization", code)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", unitOrganization, String::class.java)
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@UOCVE-001 Empty Unit Organization properties", obj.data)
        val g = GraphFactory.open().traversal()
        val userStorage = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, "2")
        val values = AbstractMapper.parseMapVertex(userStorage)
        Assert.assertEquals(0, values.size)
        val name: List<Property> = listOf(Property("name", "test"))
        val unitOrganization1 = VertexData("unitOrganization", name)
        val response1 =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", unitOrganization1, String::class.java)
        val obj1 = gson.fromJson(response1.body, FAILResponse::class.java)
        Assert.assertEquals("@UOCVE-001 Empty Unit Organization properties", obj1.data)
        val unitOrganizationStorage1 = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, "2")
        val values1 = AbstractMapper.parseMapVertex(unitOrganizationStorage1)
        Assert.assertEquals(0, values1.size)
    }

    @Test
    fun cantCreateUnitOrganizationThatExist() {
        val gson = Gson()
        val properties: List<Property> = listOf(Property("code", "1"), Property("name", "Test"))
        val unitOrganization = VertexData("unitOrganization", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", unitOrganization, String::class.java)
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@UOCVE-002 Adding this property for key [code] and value [1] violates a uniqueness constraint [vByUnitOrganizationCode]", obj.data)
        val g = GraphFactory.open().traversal()
        val organizationStorage = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, "1")
        val values = AbstractMapper.parseMapVertex(organizationStorage)
        Assert.assertEquals("Bahia", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("1", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a Unit Organization", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(format.format(date), AbstractMapper.formatDate(values["creationDate"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    fun canCreateEdgeWithTargetThatNotExist() {
        val source = VertexInfo("unitOrganization", "1")
        val target = VertexInfo("group", "3")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val response =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@UOCEE-003 Impossible find Group with code ${target.code}", obj.data)
        val g = GraphFactory.open().traversal()
        val unitOrganization = g.V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, "3")
        val values = AbstractMapper.parseMapVertex(unitOrganization)
        Assert.assertEquals(0, values.size)
    }

    @Test
    fun canCreateEdgeWithUnitOrganizationThatNotExist() {
        val source = VertexInfo("unitOrganization", "2")
        val target = VertexInfo("group", "1")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val response =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@UOCEE-002 Impossible find Unit Organization with code ${source.code}", obj.data)
        val g = GraphFactory.open().traversal()
        val unitOrganization = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, "2")
        val values = AbstractMapper.parseMapVertex(unitOrganization)
        Assert.assertEquals(0, values.size)
    }

    @Test
    fun createUnitOrganizationEdge() {
        val source = VertexInfo("unitOrganization", "1")
        val target = VertexInfo("group", "1")
        val params: Map<String, VertexInfo> = hashMapOf("source" to source, "target" to target)
        val edgeResponse =  restTemplate.postForEntity("${this.createEdgeBaseUrl(this.port)}/add", params, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val edgeObj: CreateEdgeSuccess = gson.fromJson(edgeResponse.body, CreateEdgeSuccess::class.java)
        Assert.assertEquals("SUCCESS", edgeObj.status)
        Assert.assertEquals("unitOrganization", edgeObj.data.source.label)
        Assert.assertEquals("1", edgeObj.data.source.code)
        Assert.assertEquals("group", edgeObj.data.target.label)
        Assert.assertEquals("1", edgeObj.data.target.code)
        Assert.assertEquals("has", edgeObj.data.edgeLabel)
        val g = GraphFactory.open().traversal()
        val organization = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, "1")
        val organizationValues = AbstractMapper.parseMapVertex(organization)
        Assert.assertTrue(organizationValues.size > 0)
        val unitOrganization = g.V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, "1")
        val unitOrganizationValues = AbstractMapper.parseMapVertex(unitOrganization)
        Assert.assertTrue(unitOrganizationValues.size > 0)
        val edgeOrganization = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, "1").next()
        Assert.assertTrue(edgeOrganization.edges(Direction.OUT, "has").hasNext())
        val edgeUnitOrganization = g.V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, "1").next()
        Assert.assertTrue(edgeUnitOrganization.edges(Direction.IN, "has").hasNext())
    }

    @Test
    fun updateUnitOrganizationProperty() {
        val properties : List<Property> = listOf(Property("name", "Unit Organization Test"), Property("observation", "Property updated"))
        val requestUpdate = HttpEntity(properties)
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/updateProperty/unitOrganization/1", HttpMethod.PUT, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: CreateAgentSuccess = gson.fromJson(response.body, CreateAgentSuccess::class.java)
        Assert.assertEquals("SUCCESS", obj.status)
        Assert.assertEquals("Unit Organization Test", obj.data.name)
        Assert.assertEquals("1", obj.data.code)
        Assert.assertEquals("Property updated", obj.data.observation)
        Assert.assertEquals(true, obj.data.enable)
        val g = GraphFactory.open().traversal()
        val organizationStorage = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, "1")
        val values = AbstractMapper.parseMapVertex(organizationStorage)
        Assert.assertEquals("Unit Organization Test", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("1", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("Property updated", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
        Assert.assertNotNull(AbstractMapper.parseMapValue(values["creationDate"].toString()))
    }

    @Test
    fun canUpdateUnitOrganizationDefaultProperty() {
        val properties : List<Property> = listOf(Property("name", "Unit Organization Test"), Property("code", "2"))
        val requestUpdate = HttpEntity(properties)
        val response = restTemplate.exchange(
                "${this.createVertexBaseUrl(this.port)}/updateProperty/unitOrganization/1", HttpMethod.PUT,
                requestUpdate, String::class.java
        )
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@UOUPE-002 Unit Organization property can be updated", obj.data)
    }

    @Test
    fun deleteUnitOrganization() {
        val requestUpdate = HttpEntity("unitOrganization")
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/delete/1", HttpMethod.DELETE, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: SUCCESSResponse = gson.fromJson(response.body, SUCCESSResponse::class.java)
        Assert.assertEquals("SUCCESS", obj.status)
        Assert.assertEquals(null, obj.data)
        val g = GraphFactory.open().traversal()
        val organizationStorage = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, "1")
        val values = AbstractMapper.parseMapVertex(organizationStorage)
        Assert.assertEquals("Bahia", AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals("1", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals("This is a Unit Organization", AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(format.format(date), AbstractMapper.formatDate(values["creationDate"].toString()))
        Assert.assertEquals(false, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    @Test
    fun cantDeleteUnitOrganizationThatNotExist() {
        val requestUpdate = HttpEntity("unitOrganization")
        val response = restTemplate.exchange("${this.createVertexBaseUrl(this.port)}/delete/2", HttpMethod.DELETE, requestUpdate, String::class.java)
        val gson = GsonBuilder().serializeNulls().create()
        val obj: FAILResponse = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("FAIL", obj.status)
        Assert.assertEquals("@UODE-001 Impossible find Unit Organization with code 2", obj.data)
    }
}