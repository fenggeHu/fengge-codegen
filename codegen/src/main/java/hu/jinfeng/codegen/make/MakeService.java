package hu.jinfeng.codegen.make;

import com.google.googlejavaformat.java.Formatter;
import hu.jinfeng.codegen.config.MakeCodeConfiguration;
import hu.jinfeng.codegen.vmhelper.MapperHelper;
import hu.jinfeng.codegen.model.DBHelper;
import hu.jinfeng.commons.utils.FileUtils;
import hu.jinfeng.commons.utils.NameStringUtils;
import hu.jinfeng.commons.utils.VelocityEngineUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Map;

/**
 * @Author hujinfeng  @Date 2020/11/28
 **/
@Slf4j
@Service
public class MakeService {
    @Autowired
    private DBHelper dbHelper;
    @Resource
    private MakeCodeConfiguration makeCodeConfiguration;

    private String getBaseClassName(String tableName) {
        if (StringUtils.isNotBlank(makeCodeConfiguration.getTablePrefixRemove())
                && tableName.toLowerCase().startsWith(makeCodeConfiguration.getTablePrefixRemove().toLowerCase())) {
            return tableName.substring(makeCodeConfiguration.getTablePrefixRemove().length());
        }
        return tableName;
    }

    /**
     * 生成代码
     *
     * @param database
     * @param tableName
     * @param basePackage
     */
    public void execute(String database, String tableName, String basePackage) {
        MakeContext makeContext = new MakeContext();
        makeContext.setBasePackage(basePackage);
        makeContext.setQueryPackage(basePackage + ".query");
        makeContext.setParamPackage(basePackage + ".param");
        makeContext.setEntityPackage(basePackage + ".entity");
        makeContext.setMapperPackage(basePackage + ".mapper");
        makeContext.setRepositoryPackage(basePackage + ".repository");
        makeContext.setControllerPackage(basePackage + ".controller");
        makeContext.setTableInfo(dbHelper.getTableInfo(database, tableName));
        log.info("Generate: db:{}, table:{}, package:{}",
                makeContext.getDatabase(), makeContext.getTableName(), makeContext.getBasePackage());
        this.execute(makeContext);
    }

    @SneakyThrows
    public boolean execute(MakeContext makeContext) {
        if (null != makeCodeConfiguration.getEntityInclude()) {
            makeContext.setEntityCols(Arrays.asList(makeCodeConfiguration.getEntityInclude()));
        }
        String templatePath = StringUtils.isNotBlank(makeCodeConfiguration.getTemplatePath()) ?
                makeCodeConfiguration.getTemplatePath() : VelocityEngineUtils.LOCAL_RESOURCE_PATH;

        Map<String, Object> vmContext = makeContext.buildContext();
        vmContext.put("_name", new NameStringUtils());
        vmContext.put("_mapper", new MapperHelper());
        String entityClassName = NameStringUtils.toClassName(this.getBaseClassName(makeContext.getTableInfo().getName().toLowerCase()));
        vmContext.put("entityClassName", entityClassName);
        vmContext.put("entityPropertyName", NameStringUtils.toPropertyName(entityClassName));
        //1，生产entity对象
        if (null != makeContext.getEntityPackage()) {
            String vm = FileUtils.readLocalFile(templatePath + "/original/java/entity.vm");
            if (null != vm) {
                String content = VelocityEngineUtils.parseTemplate(vm, vmContext);
                String path = makeCodeConfiguration.getCodeOutputPath() +
                        FileUtils.package2Path(makeContext.getEntityPackage()) + "/" + entityClassName + ".java";
                FileUtils.writeFile(path, new Formatter().formatSource(content));
            }
        }

        // 生成query - 超过1个索引字段才生成
        if (null != makeContext.getQueryPackage() && makeContext.getTableInfo().getIndexColumns().size() > 1) {
            String vm = FileUtils.readLocalFile(templatePath + "/original/java/query.vm");
            if (null != vm) {
                String content = VelocityEngineUtils.parseTemplate(vm, vmContext);
                String path = makeCodeConfiguration.getCodeOutputPath() +
                        FileUtils.package2Path(makeContext.getQueryPackage()) + "/" + entityClassName + "Query.java";
                FileUtils.writeFile(path, new Formatter().formatSource(content));
            }
        }

        //2, 生成mapper
        if (null != makeContext.getMapperPackage()) {
            String vm = FileUtils.readLocalFile(templatePath + "/original/java/mapper.vm");
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
            String vm = FileUtils.readLocalFile(templatePath + "/template/java/repository.vm");
            if (null != vm) {
                String content = VelocityEngineUtils.parseTemplate(vm, vmContext);
                String path = makeCodeConfiguration.getCodeOutputPath() +
                        FileUtils.package2Path(makeContext.getRepositoryPackage()) + "/" + entityClassName + "Repository.java";
                FileUtils.writeFile(path, new Formatter().formatSource(content));
            }
        }

        // 生产param对象
        if (null != makeContext.getParamPackage()) {
            String vm = FileUtils.readLocalFile(templatePath + "/original/java/param.vm");
            if (null != vm) {
                String content = VelocityEngineUtils.parseTemplate(vm, vmContext);
                String path = makeCodeConfiguration.getCodeOutputPath() +
                        FileUtils.package2Path(makeContext.getParamPackage()) + "/" + entityClassName + "Param.java";
                FileUtils.writeFile(path, new Formatter().formatSource(content));
            }
        }
        // controller
        if (null != makeContext.getRepositoryPackage()) {
            String vm = FileUtils.readLocalFile(templatePath + "/original/java/controller.vm");
            if (null != vm) {
                String content = VelocityEngineUtils.parseTemplate(vm, vmContext);
                String path = makeCodeConfiguration.getCodeOutputPath() +
                        FileUtils.package2Path(makeContext.getControllerPackage()) + "/" + entityClassName + "Controller.java";
                FileUtils.writeFile(path, new Formatter().formatSource(content));
            }
        }

        return true;
    }
}
