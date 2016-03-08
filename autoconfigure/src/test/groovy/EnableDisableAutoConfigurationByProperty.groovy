import org.grpc.spring.boot.autoconfigure.GRpcAutoConfiguration
import org.grpc.spring.boot.autoconfigure.GRpcServerProperties
import org.grpc.spring.boot.autoconfigure.component.GRpcServersWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.annotation.DirtiesContext
import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.boot.test.EnvironmentTestUtils.addEnvironment

/**
 * @author tolkv
 * @since 07/03/16
 */
@DirtiesContext
class EnableDisableAutoConfigurationByProperty extends Specification {
  AnnotationConfigApplicationContext context

  def setup() {
    context = new AnnotationConfigApplicationContext()
  }

  def clean() {
    context.close()
  }

  @Unroll
  def 'should enable auto configuration by grpc.enabled=#enableProperty property'() {
    given:
    addEnvironment(context, enableProperty, 'grpc.servers[0].address=127.0.0.1', 'grpc.servers[0].port=6329')
    context.register(GRpcAutoConfiguration)
    context.refresh()

    expect:
    context.containsBean('gRpcServersWrapper') == beanIsContained

    where:
    enableProperty       | beanIsContained
    'grpc.enabled=true'  | true
    'grpc.enabled=false' | false
  }


  def 'should customize server address and port'() {
    given:
    String[] args = (['grpc.enabled=true'] + grpcServers).toArray(new String[grpcServers.size()])
    addEnvironment(context, args)
    context.register(GRpcAutoConfiguration)
    context.register(SupressBindUnrealIPAddressGRpcWrapperConfiguration)
    context.refresh()

    expect:
    def bean = context.getBean(GRpcServersWrapper)
    bean.getGRpcServerProperties()
        .servers
        .find { it.port == port && it.address == InetAddress.getByName(ip) }

    bean.servers.size() == grpcServers.collect { it.split(']')[0] }.unique().size()

    where:
    grpcServers                                                           | ip             | port
    ['grpc.servers[0].address=127.0.0.1', 'grpc.servers[0].port=6323']    | '127.0.0.1'    | 6323
    ['grpc.servers[0].address=127.0.0.2', 'grpc.servers[0].port=6326']    | '127.0.0.2'    | 6326
    ['grpc.servers[0].address=192.168.1.19', 'grpc.servers[0].port=6324'] | '192.168.1.19' | 6324
    ['grpc.servers[0].address=127.0.0.18', 'grpc.servers[0].port=1202']   | '127.0.0.18'   | 1202
    ['grpc.servers[0].address=127.0.0.18', 'grpc.servers[0].port=1202',
     'grpc.servers[1].address=127.0.0.19', 'grpc.servers[1].port=1203']   | '127.0.0.18'   | 1202
  }

  @Configuration
  protected static class SupressBindUnrealIPAddressGRpcWrapperConfiguration {
    @Autowired
    ApplicationContext context;
    @Autowired
    GRpcServerProperties gRpcServerProperties;

    @Bean
    public GRpcServersWrapper gRpcServersWrapper() {

      def wrapper = new GRpcServersWrapper(context, gRpcServerProperties);
      wrapper.setServerStartHook({ Server -> println "supress starting" })
      wrapper
    }
  }
}
