package de.fynn.randomSitzordnung.flags;

public class FlagFactory {

    public static Flag createFlag(String... values){
        switch (values[0]){
            case "default":
                return new DefaultFlag();
            case "fixPos":
                return new FixPosFlag(Integer.parseInt(values[1]),Integer.parseInt(values[2]));
            case "fixRow":
                return new FixRowFlag(Integer.parseInt(values[1]));
            default:
                return null;
        }
    }

}
