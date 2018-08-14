package br.com.ntopus.accesscontrol

import br.com.ntopus.accesscontrol.helper.ApiControllerHerper
import br.com.ntopus.accesscontrol.helper.CreateAssociationSuccess
import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexData
import br.com.ntopus.accesscontrol.model.data.VertexLabel
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
class ApiControllerAccessRuleTests: ApiControllerHerper() {
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
    fun createAccessRuleWithExpirationDate() {
        val gson = Gson()
        val expirationDate = this.addDays(Date(), 1)
        val properties:List<Property> = listOf(
                Property("code", "2"),
                Property("expirationDate", format.format(expirationDate)))
        val accessRule = VertexData("accessRule", properties)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", accessRule, String::class.java)
        val obj: CreateAssociationSuccess = gson.fromJson(response.body, CreateAssociationSuccess::class.java)
        val objExpirationDate = this.format.parse(obj.data.expirationDate)
        Assert.assertEquals(this.format.format(objExpirationDate), this.format.format(expirationDate))
        Assert.assertEquals("2", obj.data.code)
        Assert.assertEquals(true, obj.data.enable)
        val g = GraphFactory.open().traversal()
        val userStorage = g.V().hasLabel(VertexLabel.ACCESS_RULE.label).has(PropertyLabel.CODE.label, "2")
        val values = AbstractMapper.parseMapVertex(userStorage)
        Assert.assertNotNull(AbstractMapper.parseMapValue(values["creationDate"].toString()))
        Assert.assertEquals("2", AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

//    @Test
//    fun createAccessRuleWithoutExpirationDate() {
//        val gson = Gson()
//        val expirationDate = this.addDays(Date(), 1)
//        val properties:List<Property> = listOf(
//                Property("code", "2"),
//                Property("expirationDate", format.format(expirationDate)))
//        val accessRule = VertexData("accessRule", properties)
//        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", accessRule, String::class.java)
//        val obj: CreateAssociationSuccess = gson.fromJson(response.body, CreateAssociationSuccess::class.java)
//        val objExpirationDate = this.format.parse(obj.data.expirationDate)
//        Assert.assertEquals(this.format.format(objExpirationDate), this.format.format(expirationDate))
//        Assert.assertEquals("2", obj.data.code)
//        Assert.assertEquals(true, obj.data.enable)
//        val g = GraphFactory.open().traversal()
//        val userStorage = g.V().hasLabel(VertexLabel.ACCESS_RULE.label).has(PropertyLabel.CODE.label, "2")
//        val values = AbstractMapper.parseMapVertex(userStorage)
//        Assert.assertNotNull(AbstractMapper.parseMapValue(values["creationDate"].toString()))
//        Assert.assertEquals("2", AbstractMapper.parseMapValue(values["code"].toString()))
//        Assert.assertEquals(true, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
//    }

    @Test
    fun cantCreateAccessRuleWithRequiredPropertiesEmpty() {
        val gson = Gson()
        val enable: List<Property> = listOf(Property("enable", "true"))
        val accessRule = VertexData("accessRule", enable)
        val response =  restTemplate.postForEntity("${this.createVertexBaseUrl(this.port)}/add", accessRule, String::class.java)
        val obj = gson.fromJson(response.body, FAILResponse::class.java)
        Assert.assertEquals("@ARCVE-001 Empty Access Rule properties", obj.data)
        val g = GraphFactory.open().traversal()
        val accessRuleStorage = g.V().hasLabel(VertexLabel.ACCESS_RULE.label)
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
        val accessRuleStorage1 = g.V().hasLabel(VertexLabel.ACCESS_RULE.label)
        val values1 = AbstractMapper.parseMapVertex(accessRuleStorage1)
        Assert.assertEquals(3, values1.size)
        Assert.assertEquals("1", AbstractMapper.parseMapValue(values1["code"].toString()))
        Assert.assertEquals(true, AbstractMapper.parseMapValue(values1["enable"].toString()).toBoolean())
    }
}