package hu.jinfeng.plugin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Description: 简单的计算分页
 * @Author Jinfeng.hu  @Date 2021-11-01
 **/
@ApiModel("分页")
public class Pager implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("页码")
    private int pageNum;
    @ApiModelProperty("pageSize")
    private int pageSize;
    @ApiModelProperty(value = "起始行", hidden = true)
    private int startRow;
    @ApiModelProperty(value = "结束行", hidden = true)
    private int endRow;

    public Pager(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.calculateStartAndEndRow();
    }

    public void calculateStartAndEndRow() {
        this.startRow = this.pageNum > 0 ? (this.pageNum - 1) * this.pageSize : 0;
        this.endRow = this.startRow + this.pageSize * (this.pageNum > 0 ? 1 : 0);
    }

    public static Pager defaultPager() {
        return new Pager(1, 20);
    }

    public static Pager getPager(int pageNum, int pageSize) {
        return new Pager(pageNum, pageSize);
    }

    public int getPageNum() {
        return pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getEndRow() {
        return endRow;
    }
}
