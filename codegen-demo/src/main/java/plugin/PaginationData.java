package plugin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 分页返回的data对象
 * 如果存在分页业务需求，分页返回格式采用如下格式返回
 * @Author jinfeng.hu  @Date 2022/8/9
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationData<T> {
    /**
     * 数据总量
     */
    private long total;
    /**
     * 当前页码
     */
    private int current;
    /**
     * 每页数据条数
     */
    private int pageSize;
    /**
     * 当前页的数据
     */
    private List<T> list;

    // 构造对象
    public static PaginationData of() {
        return PaginationData.builder().build();
    }

    public static PaginationData of(long total, int current, int pageSize, List list) {
        return PaginationData.builder().total(total).current(current).pageSize(pageSize).list(list).build();
    }
}
