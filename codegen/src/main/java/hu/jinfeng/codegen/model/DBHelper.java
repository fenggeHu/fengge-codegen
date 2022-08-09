package hu.jinfeng.codegen.model;

import hu.jinfeng.codegen.config.MakeCodeConfiguration;
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
import java.util.stream.Collectors;

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

//    private DataSource dataSource = null;

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

            // pk
            if (table.getPkNames().contains(columnInfo.getName())) {
                table.getPkColumns().add(columnInfo);
            }
            // uk
            if (table.getUkNames().contains(columnInfo.getName())) {
                table.getUkColumns().add(columnInfo);
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
        // 数据库的连接参数必须加上remarks=true&useInformationSchema=true才能读取到REMARKS
        table.setRemarks(tableInfo.getString("REMARKS"));

        ResultSet pkRS = metaData.getPrimaryKeys(database, null, tableName);
        while (pkRS.next()) {
            table.getPkNames().add(pkRS.getString("COLUMN_NAME"));
        }

        ResultSet ukRs = metaData.getIndexInfo(database, null, tableName, true, false);
        while (ukRs.next()) {
            String cn = ukRs.getString("COLUMN_NAME");
            if (table.getPkNames().contains(cn)) continue;
            table.getUkNames().add(cn);
        }

        ResultSet columns = metaData.getColumns(database, "%", tableName, "%");
        this.fillColumns(table, columns);

        List<ColumnInfo> indexColumns = new LinkedList<>();
        ResultSet index = metaData.getIndexInfo(database, "%", tableName, false, false);
        while (index.next()) {
            String colName = index.getString("COLUMN_NAME");
            ColumnInfo columnInfo = table.getColumnInfo(colName);
            indexColumns.add(columnInfo);
        }
        table.getIndexColumns().addAll(indexColumns.stream().distinct().collect(Collectors.toList()));
        return table;
    }

    public Connection getConnection() {
        Connection conn = null;
        try {
            //连接数据库
//            Properties props = new Properties();
////            props.put("remarksReporting", "true");
//            props.put("user", username);
//            props.put("password", password);
//            conn = DriverManager.getConnection(dbUrl, props);

            conn = DriverManager.getConnection(dbUrl, username, password);
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

//    public Connection getConnection() throws Exception {
//        if (dataSource == null) {
//            dataSource = getDruidDataSource();
//        }
//        return dataSource.getConnection();
//    }
//
//    public DruidDataSource getDruidDataSource() {
//        DruidDataSource dataSource = new DruidDataSource();
//
//        dataSource.setUrl(dbUrl);
//        dataSource.setDriverClassName(driverClassName);
//        dataSource.setUsername(username);
//        dataSource.setPassword(password);
//
//        dataSource.setInitialSize(1);
//        dataSource.setMinIdle(1);
//        dataSource.setMaxActive(20);
//        dataSource.setRemoveAbandoned(true);
//        dataSource.setRemoveAbandonedTimeout(30);
//        //配置获取连接等待超时的时间
//        dataSource.setMaxWait(20000);
//        //配置间隔多久(毫秒)才进行一次检测，检测需要关闭的空闲连接
//        dataSource.setTimeBetweenEvictionRunsMillis(20000);
//        //防止过期
//        dataSource.setValidationQuery("SELECT 'x'");
//        dataSource.setTestWhileIdle(true);
//        dataSource.setTestOnBorrow(true);
//        // 建立了连接
//        return dataSource;
//    }

}
