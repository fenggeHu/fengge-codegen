package hu.jinfeng.commons.utils;

/**
 * @Author hujinfeng  @Date 2020/11/28
 **/
public class NameStringUtils {

    /**
     * 属性命名
     */
    public static String toPropertyName(String name) {
        return toCamelCase(name);
    }

    /**
     * 类名
     */
    public static String toClassName(String name) {
        return toCamelCase(name);
    }

    public static String toDBColumnName(String name) {
        return reverseCamelLowerCase(name);
    }

    /**
     * 把驼峰转下划线,并全部转小写. UserName --> user_name
     */
    public static String reverseCamelLowerCase(String name) {
        StringBuilder sb = new StringBuilder();
        char[] ch = name.trim().toCharArray();

        for (int i = 0; i < ch.length; i++) {
            if (Character.isUpperCase(ch[i])) {
                if (i > 0) sb.append("_");
                sb.append(Character.toLowerCase(ch[i]));
            } else {
                sb.append(ch[i]);
            }
        }
        return sb.toString();
    }

    public static String toCamelCase(String name) {
        StringBuilder sb = new StringBuilder();
        char[] ch = name.trim().toCharArray();
        boolean split = false;
        for (int i = 0; i < ch.length; i++) {
            if (sb.length() == 0) {
                sb.append(Character.toUpperCase(ch[i]));
            } else {
                if (ch[i] == '-' || ch[i] == '_') {
                    split = true;
                    continue;
                }
                if (split) {
                    sb.append(Character.toUpperCase(ch[i]));
                    split = false;
                } else {
                    sb.append(ch[i]);
                }
            }
        }

        return sb.toString();
    }

}
