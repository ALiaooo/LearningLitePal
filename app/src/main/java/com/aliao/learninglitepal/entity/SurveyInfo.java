package com.aliao.learninglitepal.entity;

import java.io.Serializable;
/**
 * Created by 丽双 on 2015/4/23.
 * 问卷基本信息
 */
public class SurveyInfo {

    private long id;
    private String surveyId;//问卷id
    private String title;//问卷标题
    private int typeID;//问卷类别id
    private String typeName;//问卷类别名称
    private String companyName;//服务企业
    private String updateTime;
    private String collectEndTime;//问卷过期时间
    private String uploadTotal;//上传总份数
   /* private UserInfo userInfo;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }*/


    public String getUploadTotal() {
        return uploadTotal;
    }

    public void setUploadTotal(String uploadTotal) {
        this.uploadTotal = uploadTotal;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTypeID() {
        return typeID;
    }

    public void setTypeID(int typeID) {
        this.typeID = typeID;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCollectEndTime() {
        return collectEndTime;
    }

    public void setCollectEndTime(String collectEndTime) {
        this.collectEndTime = collectEndTime;
    }


}
