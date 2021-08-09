package de.fynn.randomSitzordnung;

import de.fynn.randomSitzordnung.flags.DefaultFlag;
import de.fynn.randomSitzordnung.flags.Flag;

public class Student {

    private String name;
    private Flag flag;

    public Student(String name){
        this.name = name;
        flag = new DefaultFlag();
    }

    public Student(String name, Flag flag){
        this.name = name;
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public Flag getFlag() {
        return flag;
    }
}
