package hu.jinfeng.codegen.make;

import hu.jinfeng.codegen.db.Table;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author hujinfeng  @Date 2020/11/28
 **/
@Data
public class MakeContext  {

    private String database;

    private String tableName;

    private String basePackage = "hu.jinfeng";

    private String modelPackage;

    private String queryPackage;

    private String mapperPackage;

    private String servicePackage;

    private String controllerPackage;
    /**
     *
     */
    private Table table;

    public Map<String, Object> getContext() {
        Map<String, Object> result = new HashMap<>();
        result.put("database", this.getDatabase());
        result.put("tableName", this.getTableName());
        result.put("table", this.table);
        result.put("modelPackage", this.getModelPackage());
        result.put("queryPackage", this.getQueryPackage());
        result.put("mapperPackage", this.getMapperPackage());
        result.put("servicePackage", this.getServicePackage());
        result.put("controllerPackage", this.getControllerPackage());

        return result;
    }
}
