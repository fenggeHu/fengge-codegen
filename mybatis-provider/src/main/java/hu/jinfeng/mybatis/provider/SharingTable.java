package hu.jinfeng.mybatis.provider;

/**
 * 分表数据设计的表名生成器
 */
public interface SharingTable {

    /**
     * 分表名
     */
    String name(Object e);
}