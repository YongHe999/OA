package com.seu.util.entity.bo;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author LANIAKEA
 * @version 1.0
 * @date 2021-07-08
 */
public class ArticleBo {

    private String id;
    @NotBlank(message = "文章标题不能为空")
    @Length(max = 30, message = "文章标题长度不能超过30")
    private String title;

//    @NotBlank(message = "文章内容不能为空")
//    @Length(max = 9999, message = "文章内容长度不能超过10000")
    private String mdContent;

    @NotBlank(message = "文章内容不能为空")
    @Length(max = 9999, message = "文章html内容长度不能超过10000")
    private String htmlContent;

    @NotNull(message = "请选择文章分类")
    private Integer cid;

    @NotNull(message = "用户id不为空")
    private String  uid;

    @NotNull(message = "状态不能为空")
    private Integer state;

    public ArticleBo() {
    }

    public ArticleBo(String aid, @NotBlank(message = "文章标题不能为空") @Length(max = 30, message = "文章标题长度不能超过30") String title, String mdContent, @NotBlank(message = "文章内容不能为空") @Length(max = 9999, message = "文章html内容长度不能超过10000") String htmlContent, @NotNull(message = "请选择文章分类") Integer cid, @NotNull(message = "用户id不为空") String uid, @NotNull(message = "状态不能为空") Integer state, @NotNull(message = "状态不能为空") String summary, Integer tid) {

        this.title = title;
        this.mdContent = mdContent;
        this.htmlContent = htmlContent;
        this.cid = cid;
        this.uid = uid;
        this.state = state;
        this.summary = summary;
        this.tid = tid;
    }

    public String getSummary() {
        return summary;
    }

    public ArticleBo setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public String getId() {
        return id;
    }

    public ArticleBo setId(String id) {
        this.id = id;
        return this;
    }

    @NotNull(message = "状态不能为空")
    private String summary;

    public Integer getState() {
        return state;
    }

    //标签id
    private Integer tid;

    public Integer getTid() {
        return tid;
    }

    public ArticleBo setTid(Integer tid) {
        this.tid = tid;
        return this;
    }

    public ArticleBo setState(Integer state) {
        this.state = state;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ArticleBo setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getMdContent() {
        return mdContent;
    }

    public ArticleBo setMdContent(String mdContent) {
        this.mdContent = mdContent;
        return this;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public ArticleBo setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
        return this;
    }

    public Integer getCid() {
        return cid;
    }

    public ArticleBo setCid(Integer cid) {
        this.cid = cid;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public ArticleBo setUid(String uid) {
        this.uid = uid;
        return this;
    }
}
