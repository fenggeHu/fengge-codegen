package hu.jinfeng.codegen.vmhelper;

import hu.jinfeng.codegen.config.MakerConfig;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jinfeng.hu  @date 2023/4/12
 **/
@Component
public class NameHelper {
    @Autowired
    private MakerConfig makerConfig;

    public String entityClassName(String baseName) {
        return StringUtils.isNotBlank(makerConfig.getEntityClassSuffix()) ?
                baseName + makerConfig.getEntityClassSuffix().trim() : baseName;
    }

    public String mapperClassName(String baseName) {
        return baseName + "Mapper";
    }

    public String repositoryClassName(String baseName) {
        return baseName + "Repository";
    }

    public String controllerClassName(String baseName) {
        return baseName + "Controller";
    }
}
