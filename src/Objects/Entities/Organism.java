package Objects.Entities;

import AI.Brain;
import AI.Move;
import AI.Population;
import Physics.Physics;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Organism extends Entity {

    private final static int WIDTH = 30;
    private final static int HEIGHT = 30;
    private final static float SLOWFACTOR = 0.5f;
    private final static float SPEED = 1.0f;

    private Rectangle shape;
    private Brain brain;
    private int moveCounter;
    private boolean alive = true;
    private boolean goalReached = false;

    private double fitnessScore;

    private int startx, starty;

    public Organism(int x, int y, Brain brain){
        super(x, y);

        this.startx = x;
        this.starty = y;

        this.brain = brain;

        initialize();
    }

    public Organism(int x, int y, int numMoves){
        super(x, y);
        this.startx = x;
        this.starty = y;

        initialize();

        this.brain = new Brain(numMoves);

    }

    private void initialize(){
        shape = new Rectangle();

        angle = 0;
        xvel = 0;
        yvel = 0;
        width = WIDTH;
        height = HEIGHT;

        fitnessScore = 0;

        shape = new Rectangle(width, height);
        shape.setFill(Color.BLUE);
        shape.setOpacity(0.5);
        shape.setTranslateZ(-1);
        boundingBox = shape;

        moveCounter = 0;
    }

    @Override
    public void update(){

        if(!alive) return;

        if(Physics.getDistance(xpos, ypos, Population.GOALX, Population.GOALY) < width){
            goalReached = true;
            shape.setFill(Color.ORANGE);
            xvel = yvel = 0;
            alive = false;
            return;
        }

        // Slow
        float mag = Math.abs(xvel);
        if(mag > 0.5){
            xvel -= mag/xvel * SLOWFACTOR;
        } else {
            xvel = 0;
        }

        mag = Math.abs(yvel);
        if(mag > 0.5){
            yvel -= mag/yvel * SLOWFACTOR;
        } else {
            yvel = 0;
        }

        // Move
        if(moveCounter >= brain.getNumMoves()){
            alive = false;
            xvel = yvel = 0;
            return;
        }

        Move cMove = brain.getMove(moveCounter);

        xvel += cMove.getX() * SPEED;
        yvel += cMove.getY() * SPEED;

        moveCounter++;
    }

    @Override
    public void draw() {
        shape.setTranslateX(xpos - getXRadius());
        shape.setTranslateY(ypos - getYRadius());
    }

    @Override
    public Node getVisuals() {
        return shape;
    }

    public boolean isAlive() {
        return alive;
    }

    public void highlight(Color c){
        shape.setFill(c);
        shape.setOpacity(1.0);
        shape.setTranslateZ(2);
    }

    public void removeHighlight(){
        shape.setFill(Color.BLUE);
        shape.setOpacity(0.5);
        shape.setTranslateZ(-1);
    }

    public void revive(){
        xpos = startx;
        ypos = starty;
        moveCounter = 0;
        goalReached = false;
        this.alive = true;
    }



    // Create an offspring from a single parent
    public Organism generateOffspring(int brainGrowth, double mutationFactor){

        Brain offspringBrain = brain.generateOffspringBrain(brainGrowth, mutationFactor);

        Organism offspring = new Organism(startx, starty, offspringBrain);

        return offspring;
    }

    public void growBrain(int brainGrowth){
       brain.grow(brainGrowth);
    }

    public void calcFitness(){

        if(goalReached){
            fitnessScore = 2000.0 / (moveCounter * moveCounter);
        }
        else {
            float dist = Physics.getDistance(xpos, ypos, Population.GOALX, Population.GOALY);
            fitnessScore = 1.0 / (dist * dist);
        }
    }

    public double getFitness(){ return fitnessScore; }

}
