package hu.jinfeng.codegen.model;

import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author hujinfeng  @Date 2020/11/27
 **/
@Data
public class TableInfo {
    // 表名
    private String name;

    private String remarks;

    private String database;
    /**
     * 所有字段
     */
    List<ColumnInfo> columns = new ArrayList<>();

    /**
     * insert字段
     */
    List<ColumnInfo> insertColumns = new LinkedList<>();
    /**
     * update字段
     */
    List<ColumnInfo> updateColumns = new LinkedList<>();

    /**
     * index columns - 把多个索引分开
     */
    Map<String, List<ColumnInfo>> indexMap = new HashMap<>();
    List<String> indexNames = new LinkedList<>();
    List<ColumnInfo> indexColumns = new LinkedList<>();
    /**
     * 主键字段 - pk可能有多个联合
     **/
    List<String> pkNames = new LinkedList<>();
    List<ColumnInfo> pkColumns = new LinkedList<>();
    /**
     * unique字段 - uk可能有多个字段联合 - 不考虑一个表有多个uk的情况
     **/
    List<String> ukNames = new LinkedList<>();
    List<ColumnInfo> ukColumns = new LinkedList<>();

    /**
     * 用于分库分表的字段
     */
    List<String> shardingNames = new LinkedList<>();
    List<ColumnInfo> shardingColumns = new LinkedList<>();

    /**
     * 返回去重后的所有索引字段
     */
    public List<ColumnInfo> getAllIndexColumns() {
        List<ColumnInfo> ret = new LinkedList<>();
        ret.addAll(pkColumns);
        List<ColumnInfo> adds = ukColumns.stream().filter(e -> {
            for (ColumnInfo col : ret) {
                if (col.getName().equals(e.getName())) return false;
            }
            return true;
        }).collect(Collectors.toList());
        ret.addAll(adds);
        adds = indexColumns.stream().filter(e -> {
            for (ColumnInfo col : ret) {
                if (col.getName().equals(e.getName())) return false;
            }
            return true;
        }).collect(Collectors.toList());
        ret.addAll(adds);

        return ret;
    }

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
     *
     * @return
     */
    public ColumnInfo getAutoIncrementField() {
        if (CollectionUtils.isEmpty(columns)) return null;
        for (ColumnInfo columnInfo : columns) {
            if (columnInfo.isAutoIncrement()) return columnInfo;
        }

        return null;
    }

    /**
     * 字段名查字段信息
     */
    public ColumnInfo getColumnInfo(String colName) {
        if (CollectionUtils.isEmpty(columns)) return null;
        for (ColumnInfo columnInfo : columns) {
            if (columnInfo.getName().equalsIgnoreCase(colName)) {
                return columnInfo;
            }
        }
        return null;
    }

}
