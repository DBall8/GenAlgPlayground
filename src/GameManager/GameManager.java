package GameManager;

import AI.Population;
import Global.Settings;
import Objects.Entities.Entity;
import Objects.Entities.Player;
import Objects.ICollidable;
import Objects.Obstacle;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.input.KeyCode.SPACE;

public class GameManager extends Pane {

    private final static double TRAINSPEED = 0.1;

    private Scene scene;
    private List<Entity> entities = new ArrayList<>();
    private List<ICollidable> obstacles = new ArrayList<>();
    private int width, height;
    GameTime time;

    Overlay overlay;
    Population pop;

    public GameManager(){
        super();
        this.width = Settings.getWindowWidth();
        this.height = Settings.getWindowHeight();

        time = new GameTime(this, TRAINSPEED);
    }

    public void start(Scene scene){
        this.scene = scene;
        Player p = new Player(50, 50);
        p.setInputListener(new UserInputListener(scene));
        addEntity(p);
        //obstacles.add(p);

        pop = new Population(entities);
        getChildren().add(pop);

        drawMaze();

        overlay = new Overlay(pop);
        getChildren().add(overlay);

        scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch(event.getCode()){
                    case SPACE:
                        time.togglePlay();
                        break;
                    case B:
                        pop.togglePaused();
                        break;
                    case COMMA:
                        pop.show(Population.PopShow.ALL);
                        break;
                    case PERIOD:
                        pop.show(Population.PopShow.STRONGEST);
                        break;
                    case SLASH:
                        pop.show(Population.PopShow.NONE);
                        break;
                }
            }
        });

        time.play();
    }

    public void draw(){
        for(Entity e: entities){
            e.draw();
        }
    }

    private void update(){
        for(Entity e: entities){
            e.update();
        }

        pop.update();
        if(overlay != null){
            overlay.update();
        }
    }

    private float checkCollisions(float timeLeft){
        float firstCollisionTime = timeLeft; // looks for first collision
        float tempTime;
        // reset collisions for each player
        for(Entity e: entities){
            e.reset();
            if((tempTime = e.checkCollisions(timeLeft, obstacles)) < firstCollisionTime){
                firstCollisionTime = tempTime;
            }
        }
        return firstCollisionTime;
    }

    private void move(float time){
        for(Entity e: entities){
            e.move(time);
        }
    }


    public void calculateFrame(){
        float timeLeft = 1.00f;
        float firstCollisionTime;
        update();

        do {
            // Check if each particle hits the box boundaries (must be done first as it resets collision)
            firstCollisionTime = checkCollisions(timeLeft);

            move(firstCollisionTime);

            timeLeft -= firstCollisionTime;

            if(pop.isFinished()){
                time.pause();
                time = new GameTime(this);
                time.play();
            }

        }while(timeLeft > 0.01f);
    }

    private void addEntity(Entity e){
        entities.add(e);
        this.getChildren().add(e.getVisuals());
    }

    private void addObstacle(int xpos, int ypos, int width, int height){
        Obstacle o = new Obstacle(xpos, ypos, width, height);
        getChildren().add(o);
        obstacles.add(o);
    }



    private void drawCross(){
        addObstacle(375, 200, 50, 400);
        addObstacle(200, 375, 400, 50);
    }

    private void drawMaze(){
        addObstacle(200, 200, 50, 400);
        addObstacle(400, 0, 50, 300);
        addObstacle(400, 500, 50, 300);
        addObstacle(600, 200, 50, 400);
    }

}
