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
            // 不包含自增和unique key
            if (col.isAutoIncrement() || tableInfo.getUkNames().contains(col.getName())) continue;
            if (sb.length() > 0) sb.append(", ");
            sb.append("`").append(col.getName()).append("`=VALUES(").append("`").append(col.getName()).append("`)");
        }

        return sb.toString();
    }
}
