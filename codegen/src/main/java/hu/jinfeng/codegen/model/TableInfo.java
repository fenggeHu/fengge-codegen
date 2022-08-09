package hu.jinfeng.codegen.model;

import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author hujinfeng  @Date 2020/11/27
 **/
@Data
public class TableInfo {

    private String name;

    private String remarks;

    private String database;
    /**
     * 所有字段
     */
    private List<ColumnInfo> columns;

    /**
     * insert sql
     */
    private List<ColumnInfo> insertColumns;
    /**
     * update sql
     */
    private List<ColumnInfo> updateColumns;
    /**
     * index columns
     */
    private List<ColumnInfo> indexColumns = new LinkedList<>();

    /**
     * 主键字段 - pk可能有多个联合
     **/
    List<String> pkNames = new LinkedList<>();
    List<ColumnInfo> pkColumns = new LinkedList<>();
    /**
     * unique字段 - uk可能有多个联合
     **/
    List<String> ukNames = new LinkedList<>();
    List<ColumnInfo> ukColumns = new LinkedList<>();

    public boolean hasDateType() {
        if (null != columns) {
            for (ColumnInfo columnInfo : columns) {
                if ("Date".equalsIgnoreCase(columnInfo.getJavaType())) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean hasDateInIndex() {
        if (null != indexColumns) {
            for (ColumnInfo columnInfo : indexColumns) {
                if ("Date".equalsIgnoreCase(columnInfo.getJavaType())) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean hasTimestampType() {
        if (null != columns) {
            for (ColumnInfo columnInfo : columns) {
                if ("Timestamp".equalsIgnoreCase(columnInfo.getJavaType())) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean hasTimestampInIndex() {
        if (null != indexColumns) {
            for (ColumnInfo columnInfo : indexColumns) {
                if ("Timestamp".equalsIgnoreCase(columnInfo.getJavaType())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 拿到第一个自增字段（暂不考虑多个自增字段）
     * @return
     */
    public String getAutoIncrementField(){
        if(CollectionUtils.isEmpty(columns)) return null;
        for (ColumnInfo columnInfo : columns) {
            if(columnInfo.isAutoIncrement()) return columnInfo.getName();
        }

        return null;
    }

    /**
     * 字段名查字段信息
     */
    public ColumnInfo getColumnInfo(String colName) {
        if(CollectionUtils.isEmpty(columns)) return null;
        for (ColumnInfo columnInfo : columns) {
            if(columnInfo.getName().equalsIgnoreCase(colName)) {
                return columnInfo;
            }
        }
        return null;
    }

}
