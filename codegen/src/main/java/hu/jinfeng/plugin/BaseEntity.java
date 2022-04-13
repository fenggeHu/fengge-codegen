package hu.jinfeng.plugin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author Jinfeng.hu  @Date 2022/4/13
 **/
@Data
public class BaseEntity {

    /** 状态值 1启用 0禁用 */
    @ApiModelProperty("状态值 1启用 0禁用")
    private Integer status;

    /** 创建时间 */
    @ApiModelProperty("创建时间")
    private String createdAt;

    /** 更新时间 */
    @ApiModelProperty("更新时间")
    private String updatedAt;
}
