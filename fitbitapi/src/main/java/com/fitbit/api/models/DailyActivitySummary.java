package com.fitbit.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DailyActivitySummary {

    @SerializedName("activities")
    @Expose
    private List<Object> activities = new ArrayList<Object>();
    @SerializedName("goals")
    @Expose
    private Goals goals;
    @SerializedName("summary")
    @Expose
    private Summary summary;
    @SerializedName("pagination")
    @Expose
    private Pagination pagination;

    /**
     * @return The activities
     */
    public List<Object> getActivities() {
        return activities;
    }

    /**
     * @param activities The activities
     */
    public void setActivities(List<Object> activities) {
        this.activities = activities;
    }

    /**
     * @return The goals
     */
    public Goals getGoals() {
        return goals;
    }

    /**
     * @param goals The goals
     */
    public void setGoals(Goals goals) {
        this.goals = goals;
    }

    /**
     * @return The summary
     */
    public Summary getSummary() {
        return summary;
    }

    /**
     * @param summary The summary
     */
    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
