package cs371m.jh54765.flightatlas;

/**
 * Created by Jacob on 4/3/18.
 */

public class Globals{
    private static Globals instance;

    // Global variable
    public boolean SHOW_ROUTES = true;
    public boolean ONLY_SHOW_COMMERCIAL_AIRPORTS = true;
    public boolean SHOW_MARKERS = true;


    // Restrict the constructor from being instantiated
    private Globals(){}


    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}