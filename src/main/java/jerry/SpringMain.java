package jerry;

import jerry.device.SerialDevice;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Scanner;


@EnableWebMvc
@SpringBootApplication
public class SpringMain {


    public static void main(String[] args)  throws Exception {
        SpringApplication app = new SpringApplication(SpringMain.class);
        app.run(args);
    }

}
