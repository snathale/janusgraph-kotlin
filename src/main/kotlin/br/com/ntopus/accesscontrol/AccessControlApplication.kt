package br.com.ntopus.accesscontrol

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["br"])
class AccessControlApplication {

//    @Bean
//    fun graph(): JanusGraph {
//        return JanusGraphFactory.open(ClassPathResource("janusgraph-cql-es.properties").file.absolutePath)
//    }
}

fun main(args: Array<String>) {
    SpringApplication.run(AccessControlApplication::class.java, *args)
}
