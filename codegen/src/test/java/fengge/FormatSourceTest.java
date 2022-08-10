package fengge;

import com.google.googlejavaformat.java.Formatter;
import hu.jinfeng.commons.utils.FileUtils;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;

/**
 * @Description:
 * @Author Jinfeng.hu  @Date 2022/3/17
 **/
public class FormatSourceTest {

    @SneakyThrows
    public static void main(String[] args) {
        String content = FileUtils.readLocalFile("/Users/max/opensource/jinfeng-stock/jinfeng-stock-common/src/main/java/hu/jinfeng/stock/common/talib/Core.java");


        FileUtils.writeFile("/Users/max/core_java.txt", new Formatter().formatSource(content));
    }
}
