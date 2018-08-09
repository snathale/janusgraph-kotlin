package br.com.ntopus.accesscontrol

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.VertexData
import br.com.ntopus.accesscontrol.model.vertex.User
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import br.com.ntopus.accesscontrol.model.vertex.base.SUCCESSResponse
import br.com.ntopus.accesscontrol.model.vertex.mapper.LocalUser
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.boot.web.server.LocalServerPort
import java.text.SimpleDateFormat
import java.util.*

data class LocalSuccess(val status: String, val data: LocalUser)
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiControllerTests {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @LocalServerPort
    private val port: Int = 0

    @Before
    fun setup() {
        GraphFactory.setInstance("janusgraph-inmemory.properties")
    }

    @Test
    fun createUser() {
        val gson = Gson()
        val initialDate = Date().time
        val properties:List<Property> = listOf(Property("code", "1"), Property("name", "test"))
        val user = VertexData("user", properties)
        val response =  restTemplate.postForEntity("${this.createBaseUrl()}/addVertex", user, String::class.java)
        val obj: LocalSuccess = gson.fromJson(response.body, LocalSuccess::class.java)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val finalDate = format.parse(obj.data.creationDate).time
        val condition = finalDate > initialDate
        Assert.assertFalse(condition)
        Assert.assertEquals("test", obj.data.name)
        Assert.assertEquals("1", obj.data.code)
        Assert.assertEquals("null", obj.data.observation)
        Assert.assertEquals(true, obj.data.enable)
    }

    fun createBaseUrl(): String {
        return "http://localhost:$port/api/v1"
    }

}