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

    private List<ColumnInfo> columns;

    /**
     * 主键字段 - pk可能有多个联合
     **/
    List<String> pkNames = new LinkedList<>();
    List<ColumnInfo> pkColumns = new LinkedList<>();

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
}
