package com.sondertara.excel.domain.school;

import java.util.List;

/**
 * @author Chimm Huang
 */
public class SchoolReportData {

    private String title;
    private GradesRanking gradesRanking;
    private List<String> hotCommentsList;
    private List<ClassScore> classScoreList;
    private String principalComment;

    public GradesRanking getGradesRanking() {
        return gradesRanking;
    }

    public void setGradesRanking(GradesRanking gradesRanking) {
        this.gradesRanking = gradesRanking;
    }

    public List<String> getHotCommentsList() {
        return hotCommentsList;
    }

    public void setHotCommentsList(List<String> hotCommentsList) {
        this.hotCommentsList = hotCommentsList;
    }

    public List<ClassScore> getClassScoreList() {
        return classScoreList;
    }

    public void setClassScoreList(List<ClassScore> classScoreList) {
        this.classScoreList = classScoreList;
    }

    public String getPrincipalComment() {
        return principalComment;
    }

    public void setPrincipalComment(String principalComment) {
        this.principalComment = principalComment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
