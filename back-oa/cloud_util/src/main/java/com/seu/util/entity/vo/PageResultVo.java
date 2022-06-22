package com.seu.util.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResultVo<T> implements Serializable {

    private static final long serialVersionUID = -5353106507752414254L;
    /**
     *总条数
     */
    private Long count;
    //当前条数
    private Long size;
    //当前页数
    private Long pages;
    /**
     * 数据
     */
    private List<T> rows;

}
