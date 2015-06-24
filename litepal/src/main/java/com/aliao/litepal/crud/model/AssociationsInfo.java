package com.aliao.litepal.crud.model;

import java.lang.reflect.Field;

/**
 * Created by 丽双 on 2015/6/23.
 * 该模型类
 */
public class AssociationsInfo {

    /**
     * 本类类名
     */
    private String selfClassName;

    /**
     * 与本类相关联的类类名
     */
    private String associatedClassName;

    /**
     * 持有外键的类
     */
    private String classHoldsForeignKey;

    /**
     * 本类中声明和其他类有关联关系的字段
     */
    private Field associateOtherModelFromSelf;

    /**
     * 关联类中声明和本类有关联关系的字段
     */
    private Field associateSelfFromOtherModel;

    /**
     * 关联类型
     */
    private int associationType;

    public int getAssociationType() {
        return associationType;
    }

    public void setAssociationType(int associationType) {
        this.associationType = associationType;
    }

    public String getSelfClassName() {
        return selfClassName;
    }

    public void setSelfClassName(String selfClassName) {
        this.selfClassName = selfClassName;
    }

    public String getAssociatedClassName() {
        return associatedClassName;
    }

    public void setAssociatedClassName(String associatedClassName) {
        this.associatedClassName = associatedClassName;
    }

    public Field getAssociateSelfFromOtherModel() {
        return associateSelfFromOtherModel;
    }

    public void setAssociateSelfFromOtherModel(Field associateSelfFromOtherModel) {
        this.associateSelfFromOtherModel = associateSelfFromOtherModel;
    }

    public String getClassHoldsForeignKey() {
        return classHoldsForeignKey;
    }

    public void setClassHoldsForeignKey(String classHoldsForeignKey) {
        this.classHoldsForeignKey = classHoldsForeignKey;
    }

    public Field getAssociateOtherModelFromSelf() {
        return associateOtherModelFromSelf;
    }

    public void setAssociateOtherModelFromSelf(Field associateOtherModelFromSelf) {
        this.associateOtherModelFromSelf = associateOtherModelFromSelf;
    }
}
