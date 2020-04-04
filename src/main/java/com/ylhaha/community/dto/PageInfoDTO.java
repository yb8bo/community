package com.ylhaha.community.dto;

import org.hibernate.validator.constraints.EAN;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页功能中页面信息
 * @author yl
 */
public class PageInfoDTO {
    //数据
    private List<QuestionDTO> questionDTOS = new ArrayList<>();
    //当前页
    private int currentPage;
    //分页条里的页面数字
    private List<Integer> pageNumbers = new ArrayList<>();
    //总页数
    private Integer totalPages;
    //是否展示第一页链接
    private boolean toFirst = true;
    //是否展示上一页链接
    private boolean toLast = true;
    //是否展示最后一页链接
    private boolean toEnd = true;
    //是否展示下一页链接
    private boolean toNext = true;

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<QuestionDTO> getQuestionDTOS() {
        return questionDTOS;
    }

    public void setQuestionDTOS(List<QuestionDTO> questionDTOS) {
        this.questionDTOS = questionDTOS;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public List<Integer> getPageNumbers() {
        return pageNumbers;
    }

    public void setPageNumbers(List<Integer> pageNumbers) {
        this.pageNumbers = pageNumbers;
    }

    public boolean isToFirst() {
        return toFirst;
    }

    public void setToFirst(boolean toFirst) {
        this.toFirst = toFirst;
    }

    public boolean isToLast() {
        return toLast;
    }

    public void setToLast(boolean toLast) {
        this.toLast = toLast;
    }

    public boolean isToEnd() {
        return toEnd;
    }

    public void setToEnd(boolean toEnd) {
        this.toEnd = toEnd;
    }

    public boolean isToNext() {
        return toNext;
    }

    public void setToNext(boolean toNext) {
        this.toNext = toNext;
    }

    public void setPageInfo(Integer totalCount, Integer currentPage, Integer pageSize) {
        //设置当前页
        setCurrentPage(currentPage);
        //得到总页数
        totalPages = totalCount%pageSize==0?totalCount/pageSize:totalCount/pageSize+1;
        //根据当前页大小设置分页条中的数字
        if(currentPage<4){
            for (int i = 1; i <= 7 && i<=totalPages; i++) {
                pageNumbers.add(i);
            }
        }else {
            for (int i = (currentPage-3); i <= (currentPage+3) && i<=totalPages; i++) {
                pageNumbers.add(i);
            }
        }
        //是否展示去上一页图标
        if(currentPage==1){
            toLast = false;
        }
        //是否展示去下一页图标
        if(currentPage.equals(totalPages)){
            toNext = false;
        }
        //是否展示区第一页图标
        if(pageNumbers.contains(1)){
            toFirst = false;
        }
        //是否展示去最后一页图标
        if(pageNumbers.contains(totalPages)){
            toEnd = false;
        }
    }
}
