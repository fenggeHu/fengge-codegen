package hu.jinfeng.codegen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author hujinfeng  @Date 2020/11/27
 **/
@SpringBootApplication(scanBasePackages = "hu.jinfeng.codegen")
@EnableSwagger2
public class CodegenApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodegenApplication.class, args);
    }
}
