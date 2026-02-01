import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DiamondCastle extends Application {

    private static final int WIDTH = 650;
    private static final int HEIGHT = 600;
    private static final int PLATFORM_WIDTH = 100;
    private static final int PLATFORM_HEIGHT = 10;
    private static final int PLAYER_SIZE = 50;
    private static final int DIAMOND_SIZE = 25;
    private static final double PLAYER_SPEED = 3.5;
    private static final double JUMP_HEIGHT = -15;
    private static final int GAME_DURATION_SECONDS = 25;

    private Pane root;
    private ImageView player;
    private List<ImageView> platforms;
    private List<ImageView> diamonds;
    private double playerVelocityY;
    private boolean jumping = false;
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private boolean gameOver = false;
    private int diamondCount = 0;
    private Timeline gameTimer;
    private Label time;
    private boolean doubleJump = false; 

    public void start(Stage primaryStage) {
        root = new Pane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        addPlayerAndBackground();
        platforms = new ArrayList<>();
        diamonds = new ArrayList<>();
        generatePlatforms();
        addDiamondInput(); 
        displayTime();
        setupGameKeys(scene);
        setupGameLoop();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Diamond Castle Game");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    
    

    private void addPlayerAndBackground() {
        Image backgroundImage = new Image("BackGround.png");
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setFitWidth(1600);
        backgroundView.setFitHeight(2250);
        root.getChildren().add(backgroundView);

        Image playerImage = new Image("player.png");
        player = new ImageView(playerImage);
        player.setFitWidth(PLAYER_SIZE);
        player.setFitHeight(PLAYER_SIZE);
        player.setLayoutX(300);
        player.setLayoutY(700);
        root.getChildren().add(player);
    }



    private void addDiamondInput() {
        Label diamondQ = new Label("Enter the number of diamonds");
        diamondQ.setTextFill(Color.CYAN);
        diamondQ.setFont(Font.font("Arial", 12));
        diamondQ.setLayoutX(10);
        diamondQ.setLayoutY(HEIGHT - 50);

        TextField diamondInput = new TextField();
        diamondInput.setLayoutX(10);
        diamondInput.setLayoutY(HEIGHT - 30);
        
        root.getChildren().addAll(diamondQ, diamondInput);

        diamondInput.setOnAction(e -> {
            int numOfDiamonds = Integer.parseInt(diamondInput.getText());
            generateDiamonds(numOfDiamonds);
            root.getChildren().removeAll(diamondQ, diamondInput);
            setupGameTimer(); 
        });

    }




   private void generatePlatforms() {
        for (int i = 0; i < 5; i++) {
            Image platformImage = new Image("platform.png");
            ImageView platformImageView = new ImageView(platformImage);
            platformImageView.setFitWidth(PLATFORM_WIDTH);
            platformImageView.setFitHeight(PLATFORM_HEIGHT);
            platformImageView.setLayoutX(new Random().nextInt(WIDTH - PLATFORM_WIDTH));
            platformImageView.setLayoutY(new Random().nextInt(HEIGHT));
            platforms.add(platformImageView);
            root.getChildren().add(platformImageView);
        }
    }




    private void generateDiamonds(int numOfDiamonds) {
        for (int i = 0; i < numOfDiamonds; i++) {
            Image diamondImage = new Image("diamond.png");
            ImageView diamondImageView = new ImageView(diamondImage);
            diamondImageView.setFitWidth(DIAMOND_SIZE);
            diamondImageView.setFitHeight(DIAMOND_SIZE);
            diamondImageView.setLayoutX(new Random().nextInt(WIDTH - DIAMOND_SIZE));
            diamondImageView.setLayoutY(new Random().nextInt(HEIGHT));
            diamonds.add(diamondImageView);
            root.getChildren().add(diamondImageView);
        }
    }
    

    private void setupGameKeys(Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                movingLeft = true;
                player.setScaleX(1);
            } else if (event.getCode() == KeyCode.RIGHT) {
                movingRight = true;
                player.setScaleX(-1);
            } else if (event.getCode() == KeyCode.SPACE) {
                jump();
            }
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                movingLeft = false;
            } else if (event.getCode() == KeyCode.RIGHT) {
                movingRight = false;
            }
        });
    }
    
    
    

    private void movePlatforms() {
        for (ImageView platform : platforms) {
            platform.setLayoutY(platform.getLayoutY() - 1);
            if (platform.getLayoutY() <= 0) {
                platform.setLayoutY(HEIGHT);
                platform.setLayoutX(new Random().nextInt(WIDTH - PLATFORM_WIDTH));
            }
        }
    }





    private void movePlayer() {
        if (!gameOver) {
            if (movingLeft && player.getLayoutX() > 0) {
                player.setLayoutX(player.getLayoutX() - PLAYER_SPEED);
            }
            if (movingRight && player.getLayoutX() < WIDTH - PLAYER_SIZE) {
                player.setLayoutX(player.getLayoutX() + PLAYER_SPEED);
            }
            player.setLayoutY(player.getLayoutY() + playerVelocityY);
            playerVelocityY += 0.8;

            if (player.getLayoutY() >= HEIGHT - PLAYER_SIZE) {
                player.setLayoutY(HEIGHT - PLAYER_SIZE);
                playerVelocityY = 0;
                jumping = false;
                doubleJump = true;            }
        }
    }

    
    

