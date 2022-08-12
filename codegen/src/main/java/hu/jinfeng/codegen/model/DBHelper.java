package hu.jinfeng.codegen.model;

import hu.jinfeng.codegen.config.MakeConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author hujinfeng  @Date 2020/11/27
 **/
@Configuration
//@PropertySource("application.properties")
@Service
@Slf4j
public class DBHelper {

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;
    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Autowired
    private MakeConfig makeConfig;

    public DatabaseMetaData getDatabaseMetaData() throws Exception {
        return getConnection().getMetaData();
    }

    @SneakyThrows
    public List<TableInfo> getAllTables(String database) {
        List<TableInfo> result = new ArrayList<>();
        DatabaseMetaData metaData = getDatabaseMetaData();
        if (null == database) {
            database = metaData.getConnection().getCatalog();
        }
        ResultSet rs = metaData.getTables(database, null, null, new String[]{"TABLE"});
        while (rs.next()) {
            TableInfo table = new TableInfo();
            result.add(table);
            table.setDatabase(rs.getString("TABLE_CAT"));
            table.setName(rs.getString("TABLE_NAME"));
            table.setRemarks(rs.getString("REMARKS"));

            ResultSet resultSet = metaData.getColumns(database, "%", table.getName(), "%");
            this.fillColumns(table, resultSet);
        }

        return result;
    }

    private void fillColumns(final TableInfo table, ResultSet columns) throws SQLException {
        while (columns.next()) {
            ColumnInfo columnInfo = ColumnInfo.builder()
                    .name(columns.getString("COLUMN_NAME"))
                    .type(columns.getString("TYPE_NAME"))
                    .size(columns.getLong("COLUMN_SIZE"))
                    // decimal小数位数
                    .digits(null == columns.getString("DECIMAL_DIGITS") ? null : columns.getInt("DECIMAL_DIGITS"))
                    .remarks(columns.getString("REMARKS"))
                    .tableName(columns.getString("TABLE_NAME"))
                    .autoIncrement("YES".equalsIgnoreCase(columns.getString("IS_AUTOINCREMENT")))
                    .nullAble(columns.getInt("NULLABLE") == 1)
                    //供参考：null \ CURRENT_TIMESTAMP \0
                    .defaultValue(columns.getString("COLUMN_DEF"))
                    .build();

            // sharding field
            if (columnInfo.isShardingField()) {
                table.shardingNames.add(columnInfo.getName());
                table.shardingColumns.add(columnInfo);
            }

            table.getColumns().add(columnInfo);
        }
    }

    @SneakyThrows
    public TableInfo getTableInfo(String database, String tableName) {
        DatabaseMetaData metaData = getDatabaseMetaData();
        if (null == database) {
            database = metaData.getConnection().getCatalog();
        }
        TableInfo table = new TableInfo();
        table.setName(tableName);
        table.setDatabase(database);

        ResultSet rs = metaData.getTables(database, null, tableName, new String[]{"TABLE"});
        rs.next();
        // 数据库的连接参数必须加上remarks=true&useInformationSchema=true才能读取到REMARKS
        table.setRemarks(rs.getString("REMARKS"));
        ResultSet resultSet = metaData.getColumns(database, "%", tableName, "%");
        this.fillColumns(table, resultSet);

        // pk
        ResultSet pkRS = metaData.getPrimaryKeys(database, null, tableName);
        while (pkRS.next()) {
            String colName = pkRS.getString("COLUMN_NAME");
            ColumnInfo columnInfo = table.getColumnInfo(colName);
            table.getPkNames().add(colName);
            table.getPkColumns().add(columnInfo);
        }

        // uk 只考虑只有一个uk索引的情况
        ResultSet ukRs = metaData.getIndexInfo(database, null, tableName, true, false);
        while (ukRs.next()) {
            String colName = ukRs.getString("COLUMN_NAME");
            String indexName = ukRs.getString("INDEX_NAME");
            // 排除 PRIMARY
            if ("PRIMARY".equalsIgnoreCase(indexName)) continue;
            // ukRs.getInt("NON_UNIQUE")
            // ukRs.getString("INDEX_NAME")
            ColumnInfo columnInfo = table.getColumnInfo(colName);
            table.getUkNames().add(colName);
            table.getUkColumns().add(columnInfo);
        }

        // 所有普通索引 - NON_UNIQUE = 1
        ResultSet index = metaData.getIndexInfo(database, "%", tableName, false, false);
        while (index.next()) {
            int nonUnique = index.getInt("NON_UNIQUE");
            if (1 != nonUnique) continue;   //  只要普通索引
            String colName = index.getString("COLUMN_NAME");
            String indexName = index.getString("INDEX_NAME");
            List<ColumnInfo> list = table.getIndexMap().get(indexName);
            if (null == list) {
                list = new LinkedList<>();
                table.getIndexMap().put(indexName, list);
            }
            ColumnInfo columnInfo = table.getColumnInfo(colName);
            list.add(columnInfo);

            if (table.getIndexNames().contains(colName)) continue;
            table.getIndexNames().add(colName);
            table.getIndexColumns().add(columnInfo);
        }

        // 更新字段
        for (ColumnInfo col : table.getColumns()) {
            //insert
            if (!col.isAutoIncrement() && !makeConfig.isInclude(makeConfig.getInsertExclude(), col.getName())) {
                table.getInsertColumns().add(col);
            }
            //update
            if (!col.isAutoIncrement() && !makeConfig.isInclude(makeConfig.getUpdateExclude(), col.getName())) {
                table.getUpdateColumns().add(col);
            }
        }

        // 把分库分表的字段可能没有索引，判断加到索引条件
//        for (ColumnInfo col : table.getShardingColumns()) {
//            if (!table.getIndexNames().contains(col.getName())) {
//                table.getIndexNames().add(col.getName());
//                table.getIndexColumns().add(col);
//            }
//            if (!table.getUkNames().contains(col.getName())) {
//                table.getUkNames().add(col.getName());
//                table.getUkColumns().add(col);
//            }
//            if (!table.getPkNames().contains(col.getName())) {
//                table.getPkNames().add(col.getName());
//                table.getPkColumns().add(col);
//            }
//        }

        return table;
    }

    public Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbUrl, username, password);
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
