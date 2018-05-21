
package example.gifmanager;

import java.util.ArrayList;
import java.util.Date;

public class DataHolder {
    private Date currentDate;
    private String adminCode;
    private String adminEmail;
    private int Nr;
    private String groupCode;
    private String team1Name;
    private String team2Name;
    private ArrayList<String> team1Members;
    private ArrayList<String> team2Members;
    private String resultImagePath;
    private String fairplayImagePath;
    private String signaturePath;
    private String reportPath;

    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}


    public String getAdminCode() {
        return adminCode;
    }

    public void setAdminCode(String adminCode) {
        this.adminCode = adminCode;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public ArrayList<String> getTeam1Members() {
        return team1Members;
    }

    public void setTeam1Members(ArrayList<String> team1Members) {
        this.team1Members = team1Members;
    }

    public ArrayList<String> getTeam2Members() {
        return team2Members;
    }

    public void setTeam2Members(ArrayList<String> team2Members) {
        this.team2Members = team2Members;
    }

    public int getNr() {
        return Nr;
    }

    public void setNr(int nr) {
        Nr = nr;
    }

    public String getTeam1Name() {
        return team1Name;
    }

    public void setTeam1Name(String team1Name) {
        this.team1Name = team1Name;
    }

    public String getTeam2Name() {
        return team2Name;
    }

    public void setTeam2Name(String team2Name) {
        this.team2Name = team2Name;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getResultImagePath() {
        return resultImagePath;
    }

    public void setResultImagePath(String resultImagePath) {
        this.resultImagePath = resultImagePath;
    }

    public String getFairplayImagePath() {
        return fairplayImagePath;
    }

    public void setFairplayImagePath(String fairplayImagePath) {
        this.fairplayImagePath = fairplayImagePath;
    }

    public String getSignaturePath() {
        return signaturePath;
    }

    public void setSignaturePath(String signaturePath) {
        this.signaturePath = signaturePath;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }
}