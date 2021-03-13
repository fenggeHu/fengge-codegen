package hu.jinfeng.codegen.make;

import hu.jinfeng.codegen.config.MakeCodeConfiguration;
import hu.jinfeng.commons.utils.FileUtils;
import hu.jinfeng.commons.utils.NameStringUtils;
import hu.jinfeng.commons.utils.VelocityEngineUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author hujinfeng  @Date 2020/11/28
 **/
@Service
public class MakeService {
    @Resource
    private MakeCodeConfiguration makeCodeConfiguration;

    private String getBaseClassName(String tableName) {
        if (StringUtils.isNotBlank(makeCodeConfiguration.getTablePrefixRemove())
                && tableName.startsWith(makeCodeConfiguration.getTablePrefixRemove())) {
            return tableName.substring(makeCodeConfiguration.getTablePrefixRemove().length());
        }
        return tableName;
    }

    public boolean execute(MakeContext makeContext) {
        Map<String, Object> vmContext = makeContext.buildContext();
        vmContext.put("_nameString", new NameStringUtils());
        String entityClassName = NameStringUtils.toClassName(this.getBaseClassName(makeContext.getTableInfo().getName()));
        vmContext.put("entityClassName", entityClassName);
        //1，生产entity对象
        if (null != makeContext.getEntityPackage()) {
            String vm = FileUtils.readLocalFile(VelocityEngineUtils.LOCAL_RESOURCE_PATH + "template/java/entity.vm");
            String content = VelocityEngineUtils.parseTemplate(vm, vmContext);
            String path = VelocityEngineUtils.LOCAL_TEST_SRC_PATH +
                    FileUtils.package2Path(makeContext.getEntityPackage()) + "/" + entityClassName + ".java";
            FileUtils.writeFile(path, content);
        }
        //2, 生成mapper对象
        if (null != makeContext.getMapperPackage()) {
            String vm = FileUtils.readLocalFile(VelocityEngineUtils.LOCAL_RESOURCE_PATH + "template/java/mapper.vm");
            String content = VelocityEngineUtils.parseTemplate(vm, vmContext);
            String path = VelocityEngineUtils.LOCAL_TEST_SRC_PATH +
                    FileUtils.package2Path(makeContext.getMapperPackage()) + "/" + entityClassName + "Mapper.java";
            FileUtils.writeFile(path, content);
        }


        return true;
    }
}
