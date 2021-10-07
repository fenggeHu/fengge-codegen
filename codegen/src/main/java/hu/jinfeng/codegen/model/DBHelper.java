package hu.jinfeng.codegen.model;

import com.alibaba.druid.pool.DruidDataSource;
import hu.jinfeng.codegen.config.MakeCodeConfiguration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
    private MakeCodeConfiguration makeCodeConfiguration;

    private DruidDataSource dataSource = null;

    public DatabaseMetaData getDatabaseMetaData() throws Exception {
        DatabaseMetaData metaData = getConnection().getMetaData();
        return metaData;
    }

    public List<TableInfo> getAllTables(String database) throws Exception {
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

            ResultSet columns = metaData.getColumns(database, "%", table.getName(), "%");
            this.fillColumns(table, columns);
        }

        return result;
    }

    private void fillColumns(final TableInfo table, ResultSet columns) throws SQLException {
        List<ColumnInfo> fields = new ArrayList<>();
        List<ColumnInfo> insertFields = new ArrayList<>();
        List<ColumnInfo> updateFields = new ArrayList<>();
        table.setColumns(fields);
        table.setInsertColumns(insertFields);
        table.setUpdateColumns(updateFields);
        while (columns.next()) {
            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.setDbType(dataSource.getDbType());
            columnInfo.setName(columns.getString("COLUMN_NAME"));
            columnInfo.setType(columns.getString("TYPE_NAME"));
            columnInfo.setSize(columns.getString("COLUMN_SIZE"));
            columnInfo.setRemarks(columns.getString("REMARKS"));
            columnInfo.setTableName(columns.getString("TABLE_NAME"));
            columnInfo.setAutoIncrement("YES".equalsIgnoreCase(columns.getString("IS_AUTOINCREMENT")));
            //供参考：null \ CURRENT_TIMESTAMP \0
            columnInfo.setDefaultValue(columns.getString("COLUMN_DEF"));

            System.out.println("=========>>>>>>" + columns.getString("COLUMN_NAME"));
            System.out.println(columns.getObject("DATA_TYPE"));
            System.out.println(columns.getString("TABLE_CAT"));
            System.out.println(columns.getString("TYPE_NAME"));
            System.out.println(columns.getString("BUFFER_LENGTH"));
            System.out.println(columns.getString("DECIMAL_DIGITS"));
            System.out.println(columns.getString("NUM_PREC_RADIX"));
            System.out.println("NULLABLE: " + columns.getString("NULLABLE"));
            System.out.println(columns.getString("COLUMN_DEF"));
            System.out.println(columns.getString("SQL_DATA_TYPE"));
            System.out.println(columns.getString("SQL_DATETIME_SUB"));
            System.out.println("IS_NULLABLE: " + columns.getString("IS_NULLABLE"));
            System.out.println(columns.getString("SCOPE_TABLE"));
            System.out.println(columns.getString("SOURCE_DATA_TYPE"));
            System.out.println(columns.getString("IS_GENERATEDCOLUMN"));

            if (table.getPkNames().contains(columnInfo.getName())) {
                table.getPkColumns().add(columnInfo);
            }
            fields.add(columnInfo);
            //insert
            if (makeCodeConfiguration.getInsertExclude() == null) {
                insertFields.add(columnInfo);
            } else {
                boolean exc = false;
                for (String exclude : makeCodeConfiguration.getInsertExclude()) {
                    if (exclude.equalsIgnoreCase(columnInfo.getName())) {
                        exc = true;
                        break;
                    }
                }
                if (!exc) insertFields.add(columnInfo);
            }
            //update
            if (makeCodeConfiguration.getUpdateExclude() == null) {
                updateFields.add(columnInfo);
            } else {
                boolean exc = false;
                for (String exclude : makeCodeConfiguration.getUpdateExclude()) {
                    if (exclude.equalsIgnoreCase(columnInfo.getName())) {
                        exc = true;
                        break;
                    }
                }
                if (!exc) updateFields.add(columnInfo);
            }
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

        ResultSet tableInfo = metaData.getTables(database, null, tableName, new String[]{"TABLE"});
        tableInfo.next();
        table.setRemarks(tableInfo.getString("REMARKS"));

        ResultSet pkRS = metaData.getPrimaryKeys(database, null, tableName);
        while (pkRS.next()) {
            table.getPkNames().add(pkRS.getString("COLUMN_NAME"));
        }

        ResultSet columns = metaData.getColumns(database, "%", tableName, "%");
        this.fillColumns(table, columns);

        return table;
    }

    public Connection getConnection() throws Exception {
        if (dataSource == null) {
            dataSource = getDruidDataSource();
        }
        return dataSource.getConnection();
    }

    public DruidDataSource getDruidDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(dbUrl);
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        dataSource.setInitialSize(1);
        dataSource.setMinIdle(1);
        dataSource.setMaxActive(20);
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeout(30);
        //配置获取连接等待超时的时间
        dataSource.setMaxWait(20000);
        //配置间隔多久(毫秒)才进行一次检测，检测需要关闭的空闲连接
        dataSource.setTimeBetweenEvictionRunsMillis(20000);
        //防止过期
        dataSource.setValidationQuery("SELECT 'x'");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(true);
        // 建立了连接
        return dataSource;
    }

}
