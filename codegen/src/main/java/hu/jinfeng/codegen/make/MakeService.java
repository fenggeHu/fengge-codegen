package hu.jinfeng.codegen.make;

import com.google.googlejavaformat.java.Formatter;
import hu.jinfeng.codegen.config.MakerConfig;
import hu.jinfeng.codegen.model.TableInfo;
import hu.jinfeng.codegen.vmhelper.MapperHelper;
import hu.jinfeng.codegen.model.DBHelper;
import hu.jinfeng.codegen.vmhelper.NameHelper;
import hu.jinfeng.codegen.vmhelper.RepositoryHelper;
import hu.jinfeng.commons.utils.FileUtils;
import hu.jinfeng.commons.utils.NameStringUtils;
import hu.jinfeng.commons.utils.StringUtil;
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
    @Autowired
    private MapperHelper mapperHelper;
    @Autowired
    private RepositoryHelper repositoryHelper;
    @Autowired
    private MakerConfig makerConfig;
    @Autowired
    private NameHelper nameHelper;

    /**
     * 生成代码
     *
     * @param database
     * @param tableName
     * @param basePackage
     */
    public void execute(String database, String tableName, String basePackage) {
        TableInfo tableInfo = dbHelper.getTableInfo(database, tableName);
        if (null == tableInfo) {
            return;
        }
        MakeContext makeContext = new MakeContext();
        makeContext.setBasePackage(basePackage);
        makeContext.setQueryPackage(basePackage + ".query");
        makeContext.setParamPackage(basePackage + ".param");
        makeContext.setEntityPackage(basePackage + ".entity");
        makeContext.setMapperPackage(basePackage + ".mapper");
        makeContext.setRepositoryPackage(basePackage + ".repository");
        makeContext.setControllerPackage(basePackage + ".controller");
        makeContext.setTableInfo(tableInfo);
        log.info("Generate: db:{}, table:{}, package:{}",
                makeContext.getDatabase(), makeContext.getTableName(), makeContext.getBasePackage());
        this.execute(makeContext);
    }

    @SneakyThrows
    public boolean execute(MakeContext makeContext) {
        if (null != makerConfig.getEntityColumnInclude()) {
            makeContext.setEntityCols(Arrays.asList(makerConfig.getEntityColumnInclude()));
        }
        String templatePath = StringUtils.isNotBlank(makerConfig.getTemplatePath()) ?
                makerConfig.getTemplatePath() : VelocityEngineUtils.LOCAL_RESOURCE_PATH;

        Map<String, Object> vmContext = makeContext.buildContext();
        vmContext.put("_db", dbHelper);
        vmContext.put("_useSwagger", makerConfig.isSwagger());
        vmContext.put("_nameUtils", new NameStringUtils());
        vmContext.put("_name", nameHelper);
        vmContext.put("_mapper", mapperHelper);
        vmContext.put("_repository", repositoryHelper);
        vmContext.put("_stringUtil", new StringUtil());
        String baseClassName = mapperHelper.getBaseClassName(makeContext.getTableInfo().getName().toLowerCase());
        vmContext.put("baseClassName", baseClassName);
        String entityClassName = nameHelper.entityClassName(baseClassName);
        vmContext.put("entityClassName", entityClassName);
        vmContext.put("entityPropertyName", NameStringUtils.toPropertyName(baseClassName));
        String mapperClassName = nameHelper.mapperClassName(baseClassName);
        vmContext.put("mapperClassName", mapperClassName);
        String repositoryClassName = nameHelper.repositoryClassName(baseClassName);
        vmContext.put("repositoryClassName", repositoryClassName);
        String controllerClassName = nameHelper.controllerClassName(baseClassName);
        vmContext.put("controllerClassName", controllerClassName);
        //1，生产entity对象
        if (null != makeContext.getEntityPackage()) {
            String vm = FileUtils.readLocalFile(templatePath + "/template/java/entity.vm");
            if (null != vm) {
                String content = VelocityEngineUtils.parseTemplate(vm, vmContext);
                String path = makerConfig.getCodeOutputPath() +
                        FileUtils.package2Path(makeContext.getEntityPackage()) + "/" + entityClassName + ".java";
                this.write(path, content);
            }
        }

        // 生成query - 超过1个索引字段才生成
        if (null != makeContext.getQueryPackage() && makeContext.getTableInfo().getAllIndexColumns().size() > 1) {
            String vm = FileUtils.readLocalFile(templatePath + "/template/java/query.vm");
            if (null != vm) {
                String content = VelocityEngineUtils.parseTemplate(vm, vmContext);
                String path = makerConfig.getCodeOutputPath() +
                        FileUtils.package2Path(makeContext.getQueryPackage()) + "/" + baseClassName + "Query.java";
                this.write(path, content);
            }
        }

        //2, 生成mapper
        if (null != makeContext.getMapperPackage()) {
            String vm = FileUtils.readLocalFile(templatePath + "/template/java/mapper.vm");
            if (null != vm) {
                String content = VelocityEngineUtils.parseTemplate(vm, vmContext);
                String path = makerConfig.getCodeOutputPath() +
                        FileUtils.package2Path(makeContext.getMapperPackage()) + "/" + mapperClassName + ".java";
                content = content.replace("  ", " ").replace("  ", " ");
                this.write(path, content);
            }
        }
        //3, 生成service
        if (null != makeContext.getRepositoryPackage()) {
            String vm = FileUtils.readLocalFile(templatePath + "/template/java/repository.vm");
            if (null != vm) {
                String content = VelocityEngineUtils.parseTemplate(vm, vmContext);
                String path = makerConfig.getCodeOutputPath() +
                        FileUtils.package2Path(makeContext.getRepositoryPackage()) + "/" + repositoryClassName + ".java";
                this.write(path, content);
            }
        }

        // 生产param对象
        if (null != makeContext.getParamPackage()) {
            String vm = FileUtils.readLocalFile(templatePath + "/template/java/param.vm");
            if (null != vm) {
                String content = VelocityEngineUtils.parseTemplate(vm, vmContext);
                String path = makerConfig.getCodeOutputPath() +
                        FileUtils.package2Path(makeContext.getParamPackage()) + "/" + baseClassName + "Param.java";
                this.write(path, content);
            }
        }
        // controller
        if (null != makeContext.getRepositoryPackage()) {
            String vm = FileUtils.readLocalFile(templatePath + "/template/java/controller.vm");
            if (null != vm) {
                String content = VelocityEngineUtils.parseTemplate(vm, vmContext);
                String path = makerConfig.getCodeOutputPath() +
                        FileUtils.package2Path(makeContext.getControllerPackage()) + "/" + controllerClassName + ".java";
                this.write(path, content);
            }
        }
        // application
        String vm = FileUtils.readLocalFile(templatePath + "/template/java/application.vm");
        if (null != vm) {
            String content = VelocityEngineUtils.parseTemplate(vm, vmContext);
            String path = makerConfig.getCodeOutputPath() +
                    FileUtils.package2Path(makeContext.getBasePackage()) + "/" + NameStringUtils.toClassName2(makeContext.getBasePackage()) + "Application.java";
            this.write(path, content);
        }
        // resources
        String yml = FileUtils.readLocalFile(templatePath + "/template/resources/application.yml.vm");
        if (null != yml) {
            String content = VelocityEngineUtils.parseTemplate(yml, vmContext);
            String path = makerConfig.getCodeOutputPath() + "/application.yml";
            FileUtils.writeFile(path, content);
        }

        return true;
    }

    private void write(String path, String content) {
        try {
            FileUtils.writeFile(path, new Formatter().formatSource(content));
        } catch (Exception e) {
            log.warn("Formatter", e);
            FileUtils.writeFile(path, content);
        }
    }
}
