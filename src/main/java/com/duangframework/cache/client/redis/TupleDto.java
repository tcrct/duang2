package com.duangframework.cache.client.redis;

/**
 * @author Created by laotang
 * @date createed in 2018/1/20.
 */
public class TupleDto implements java.io.Serializable {

    private String element;
    private Double score;

    public TupleDto(String element, Double score) {
        this.element = element;
        this.score = score;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
