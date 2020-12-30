package com.duangframework.db.mysql.common;

/**
 * Mysql DB operation constant.
 * 
 */
public final class Operator {

    public static final String EMPTY_SPACE= " ";

    public static final String INSERT =  "insert into";
    public static final String UPDATE = "update";
    public static final String DELETE = "delete";
    //query condition
    public static final String EQ = "=";
    public static final String GT = ">";
    public static final String GTE = ">=";
    public static final String LT = "<";
    public static final String LTE = "<=";
    public static final String NE = "!=";
    public static final String IN = "in";
    public static final String NIN = "not in";
    public static final String WHERE = "where";
    //like
    public static final String LIKE = "like";
    //between 
    public static final String BETWEEN = "between";
    
    //query logic
    public static final String AND = "and";
    public static final String OR = "or";
    
    //update
    public static final String SET = "set";
    
    //aggregation
    public static final String ORDER = "order by";
    public static final String LIMIT = "limit";
    public static final String GROUP = "group on";
    public static final String SUM = "sum";
    public static final String COUNT = "count";
    
}
