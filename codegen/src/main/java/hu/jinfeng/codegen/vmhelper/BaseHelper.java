package hu.jinfeng.codegen.vmhelper;

import hu.jinfeng.codegen.config.MakerConfig;
import hu.jinfeng.codegen.model.ColumnInfo;
import hu.jinfeng.commons.utils.NameStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author jinfeng.hu  @Date 2022/8/12
 **/
public class BaseHelper {
    @Autowired
    private MakerConfig makerConfig;

    public String getBaseClassName(String tableName) {
        if (StringUtils.isNotBlank(makerConfig.getTablePrefixRemove())
                && tableName.toLowerCase().startsWith(makerConfig.getTablePrefixRemove().toLowerCase())) {
            return tableName.substring(makerConfig.getTablePrefixRemove().length());
        }
        return tableName;
    }

    /**
     * 字符太长了加入换行
     */
    protected int newLine(final StringBuilder sb, int start) {
        if (sb.length() - start > 80) {
            sb.append("\"+\n\"");
            start = sb.length();
        }
        return start;
    }

    /**
     * 合并method条件
     */
    public String mergeParam(final Collection<ColumnInfo> column1, final Collection<ColumnInfo> column2) {
        StringBuilder sb = new StringBuilder();
        for (ColumnInfo col : column1) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            String prop = NameStringUtils.toPropertyName(col.getName());
            sb.append(col.getJavaType()).append(" ").append(prop);
        }
        List<String> names = column1.stream().map(e -> e.getName()).collect(Collectors.toList());
        for (ColumnInfo col : column2) {
            // 去重
            if (names.contains(col.getName())) continue;
            if (sb.length() > 0) {
                sb.append(", ");
            }
            String prop = NameStringUtils.toPropertyName(col.getName());
            sb.append(col.getJavaType()).append(" ").append(prop);
        }

        return sb.toString();
    }

    /**
     * 构建method条件
     */
    public String buildParam(final Collection<ColumnInfo> columnInfos, final Collection<String> excludes) {
        StringBuilder sb = new StringBuilder();
        for (ColumnInfo col : columnInfos) {
            if (null != excludes && excludes.contains(col.getName())) continue;
            if (sb.length() > 0) {
                sb.append(", ");
            }
            String prop = NameStringUtils.toPropertyName(col.getName());
            sb.append(col.getJavaType()).append(" ").append(prop);
        }

        return sb.toString();
    }

    /**
     * 构建调用入参
     */
    public String buildArgs(final Collection<String> columnInfos, final Collection<String> excludes) {
        StringBuilder sb = new StringBuilder();
        for (String col : columnInfos) {
            if (null != excludes && excludes.contains(col)) continue;
            if (sb.length() > 0) {
                sb.append(", ");
            }
            String prop = NameStringUtils.toPropertyName(col);
            sb.append(prop);
        }

        return sb.toString();
    }

    public String mergeArgs(final Collection<String> column1, final Collection<String> column2) {
        StringBuilder sb = new StringBuilder();
        for (String col : column1) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            String prop = NameStringUtils.toPropertyName(col);
            sb.append(prop);
        }
        for (String col : column2) {
            if (column1.contains(col)) continue;
            if (sb.length() > 0) {
                sb.append(", ");
            }
            String prop = NameStringUtils.toPropertyName(col);
            sb.append(prop);
        }

        return sb.toString();
    }
}
