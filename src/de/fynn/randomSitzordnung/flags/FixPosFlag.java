package de.fynn.randomSitzordnung.flags;

public class FixPosFlag extends Flag{

    private int x;
    private int y;

    public FixPosFlag(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String getName() {
        return "fixPos";
    }

    @Override
    public String getValue() {
        return x+","+y;
    }

}
