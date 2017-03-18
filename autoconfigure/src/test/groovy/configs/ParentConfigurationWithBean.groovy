package configs

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

/**
 * @author tolkv
 * @since 3/18/2017
 */
@TestConfiguration
class ParentConfigurationWithBean {
    @Bean(name = 'test.help.bean')
    String helperBean() {
        'helpMe'
    }
}
