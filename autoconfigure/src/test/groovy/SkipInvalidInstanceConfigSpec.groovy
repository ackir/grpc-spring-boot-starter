import configs.TestDefaultConfiguration
import org.grpc.spring.boot.autoconfigure.annotation.GRPCLocalPort
import org.junit.Rule
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.rule.OutputCapture
import spock.lang.Ignore
import spock.lang.Specification

import java.util.regex.Matcher

import static org.hamcrest.Matchers.*
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

/**
 * @author tolkv
 * @version 05/03/2017
 */
@SpringBootTest(webEnvironment = NONE, classes = [TestDefaultConfiguration],
    properties = [
        'grpc.servers[1].address=127.0.0.1',
        'grpc.servers[1].port=0',
    ])
class SkipInvalidInstanceConfigSpec extends Specification {
  @GRPCLocalPort
  Integer port

  @Rule
  OutputCapture capture

  @Ignore('broken in spring boot 2.0')
  //TODO FIX
  def 'should skip invalid server config'() {
    expect:
      port != 0
      capture.expect(containsString("is not valid. Skipped"))
  }
}
