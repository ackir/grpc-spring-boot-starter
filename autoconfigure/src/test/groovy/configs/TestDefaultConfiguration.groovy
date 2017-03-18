package configs

import org.grpc.spring.boot.autoconfigure.annotation.EnableGRpcServer
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Configuration

/**
 * @author tolkv
 * @since 07/03/16
 */
@SpringBootConfiguration
@EnableGRpcServer
class TestDefaultConfiguration {
}
