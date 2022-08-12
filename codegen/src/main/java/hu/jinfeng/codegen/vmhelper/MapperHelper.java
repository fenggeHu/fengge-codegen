package hu.jinfeng.codegen.vmhelper;

import hu.jinfeng.codegen.model.ColumnInfo;
import hu.jinfeng.codegen.model.TableInfo;
import hu.jinfeng.commons.utils.NameStringUtils;

/**
 * @Description: 构建mybatis mapper的元素
 * @Author Jinfeng.hu  @Date 2022/8/9
 **/
public class MapperHelper {

    public String insertColumns(final TableInfo tableInfo) {
        StringBuilder sb = new StringBuilder();

        for (ColumnInfo col : tableInfo.getInsertColumns()) {
            if (col.isAutoIncrement()) continue;
            if (sb.length() > 0) sb.append(", ");
            sb.append("`").append(col.getName()).append("`");
        }

        return sb.toString();
    }

    public String insertValues(final TableInfo tableInfo) {
        StringBuilder sb = new StringBuilder();

        for (ColumnInfo col : tableInfo.getInsertColumns()) {
            if (col.isAutoIncrement()) continue;
            if (sb.length() > 0) sb.append(", ");
            sb.append("#{").append(NameStringUtils.toPropertyName(col.getName())).append("}");
        }

        return sb.toString();
    }

    public String batchInsertValues(final TableInfo tableInfo) {
        StringBuilder sb = new StringBuilder();

        for (ColumnInfo col : tableInfo.getInsertColumns()) {
            if (col.isAutoIncrement()) continue;
            if (sb.length() > 0) sb.append(", ");
            sb.append("#{it.").append(NameStringUtils.toPropertyName(col.getName())).append("}");
        }

        return sb.toString();
    }

    public String onDuplicateKeyUpdate(final TableInfo tableInfo) {
        StringBuilder sb = new StringBuilder();

        for (ColumnInfo col : tableInfo.getInsertColumns()) {
            // update不包含自增和unique key、分库分表字段
            if (col.isAutoIncrement() || tableInfo.getUkNames().contains(col.getName())
                    || tableInfo.getShardingNames().contains(col.getName())) continue;
            if (sb.length() > 0) sb.append(", ");
            sb.append("`").append(col.getName()).append("`=VALUES(").append("`").append(col.getName()).append("`)");
        }

        return sb.toString();
    }

    /**
     * 分库分表、主键、索引字段组合成where条件
     */
    public String indexWhere(final TableInfo table) {
        StringBuilder sb = new StringBuilder();
        for (String col : table.getIndexNames()) {
            if (sb.length() > 0) sb.append(", ");
            String prop = NameStringUtils.toPropertyName(col);
            sb.append("\"<if test='").append(prop).append(" != null'> AND `")
                    .append(col).append("` = #{").append(prop).append("} </if> \"\n");
        }

        return sb.toString();
    }

    public String indexParams(final TableInfo table) {
        StringBuilder sb = new StringBuilder();
        for (ColumnInfo col : table.getIndexColumns()) {
            if (sb.length() > 0) sb.append(", ");
            String prop = NameStringUtils.toPropertyName(col.getName());
            sb.append("@Param(\"").append(prop).append("\") ").append(col.getJavaType()).append(" ").append(prop);
        }

        return sb.toString();
    }

    public String ukWhere(final TableInfo table) {
        if (table.getUkNames().isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String col : table.getUkNames()) {
            if (sb.length() > 0) {
                sb.append(" AND ");
            }
            sb.append("`").append(col).append("` = #{").append(NameStringUtils.toPropertyName(col)).append("} ");
        }

        return " WHERE " + sb.toString();
    }

    public String ukParam(final TableInfo table) {
        StringBuilder sb = new StringBuilder();
        for (ColumnInfo col : table.getUkColumns()) {
            if (sb.length() > 0) sb.append(", ");
            String prop = NameStringUtils.toPropertyName(col.getName());
            sb.append("@Param(\"").append(prop).append("\") ").append(col.getJavaType()).append(" ").append(prop);
        }

        return sb.toString();
    }

    /**
     * 路由条件
     */
    public String shardingCondition(final TableInfo table) {
        if (table.getShardingNames().isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String col : table.getShardingNames()) {
            if (sb.length() > 0) {
                sb.append(" AND ");
            }
            sb.append("`").append(col).append("` = #{").append(NameStringUtils.toPropertyName(col)).append("}");
        }
        return "(" + sb + ")";
    }
}
