package hu.jinfeng.codegen.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

/**
 * @Author hujinfeng  @Date 2021/3/10
 **/
@Configuration
@Service
@Data
public class MakeCodeConfiguration {

    /**
     * 生成代码的类名删除表前缀
     */
    @Value("${fengge.code.table.prefix.remove}")
    private String tablePrefixRemove;
    /**
     * 生成代码输出目录
     */
    @Value("${fengge.code.output.path}")
    private String codeOutputPath;

}
