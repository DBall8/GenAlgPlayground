package Global;

public class Settings {

    private static final int FRAMERATE = 60;
    public final static boolean BOUNCE = false;
    private static int WIDTH = 800;
    private static int HEIGHT = 800;

    private static class Settings_{
        private static final Settings instance = new Settings();
    }

    private static Settings getInstance(){ return Settings_.instance; }

    public static void setWindowSize(int width, int height){
        getInstance().WIDTH = width;
        getInstance().HEIGHT = height;
    }
    public static int getFramerate(){ return FRAMERATE; }
    public static int getWindowWidth(){ return getInstance().WIDTH; }
    public static int getWindowHeight(){ return getInstance().HEIGHT; }
}
