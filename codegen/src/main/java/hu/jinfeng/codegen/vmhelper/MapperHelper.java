package hu.jinfeng.codegen.vmhelper;

import hu.jinfeng.codegen.model.ColumnInfo;
import hu.jinfeng.codegen.model.TableInfo;
import hu.jinfeng.commons.utils.NameStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 构建mybatis mapper的元素
 * @Author Jinfeng.hu  @Date 2022/8/9
 **/
@Component
public class MapperHelper extends BaseHelper {
    @Autowired
    private NameHelper nameHelper;

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
     * 构建where条件
     */
    public String mergeWhere(final Collection<String> names, final Collection<String> names2) {
        StringBuilder sb = new StringBuilder();
        for (String col : names) {
            if (sb.length() > 0) {
                sb.append(" AND ");
            }
            String prop = NameStringUtils.toPropertyName(col);
            sb.append("`").append(col).append("` = #{").append(prop).append("} ");
        }
        for (String col : names2) {
            if (names.contains(col)) continue;
            if (sb.length() > 0) {
                sb.append(" AND ");
            }
            String prop = NameStringUtils.toPropertyName(col);
            sb.append("`").append(col).append("` = #{").append(prop).append("} ");
        }

        return sb.toString();
    }

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
     * 构建test where条件
     */
    public String mergeTestWhere(final Collection<String> names, final Collection<String> names2) {
        StringBuilder sb = new StringBuilder();
        for (String col : names) {
            if (sb.length() > 0) sb.append(", ");
            String prop = NameStringUtils.toPropertyName(col);
            sb.append("\"<if test='").append(prop).append(" != null'> AND `")
                    .append(col).append("` = #{").append(prop).append("} </if> \"\n");
        }
        for (String col : names2) {
            if (names.contains(col)) continue;
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
    public String buildMybatisParam(final Collection<ColumnInfo> columns, final Collection<String> excludes) {
        StringBuilder sb = new StringBuilder();
        for (ColumnInfo col : columns) {
            if (null != excludes && excludes.contains(col.getName())) continue;
            if (sb.length() > 0) sb.append(", ");
            String prop = NameStringUtils.toPropertyName(col.getName());
            sb.append("@Param(\"").append(prop).append("\") ").append(col.getJavaType()).append(" ").append(prop);
        }

        return sb.toString();
    }

    public String mergeMybatisParam(final Collection<ColumnInfo> columns, final Collection<ColumnInfo> columns2) {
        StringBuilder sb = new StringBuilder();
        for (ColumnInfo col : columns) {
            if (sb.length() > 0) sb.append(", ");
            String prop = NameStringUtils.toPropertyName(col.getName());
            sb.append("@Param(\"").append(prop).append("\") ").append(col.getJavaType()).append(" ").append(prop);
        }
        List<String> names = columns.stream().map(e -> e.getName()).collect(Collectors.toList());
        for (ColumnInfo col : columns2) {
            if (names.contains(col.getName())) continue;
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
        return mergeTestWhere(table.getIndexNames(), table.getShardingNames());
    }

    public String indexParams(final TableInfo table) {
        return mergeMybatisParam(table.getIndexColumns(), table.getShardingColumns());
    }

    public String ukWhere(final TableInfo table) {
        String cond = mergeWhere(table.getUkNames(), table.getShardingNames());
        return cond.length() == 0 ? "" : " WHERE " + cond;
    }

    public String ukParam(final TableInfo table) {
        return mergeMybatisParam(table.getUkColumns(), table.getShardingColumns());
    }

    public String pkWhere(final TableInfo table) {
        String cond = mergeWhere(table.getPkNames(), table.getShardingNames());
        return cond.length() == 0 ? "" : " WHERE " + cond;
    }

    public String pkParam(final TableInfo table) {
        return mergeMybatisParam(table.getPkColumns(), table.getShardingColumns());
    }

    public String genAllSqlWithIndex(final TableInfo table) {
        String entityClassName = nameHelper.entityClassName(this.getBaseClassName(table.getName().toLowerCase()));
        return genAllSqlWithIndex(table, entityClassName);
    }

    public String genAllSqlWithIndex(final TableInfo table, String entityClassName) {
        StringBuilder sb = new StringBuilder();
        // 所有的索引创建一个查询
        List<ColumnInfo> allIndex = table.getAllIndexColumns();
        sb.append("/** 按所有索引字段查询 */\n")
                .append(this.getSelect(table.getName())).append(" WHERE 1=1 \",");
        List<String> allNames = allIndex.stream().map(e -> e.getName()).collect(Collectors.toList());
        String allCond = mergeTestWhere(allNames, table.getShardingNames());
        sb.append(allCond).append(",\" limit #{size} </script>\"").append("\n})\n")
                .append("List<").append(entityClassName).append("> ");
        sb.append("selectByIndex(");
        String allParam = mergeMybatisParam(allIndex, table.getShardingColumns());
        sb.append(allParam).append(", @Param(\"size\") Integer size").append(");\n\n");

        // 每组索引创建一个查询
        if (!table.getIndexMap().isEmpty()) {
            for (Map.Entry<String, List<ColumnInfo>> entry : table.getIndexMap().entrySet()) {
                String index = entry.getKey();
                sb.append("/** 按索引 ").append(index).append(" 查询 */\n")
                        .append(this.getSelect(table.getName())).append(" WHERE 1=1 \",");
                List<String> names = entry.getValue().stream().map(e -> e.getName()).collect(Collectors.toList());
                String cond = mergeTestWhere(names, table.getShardingNames());
                sb.append(cond).append(",\" limit #{size} </script>\"").append("\n})\n")
                        .append("List<").append(entityClassName).append("> ");

                String method = "selectBy" + NameStringUtils.toClassName(index);
                sb.append(method).append("(");

                String param = mergeMybatisParam(entry.getValue(), table.getShardingColumns());
                sb.append(param).append(", @Param(\"size\") Integer size").append(");\n\n");
            }
        }
        return sb.toString();
    }

    private String getSelect(String tableName) {
        return "@Select({\"<script>\",\n \"SELECT * FROM `" + tableName + "`";
    }
}
