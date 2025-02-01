import java.lang.Math;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;


class Snake {
    int head;
    int tail;
    public Snake(int head, int tail) {
        this.head = head;
        this.tail = tail;
    }
}


class Ladder {
    int start;
    int end;
    public Ladder(int start, int end) {
        this.start = start; 
        this.end =end;
    }
}

class Dice {
    private static Dice dice;
    private int min;
    private int max;

    private Dice() {
        this.min = 1;
        this.max = 6;
    }

    public static Dice getInstance() {
        if(dice == null) {
            dice = new Dice();
        }
        return dice;
    }

    public int roll() {
        double randomDouble = Math.floor(Math.random()*(this.max-this.min+1));
        return ((int)randomDouble)+1;
    }    
}

class Player {
    private int id;
    private int position;
 
    public Player(int id, int position) {
        this.id = id;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}

class Board {
    private List<Snake> snakes;
    private List<Ladder> ladders;
    private int dimension;


    public Board(int dimension) {
        System.out.println("Creating board with dimension: " + dimension);
        this.ladders = new ArrayList<>();
        this.snakes = new ArrayList<>();
        this.dimension = dimension;
        generateLadders();
        generateSnakes();
        System.out.println("Created board with dimension: " + dimension);
    }

    private void generateLadders() {
        for(int i=0;i<dimension;i++) {
            ladders.add(generateLadder());
        }
    }

    private Ladder generateLadder() {
        while(true) {
            int start = getRandomNumber(2,dimension*dimension-2);
            int end = getRandomNumber(start+1, dimension*dimension-1);
            if(validateLadder(start, end)) {
                System.out.printf("Ladder Created, start: %d, end: %d %n", start, end);
                return new Ladder(start, end);
            }
        }
    }

    private boolean validateLadder(int start, int end) {
        if(start<=1 || end>=(dimension*dimension) || (start>=end)) {
            return false;
        }

        return validateAmongladder(start, end);

    }

    private boolean validateAmongladder(int start, int end) {
        for(int i=0;i<ladders.size();i++) {
            Ladder ladder = ladders.get(i);
            if(ladder.start==start || ladder.start==end || ladder.end ==start || ladder.end == end) {
                return false;
            }
        }
        return true;
    }

    private void generateSnakes() {
        for(int i=0;i<dimension;i++) {
            snakes.add(generateSnake());
        }
    }

    private Snake generateSnake() {
        while(true) {
            int tail = getRandomNumber(1,dimension*dimension-2);
            int head = getRandomNumber(tail+1, dimension*dimension-1);
            if(validateSnake(head, tail)) {
                System.out.printf("Snake Created, head: %d, tail: %d %n", head, tail);
                return new Snake(head, tail);
            }
        }
    }

    private boolean validateSnake(int head, int tail) {
        if(tail<1 || head>=(dimension*dimension) || (tail>=head)) {
            return false;
        }

        return validateAmongladder(head, tail) || validateAmongSnake(head, tail);

    }

    private boolean validateAmongSnake(int head, int tail) {
        for(int i=0;i<snakes.size();i++) {
            Snake snake = snakes.get(i);
            if(snake.head==head || snake.head==tail || snake.tail ==head || snake.tail == tail) {
                return false;
            }
        }
        return true;
    }
    

    private int getRandomNumber(int start, int end) {
        double randomNumber = Math.floor(Math.random()*(end-start+1));
        return start+((int)randomNumber);
    }

    public void move(Player player, int start, int diceOutcome) {
        System.out.printf("Player : %d , Initial Position %d, diceOutcome %d.   -------    ", player.getId(), start, diceOutcome);
        int nextPosition = start+diceOutcome;
        int totalPosition = dimension*dimension;
        if(nextPosition > totalPosition) {
            System.out.printf("Invalid Move for player.%n");
            return ;
        }
        if(nextPosition == totalPosition) {
            player.setPosition(nextPosition);
            return ;
        }

        for(int i=0;i<snakes.size();i++) {
            if(nextPosition == snakes.get(i).head) {
                nextPosition = snakes.get(i).tail;
                player.setPosition(nextPosition);
                return ;
            }
        }

        for(int i=0;i<ladders.size();i++) {
            if(nextPosition == ladders.get(i).start) {
                nextPosition = ladders.get(i).end;
                player.setPosition(nextPosition);
                return ;
            }
        }
        player.setPosition(nextPosition);
    }

    public boolean checkWin(Player player) {
        return player.getPosition() == (dimension*dimension);
    }
}


class Game {
    private Board board;
    private Deque<Player> players;
    private List<Player> winners;
    private Dice dice;
    private int dimension;

    public Game(int dimension, int playerNum) {
        this.board = new Board(dimension);
        this.players = new ArrayDeque<>();
        this.winners = new ArrayList<>();
        generatePlayers(playerNum);
        this.dice = Dice.getInstance();
        this.dimension = dimension;
    }

    private void generatePlayers(int playerNum) {
        for(int i=0;i<playerNum;i++) {
            this.players.add(new Player(i+1, 0));
        }
    }

    public void startGame() {
        while(!players.isEmpty()) {
            Player player = players.pollFirst();
            int diceOutcome = dice.roll();
            board.move(player, player.getPosition(), diceOutcome);
            System.out.printf("PlayerId: %d moved to %d.%n", player.getId(), player.getPosition());
            if(board.checkWin(player)) {
                winners.add(player);
            } else {
                players.offerLast(player);
            }

            // showBoard();
        }
    }

    private void showBoard() {
        for(int i=1;i<=dimension;i++) {
            for(int j=1;j<=dimension;j++) {
                int boardPosition = (i-1)*dimension+j;
                System.out.printf("%d: ", boardPosition);
                int k=players.size();
                while(k>0) {
                    k--;
                    Player player=players.pop();
                    if(player.getPosition() == boardPosition) {
                        System.out.printf("%d ", player.getPosition());
                    }
                    players.add(player);
                }
            }
            System.out.println(" ");
        }
    }

    public void printWinner() {
        for(int i=0;i<winners.size();i++) {
            System.out.printf("PlayerId: %d, Rank: %d.%n", winners.get(i).getId(), i+1);
        }
    }
    

}

public class SnakeAndLadderGame {
    public static void main(String args[]) {
        System.out.println("Starting Game...");
        Game game = new Game(10, 10);
        game.startGame();
        game.printWinner();
        System.out.println("Game End...");

    }
}