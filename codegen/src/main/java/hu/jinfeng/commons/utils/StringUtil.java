package hu.jinfeng.commons.utils;

/**
 * @Description: 处理字符串
 * @Author jinfeng.hu  @Date 2022/8/10
 **/
public class StringUtil {

    public static String replace(String txt, String searchString, String replacement) {
        if (null == txt) return "";
        return txt.replace(searchString, replacement);
    }

    public static String rmNewline(String txt) {
        return replace(txt, "\n", " ");
    }

    public static String firstLine(String txt) {
        if (null == txt) return "";
        int index = txt.indexOf("\n");
        if (index > 0) {
            return txt.substring(0, index).trim();
        } else {
            return txt.trim();
        }
    }

    public static void main(String[] args) {
        String txt = "上市状态： L上市 D退市 P暂停上市\n" +
                "上市状态： L上市 D退市 P暂停上市\n" +
                "上市状态： L上市 D退市 P暂停上市\n" +
                "上市状态： L上市 D退市 P暂停上市";
        String line = firstLine(txt);
        System.out.println(line);
    }
}
