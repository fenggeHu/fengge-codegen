package generator.controller;

import generator.domain.ApiResponse;
import generator.domain.GenTable;
import generator.domain.GenTableColumn;
import generator.page.TableDataInfo;
import generator.service.GenTableColumnService;
import generator.service.GenTableService;
import generator.text.Convert;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码生成 操作处理
 *
 * @author fengge
 */
@RestController
@RequestMapping("/tool/gen")
public class GenController extends BaseController {
    @Autowired
    private GenTableService genTableService;

    @Autowired
    private GenTableColumnService genTableColumnService;

    /**
     * 查询代码生成列表
     */
//    @PreAuthorize("@ss.hasPermi('tool:gen:list')")
    @GetMapping("/list")
    public TableDataInfo genList(GenTable genTable) {
        startPage();
        List<GenTable> list = genTableService.selectGenTableList(genTable);
        return getDataTable(list);
    }

    /**
     * 修改代码生成业务
     */
//    @PreAuthorize("@ss.hasPermi('tool:gen:query')")
    @GetMapping(value = "/{tableId}")
    public ApiResponse getInfo(@PathVariable Long tableId) {
        GenTable table = genTableService.selectGenTableById(tableId);
        List<GenTable> tables = genTableService.selectGenTableAll();
        List<GenTableColumn> list = genTableColumnService.selectGenTableColumnListByTableId(tableId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("info", table);
        map.put("rows", list);
        map.put("tables", tables);
        return ApiResponse.success(map);
    }

    /**
     * 查询数据库列表
     */
//    @PreAuthorize("@ss.hasPermi('tool:gen:list')")
    @GetMapping("/db/list")
    public TableDataInfo dataList(GenTable genTable) {
        startPage();
        List<GenTable> list = genTableService.selectDbTableList(genTable);
        return getDataTable(list);
    }

    /**
     * 查询数据表字段列表
     */
//    @PreAuthorize("@ss.hasPermi('tool:gen:list')")
    @GetMapping(value = "/column/{tableId}")
    public TableDataInfo columnList(Long tableId) {
        TableDataInfo dataInfo = new TableDataInfo();
        List<GenTableColumn> list = genTableColumnService.selectGenTableColumnListByTableId(tableId);
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }

    /**
     * 导入表结构（保存）
     */
//    @PreAuthorize("@ss.hasPermi('tool:gen:import')")
//    @Log(title = "代码生成", businessType = BusinessType.IMPORT)
    @PostMapping("/importTable")
    public ApiResponse importTableSave(String tables) {
        String[] tableNames = Convert.toStrArray(tables);
        // 查询表信息
        List<GenTable> tableList = genTableService.selectDbTableListByNames(tableNames);
        genTableService.importGenTable(tableList);
        return ApiResponse.success();
    }

    /**
     * 修改保存代码生成业务
     */
//    @PreAuthorize("@ss.hasPermi('tool:gen:edit')")
//    @Log(title = "代码生成", businessType = BusinessType.UPDATE)
    @PutMapping
    public ApiResponse editSave(@Validated @RequestBody GenTable genTable) {
        genTableService.validateEdit(genTable);
        genTableService.updateGenTable(genTable);
        return ApiResponse.success();
    }

    /**
     * 删除代码生成
     */
//    @PreAuthorize("@ss.hasPermi('tool:gen:remove')")
//    @Log(title = "代码生成", businessType = BusinessType.DELETE)
    @DeleteMapping("/{tableIds}")
    public ApiResponse remove(@PathVariable Long[] tableIds) {
        genTableService.deleteGenTableByIds(tableIds);
        return ApiResponse.success();
    }

    /**
     * 预览代码
     */
//    @PreAuthorize("@ss.hasPermi('tool:gen:preview')")
    @GetMapping("/preview/{tableId}")
    public ApiResponse preview(@PathVariable("tableId") Long tableId) throws IOException {
        Map<String, String> dataMap = genTableService.previewCode(tableId);
        return ApiResponse.success(dataMap);
    }

    /**
     * 生成代码（下载方式）
     */
//    @PreAuthorize("@ss.hasPermi('tool:gen:code')")
//    @Log(title = "代码生成", businessType = BusinessType.GENCODE)
    @GetMapping("/download/{tableName}")
    public void download(HttpServletResponse response, @PathVariable("tableName") String tableName) throws IOException {
        byte[] data = genTableService.downloadCode(tableName);
        genCode(response, data);
    }

    /**
     * 生成代码（自定义路径）
     */
//    @PreAuthorize("@ss.hasPermi('tool:gen:code')")
//    @Log(title = "代码生成", businessType = BusinessType.GENCODE)
    @GetMapping("/genCode/{tableName}")
    public ApiResponse genCode(@PathVariable("tableName") String tableName) {
        genTableService.generatorCode(tableName);
        return ApiResponse.success();
    }

    /**
     * 同步数据库
     */
//    @PreAuthorize("@ss.hasPermi('tool:gen:edit')")
//    @Log(title = "代码生成", businessType = BusinessType.UPDATE)
    @GetMapping("/syncDb/{tableName}")
    public ApiResponse syncDb(@PathVariable("tableName") String tableName) {
        genTableService.syncDb(tableName);
        return ApiResponse.success();
    }

    /**
     * 批量生成代码
     */
//    @PreAuthorize("@ss.hasPermi('tool:gen:code')")
//    @Log(title = "代码生成", businessType = BusinessType.GENCODE)
    @GetMapping("/batchGenCode")
    public void batchGenCode(HttpServletResponse response, String tables) throws IOException {
        String[] tableNames = Convert.toStrArray(tables);
        byte[] data = genTableService.downloadCode(tableNames);
        genCode(response, data);
    }

    /**
     * 生成zip文件
     */
    private void genCode(HttpServletResponse response, byte[] data) throws IOException {
        response.reset();
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment; filename=\"fengge.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(data, response.getOutputStream());
    }
}