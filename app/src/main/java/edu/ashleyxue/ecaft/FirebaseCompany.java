package edu.ashleyxue.ecaft;

/**
 * Created by Ashley on 1/5/2017.
 */
public class FirebaseCompany {
    public String id;
    public String information;
    public String jobtitles;
    public String jobtypes;
    public String location;
    public String majors;
    public String name;
    public boolean optcpt;
    public boolean sponsor;
    public String website;

    public FirebaseCompany() {}

    public FirebaseCompany(String id, String information, String jobtitles,
                           String jobtypes, String location, String majors,
                           String name, boolean optcpt, boolean sponsor, String
                           website) {
        this.id = id;
        this.information = information;
        this.jobtitles = jobtitles;
        this.jobtypes = jobtypes;
        this.location = location;
        this.majors = majors;
        this.name = name;
        this.optcpt = optcpt;
        this.sponsor = sponsor;
        this.website = website;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getJobtitles() {
        return jobtitles;
    }

    public void setJobtitles(String jobtitles) {
        this.jobtitles = jobtitles;
    }

    public String getJobtypes() {
        return jobtypes;
    }

    public void setJobtypes(String jobtypes) {
        this.jobtypes = jobtypes;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMajors() {
        return majors;
    }

    public void setMajors(String majors) {
        this.majors = majors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOptcpt() {
        return optcpt;
    }

    public void setOptcpt(boolean optcpt) {
        this.optcpt = optcpt;
    }

    public boolean isSponsor() {
        return sponsor;
    }

    public void setSponsor(boolean sponsor) {
        this.sponsor = sponsor;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
