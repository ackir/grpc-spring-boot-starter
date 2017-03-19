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
  @Autowired
  AnnotationConfigApplicationContext applicationContext

  def 'should valid init grpc server bean with default configuration'() {
    given: 'get all server instances. According to config above – two instances'
    def beans = applicationContext.getBeansOfType(GRpcServersWrapper)
    def serversWrapper = beans.values().first()
    def firstServerWith6565Port = serversWrapper.servers.first()
    def secondServerWithRandomPort = serversWrapper.servers[1]

    expect: 'should have only one bean'
    beans.size() == 1

    and: 'contains two configured server'
    serversWrapper.servers.size() == 2

    and: 'should init server with 6565 port according to config'
    with(firstServerWith6565Port) {
      port == DEFAULT_GRPC_PORT
    }

    and: 'should start server with random port'
    with(secondServerWithRandomPort) {
      port != 0
      port != 6565
      println "Port: $port"
    }

  }

  public static final int DEFAULT_GRPC_PORT = 6565
}
