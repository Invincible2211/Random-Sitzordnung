package de.fynn.randomSitzordnung;

import de.fynn.randomSitzordnung.flags.FixPosFlag;
import de.fynn.randomSitzordnung.flags.FixRowFlag;

import java.util.ArrayList;
import java.util.List;

public class SeatingArrangementManager {

    private List<Student> students;
    private int rows;
    private List<Student> defaultStudents = new ArrayList<>();
    private List<Student> fixPlace = new ArrayList<>();
    private List<Student> fixRow = new ArrayList<>();

    public SeatingArrangementManager(List<Student> students, int rows){
        this.students = students;
        this.rows = rows;
        random();
    }

    public List<Student> random(){
        for (Student s:
                students) {
            switch (s.getFlag().getName()){
                case "fixRow":
                    fixRow.add(s);
                    break;
                case "fixPos":
                    fixPlace.add(s);
                    break;
                case "default":
                    defaultStudents.add(s);
                    break;
            }
        }
        List<Student> studentList = new ArrayList<>();
        int[] studentsPerRow = studentsPerRow(students.size(),rows);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < studentsPerRow[i]; j++) {
                if (fixPosStudentsContainsPosition(i,j)){
                    for (int k = 0; k < fixPlace.size(); k++) {
                        if(((FixPosFlag)fixPlace.get(i).getFlag()).getX()==i&&((FixPosFlag)fixPlace.get(i).getFlag()).getY()==j){
                            studentList.add(fixPlace.remove(i));
                            break;
                        }
                    }
                }else if(fixRowStudentsContainsPosition(i)){
                        int space = studentsPerRow[i]-(j);
                        int random = (int)(1+(Math.random()*space));
                        if (random<=fixStudentsInRow(i)){
                            List<Student> studentsInRow = getStudentsInRow(i);
                            Student s = studentsInRow.remove((int)(Math.random()*studentsInRow.size()));
                            studentList.add(fixRow.remove(fixRow.indexOf(s)));
                        }else {
                            studentList.add(defaultStudents.remove((int)(Math.random()*defaultStudents.size())));
                        }
                }else {
                    studentList.add(defaultStudents.remove((int)(Math.random()*defaultStudents.size())));
                }
            }
        }
        students = studentList;
        return students;
    }

    public List<Student> getSeatingArrangement(){
        return students;
    }

    private int[] studentsPerRow(int studentAmount, int rows){
        int[] studentsPerRow = new int[rows];
        for (int i = 0; i < studentAmount; i++) {
            studentsPerRow[i%rows]++;
        }
        return studentsPerRow;
    }

    private boolean fixPosStudentsContainsPosition(int x, int y){
        for (Student s:
             fixPlace) {
            if(((FixPosFlag)s.getFlag()).getX()==x&&((FixPosFlag)s.getFlag()).getY()==y){
                return true;
            }
        }
        return false;
    }

    private boolean fixRowStudentsContainsPosition(int row){
        for (Student s:
             fixRow) {
            if(((FixRowFlag)s.getFlag()).getRow()==row){
                return true;
            }
        }
        return false;
    }

    private int fixStudentsInRow(int row){
        List<Student> studentsInRow = new ArrayList<>();
        for (Student s:
             fixRow) {
            if(((FixRowFlag)s.getFlag()).getRow()==row){
                studentsInRow.add(s);
            }
        }
        return studentsInRow.size();
    }

    public List<Student> getStudentsInRow(int row){
        List<Student> studentsInRow = new ArrayList<>();
        for (Student s:
                fixRow) {
            if(((FixRowFlag)s.getFlag()).getRow()==row){
                studentsInRow.add(s);
            }
        }
        return studentsInRow;
    }

}
