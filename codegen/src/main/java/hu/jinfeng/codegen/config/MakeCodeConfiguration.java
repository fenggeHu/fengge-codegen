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
    @Value("${table.prefix.remove}")
    private String tablePrefixRemove;
    /**
     * 生成代码输出目录
     */
    @Value("${output.path}")
    private String codeOutputPath;
    /**
     * insert语句排除字段
     */
    @Value("${mapper.insert.exclude}")
    private String[] insertExclude;
    /**
     * update语句排除字段
     */
    @Value("${mapper.update.exclude}")
    private String[] updateExclude;

}
