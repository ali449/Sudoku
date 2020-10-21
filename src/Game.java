import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Game
{
    private static final int BOARD_START_INDEX = 0;
    private static final int BOARD_SIZE = 9;
    private static final int BOX_SIZE = 3;
    private static final int UNASSIGNED = 0;
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 9;

    private int[][] initial;
    private int[][] board;

    Game()
    {
        initial = new int[BOARD_SIZE][BOARD_SIZE];
        board = new int[BOARD_SIZE][BOARD_SIZE];
    }

    void initRandom()
    {
        Random random = new Random();

        int i1, j1, i2, j2;

        int minj = 0, maxj = 2, mini = 0, maxi = 2;

        for (int i = 0; i < board.length; i++)
        {
            Arrays.fill(initial[i], 0);
            Arrays.fill(board[i], 0);
        }


        for (int i = 0; i < BOX_SIZE; i++)
        {
            for (int j = 0; j < BOX_SIZE; j++)
            {
                while (true)
                {
                    i1 = random.nextInt(maxi - mini + 1) + mini;
                    j1 = random.nextInt(maxj - minj + 1) + minj;
                    board[i1][j1] = random.nextInt(MAX_VALUE) + MIN_VALUE;
                    i2 = random.nextInt(maxi - mini + 1) + mini;
                    j2 = random.nextInt(maxj - minj + 1) + minj;
                    board[i2][j2] = random.nextInt(MAX_VALUE) + MIN_VALUE;

                    if ((i1 == i2 && j1 == j2) || !isValid(i1, j1) || !isValid(i2, j2))
                        board[i1][j1] = board[i2][j2] = 0;
                    else
                    {
                        initial[i1][j1] = board[i1][j1];
                        initial[i2][j2] = board[i2][j2];
                        break;
                    }
                }

                minj += 3;
                maxj += 3;
            }
            minj = 0;
            maxj = 2;

            mini += 3;
            maxi += 3;

        }

        //initial = board = arr;
    }

    void initFromFile()
    {
        int i = 0, j = 0;

        List<String> list = new ArrayList<>();

        try(Stream<String> stream = Files.lines(Paths.get("src/data.txt")))
        {
            list = stream.collect(Collectors.toList());
        }catch (IOException e)
        {
            e.printStackTrace();
        }

        for (String str: list)
        {
            for (char ch: str.toCharArray())
            {
                if (ch != ' ')
                {
                    initial[i][j] = ch - 48;
                    board[i][j] = ch - 48;
                    j++;
                }
            }

            j = 0;
            i++;
        }
    }

    boolean solve()
    {
        for (int row = BOARD_START_INDEX; row < BOARD_SIZE; row++)
            for (int col = BOARD_START_INDEX; col < BOARD_SIZE; col++)
                if(board[row][col] == UNASSIGNED)
                {
                    for (int num = MIN_VALUE; num <= MAX_VALUE; num++)
                    {
                        //Assign a random number
                        board[row][col] = num;
                        //Check random number can satisfy constraint
                        if (isValid(row, col) && solve())
                            return true;

                        board[row][col] = UNASSIGNED;
                    }
                    return false;
                }

        return true;
    }

    //Check if the row, column, and 3x3 box are valid
    private boolean isValid(int row, int col)
    {
        return checkRow(row) && checkColumn(col) && checkBox(row, col);
    }

    private boolean checkRow(int row)
    {
        boolean[] constraint = new boolean[BOARD_SIZE];

        return IntStream.range(BOARD_START_INDEX, BOARD_SIZE)
               .allMatch(column -> checkConstraint(row, column, constraint));
    }

    private boolean checkColumn(int col)
    {
        boolean[] constraint = new boolean[BOARD_SIZE];

        return IntStream.range(BOARD_START_INDEX, BOARD_SIZE)
               .allMatch(row -> checkConstraint(row, col, constraint));
    }

    //Check 3x3 grid
    private boolean checkBox(int row, int col)
    {
        boolean[] constraint = new boolean[BOARD_SIZE];

        int boxRowStart = row - row % BOX_SIZE;
        int boxRowEnd = boxRowStart + BOX_SIZE;
        int boxColStart = col - col % BOX_SIZE;
        int boxColEnd = boxColStart + BOX_SIZE;

        for (int i = boxRowStart; i < boxRowEnd; i++)
            for (int j = boxColStart; j < boxColEnd; j++)
                if(!checkConstraint(i, j, constraint))
                    return false;

        return true;
    }

    private boolean checkConstraint(int row, int col, boolean[] constraint)
    {
        if(board[row][col] != UNASSIGNED)
        {
            if (!constraint[board[row][col] - 1])
                constraint[board[row][col] - 1] = true;
            else
                return false;
        }

        return true;
    }

    public int[][] getInitial()
    {
        return initial;
    }

    public int[][] getBoard()
    {
        return board;
    }

    public void print()
    {
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
                System.out.print(board[i][j]+" ");
            System.out.println();
        }
    }
}
