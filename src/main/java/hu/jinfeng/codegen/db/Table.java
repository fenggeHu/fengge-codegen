package hu.jinfeng.codegen.db;

import lombok.Data;

import java.util.List;

/**
 * @Author hujinfeng  @Date 2020/11/27
 **/
@Data
public class Table {
    private String name;

    private String remarks;

    private String database;

    private List<Column> columns;
}
