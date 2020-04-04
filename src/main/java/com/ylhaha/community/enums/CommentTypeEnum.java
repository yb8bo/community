package com.ylhaha.community.enums;

/**
 * @author yl
 */
public enum CommentTypeEnum {

    QUESTION(1),
    COMMENT(2);
    private Integer type;

    CommentTypeEnum(Integer type){
        this.type = type;
    }

    public static boolean isExit(Integer type) {
        for (CommentTypeEnum commentTypeEnum : CommentTypeEnum.values()) {
            if(commentTypeEnum.getType().equals(type)){
                return true;
            }
        }
        return false;
    }

    public Integer getType() {
        return type;
    }
}
