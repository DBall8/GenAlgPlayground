package Objects.Entities;

import AI.Move;
import GameManager.Population;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Organism extends Entity {

    private final static int WIDTH = 30;
    private final static int HEIGHT = 30;
    private final static float SLOWFACTOR = 0.5f;
    private final static float SPEED = 1.0f;

    private Rectangle shape;
    private Move[] moves;
    private int moveCounter;
    private boolean alive = true;

    private int startx, starty;

    public Organism(int x, int y, Move[] moves){
        super(x, y);

        this.startx = x;
        this.starty = y;

        this.moves = moves;

        initialize();
    }

    public Organism(int x, int y, int numMoves){
        super(x, y);
        this.startx = x;
        this.starty = y;

        initialize();

        randomizeMoves(numMoves);

    }

    private void initialize(){
        shape = new Rectangle();

        angle = 0;
        xvel = 0;
        yvel = 0;
        width = WIDTH;
        height = HEIGHT;

        shape = new Rectangle(width, height);
        shape.setFill(Color.BLUE);
        shape.setOpacity(0.5);
        shape.setTranslateZ(-1);
        boundingBox = shape;

        moveCounter = 0;
    }

    private void randomizeMoves(int numMoves){
        moves = new Move[numMoves];
        for(int i=0; i<numMoves; i++){
            moves[i] = new Move();
        }
    }

    @Override
    public void update(){

        if(!alive) return;

        if(xpos == Population.GOALX && ypos == Population.GOALY){
            alive = false;
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
        if(moveCounter >= moves.length){
            alive = false;
            xvel = yvel = 0;
            return;
        }

        Move cMove = moves[moveCounter];

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
        this.alive = true;
    }

    // Create an offspring from a single parent
    public Organism generateOffspring(double mutationFactor){

        // Create a new move list
        Move[] offspringMoves = new Move[moves.length];
        double rand;
        for(int i=0; i<moves.length; i++){
            rand = Math.random();
            if(rand < mutationFactor){
                offspringMoves[i] = new Move();
            }
            else{
                offspringMoves[i] = moves[i].copy();
            }
        }

        Organism offspring = new Organism(startx, starty, offspringMoves);

        return offspring;
    }

    // Generate an offspring from two parents with a potential to extend the number of moves
    public Organism generateOffspring(Organism mom, double mutationFactor){
        // Create a new move list
        Move[] offspringMoves = new Move[moves.length];
        double rand;
        int i;
        for(i=0; i<moves.length; i++){
            rand = Math.random();
            if(rand < mutationFactor){
                offspringMoves[i] = new Move();
            }
            else if(rand < (1.0 - rand) /2){
                offspringMoves[i] = mom.getMove(i).copy();
            }
            else{
                offspringMoves[i] = moves[i].copy();
            }
        }

        Organism offspring = new Organism(startx, starty, offspringMoves);

        return offspring;

    }

    public Move getMove(int i){
        if(i < moves.length) {
            return moves[i];
        } else {
            System.err.println("Cant get this move.");
            return null;
        }
    }
    public int getTurnsTaken(){ return moveCounter; }

}
