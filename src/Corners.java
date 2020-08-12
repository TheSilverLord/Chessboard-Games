public class Corners
{
    private Chessboard chessboard;

    Corners()
    {
        chessboard = new Chessboard();
        for(char a = 'a'; a <= 'c'; a++)
        {
            for(int i = 8; i >= 6; i--) chessboard.addChecker(new Checker(false, chessboard.getCell(a, i)));
        }
        for (char a = 'f'; a <= 'h'; a++)
        {
            for (int i = 3; i >= 1; i--) chessboard.addChecker(new Checker(true, chessboard.getCell(a, i)));
        }
    }
}
