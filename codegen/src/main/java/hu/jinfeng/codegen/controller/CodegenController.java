package hu.jinfeng.codegen.controller;

import hu.jinfeng.codegen.model.DBHelper;
import hu.jinfeng.codegen.make.MakeService;
import hu.jinfeng.codegen.model.TableInfo;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

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
    public String make(String database, String tableName, String basePackage) {
        String[] tables = tableName.split(",");
        Arrays.stream(tables).forEach(e -> {
            makeService.execute(database, e, basePackage);
        });
        return "success";
    }


    /**
     * 生成库里所有的表
     *
     * @return
     */
    @GetMapping(value = "/makeAll")
    public String makeAll(String database, String basePackage) {
        List<TableInfo> tables = dbHelper.getAllTables(null);
        for (TableInfo table : tables) {
            makeService.execute(database, table.getName(), basePackage);
        }
        return "success";
    }
}
