package generator.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import generator.domain.ApiResponse;
import generator.page.PageDomain;
import generator.page.TableDataInfo;
import generator.page.TableSupport;
import generator.util.DateUtils;
import generator.util.PageUtils;
import generator.util.SqlUtil;
import generator.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;

/**
 * web层通用数据处理
 *
 * @author fengge
 */
public class BaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage() {
        PageUtils.startPage();
    }

    /**
     * 设置请求排序数据
     */
    protected void startOrderBy() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        if (StringUtils.isNotEmpty(pageDomain.getOrderBy())) {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.orderBy(orderBy);
        }
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected TableDataInfo getDataTable(List<?> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.OK.value());
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }

    /**
     * 返回成功
     */
    public ApiResponse success() {
        return ApiResponse.success();
    }

    /**
     * 返回失败消息
     */
    public ApiResponse error() {
        return ApiResponse.error();
    }

    /**
     * 返回成功消息
     */
    public ApiResponse success(String message) {
        return ApiResponse.success(message);
    }

    /**
     * 返回失败消息
     */
    public ApiResponse error(String message) {
        return ApiResponse.error(message);
    }

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected ApiResponse toAjax(int rows) {
        return rows > 0 ? ApiResponse.success() : ApiResponse.error();
    }

    /**
     * 响应返回结果
     *
     * @param result 结果
     * @return 操作结果
     */
    protected ApiResponse toAjax(boolean result) {
        return result ? success() : error();
    }

    /**
     * 页面跳转
     */
    public String redirect(String url) {
        return StringUtils.format("redirect:{}", url);
    }

    /**
     * 获取用户缓存信息
     */
//    public LoginUser getLoginUser() {
//        return SecurityUtils.getLoginUser();
//    }

    /**
     * 获取登录用户id
     */
//    public Long getUserId() {
//        return getLoginUser().getUserId();
//    }

    /**
     * 获取登录部门id
     */
//    public Long getDeptId() {
//        return getLoginUser().getDeptId();
//    }

    /**
     * 获取登录用户名
     */
//    public String getUsername() {
//        return getLoginUser().getUsername();
//    }
}