private boolean checkOnPlatform() {
    double playerY = player.getLayoutY() + PLAYER_SIZE;
    double playerX = player.getLayoutX(); 

    for (ImageView platform : platforms) {
        double platformTop = platform.getLayoutY();
        double platformBottom = platformTop + PLATFORM_HEIGHT; 
        double platformLeft = platform.getLayoutX(); 
        double platformRight = platformLeft + PLATFORM_WIDTH; 

        if (playerY >= platformTop && playerY <= platformBottom && playerX + PLAYER_SIZE >= platformLeft && playerX <= platformRight) {
            return true;
        }
    }

    return false;
}


    private void jump() {
        if (!jumping) {
            playerVelocityY = JUMP_HEIGHT;
            jumping = true;
        } else if (doubleJump && (checkOnPlatform() || (movingLeft || movingRight))) { 
                    playerVelocityY = JUMP_HEIGHT;
            doubleJump = false; 
        }
    }





private void checkCollisions() {

    for (ImageView platform : platforms) {
        if (player.getBoundsInParent().intersects(platform.getBoundsInParent()) && playerVelocityY > 0) {
            playerVelocityY = -2;
            jumping = false;
            doubleJump = true;
        }
    }

    for (int i = 0; i < diamonds.size(); i++) {
        ImageView diamond = diamonds.get(i);
        if (player.getBoundsInParent().intersects(diamond.getBoundsInParent())) {
            root.getChildren().remove(diamond);
            diamonds.remove(i);
            diamondCount++;
            System.out.println("Diamonds collected: " + diamondCount);
            if (diamonds.isEmpty()) {
                gameTimer.pause();
                endGame();
                System.out.println("Congratulations! You collected all diamonds!");
            }
        }
    }
}
    
    
    
    
        private void setupGameLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateTimer();
                movePlayer();
                movePlatforms();
                checkCollisions();
            }
        }.start();
    }



    private void setupGameTimer() {
        gameTimer = new Timeline(new KeyFrame(Duration.seconds(GAME_DURATION_SECONDS), event -> {
            endGame();
        }));
        gameTimer.setCycleCount(1);
        gameTimer.play();
    }
    
    
  
      private void updateTimer() {
        if (gameTimer != null) { 
            int remainingTime = (int) (gameTimer.getCycleDuration().toSeconds() - gameTimer.getCurrentTime().toSeconds());
            time.setText("Time: " + remainingTime + "s");
        }
    }
  
    

    private void displayTime() {
        time = new Label();
        time.setFont(Font.font(20));
        time.setTextFill(Color.CYAN); 
        time.setLayoutX(10);
        time.setLayoutY(10);
        root.getChildren().add(time);
    }
    
    

    private void endGame() {
        System.out.println("Game Over! Diamonds collected: " + diamondCount);
        gameOver = true;
        movingLeft = false;
        movingRight = false;
    }
}