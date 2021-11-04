package hu.jinfeng.codegen.controller;

import hu.jinfeng.codegen.model.DBHelper;
import hu.jinfeng.codegen.model.TableInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.DatabaseMetaData;
import java.util.List;

/**
 * @Author hujinfeng  @Date 2020/11/27
 **/
@Api(tags = "查询数据库信息")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class DatabaseController {
    private final DBHelper dbHelper;

    @ApiOperation("取DB Meta信息")
    @GetMapping("/getDBMetaData")
    public DatabaseMetaData getDatabaseMetaData() throws Exception {
        return dbHelper.getDatabaseMetaData();
    }

    @ApiOperation("取DB 所有表的信息")
    @GetMapping("/getAllTables")
    public List<TableInfo> getAllTables(String database) throws Exception {
        return dbHelper.getAllTables(database);
    }

    @ApiOperation("取表信息")
    @GetMapping("/getTableInfo")
    public TableInfo getTableInfo(String database, String tableName) throws Exception {
        return dbHelper.getTableInfo(database, tableName);
    }

}
