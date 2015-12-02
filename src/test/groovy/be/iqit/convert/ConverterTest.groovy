package be.iqit.convert

import com.fasterxml.jackson.databind.JsonNode
import spock.lang.Specification

/**
 * Created by dvanroeyen on 02/12/15.
 */
class ConverterTest extends Specification {

    FactoryConverter converter = new FactoryConverter().withDefaultConverter(new ObjectMapperConverter())

    def "can convert to String"() {
        given:
        TestDomainA domain = new TestDomainA(id:1, name:"test")

        when:
        String result = converter.convert(domain, String).toBlocking().first()

        then:
        result == '{"id":1,"name":"test"}'
    }


    def "can convert to JsonNode"() {
        given:
        TestDomainA domain = new TestDomainA(id:1, name:"test")

        when:
        JsonNode result = converter.convert(domain, JsonNode).toBlocking().first()

        then:
        result.toString() == '{"id":1,"name":"test"}'
    }

    def "can convert from String"() {
        given:
        String value = '{"id":1,"name":"test"}'

        when:
        TestDomainA domain = converter.convert(value, TestDomainA).toBlocking().first()

        then:
        domain != null
        domain.id == 1
        domain.name == 'test'
    }

    def "can convert from JsonNode"() {
        given:
        JsonNode value = converter.convert(new TestDomainA(id:1,name:"test"), JsonNode).toBlocking().first()

        when:
        TestDomainA domain = converter.convert(value, TestDomainA).toBlocking().first()

        then:
        domain != null
        domain.id == 1
        domain.name == 'test'
    }

    def "can convert from A to B"() {
        given:
        TestDomainA value = new TestDomainA(id:1,name:"test")

        when:
        TestDomainB domain = converter.convert(value, TestDomainB).toBlocking().first()

        then:
        domain != null
        domain instanceof TestDomainB
        domain.id == 1
        domain.name == 'test'
    }

    def "can convert a list"() {
        given:
        def values = [new TestDomainA(id:1,name:"test1"),new TestDomainA(id:2,name:"test2"),new TestDomainB(id:3,name:"test3")]

        when:
        def domains = converter.convert(values, TestDomainB).toBlocking().first()

        then:
        domains != null
        domains.size()==3
    }

    def "can convert null"() {
        given:
        Object value = null

        when:
        Object result = converter.convert(value, TestDomainA).toBlocking().first()

        then:
        result == null
    }

    def "can convert nested"() {
        given:
        TestDomainA value = new TestDomainA(id:1,name:"test")

        when:
        String result = converter.convert(converter.convert(value, TestDomainB), String).toBlocking().first()

        then:
        result == '{"id":1,"name":"test"}'
    }

    def "can convert throwable to String"() {
        given:
        Throwable value = new IllegalArgumentException("some exception")

        when:
        Object result = converter.convert(converter.convert(value, TestErrorDTO), String).toBlocking().first()

        then:
        result == '{"message":"some exception"}'

    }
}
