import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable
{
    @FXML
    Canvas canvas;
    @FXML
    Button solve;
    @FXML
    Button readFile;
    @FXML
    Button random;

    private Game game;

    private int[][] board;

    private GraphicsContext context;

    private boolean hasInit;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        game = new Game();

        board = game.getInitial();

        context = canvas.getGraphicsContext2D();

        File file = new File("logo.png");

        context.drawImage(new Image(file.toString()), 65.0, 200.0);

        readFile.setOnMouseClicked(event ->
        {
            game.initFromFile();
            hasInit = true;
            drawSquares(context);
        });

        random.setOnMouseClicked(event ->
        {
            game.initRandom();
            board = game.getBoard();
            hasInit = true;
            drawSquares(context);
        });

        solve.setOnMouseClicked(event -> on_start_clicked());
    }

    //Start solving problem
    private void on_start_clicked()
    {
        if(!hasInit)
            return;

        boolean result = game.solve();

        if(!result)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Result");
            alert.setHeaderText("Task Failed!");
            alert.show();
            return;
        }

        board = game.getBoard();
        drawSquares(context);

    }

    //Square 450x450 by each square 50px
    private void drawSquares(GraphicsContext context)
    {
        int[][] initial = game.getInitial();

        context.clearRect(0,0, 500, 500);

        for(int row = 0; row < 9; row++)
            for(int col = 0; col < 9; col++)
            {
                //find position y of cell
                int posY = row * 50 + 2 + row *5;
                //find position x of cell
                int posX = col * 50 + 2 + col * 5;


                if(row % 3 == 0 && col % 3 == 0)
                {
                    context.setStroke(Color.WHITE);
                    context.strokeRect(posX-2, posY-2, 162, 162);
                }

                //4px for blank space
                int width = 46;

                //Draw square
                context.setFill(Color.WHITE);
                context.fillRoundRect(posX, posY, width, width, 10, 10);

                //Draw numbers
                posY += 28;
                posX += 18;
                context.setFill(Color.BLACK);
                context.setFont(new Font(20));

                if(initial[row][col] != 0)
                {
                    context.fillText(initial[row][col] + "", posX, posY);
                }

                //Draw solution
                if(board[row][col] != initial[row][col])
                {
                    //Clear number square
                    context.setFill(Color.WHITE);
                    context.fillRoundRect(posX - 18, posY - 28, width, width, 10, 10);
                    //Draw solution in square
                    context.setFill(Color.BLUE);
                    context.fillText(board[row][col] + "", posX, posY);
                }

            }

    }

}
