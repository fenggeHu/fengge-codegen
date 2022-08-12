package hu.jinfeng.codegen.vmhelper;

import hu.jinfeng.codegen.model.ColumnInfo;
import hu.jinfeng.codegen.model.TableInfo;
import hu.jinfeng.commons.utils.NameStringUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 构建mybatis mapper的元素
 * @Author Jinfeng.hu  @Date 2022/8/9
 **/
public class MapperHelper {
    /**
     * 字符太长了加入换行
     */
    private int newLine(final StringBuilder sb, int start) {
        if (sb.length() - start > 80) {
            sb.append("\"+\n\"");
            start = sb.length();
        }
        return start;
    }

    public String insertColumns(final TableInfo tableInfo) {
        StringBuilder sb = new StringBuilder();
        int start = 0;
        for (ColumnInfo col : tableInfo.getInsertColumns()) {
            if (col.isAutoIncrement()) continue;
            start = newLine(sb, start); // 太长了换行
            if (sb.length() > 0) sb.append(", ");
            sb.append("`").append(col.getName()).append("`");
        }

        return sb.toString();
    }

    public String insertValues(final TableInfo tableInfo) {
        StringBuilder sb = new StringBuilder();
        int start = 0;
        for (ColumnInfo col : tableInfo.getInsertColumns()) {
            if (col.isAutoIncrement()) continue;
            start = newLine(sb, start);
            if (sb.length() > 0) sb.append(", ");
            sb.append("#{").append(NameStringUtils.toPropertyName(col.getName())).append("}");
        }

        return sb.toString();
    }

    public String batchInsertValues(final TableInfo tableInfo) {
        StringBuilder sb = new StringBuilder();
        int start = 0;
        for (ColumnInfo col : tableInfo.getInsertColumns()) {
            if (col.isAutoIncrement()) continue;
            start = newLine(sb, start); // 太长了换行
            if (sb.length() > 0) sb.append(", ");
            sb.append("#{it.").append(NameStringUtils.toPropertyName(col.getName())).append("}");
        }

        return sb.toString();
    }

    public String onDuplicateKeyUpdate(final TableInfo tableInfo) {
        StringBuilder sb = new StringBuilder();
        int start = 0;
        for (ColumnInfo col : tableInfo.getInsertColumns()) {
            // update不包含自增和unique key、分库分表字段
            if (col.isAutoIncrement() || tableInfo.getUkNames().contains(col.getName())
                    || tableInfo.getShardingNames().contains(col.getName())) continue;
            start = newLine(sb, start); // 太长了换行
            if (sb.length() > 0) sb.append(", ");
            sb.append("`").append(col.getName()).append("`=VALUES(").append("`").append(col.getName()).append("`)");
        }

        return sb.toString();
    }

    public String updateSet(final TableInfo table) {
        StringBuilder sb = new StringBuilder();
        int start = 0;
        List<String> names = table.getUpdateColumns().stream().map(e -> e.getName()).collect(Collectors.toList());
        for (String col : names) {
            start = newLine(sb, start); // 太长了换行
            if (sb.length() > 0) sb.append(", ");
            sb.append("`").append(col).append("` = #{").append(NameStringUtils.toPropertyName(col)).append("} ");
        }
        return sb.toString();
    }

    /**
     * 构建where条件
     */
    public String buildWhere(final Collection<String> names, final Collection<String> excludes) {
        StringBuilder sb = new StringBuilder();
        for (String col : names) {
            if (null != excludes && excludes.contains(col)) continue;
            if (sb.length() > 0) {
                sb.append(" AND ");
            }
            String prop = NameStringUtils.toPropertyName(col);
            sb.append("`").append(col).append("` = #{").append(prop).append("} ");
        }

        return sb.toString();
    }

    /**
     * 构建test where条件
     */
    public String buildTestWhere(final Collection<String> names, final Collection<String> excludes) {
        StringBuilder sb = new StringBuilder();
        for (String col : names) {
            if (null != excludes && excludes.contains(col)) continue;
            if (sb.length() > 0) sb.append(", ");
            String prop = NameStringUtils.toPropertyName(col);
            sb.append("\"<if test='").append(prop).append(" != null'> AND `")
                    .append(col).append("` = #{").append(prop).append("} </if> \"\n");
        }

        return sb.toString();
    }

    /**
     * 构建方法入参
     */
    public String buildParam(final Collection<ColumnInfo> columns, final Collection<String> excludes) {
        StringBuilder sb = new StringBuilder();
        for (ColumnInfo col : columns) {
            if (null != excludes && excludes.contains(col.getName())) continue;
            if (sb.length() > 0) sb.append(", ");
            String prop = NameStringUtils.toPropertyName(col.getName());
            sb.append("@Param(\"").append(prop).append("\") ").append(col.getJavaType()).append(" ").append(prop);
        }

        return sb.toString();
    }

    /**
     * 分库分表、主键、索引字段组合成where条件
     */
    public String indexWhere(final TableInfo table) {
        String cond = buildTestWhere(table.getIndexNames(), null);
        String sharding = buildTestWhere(table.getShardingNames(), table.getIndexNames());
        return sharding.length() == 0 ? cond : cond + " \n, " + sharding;
    }

    public String indexParams(final TableInfo table) {
        String param = buildParam(table.getIndexColumns(), null);
        String sharding = buildParam(table.getShardingColumns(), table.getIndexNames());
        return sharding.length() == 0 ? param : param + " \n, " + sharding;
    }

    public String ukWhere(final TableInfo table) {
        String cond = buildWhere(table.getUkNames(), null);
        String sharding = buildWhere(table.getShardingNames(), table.getUkNames());
        cond = sharding.length() == 0 ? cond : cond + " \n, " + sharding;
        return cond.length() == 0 ? "" : " WHERE " + cond;
    }

    public String ukParam(final TableInfo table) {
        String param = buildParam(table.getUkColumns(), null);
        String sharding = buildParam(table.getShardingColumns(), table.getUkNames());
        return sharding.length() == 0 ? param : param + " \n, " + sharding;
    }

    public String pkWhere(final TableInfo table) {
        String cond = buildWhere(table.getPkNames(), null);
        String sharding = buildWhere(table.getShardingNames(), table.getPkNames());
        cond = sharding.length() == 0 ? cond : cond + " AND " + sharding;
        return cond.length() == 0 ? "" : " WHERE " + cond;
    }

    public String pkParam(final TableInfo table) {
        String param = buildParam(table.getPkColumns(), null);
        String sharding = buildParam(table.getShardingColumns(), table.getPkNames());
        return sharding.length() == 0 ? param : param + " \n, " + sharding;
    }

    public String genAllSqlWithIndex(final TableInfo table) {

        return null;
    }

}
