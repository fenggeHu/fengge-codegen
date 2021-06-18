package hu.jinfeng.codegen.model;

import hu.jinfeng.commons.utils.TypeDBAndJava;
import lombok.Data;

/**
 * @Author hujinfeng  @Date 2020/11/27
 **/
@Data
public class ColumnInfo {
    /**
     * COLUMN_NAME
     */
    private String name;
    /**
     * TYPE_NAME
     */
    private String type;
    /**
     * COLUMN_SIZE
     */
    private String size;
    /**
     * REMARKS
     */
    private String remarks;

    /**
     * 默认值 - 供参考：null \ CURRENT_TIMESTAMP \ 0
     */
    private String defaultValue;
    /**
     * TABLE_NAME
     */
    private String tableName;
    /**
     * DB Type - 数据库
     */
    private String dbType;

    /**
     * 是否自增字段
     */
    private boolean autoIncrement;
    /**
     * 字段类型转java类型
     */
    public String getJavaType() {
        return TypeDBAndJava.getJavaType(this.dbType, this.type);
    }
}
