package jerry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@EnableWebMvc
@SpringBootApplication
public class SpringMain {

    public static void main(String[] args)  throws Exception {
        SpringApplication app = new SpringApplication(SpringMain.class);
        app.run(args);
    }
}
