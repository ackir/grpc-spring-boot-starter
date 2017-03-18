import configs.ParentConfigurationWithBean
import configs.TestDefaultConfiguration
import org.grpc.spring.boot.autoconfigure.GRpcServerProperties
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import spock.lang.Specification

/**
 * @author tolkv
 * @since 3/18/2017
 */
class ParentContextTest extends Specification {

    def 'should init with parent context'() {
        given: 'random port configuration'
        def properties = new Properties()
        properties.with {
            put 'grpc.servers[0].address', '127.0.0.1'
            put 'grpc.servers[0].port', '0'
        }

        ConfigurableApplicationContext context = new SpringApplicationBuilder()
                .parent(ParentConfigurationWithBean)
                .child(TestDefaultConfiguration)
                .properties(properties)
                .run()

        expect: 'bean from starter'
        context.getBean(GRpcServerProperties)
        and: 'bean from additional parent context'
        context.getBean('test.help.bean')
    }
}
