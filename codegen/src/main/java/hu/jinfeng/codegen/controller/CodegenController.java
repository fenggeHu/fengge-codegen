package hu.jinfeng.codegen.controller;

import com.google.gson.Gson;
import hu.jinfeng.codegen.model.DBHelper;
import hu.jinfeng.codegen.model.TableInfo;
import hu.jinfeng.codegen.make.MakeContext;
import hu.jinfeng.codegen.make.MakeService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.DatabaseMetaData;
import java.util.Arrays;
import java.util.List;

/**
 * @Author hujinfeng  @Date 2020/11/27
 **/
@Api(tags = "查询DB信息")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class CodegenController {
    Gson gson = new Gson();
    private final DBHelper dbHelper;
    private final MakeService makeService;

    @ApiOperation("取DB Meta信息")
    @GetMapping("/getDBMetaData")
    public String getDatabaseMetaData() throws Exception {
        DatabaseMetaData metaData = dbHelper.getDatabaseMetaData();
        return gson.toJson(metaData);
    }

    @ApiOperation("取DB 所有表的信息")
    @GetMapping("/getAllTables")
    public String getAllTables(String database) throws Exception {
        List<TableInfo> tableInfos = dbHelper.getAllTables(database);
        return gson.toJson(tableInfos);
    }

    @ApiOperation("取表信息")
    @GetMapping("/getTableInfo")
    public String getTableInfo(String database, String tableName) throws Exception {

        return gson.toJson(dbHelper.getTableInfo(database, tableName));
    }

    @ApiOperation("生成代码")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", dataType = "String", name = "database", value = "数据库名称")
            , @ApiImplicitParam(paramType = "query", dataType = "String", name = "tableName", value = "表名", required = true)
            , @ApiImplicitParam(paramType = "query", dataType = "String", name = "basePackage", defaultValue = "hu.jinfeng")
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
