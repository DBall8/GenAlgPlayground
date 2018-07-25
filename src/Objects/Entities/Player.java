package Objects.Entities;

import GameManager.UserInputListener;
import Objects.ICollidable;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Player extends Entity implements ICollidable {

    private Rectangle shape;
    private UserInputListener input;

    public Player(int x, int y){
        super(x, y);
        angle = 0;
        xvel = 0;
        yvel = 0;
        width = height = 30;

        shape = new Rectangle(width, height);
        shape.setFill(Color.GREEN);
        boundingBox = shape;
    }

    public void setInputListener(UserInputListener input){
        this.input = input;
    }

    @Override
    public void update(){
        if(input.isDown() && !input.isUp()){
            yvel += 0.5;
        }
        else if(!input.isDown() && input.isUp()){
            yvel -= 0.5;
        }
        else{
            if(Math.abs(yvel) >= 0.1f) {
                yvel -= Math.abs(yvel) / yvel * 0.1f;
            }
            else {
                yvel = 0;
            }
        }

        if(input.isRight() && !input.isLeft()){
            xvel += 0.5;
        }
        else if(!input.isRight() && input.isLeft()){
            xvel -= 0.5;
        }
        else{
            if(Math.abs(xvel) >= 0.1f) {
                xvel -= Math.abs(xvel) / xvel * 0.1f;
            }
            else{
                xvel = 0;
            }
        }

        //System.out.format("X: %f Y: %f\n", xpos, ypos);

    }

    @Override
    public Node getVisuals(){ return shape; }

    @Override
    public void draw(){
        shape.setTranslateX(xpos - getXRadius());
        shape.setTranslateY(ypos - getYRadius());
        shape.setRotate(angle);
    }

    @Override
    public float rightX() {
        return xpos + getXRadius();
    }

    @Override
    public float leftX() {
        return xpos - getXRadius();
    }

    @Override
    public float bottomY() {
        return ypos + getYRadius();
    }

    @Override
    public float topY() {
        return ypos - getYRadius();
    }
}
