package hu.jinfeng.codegen.db;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
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

    private DruidDataSource dataSource = null;

    public DatabaseMetaData getDatabaseMetaData() throws Exception {
        return getConnection().getMetaData();
    }

    public List<Table> getAllTables(String database) throws Exception {
        List<Table> result = new ArrayList<>();
        DatabaseMetaData metaData = getDatabaseMetaData();
        if(null == database) {
            database = metaData.getConnection().getCatalog();
        }
        ResultSet rs = metaData.getTables(database, null, null, new String[]{"TABLE"});
        while(rs.next()) {
            Table table = new Table();
            result.add(table);
            table.setDatabase(rs.getString("TABLE_CAT"));
            table.setName(rs.getString("TABLE_NAME"));
            table.setRemarks(rs.getString("REMARKS"));

            ResultSet columns = metaData.getColumns(database, "%", table.getName(), "%");
            List<Column> fields = new ArrayList<>();
            table.setColumns(fields);
            while (columns.next()) {
                Column column = new Column();
                column.setName(columns.getString("COLUMN_NAME"));
                column.setTableName(columns.getString("TABLE_NAME"));
                column.setType(columns.getString("TYPE_NAME"));
                column.setSize(columns.getString("COLUMN_SIZE"));
                column.setRemarks(columns.getString("REMARKS"));
                fields.add(column);
            }
        }

        return result;
    }

    public Table getTableInfo(String database, String tableName) throws Exception {
        DatabaseMetaData metaData = getDatabaseMetaData();
        if(null == database) {
            database = metaData.getConnection().getCatalog();
        }
        Table table = new Table();
        table.setName(tableName);
        table.setDatabase(database);
        ResultSet rs = metaData.getTables(database, null, tableName, new String[]{"TABLE"});
        rs.next();
        table.setRemarks(rs.getString("REMARKS"));
        ResultSet columns = getDatabaseMetaData().getColumns(database, "%", tableName, "%");
        List<Column> fields = new ArrayList<>();
        table.setColumns(fields);
        while (columns.next()) {
            Column column = new Column();
            column.setName(columns.getString("COLUMN_NAME"));
            column.setType(columns.getString("TYPE_NAME"));
            column.setSize(columns.getString("COLUMN_SIZE"));
            column.setRemarks(columns.getString("REMARKS"));
            fields.add(column);
        }

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
