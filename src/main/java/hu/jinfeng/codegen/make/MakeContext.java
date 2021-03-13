package hu.jinfeng.codegen.make;

import hu.jinfeng.codegen.db.TableInfo;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author hujinfeng  @Date 2020/11/28
 **/
@Data
public class MakeContext {

    private String database;

    private String tableName;

    private String basePackage = "hu.jinfeng";

    private String entityPackage;

    private String queryPackage;

    private String mapperPackage;

    private String servicePackage;

    private String controllerPackage;
    /**
     *
     */
    private TableInfo tableInfo;

    public Map<String, Object> buildContext() {
        Map<String, Object> result = new HashMap<>();
        result.put("database", this.getDatabase());
        result.put("tableName", this.getTableName());
        result.put("table", this.tableInfo);
        result.put("entityPackage", this.getEntityPackage());
        result.put("queryPackage", this.getQueryPackage());
        result.put("mapperPackage", this.getMapperPackage());
        result.put("servicePackage", this.getServicePackage());
        result.put("controllerPackage", this.getControllerPackage());

        return result;
    }
}
