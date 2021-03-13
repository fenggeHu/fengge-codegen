package hu.jinfeng.codegen.controller;

import com.google.gson.Gson;
import hu.jinfeng.codegen.db.DBHelper;
import hu.jinfeng.codegen.db.TableInfo;
import hu.jinfeng.codegen.make.MakeContext;
import hu.jinfeng.codegen.make.MakeService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.DatabaseMetaData;
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
            , @ApiImplicitParam(paramType = "query", dataType = "String", name = "entityPackage")
            , @ApiImplicitParam(paramType = "query", dataType = "String", name = "queryPackage")
            , @ApiImplicitParam(paramType = "query", dataType = "String", name = "mapperPackage")
            , @ApiImplicitParam(paramType = "query", dataType = "String", name = "servicePackage")
            , @ApiImplicitParam(paramType = "query", dataType = "String", name = "controllerPackage")
    })
    @GetMapping(value = "/make")
    public String make(MakeContext makeContext) throws Exception {
        makeContext.setTableInfo(dbHelper.getTableInfo(makeContext.getDatabase(), makeContext.getTableName()));
        return gson.toJson(makeService.execute(makeContext));
    }

}
