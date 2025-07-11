package com.autohome.mcpstore.models;

public class McpStoreConfig {
    private String title;
    private String submitServerUrl;
    private String issueUrl;

    public McpStoreConfig() {
    }

    public String getSubmitServerUrl() {
        return submitServerUrl;
    }

    public void setSubmitServerUrl(String submitServerUrl) {
        this.submitServerUrl = submitServerUrl;
    }

    public String getIssueUrl() {
        return issueUrl;
    }

    public void setIssueUrl(String issueUrl) {
        this.issueUrl = issueUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
