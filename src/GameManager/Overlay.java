package GameManager;

import AI.Population;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Overlay extends Group {

    Population pop;
    private Text generationText;
    private Text averageFitnessText;
    private Text completedText;

    public Overlay(Population pop){
        super();

        this.pop = pop;

        generationText = new Text("Generation: 1");
        generationText.setFont(new Font(20));
        generationText.setTranslateX(20);
        generationText.setTranslateY(700);

        averageFitnessText = new Text("Average Fitness: 0");
        averageFitnessText.setTranslateX(20);
        averageFitnessText.setTranslateY(740);
        averageFitnessText.setFont(new Font(20));

        completedText = new Text("Training Completed");
        completedText.setFont(new Font(40));
        completedText.setFill(Color.BLUE);
        completedText.setTranslateX(230);
        completedText.setTranslateY(410);
        completedText.setOpacity(0.0);


        getChildren().addAll(generationText, averageFitnessText, completedText);
    }

    public void update(){
        generationText.setText("Generation: " + pop.getGeneration());
        averageFitnessText.setText("Average Fitness: " + (pop.getAverageFitness()* 10000));
        if(pop.isFinished()){
            completedText.setOpacity(1.0);
        }
    }
}
