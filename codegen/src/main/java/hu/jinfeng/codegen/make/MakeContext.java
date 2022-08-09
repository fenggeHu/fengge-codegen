package hu.jinfeng.codegen.make;

import hu.jinfeng.codegen.model.TableInfo;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
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

    private String paramPackage;

    private String queryPackage;

    private String mapperPackage;

    private String repositoryPackage;

    private String controllerPackage;
    /**
     * 库表信息
     */
    private TableInfo tableInfo;

    /**
     *
     */
    private List<String> entityCols;

    public String getDatabase() {
        return this.database != null ? this.database : this.tableInfo.getDatabase();
    }

    public String getTableName() {
        return this.tableName != null ? this.tableName : this.tableInfo.getName();
    }

    public Map<String, Object> buildContext() {
        Map<String, Object> result = new HashMap<>();
        result.put("database", this.getDatabase());
        result.put("tableName", this.getTableName());
        result.put("tableInfo", this.tableInfo);
        result.put("basePackage", this.getBasePackage());
        result.put("paramPackage", this.getParamPackage());
        result.put("entityPackage", this.getEntityPackage());
        result.put("queryPackage", this.getQueryPackage());
        result.put("mapperPackage", this.getMapperPackage());
        result.put("repositoryPackage", this.getRepositoryPackage());
        result.put("controllerPackage", this.getControllerPackage());
        result.put("entityCols", this.getEntityCols());

        return result;
    }
}
