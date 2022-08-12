package hu.jinfeng.codegen.vmhelper;

import hu.jinfeng.codegen.model.TableInfo;
import org.springframework.stereotype.Component;


/**
 * @Description:
 * @Author jinfeng.hu  @Date 2022/8/12
 **/
@Component
public class RepositoryHelper extends BaseHelper {

    public String pkParam(final TableInfo table) {
        return mergeParam(table.getPkColumns(), table.getShardingColumns());
    }

    public String pkArgs(final TableInfo table) {
        return mergeArgs(table.getPkNames(), table.getShardingNames());
    }
}
