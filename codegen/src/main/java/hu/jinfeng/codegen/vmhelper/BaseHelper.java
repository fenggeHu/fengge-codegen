package hu.jinfeng.codegen.vmhelper;

import hu.jinfeng.codegen.config.MakeConfig;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description:
 * @Author jinfeng.hu  @Date 2022/8/12
 **/
public class BaseHelper {
    @Autowired
    private MakeConfig makeConfig;

    public String getBaseClassName(String tableName) {
        if (StringUtils.isNotBlank(makeConfig.getTablePrefixRemove())
                && tableName.toLowerCase().startsWith(makeConfig.getTablePrefixRemove().toLowerCase())) {
            return tableName.substring(makeConfig.getTablePrefixRemove().length());
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

}
