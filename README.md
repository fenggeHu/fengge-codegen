# fengge-codegen
1，修改数据库连接
2，启动，http://localhost:8080/swagger-ui.html
3，make，输入package、输入表名（多个表逗号分隔）。

# 分库分表
- 在字段的备注中加入注解标记是否分库或分表字段。
- eg: 分库字段 @{shardingDB} ； 分表字段 @{shardingTable}

# 模板生成
- sql、mybatis、base repository、 controller

# 设计步骤
数据库设计 -- mapper -[common service] -->api -- admin

