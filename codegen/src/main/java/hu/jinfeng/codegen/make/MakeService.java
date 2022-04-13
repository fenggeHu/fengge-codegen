package hu.jinfeng.codegen.make;

import com.google.googlejavaformat.java.Formatter;
import hu.jinfeng.codegen.config.MakeCodeConfiguration;
import hu.jinfeng.commons.utils.FileUtils;
import hu.jinfeng.commons.utils.NameStringUtils;
import hu.jinfeng.commons.utils.VelocityEngineUtils;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
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
                && tableName.toLowerCase().startsWith(makeCodeConfiguration.getTablePrefixRemove().toLowerCase())) {
            return tableName.substring(makeCodeConfiguration.getTablePrefixRemove().length());
        }
        return tableName;
    }

    @SneakyThrows
    public boolean execute(MakeContext makeContext) {
        if (null != makeCodeConfiguration.getEntityInclude()) {
            makeContext.setEntityCols(Arrays.asList(makeCodeConfiguration.getEntityInclude()));
        }

        Map<String, Object> vmContext = makeContext.buildContext();
        vmContext.put("_nameString", new NameStringUtils());
        String entityClassName = NameStringUtils.toClassName(this.getBaseClassName(makeContext.getTableInfo().getName().toLowerCase()));
        vmContext.put("entityClassName", entityClassName);
        //1，生产entity对象
        if (null != makeContext.getEntityPackage()) {
            String vm = FileUtils.readLocalFile(VelocityEngineUtils.LOCAL_RESOURCE_PATH + "original/java/entity.vm");
            if (null != vm) {
                String content = VelocityEngineUtils.parseTemplate(vm, vmContext);
                String path = makeCodeConfiguration.getCodeOutputPath() +
                        FileUtils.package2Path(makeContext.getEntityPackage()) + "/" + entityClassName + ".java";
                FileUtils.writeFile(path, new Formatter().formatSource(content));
            }
        }

        // 生成query - 超过1个索引字段才生成
        if (null != makeContext.getQueryPackage() && makeContext.getTableInfo().getIndexColumns().size() > 1) {
            String vm = FileUtils.readLocalFile(VelocityEngineUtils.LOCAL_RESOURCE_PATH + "original/java/query.vm");
            if (null != vm) {
                String content = VelocityEngineUtils.parseTemplate(vm, vmContext);
                String path = makeCodeConfiguration.getCodeOutputPath() +
                        FileUtils.package2Path(makeContext.getQueryPackage()) + "/" + entityClassName + "Query.java";
                FileUtils.writeFile(path, new Formatter().formatSource(content));
            }
        }

        //2, 生成mapper
        if (null != makeContext.getMapperPackage()) {
            String vm = FileUtils.readLocalFile(VelocityEngineUtils.LOCAL_RESOURCE_PATH + "original/java/mapper.vm");
            if (null != vm) {
                String content = VelocityEngineUtils.parseTemplate(vm, vmContext);
                String path = makeCodeConfiguration.getCodeOutputPath() +
                        FileUtils.package2Path(makeContext.getMapperPackage()) + "/" + entityClassName + "Mapper.java";
                try {
                    FileUtils.writeFile(path, new Formatter().formatSource(content.replace("  ", " ").replace("  ", " ")));
                } catch (Exception e) {
                    FileUtils.writeFile(path, content.replace("  ", " ").replace("  ", " "));
                }
            }
        }
        //3, 生成service
        if (null != makeContext.getRepositoryPackage()) {
            String vm = FileUtils.readLocalFile(VelocityEngineUtils.LOCAL_RESOURCE_PATH + "template/java/repository.vm");
            if (null != vm) {
                String content = VelocityEngineUtils.parseTemplate(vm, vmContext);
                String path = makeCodeConfiguration.getCodeOutputPath() +
                        FileUtils.package2Path(makeContext.getRepositoryPackage()) + "/" + entityClassName + "Repository.java";
                FileUtils.writeFile(path, new Formatter().formatSource(content));
            }
        }


        return true;
    }
}
