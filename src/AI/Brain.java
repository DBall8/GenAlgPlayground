package AI;

public class Brain {
    private Move[] moves;

    public Brain(int numMoves){
        moves = new Move[numMoves];
        for(int i=0; i<numMoves; i++){
            moves[i] = new Move();
        }
    }

    public Brain(Move[] moves){
        this.moves = moves;
    }

    public Move getMove(int i){
        if(i >= moves.length){
            System.err.println("Attempted to retrieve a move that does not exist.");
            return null;
        }
        return moves[i];
    }

    public Brain generateOffspringBrain(int growth, double mutationFactor){
        // Create a new move list
        Move[] offspringMoves = new Move[moves.length + growth];
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
        for(int i=0; i<growth; i++){
            offspringMoves[moves.length + i] = new Move();
        }

        return new Brain(offspringMoves);
    }

    public void grow(int growth){

        if(growth <= 0){
            return;
        }

        // Create a new move list
        Move[] newMoves = new Move[moves.length + growth];
        for(int i=0; i<moves.length; i++){
            newMoves[i] = moves[i];
        }
        for(int i=0; i<growth; i++){
            newMoves[moves.length + i] = new Move();
        }

        moves = newMoves;
    }

    public int getNumMoves(){ return moves.length; }
}
