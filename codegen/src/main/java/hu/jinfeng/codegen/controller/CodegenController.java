package hu.jinfeng.codegen.controller;

import hu.jinfeng.codegen.model.DBHelper;
import hu.jinfeng.codegen.make.MakeContext;
import hu.jinfeng.codegen.make.MakeService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * @Author hujinfeng  @Date 2020/11/27
 **/
@Api(tags = "生成代码")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class CodegenController {
    private final DBHelper dbHelper;
    private final MakeService makeService;

    @ApiOperation(value = "make", tags = "生成代码")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", dataType = "String", name = "database", value = "数据库名称")
            , @ApiImplicitParam(paramType = "query", dataType = "String", name = "tableName", value = "表名", required = true)
            , @ApiImplicitParam(paramType = "query", dataType = "String", name = "basePackage", defaultValue = "hu.jinfeng", required = true)
//            , @ApiImplicitParam(paramType = "query", dataType = "String", name = "entityPackage", defaultValue = "hu.jinfeng.entity")
//            , @ApiImplicitParam(paramType = "query", dataType = "String", name = "queryPackage", defaultValue = "hu.jinfeng.query")
//            , @ApiImplicitParam(paramType = "query", dataType = "String", name = "mapperPackage", defaultValue = "hu.jinfeng.mapper")
//            , @ApiImplicitParam(paramType = "query", dataType = "String", name = "servicePackage", defaultValue = "hu.jinfeng.service")
//            , @ApiImplicitParam(paramType = "query", dataType = "String", name = "controllerPackage", defaultValue = "hu.jinfeng.controller")
    })
    @GetMapping(value = "/make")
    public String make(String database, String tableName, String basePackage) throws Exception {
        String[] tables = tableName.split(",");
        Arrays.stream(tables).forEach(e -> {
            MakeContext makeContext = new MakeContext();
            makeContext.setBasePackage(basePackage);
            makeContext.setQueryPackage(basePackage + ".query");
            makeContext.setEntityPackage(basePackage + ".entity");
            makeContext.setMapperPackage(basePackage + ".mapper");
            makeContext.setServicePackage(basePackage + ".service");
            makeContext.setControllerPackage(basePackage + ".controller");
            makeContext.setTableInfo(dbHelper.getTableInfo(database, e));
            makeService.execute(makeContext);
        });
        return "success";
    }

}
