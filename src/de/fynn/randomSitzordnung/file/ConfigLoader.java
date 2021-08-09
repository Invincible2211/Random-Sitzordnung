package de.fynn.randomSitzordnung.file;

import de.fynn.randomSitzordnung.Profile;
import de.fynn.randomSitzordnung.Student;
import de.fynn.randomSitzordnung.flags.FlagFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ConfigLoader {

    private JSONObject cfg;

    public ConfigLoader(){
        loadCFG();
    }

    private void loadCFG(){
        try {
            JSONParser parser = new JSONParser();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("resources/config.json")));
            cfg = (JSONObject) parser.parse(bufferedReader);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public int getRows(){
        loadCFG();
        return Integer.parseInt(cfg.get("rows")+"");
    }

    public void setRows(int rows){
        cfg.put("rows",rows);
        save();
    }

    public void saveStudentsToCFG(List<Student> students){
        JSONArray jsonArray = new JSONArray();
        for (Student s:
             students) {
            JSONObject student = new JSONObject();
            student.put("name",s.getName());
            student.put("flag",s.getFlag().getName()+","+s.getFlag().getValue());
            jsonArray.add(student);
        }
        cfg.put("students",jsonArray);
        save();
    }

    public List<Student> getStudentsFromCFG(){
        loadCFG();
        JSONArray jsonArray = (JSONArray) cfg.get("students");
        Iterator<JSONObject> iterator = jsonArray.iterator();
        List<Student> students = new ArrayList<>();
        while (iterator.hasNext()){
            JSONObject student = iterator.next();
            students.add(new Student((String)student.get("name"), FlagFactory.createFlag(((String)student.get("flag")).split(","))));
        }
        return students;
    }

    public String getColor(){
        loadCFG();
        return (String) cfg.get("color");
    }

    public void setColor(String color){
        cfg.put("color",color);
        save();
    }

    public String getColor2(){
        loadCFG();
        return (String) cfg.get("color2");
    }

    public void setColor2(String color){
        cfg.put("color2",color);
        save();
    }

    public String getPathExport(){
        loadCFG();
        return (String) cfg.get("export");
    }

    public void setPathExport(String path){
        cfg.put("export",path);
        save();
    }

    public String getPathImport(){
        loadCFG();
        return (String) cfg.get("import");
    }

    public void setPathImport(String path){
        cfg.put("import",path);
        save();
    }

    public String getPathSelect(){
        loadCFG();
        return (String) cfg.get("select");
    }

    public void setPathSelect(String path){
        cfg.put("select",path);
        save();
    }

    public boolean colorGradient(){
        loadCFG();
        return cfg.get("colorGradient").equals(true);
    }

    public void setColorGradient(boolean gradient){
        cfg.put("colorGradient",gradient);
        save();
    }

    public void saveProfile(Profile profile){
        JSONObject jsonprofile = new JSONObject();
        jsonprofile.put("room",profile.getRoom());
        jsonprofile.put("courseNumber",profile.getCourseNumber());
        jsonprofile.put("name",profile.getName());
        JSONArray students = new JSONArray();
        for (Student s:
                profile.getStudents()) {
                JSONObject student = new JSONObject();
                student.put("name",s.getName());
                student.put("flag",s.getFlag().getName()+","+s.getFlag().getValue());
                students.add(student);
        }
        jsonprofile.put("students",students);
        JSONObject obj = new JSONObject();
        obj.put(profile.getName(),jsonprofile);
        JSONArray jsonArray = (JSONArray) cfg.get("profiles");
        jsonArray.add(obj);
        cfg.put("profiles",jsonArray);
        save();
    }

    public List<Profile> getProfiles(){
        loadCFG();
        List<Profile> profiles = new ArrayList<>();
        JSONArray profileArray = (JSONArray) cfg.get("profiles");
        for (Object o:
             profileArray) {
            JSONObject profileObjectParent = (JSONObject) o;
            JSONObject profileObject = (JSONObject) profileObjectParent.get(profileObjectParent.toString().split("\"")[1]);
            JSONArray jsonArray1 = (JSONArray) profileObject.get("students");
            Iterator<JSONObject> iterator = jsonArray1.iterator();
            List<Student> students = new ArrayList<>();
            while (iterator.hasNext()){
                JSONObject student = iterator.next();
                students.add(new Student((String)student.get("name"), FlagFactory.createFlag(((String)student.get("flag")).split(","))));
            }
            String room = (String) profileObject.get("room");
            String courseNumber = (String) profileObject.get("courseNumber");
            String name = (String) profileObject.get("name");
            Profile profile = new Profile(name,students);
            if(!room.equals(""))profile.setRoom(room);
            if(!courseNumber.equals(""))profile.setCourseNumber(courseNumber);
            profiles.add(profile);
        }
        return profiles;
    }

    public void deleteProfile(Profile profile){
        JSONObject jsonprofile = new JSONObject();
        jsonprofile.put("room",profile.getRoom());
        jsonprofile.put("courseNumber",profile.getCourseNumber());
        jsonprofile.put("name",profile.getName());
        JSONArray students = new JSONArray();
        for (Student s:
                profile.getStudents()) {
            JSONObject student = new JSONObject();
            student.put("name",s.getName());
            student.put("flag",s.getFlag().getName()+","+s.getFlag().getValue());
            students.add(student);
        }
        jsonprofile.put("students",students);
        JSONObject obj = new JSONObject();
        obj.put(profile.getName(),jsonprofile);
        JSONArray profileArray = (JSONArray) cfg.get("profiles");
        profileArray.remove(obj);
        save();
    }

    public void editProfile(Profile profileOld,Profile profile){
        JSONObject jsonprofile = new JSONObject();
        jsonprofile.put("room",profile.getRoom());
        jsonprofile.put("courseNumber",profile.getCourseNumber());
        jsonprofile.put("name",profile.getName());
        JSONArray students = new JSONArray();
        for (Student s:
                profile.getStudents()) {
            JSONObject student = new JSONObject();
            student.put("name",s.getName());
            student.put("flag",s.getFlag().getName()+","+s.getFlag().getValue());
            students.add(student);
        }
        jsonprofile.put("students",students);
        JSONObject obj = new JSONObject();
        obj.put(profile.getName(),jsonprofile);

        JSONObject jsonprofile1 = new JSONObject();
        jsonprofile1.put("room",profileOld.getRoom());
        jsonprofile1.put("courseNumber",profileOld.getCourseNumber());
        jsonprofile1.put("name",profileOld.getName());
        JSONArray students1 = new JSONArray();
        for (Student s:
                profileOld.getStudents()) {
            JSONObject student = new JSONObject();
            student.put("name",s.getName());
            student.put("flag",s.getFlag().getName()+","+s.getFlag().getValue());
            students1.add(student);
        }
        jsonprofile1.put("students",students);
        JSONObject obj1 = new JSONObject();
        obj1.put(profileOld.getName(),jsonprofile1);

        JSONArray profileArray = (JSONArray) cfg.get("profiles");
        profileArray.set(profileArray.indexOf(obj1), obj);
        save();
    }

    private void save(){
        try {
            BufferedWriter writer = new BufferedWriter (new FileWriter(new File("resources/config.json")));
            writer.write(cfg.toJSONString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reset(){
        cfg.put("color","#b399ff");
        cfg.put("color2","#e6994d");
        cfg.put("select","");
        cfg.put("import","");
        cfg.put("export","");
        cfg.put("rows",3);
        cfg.put("colorGradient",true);
        cfg.put("students",new JSONArray());
        cfg.put("profiles",new JSONArray());
        save();
    }

}
