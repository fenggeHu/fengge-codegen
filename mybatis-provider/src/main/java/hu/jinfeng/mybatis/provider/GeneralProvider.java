package hu.jinfeng.mybatis.provider;

import hu.jinfeng.commons.utils.NameStringUtils;
import lombok.SneakyThrows;
import org.apache.ibatis.jdbc.SQL;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MyBatis SQL Provider
 *
 * @Author hujinfeng  @Date 2020/11/27
 **/
public class GeneralProvider {
    private static final Map<Class<?>, EntityInfo> ENTITY_INFO_MAP = new ConcurrentHashMap<>();

    public String insertSQL(Object object) {
        if (null == object) throw new RuntimeException("entity object is null");
        EntityInfo entityInfo = ENTITY_INFO_MAP.computeIfAbsent(object.getClass(), EntityInfo::new);
        SQL sql = new SQL().INSERT_INTO(getTableName(entityInfo, object));

        for (FieldInfo fieldInfo : entityInfo.fields) {
            if (!fieldInfo.nullable && fieldInfo.isNull(object)) {
                throw new RuntimeException("字段值不能为空: " + fieldInfo.fieldName);
            }
            if (fieldInfo.insertable && fieldInfo.isNotNull(object)) {
                sql.VALUES(fieldInfo.dbFieldName, String.format("#{%s}", fieldInfo.fieldName));
            }
        }

        return sql.toString();
    }

    public String updateSQL(Object object) {
        if (null == object) throw new RuntimeException("entity object is null");
        EntityInfo entityInfo = ENTITY_INFO_MAP.computeIfAbsent(object.getClass(), EntityInfo::new);

        if (!entityInfo.primaryKeyField.isNotNull(object)) {
            throw new RuntimeException("主键不能为空。Object:" + object);
        }

        SQL sql = new SQL().UPDATE(getTableName(entityInfo, object));
        for (FieldInfo fieldInfo : entityInfo.fields) {
            if (fieldInfo.fieldName.equals(entityInfo.primaryKeyField.fieldName)) {
                continue;
            }
            if (fieldInfo.updatable) {
                sql.SET(String.format("%s = #{%s}", fieldInfo.dbFieldName, fieldInfo.fieldName));
            }
        }
        sql.WHERE(String.format("%s = #{%s}", entityInfo.primaryKeyField.dbFieldName, entityInfo.primaryKeyField.fieldName));

        return sql.toString();
    }

    private String getTableName(EntityInfo entityInfo, Object object) {
        return entityInfo.tableName != null ? entityInfo.tableName : entityInfo.sharingTable.name(object);
    }

    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    private static class FieldInfo {
        String dbFieldName;
        String fieldName;
        Field field;
        boolean nullable = true;
        boolean insertable = true;
        boolean updatable = true;

        FieldInfo(Field field, Column column) {
            fieldName = field.getName();
            if (!isBlank(column.name())) {
                dbFieldName = column.name();
            } else {
                if (column.camelCase()) { //驼峰命名规则，createTime 转 create_time
                    dbFieldName = NameStringUtils.reverseCamelLowerCase(fieldName);
                } else {
                    dbFieldName = fieldName;
                }
            }
            nullable = column.nullable();
            insertable = column.insertable();
            updatable = column.updatable();
            field.setAccessible(true);
            this.field = field;
        }

        FieldInfo(Field field, Entity entity) {
            fieldName = field.getName();
            if (entity.camelCase()) {
                dbFieldName = NameStringUtils.reverseCamelLowerCase(fieldName);
            } else {
                dbFieldName = fieldName;
            }
            field.setAccessible(true);
            this.field = field;
        }

        boolean isNull(Object obj) {
            try {
                return field.get(obj) == null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        boolean isNotNull(Object obj) {
            return !isNull(obj);
        }
    }

    private static class EntityInfo {
        String tableName;
        SharingTable sharingTable;
        FieldInfo primaryKeyField;
        List<FieldInfo> fields = new ArrayList<>();

        @SneakyThrows
        EntityInfo(Class<?> cls) {
            Entity entity = cls.getAnnotation(Entity.class);
            if (null == entity) throw new RuntimeException("未指定Entity注解。Class:" + cls.getName());

            boolean notDefinedTable = isBlank(entity.table())
                    && SharingTable.class.isAssignableFrom(entity.sharingTable());
            if (notDefinedTable) { //当注解没有设置表名时，使用entity类名转表名
                if (entity.camelCase()) {
                    tableName = NameStringUtils.reverseCamelLowerCase(cls.getSimpleName());
                } else {
                    tableName = cls.getSimpleName();
                }
            } else if (!isBlank(entity.table())) {
                tableName = entity.table();
            } else if (SharingTable.class.isAssignableFrom(entity.sharingTable())) {
                sharingTable = (SharingTable) entity.sharingTable().getDeclaredConstructor().newInstance();
            }

            while (!Object.class.equals(cls)) {
                for (Field field : cls.getDeclaredFields()) {
                    if (field.isSynthetic()) {
                        continue;
                    }
                    Column column = field.getAnnotation(Column.class);
                    if (null != column && column.ignore()) {
                        continue;
                    }

                    FieldInfo fieldInfo;
                    if (null == column) {
                        fieldInfo = new FieldInfo(field, entity);
                    } else {
                        fieldInfo = new FieldInfo(field, column);
                    }
                    fields.add(fieldInfo);

                    if (field.getName().equalsIgnoreCase(entity.primaryKey())) {
                        primaryKeyField = fieldInfo;
                    }
                }

                cls = cls.getSuperclass();
            }
        }
    }
}