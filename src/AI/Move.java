package AI;

public class Move {
    private int x;
    private int y;

    public Move(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Move(){
        double rand = Math.random()*3;
        if(rand < 1){
            x = -1;
        }
        else if(rand < 2){
            x = 0;
        }
        else{
            x = 1;
        }
        rand = Math.random()*3;
        if(rand < 1){
            y = -1;
        }
        else if(rand < 2){
            y = 0;
        }
        else{
            y = 1;
        }
    }

    public Move copy(){
        return new Move(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
