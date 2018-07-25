package GameManager;

import Objects.Entities.Entity;
import Objects.Entities.Organism;
import Physics.Physics;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;

public class Population extends Group {

    public final static int GOALX = 750;
    public final static int GOALY = 750;

    private final static int ORGPERGEN = 100;
    private final static double MUTATIONFACTOR = 0.3;
    private final static int NUMGENERATIONS = 100;

    private final static int INITIALMOVES = 50;
    private final static int GROWAFTER = 0;
    private final static int MOVEGROWTH = 60;
    private final static int MAXMOVES = 600;

    private Organism[] organisms;
    private int numOrganisms;
    private boolean active, done;

    private List<Entity> entities;

    private int generation;

    private Organism fittest;
    private Organism secondFittest;

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

        Circle goal = new Circle(5);
        goal.translateXProperty().set(GOALX);
        goal.translateYProperty().set(GOALY);
        goal.setFill(Color.RED);
        getChildren().add(goal);
    }

    public void update(){
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
                secondFittest.removeHighlight();
            }
            Organism[] winners = getFittestOrganisms();
            fittest = winners[0];
            secondFittest = winners[1];

            fittest.highlight(Color.CYAN);
            secondFittest.highlight(Color.PURPLE);

            clearPopulation();
            generation++;

            System.out.println("Generation: " + generation);
            if(GROWAFTER != 0 && (generation%GROWAFTER) == 0){
                populate(fittest, secondFittest);
                System.out.println("Increasing moves by " + MOVEGROWTH);
            } else {
                populate(fittest, secondFittest);
            }
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

    private Organism getFittestOrganism(){
        Organism fittest = organisms[0];
        float fittestScore = Float.MAX_VALUE;

        Organism currOrg;
        float currFitness;
        // compare each successive organism
        for(int i=1; i<ORGPERGEN; i++){
            currOrg = organisms[i];
            currFitness = calcFitness(currOrg);
            if(currFitness < fittestScore){
                fittest = currOrg;
                fittestScore = currFitness;
            }
        }

        return fittest;
    }

    private Organism[] getFittestOrganisms(){
        Organism fittest;
        Organism secondFittest;
        float fittestScore, secondFittestScore;

        // Figure out which of the first two is the strongest
        if(calcFitness(organisms[0]) < calcFitness(organisms[1])){
            fittest = organisms[0];
            fittestScore = calcFitness(organisms[0]);
            secondFittest = organisms[1];
            secondFittestScore = calcFitness(organisms[1]);
        } else {
            fittest = organisms[1];
            fittestScore = calcFitness(organisms[1]);
            secondFittest = organisms[0];
            secondFittestScore = calcFitness(organisms[0]);
        }

        Organism currOrg;
        float currFitness;
        // compare each successive organism
        for(int i=2; i<ORGPERGEN; i++){
            currOrg = organisms[i];
            currFitness = calcFitness(currOrg);
            if(currFitness < fittestScore){
                secondFittest = fittest;
                secondFittestScore = fittestScore;

                fittest = currOrg;
                fittestScore = currFitness;
            }
            else if(currFitness < secondFittestScore){
                secondFittest = currOrg;
                secondFittestScore = currFitness;
            }
        }

        return new Organism[]{fittest, secondFittest};
    }

    private float calcFitness(Organism o){
        float dist = Physics.getDistance(o.getX(), o.getY(), GOALX, GOALY);
        int turnsTaken = o.getTurnsTaken();
        return dist + turnsTaken;
    }

    private boolean addOrganism(Organism o){
        if(numOrganisms >= ORGPERGEN){
            //System.err.println("Failed to add organism to population.");
            return false;
        }
        organisms[numOrganisms] = o;
        numOrganisms++;
        getChildren().add(o.getVisuals());
        entities.add(o);
        return true;
    }

    private void clearPopulation(){

        for(Organism o: organisms){
            getChildren().remove(o.getVisuals());
            entities.remove(o);
        }

        organisms = new Organism[ORGPERGEN];
        numOrganisms = 0;

    }

    private void populate(){
        Organism o;
        boolean full = false;
        while(!full){
            o = new Organism(50, 50, INITIALMOVES);
            full = !addOrganism(o);
        }
    }

    private void populate(Organism fittest){
        Organism o;
        boolean full = false;

        fittest.revive();
        while(!full && numOrganisms < ORGPERGEN - 1){
            o = fittest.generateOffspring(MUTATIONFACTOR);
            full = !addOrganism(o);
        }

        addOrganism(fittest);

    }

    private void populate(Organism fittest, Organism secondFittest){
        Organism o;
        boolean full = false;

        fittest.revive();
        secondFittest.revive();
        while(!full && numOrganisms < ORGPERGEN - 2){
            o = fittest.generateOffspring(secondFittest, MUTATIONFACTOR);
            full = !addOrganism(o);
        }
        addOrganism(secondFittest);
        addOrganism(fittest);

    }


}
