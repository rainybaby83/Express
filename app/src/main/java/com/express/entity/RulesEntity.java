package com.express.entity;


@lombok.Getter
public class RulesEntity {
    private String keyword;
    private String codeLeft;
    private String codeRight;


    public RulesEntity(String keyword, String left, String right) {
        this.keyword = keyword;
        this.codeLeft = left;
        this.codeRight = right;
    }

}
