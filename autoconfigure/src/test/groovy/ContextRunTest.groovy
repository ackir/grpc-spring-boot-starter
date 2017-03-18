import configs.TestDefaultConfiguration
import org.grpc.spring.boot.autoconfigure.component.GRpcServersWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

/**
 * @author tolkv
 * @since 07/03/16
 */
@SpringBootTest(webEnvironment = NONE, classes = [TestDefaultConfiguration],
        properties=[
                'grpc.servers[0].address=127.0.0.1',
                'grpc.servers[0].port=6565',
                'grpc.servers[1].address=127.0.0.1',
                'grpc.servers[1].port=0',
        ])
class ContextRunTest extends Specification {
  public static final int DEFAULT_GRPC_PORT = 6565
  @Autowired
  AnnotationConfigApplicationContext applicationContext

  def setup() {

  }

  def 'should valid init grpc server bean with default configuration'() {
    given:
    def servers = applicationContext.getBeansOfType(GRpcServersWrapper)

    expect:
    servers.size() == 1
    GRpcServersWrapper serversWrapper = servers.values().first()
    def serverWithDefaultPort = serversWrapper.servers.first()
    def serverWithRandomPort = serversWrapper.servers[1]

    and:
    with(serverWithDefaultPort) {
      port == DEFAULT_GRPC_PORT
    }

    and:
    with(serverWithRandomPort) {
      port != 0
      println "Port: $port"
    }

    and:
    serversWrapper.servers.size() == 2
  }

  def clean() {
    if (applicationContext != null)
      applicationContext.close()
  }
}
