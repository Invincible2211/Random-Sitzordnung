package de.fynn.randomSitzordnung;

public enum Flag {

    FIXROW,FIXPOSITION,DEFAULT;

    private String value = "";

    public void setValue(String value){
        this.value = value;
    }

    /**
     *
     * @return the Value of the Flag
     */
    public String getValue(){
        return value;
    }

}
