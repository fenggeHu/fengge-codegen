package hu.jinfeng.codegen.make;

import hu.jinfeng.commons.utils.FileUtils;
import hu.jinfeng.commons.utils.NameStringUtils;
import hu.jinfeng.commons.utils.VelocityEngineUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author hujinfeng  @Date 2020/11/28
 **/
@Service
public class MakeService {

    public boolean execute(MakeContext makeContext) {
        Map<String, Object> vmContext = makeContext.buildContext();
        vmContext.put("_nameString", new NameStringUtils());
        String modelClassName = NameStringUtils.toClassName(makeContext.getTable().getName());
        vmContext.put("modelClassName", modelClassName);
        //1，生产model对象
        if (null != makeContext.getModelPackage()) {
            String vm = FileUtils.readLocalFile(VelocityEngineUtils.LOCAL_RESOURCE_PATH + "template/model.vm");
            String content = VelocityEngineUtils.parseTemplate(vm, vmContext);
            String path = VelocityEngineUtils.LOCAL_TEST_SRC_PATH +
                    FileUtils.package2Path(makeContext.getModelPackage()) + "/" + modelClassName + ".java";
            FileUtils.writeFile(path, content);
        }
        //2, 生成mapper对象
        if (null != makeContext.getMapperPackage()) {
            String vm = FileUtils.readLocalFile(VelocityEngineUtils.LOCAL_RESOURCE_PATH + "template/mapper.vm");
            String content = VelocityEngineUtils.parseTemplate(vm, vmContext);
            String path = VelocityEngineUtils.LOCAL_TEST_SRC_PATH +
                    FileUtils.package2Path(makeContext.getMapperPackage()) + "/" + modelClassName + "Mapper.java";
            FileUtils.writeFile(path, content);
        }



        return true;
    }
}
