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
@SpringBootTest(webEnvironment = NONE, classes = [TestDefaultConfiguration])
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
    GRpcServersWrapper server = servers.values().first()

    and:
    with(server.getGRpcServerProperties().servers.first()) {
      address == InetAddress.getLocalHost()
      port == DEFAULT_GRPC_PORT
    }

    and:
    server.servers.size() == 2
  }

  def clean() {
    if (applicationContext != null)
      applicationContext.close()
  }
}
