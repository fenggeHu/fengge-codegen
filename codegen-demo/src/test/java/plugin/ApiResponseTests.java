package plugin;

import org.junit.Test;

/**
 * @Description:
 * @Author jinfeng.hu  @Date 2022/8/9
 **/
public class ApiResponseTests {

    @Test
    public void testApiResponse() {
        ApiResponse response = ApiResponse.success(PaginationData.of());
    }

}
