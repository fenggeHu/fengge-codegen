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
        Map<String, Object> vmContext = makeContext.getContext();
        vmContext.put("_nameString", new NameStringUtils());
        //1，生产model对象
        if (null != makeContext.getModelPackage()) {
            String vm = FileUtils.readLocalFile(VelocityEngineUtils.LOCAL_RESOURCE_PATH + "template/model.vm");
            String content = VelocityEngineUtils.parseTemplate(vm, vmContext);
            String path = VelocityEngineUtils.LOCAL_RESOURCE_PATH + "output/" +
                    FileUtils.package2Path(makeContext.getModelPackage()) + "/" +
                    NameStringUtils.toClassName(makeContext.getTable().getName()) + ".java";
            FileUtils.writeFile(path, content);
        }

        return true;
    }
}
