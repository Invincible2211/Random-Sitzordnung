package de.fynn.randomSitzordnung.flags;

public class FixRowFlag extends Flag{

    private int row;

    public FixRowFlag(int row){
        this.row = row;
    }

    public int getRow() {
        return row;
    }

    @Override
    public String getName() {
        return "fixRow";
    }

    @Override
    public String getValue() {
        return ""+row;
    }

}
