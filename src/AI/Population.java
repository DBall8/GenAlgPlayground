package AI;

import Objects.Entities.Entity;
import Objects.Entities.Organism;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;

public class Population extends Group {

    public final static int STARTX = 50;
    public final static int STARTY = 400;

    public final static int GOALX = 750;
    public final static int GOALY = 400;

    private final static int ORGPERGEN = 100;
    private final static double MUTATIONFACTOR = 0.1;
    private final static int NUMGENERATIONS = 2000;

    private final static int INITIALMOVES = 50; // 50
    private final static int GROWAFTER = 100; // 100
    private final static int MOVEGROWTH = 50; // 50
    private final static int MAXMOVES = 2000;

    private Organism[] organisms;
    private int numOrganisms;
    private boolean active, done;

    private List<Entity> entities;

    private int generation;

    private int currNumMoves = INITIALMOVES;

    private Organism fittest;
    private double averageFitness;
    private Circle goal;

    double prevFittest = 0;

    private boolean paused = false;

    public enum PopShow{
        ALL,
        STRONGEST,
        NONE
    }

    PopShow popShow = PopShow.ALL;

    public Population(List<Entity> entities){
        super();

        this.entities = entities;

        this.numOrganisms = 0;
        organisms = new Organism[ORGPERGEN];

        populate();

        generation = 1;
        System.out.println("Generation: 1");
        active = true;
        done = false;

        goal = new Circle(5);
        goal.translateXProperty().set(GOALX);
        goal.translateYProperty().set(GOALY);
        goal.setFill(Color.RED);
        getChildren().add(goal);
    }

    public void update(){
        if(paused){ return; }

        if(done){
            if(active){
                active = checkIfActive();
            } else{
                for(Organism o: organisms){
                    o.revive();
                }
                active = true;
            }
            return;
        }

        if(active){
            active = checkIfActive();
        } else{
            if(generation > 1){
                fittest.removeHighlight();
            }

            generation++;

            //System.out.println("Generation: " + generation);

            if(GROWAFTER != 0 && (generation%GROWAFTER) == 0 && currNumMoves < MAXMOVES){
                rePopulate(MOVEGROWTH);
                fittest.growBrain(MOVEGROWTH);
                currNumMoves += MOVEGROWTH;
                //System.out.println("Increasing moves by " + MOVEGROWTH);
            } else {
                rePopulate(0);
            }

            System.out.println("GEN: " + generation + " FITTEST: " + fittest.getFitness());
            fittest.highlight(Color.CYAN);
            calcAverageFitness();
            //System.out.println("Average fitness: " + averageFitness);
            active = true;
        }

        if(generation > NUMGENERATIONS){
            done = true;
        }

    }

    private boolean checkIfActive(){
        for(Organism o: organisms){
            if(o.isAlive()){
                return true;
            }
        }

        return false;
    }

    private void calcFitness(){
        for(Organism o: organisms){
            o.calcFitness();
        }
    }

    private void calcAverageFitness(){
        float fittnessSum = 0;
        for(Organism o: organisms){
            fittnessSum += o.getFitness();
        }

        averageFitness = fittnessSum/numOrganisms;
    }


    private boolean addOrganism(Organism o){
        if(numOrganisms >= ORGPERGEN){
            //System.err.println("Failed to add organism to population.");
            return false;
        }
        organisms[numOrganisms] = o;
        numOrganisms++;
        if(popShow == PopShow.ALL) getChildren().add(o.getVisuals());
        entities.add(o);
        return true;
    }

    private Organism getFittestOrganism(){
        Organism fittest = organisms[0];
        double fittestScore = 0;

        Organism currOrg;
        double currFitness;
        // compare each successive organism
        for(int i=1; i<ORGPERGEN; i++){
            currOrg = organisms[i];
            currFitness = currOrg.getFitness();
            if(currFitness > fittestScore){
                fittest = currOrg;
                fittestScore = currFitness;
            }
        }

        if(this.fittest != null && this.fittest.getFitness() > fittestScore){
            return this.fittest;
        }

        return fittest;
    }

    private Organism naturalSelection(){
        float fittnessSum = 0;
        for(Organism o: organisms){
            fittnessSum += o.getFitness();
        }

        double rand = Math.random()*fittnessSum;

        float currNumber = 0;
        int i = 0;

        while(currNumber < fittnessSum){

            currNumber += organisms[i].getFitness();

            if(rand < currNumber){
                return organisms[i];
            }

            i++;
        }

        System.err.println("No organism naturally selected.");
        return null;
    }

    private void clearPopulation(){

        getChildren().clear();
        getChildren().add(goal);

        for(Organism o: organisms){
            entities.remove(o);
        }

        organisms = new Organism[ORGPERGEN];
        numOrganisms = 0;

    }

    private void populate(){
        Organism o;
        boolean full = false;
        while(!full){
            o = new Organism(STARTX, STARTY, currNumMoves);
            full = !addOrganism(o);
        }
    }

    private void rePopulate(int moveGrowth){
        Organism[] nextGen = new Organism[ORGPERGEN];
        boolean full = false;
        int i;

        calcFitness();

        if(generation > 2) {
            prevFittest = fittest.getFitness();
        }
        fittest = getFittestOrganism();
        fittest.revive();

        if(fittest.getFitness() < prevFittest){
            System.err.println("FITNESS GOT WORSE");
        }

        for(i=0; i<ORGPERGEN - 1; i++){
            nextGen[i] = naturalSelection().generateOffspring(moveGrowth, MUTATIONFACTOR);
        }

        clearPopulation();

        i=0;
        while(!full && i < ORGPERGEN - 1){
            full = !addOrganism(nextGen[i]);
            i++;
        }

        if(numOrganisms  < ORGPERGEN - 1){
            System.out.println("Unable to add fittest organism.");
        }

        addOrganism(fittest);
        if(popShow == PopShow.STRONGEST) getChildren().add(fittest.getVisuals());

    }

    public void show(PopShow show){
        if(show == popShow){
            return;
        }
        popShow = show;

        if(done){
            getChildren().clear();
            getChildren().add(goal);
            if(show == PopShow.ALL){
                for(Organism o: organisms){
                    getChildren().add(o.getVisuals());
                }
            }
            else if(show == PopShow.STRONGEST){
                getChildren().add(fittest.getVisuals());
            }
        }
    }

    public void togglePaused(){ paused = !paused; }
    public int getGeneration(){ return generation; }
    public double getAverageFitness(){ return averageFitness; }

    public boolean isFinished() {
        return done;
    }
}
