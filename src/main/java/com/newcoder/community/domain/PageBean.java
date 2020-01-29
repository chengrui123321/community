package com.newcoder.community.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 分页信息
 */
public class PageBean<T> {

    /** 分页查询数据 */
    private List<T> content;

    /** 每页记录数 */
    private Integer size = 10;

    /** 当前页 */
    private Integer current = 1;

    /** 总页数 */
    private Integer totalPage;

    /** 总记录数 */
    private Integer rows;

    /** 查询路径 */
    private String path;


    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
        this.totalPage = rows % size == 0 ? rows / size : rows / size + 1;
    }

    public Integer getFrom() {
        int from = this.current - 2;
        return from < 1 ? 1 : from;
    }


    public Integer getTo() {
        int to = this.current + 2;
        return to > this.totalPage ? this.totalPage : to;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public PageBean(List<T> content, Integer rows) {
        this.content = content;
        this.rows = rows;
    }

    public PageBean() {
    }
}
