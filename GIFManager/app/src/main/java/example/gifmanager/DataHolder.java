package example.gifmanager;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

public class DataHolder {

    private String currentDate;
    private String adminCode;
    private String adminEmail;
    private String Nr;
    private String groupCode;
    private String timeOfMatch;
    private String fieldNumber;
    private String team1Name;
    private String team2Name;
    private String team1Url;
    private String team2Url;
    private ArrayList<String> team1Members;
    private ArrayList<String> team2Members;
    private String resultImagePath;
    private String fairplayImagePath;
    private String team1SignaturePath;
    private String team2SignaturePath;
    private String reportPath;
    private int activeTeam;
    private String parceUrl;
    private int target_age; //2 digit number

    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}

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

    public String getNr() {
        return Nr;
    }

    public void setNr(String nr) {
        Nr = nr;
    }

    public String getFieldNumber(){
        return fieldNumber;
    }

    public void setFieldNumber(String fieldNumber){
        this.fieldNumber = fieldNumber;
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

    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    public String getAdminCode() {
        return adminCode;
    }

    public void setAdminCode(String adminCode) {
        this.adminCode = adminCode;
    }

    public String getTeam1SignaturePath() {
        return team1SignaturePath;
    }

    public void setTeam1SignaturePath(String team1SignaturePath) {
        this.team1SignaturePath = team1SignaturePath;
    }

    public String getTeam2SignaturePath() {
        return team2SignaturePath;
    }

    public void setTeam2SignaturePath(String team2SignaturePath) {
        this.team2SignaturePath = team2SignaturePath;
    }


    public String getTimeOfMatch() {
        return timeOfMatch;
    }

    public void setTimeOfMatch(String timeOfMatch) {
        this.timeOfMatch = timeOfMatch;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public void setTeam1Url(String team1Url){
        this.team1Url = team1Url;
    }

    public String getTeam1Url(){
        return team1Url;
    }

    public void setTeam2Url(String team2Url){
        this.team2Url = team2Url;
    }

    public String getTeam2Url(){
        return team2Url;
    }

    public void setActiveTeam(int activeTeam) {
        this.activeTeam = activeTeam;
    }
    public int getActiveTeam(){
        return activeTeam;
    }
    public void setParceUrl(){
        this.parceUrl = "http://teamplaycup.se/cup/?games&home=kurirenspelen/"+currentDate+"&scope=all&arena="+fieldNumber+"&field=";
    }
    public String getParceUrl(){
        return parceUrl;
    }
    public void setTarget_age(int target_age){this.target_age = target_age;}
    public int getTarget_age(){return target_age;}

    // initialized variables and sets default values
    public void initDataHolder(){

        this.currentDate = "";
        this.adminCode = "";
        this.adminEmail = "";
        this.Nr = "";
        this.groupCode = "";
        this.timeOfMatch = "";
        this.fieldNumber = "";
        this.team1Name = "";
        this.team2Name = "";
        this.team1Url = "";
        this.team2Url = "";
        this.team1Members = new ArrayList<>();
        this.team2Members = new ArrayList<>();
        this.resultImagePath = Environment.getExternalStorageDirectory() + File.separator +
                "GIFManager" + File.separator + "IMG_result.jpg";
        this.fairplayImagePath = Environment.getExternalStorageDirectory() + File.separator +
                "GIFManager" + File.separator + "IMG_fairplay.jpg";
        this.team1SignaturePath = Environment.getExternalStorageDirectory() + File.separator +
                "GIFManager" + File.separator + "Signatures" + File.separator + "signature_home.png";
        this.team2SignaturePath = Environment.getExternalStorageDirectory() + File.separator +
                "GIFManager" + File.separator + "Signatures" + File.separator + "signature_visit.png";
        this.reportPath = "";
        this.activeTeam = 0;
        this.parceUrl = "";
        this.target_age = 00;
    }

}
