package hu.jinfeng.codegen.model;

import hu.jinfeng.commons.utils.TypeDBAndJava;
import lombok.Builder;
import lombok.Data;

/**
 * @Author hujinfeng  @Date 2020/11/27
 **/
@Data
@Builder
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
     * int类型size+1;
     * TINYTEXT=255; TEXT=65535; MEDIUMTEXT=16777215; LONGTEXT=2147483647
     * TIMESTAMP=23
     */
    private long size;
    /**
     * DECIMAL_DIGITS
     */
    private Integer digits;
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
     * 是否可为空
     */
    private boolean nullAble;
    /**
     * 字段类型转java类型
     */
    public String getJavaType() {
        return TypeDBAndJava.getJavaType(this.dbType, this.type);
    }
}
