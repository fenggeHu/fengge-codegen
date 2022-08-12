package hu.jinfeng.codegen.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;

/**
 * @Author hujinfeng  @Date 2021/3/10
 **/
@Configuration
@Service
@Data
public class MakeConfig {

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
     * 代码模板目录
     */
    @Value("${template.path}")
    private String templatePath;
    /**
     * insert语句排除字段
     */
    @Value("${mapper.insert.exclude:}")
    private String[] insertExclude;
    /**
     * update语句排除字段
     */
    @Value("${mapper.update.exclude:}")
    private String[] updateExclude;
    /**
     * 指定Entity基类属性，并在生成entity时继承Entity并过滤掉相关属性/字段
     */
    @Value("${entity.column.include:}")
    private String[] entityInclude;
    /**
     * 分库分表字段
     */
    @Value("${table.sharding.fields:}")
    private String[] shardingFields;

    public String getCodeOutputPath() {
        String resource = this.getClass().getResource("/").getPath();
        String root = Paths.get(resource).getParent().getParent().getParent().toString();
        return root + codeOutputPath;
    }

    public static boolean isInclude(String[] cols, String field) {
        if (null == cols) return false;
        for (String f : cols) {
            if (f.equalsIgnoreCase(field)) return true;
        }
        return false;
    }

    public boolean isShardingField(String field) {
        return isInclude(shardingFields, field);
    }
}
