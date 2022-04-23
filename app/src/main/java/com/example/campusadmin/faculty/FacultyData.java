package com.example.campusadmin.faculty;

public class FacultyData {
    private String name,email,post,download_url,unique_key;

    public FacultyData() {
    }

    public FacultyData(String name, String email, String post, String download_url, String unique_key) {
        this.name = name;
        this.email = email;
        this.post = post;
        this.download_url = download_url;
        this.unique_key = unique_key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getUnique_key() {
        return unique_key;
    }

    public void setUnique_key(String unique_key) {
        this.unique_key = unique_key;
    }
}
