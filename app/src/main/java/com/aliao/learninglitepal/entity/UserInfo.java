package com.aliao.learninglitepal.entity;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aspsine on 2015/4/28.
 * 用户
 */
public class UserInfo implements Serializable {
    private long id;
    private String userId;
    private String userName;
    private String permissionEndTime;
    private String realName;
    private String email;

    private List<SurveyInfo> surveyInfos = new ArrayList<>();//与模板问卷一对多

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPermissionEndTime() {
        return permissionEndTime;
    }

    public void setPermissionEndTime(String permissionEndTime) {
        this.permissionEndTime = permissionEndTime;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public List<SurveyInfo> getSurveyInfos() {
        return surveyInfos;
    }

    public void setSurveyInfos(List<SurveyInfo> surveyInfos) {
        this.surveyInfos = surveyInfos;
    }



    @Override
    public String toString() {
        return "UserInfo{" +
                "iUserID='" + userId + '\'' +
                ", sUserName='" + userName + '\'' +
                ", dPermissionEndTime='" + permissionEndTime + '\'' +
                ", sRealName='" + realName + '\'' +
                '}';
    }
}
