import edu.princeton.cs.algs4.Stack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Board
{
    private final int size;
    private final byte[] bitRepresent;
    private final int hamming;
    private final int manhattan;
    private final int bytelength;
    private static final long[] factorial;
    private static final int[] logFactorial;
    private final int zeroRow, zeroCol;

    static
    {
        factorial = new long[21];
        factorial[0] = 1;
        for (int i = 1; i < 21; i++)
            factorial[i] = factorial[i - 1] * i;
        logFactorial = new int[21];
        logFactorial[0] = 0;
        double f = 0;
        for (int i = 1; i < 21; i++)
        {
            f += Math.log(i) / Math.log(2);
            logFactorial[i] = (int) Math.ceil(f);
        }
    }

    public Board(int[][] blocks)           // construct a board from an n-by-n array of blocks
    // (where blocks[i][j] = block in row i, column j)
    {
        if (blocks == null)
            throw new IllegalArgumentException();
        size = blocks.length;
        bytelength = (int) Math.ceil(logFactorial[size * size] / 8.0);
        bitRepresent = permuToBits(blocks);
        int ham = 0;
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (blocks[i][j] != size * i + j + 1)
                    ham++;
        ham--;
        hamming = ham;
        int man = 0;
        int tmpRow = 0, tmpCol = 0;
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
            {
                int item = blocks[i][j] - 1;
                if (item == -1)
                {
                    tmpRow = i;
                    tmpCol = j;
                    continue;
                }
                int row = item / size;
                int col = item % size;
                man += Math.abs(i - row) + Math.abs(j - col);
            }
        manhattan = man;
        zeroRow = tmpRow;
        zeroCol = tmpCol;
    }

    private Board(int[][] blocks, int hamming, int manhattan, int size, int bytelength,
                  int zeroRow, int zeroCol)
    {
        this.hamming = hamming;
        this.manhattan = manhattan;
        this.size = size;
        this.bytelength = bytelength;
        this.zeroCol = zeroCol;
        this.zeroRow = zeroRow;
        bitRepresent = permuToBits(blocks);
    }

//    private byte[] addBytes(byte[] a, byte[] b)
//    {
//        int carry = 0;
//        byte[] s = new byte[a.length];
//        for (int i = 0; i < a.length; i++)
//        {
//            int a1 = a[i] + 128;
//            int b1 = b[i] + 128;
//            int c = a1 + b1 + carry;
//            if (c > 255)
//            {
//                carry = 1;
//                c -= 256;
//            } else carry = 0;
//            s[i] = (byte) (c - 128);
//        }
//        return s;
//    }

    private byte[] longToBytes(long num)
    {
        byte[] bytes = new byte[bytelength];
        for (int i = 0; i < bytelength; i++)
        {
            bytes[i] = (byte) (num >>> (8 * (bytelength - 1 - i)));
        }
        return bytes;
    }

    private long bytesToLong(byte[] bytes)
    {
        long num = 0;
        for (byte aByte : bytes)
        {
            num *= 256;
            num += aByte;
            if (aByte < 0)
                num += 256;
        }
        return num;
    }

    private byte[] permuToBits(int[][] permu)
    {
        int equal = 0;
        long index = 0;
        List<Integer> nums = new ArrayList<>(Arrays.asList(IntStream.range(0, size * size).boxed().toArray(Integer[]::new)));
        while (equal < size * size - 1)
        {
            int larger = permu[equal / size][equal % size];
            index += nums.indexOf(larger) * factorial[size * size - equal - 1];
            nums.remove(Integer.valueOf(larger));
            equal++;
        }
        return longToBytes(index);
    }

    private int[][] bitsToPermu(byte[] bytes)
    {
        long num = bytesToLong(bytes);
        int[][] board = new int[size][size];
        int index = 0;
        List<Integer> nums = new ArrayList<>(Arrays.asList(IntStream.range(0, size * size).boxed().toArray(Integer[]::new)));
        while (index < size * size)
        {
            long fact = factorial[size * size - index - 1];
            int n = (int) (num / fact);
            int block = nums.get(n);
            board[index / size][index % size] = block;
            nums.remove(Integer.valueOf(block));
            num = num % fact;
            index++;
        }
        return board;
    }

    public int dimension()                 // board dimension n
    {
        return size;
    }

    public int hamming()                   // number of blocks out of place
    {
        return hamming;
    }

    public int manhattan()                 // sum of Manhattan distances between blocks and goal
    {
        return manhattan;
    }

    public boolean isGoal()                // is this board the goal board?
    {
        return hamming == 0;
    }

    private void exch(int[][] tmpboard, int r1, int c1, int r2, int c2)
    {
        int tmp = tmpboard[r1][c1];
        tmpboard[r1][c1] = tmpboard[r2][c2];
        tmpboard[r2][c2] = tmp;
    }

    public Board twin()                    // a board that is obtained by exchanging any pair of blocks
    {
        int[][] board = bitsToPermu(bitRepresent);
        int index = (zeroRow * size + zeroCol + 1) % (size * size);
        int index1 = (index + 1) % (size * size);
        int[][] board1 = new int[size][size];
        for (int i = 0; i < board.length; i++)
            System.arraycopy(board[i], 0, board1[i], 0, size);
        exch(board1, index / size, index % size, index1 / size, index1 % size);
        return new Board(board1);
    }

    public boolean equals(Object y)        // does this board equal y?
    {
        if (y == this)
            return true;
        if (y == null)
            return false;
        if (y.getClass() != this.getClass())
            return false;
        if (size != ((Board) y).size)
            return false;
        for (int i = 0; i < bytelength; i++)
        {
            if (bitRepresent[i] != ((Board) y).bitRepresent[i])
                return false;
        }
        return true;
    }

    private void addNeighbor(int[][] board, int r, int c, int row, int col, Stack<Board> neighbors)
    {
        int item = board[r][c] - 1;
        int goalCol = item % size;
        int goalRow = item / size;
        int displacementMan, displacementHam;
        if (r == row)
            displacementMan = col > c && goalCol > c || col < c && goalCol < c ? -1 : 1;
        else displacementMan = row > r && goalRow > r || row < r && goalRow < r ? -1 : 1;
        int man = manhattan + displacementMan;
        if (goalCol == col && goalRow == row)
            displacementHam = -1;
        else if (goalCol == c && goalRow == r)
            displacementHam = 1;
        else displacementHam = 0;
        int ham = hamming + displacementHam;
        exch(board, r, c, row, col);
        neighbors.push(new Board(board, ham, man, size, bytelength, r, c));
        exch(board, row, col, r, c);
    }

    public Iterable<Board> neighbors()     // all neighboring boards
    {
        Stack<Board> neighbors = new Stack<>();
        int[][] board = bitsToPermu(bitRepresent);
        if (zeroRow != 0)
            addNeighbor(board, zeroRow - 1, zeroCol, zeroRow, zeroCol, neighbors);
        if (zeroRow != size - 1)
            addNeighbor(board, zeroRow + 1, zeroCol, zeroRow, zeroCol, neighbors);
        if (zeroCol != 0)
            addNeighbor(board, zeroRow, zeroCol - 1, zeroRow, zeroCol, neighbors);
        if (zeroCol != size - 1)
            addNeighbor(board, zeroRow, zeroCol + 1, zeroRow, zeroCol, neighbors);
        return neighbors;
    }

    public String toString()               // string representation of this board (in the output format specified below)
    {
        int[][] board = bitsToPermu(bitRepresent);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(size).append("\n");
        for (int[] ints : board)
        {
            for (int anInt : ints)
                stringBuilder.append(String.format("%2d ", anInt));
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) // unit tests (not graded)
    {
        int[][] blocks = new int[][]{{8, 1, 3}, {4, 0, 2}, {7, 6, 5}};
        Board board = new Board(blocks);
//        System.out.print(Integer.toString(board.dimension()) + ' ' + board.isGoal() +
//                ' ' + board.manhattan() + ' ' + board.hamming() + '\n' + board);
        int[][] blocks1 = new int[][]{{8, 1, 3}, {4, 2, 0}, {7, 6, 5}};
        for (Board board1 : board.neighbors())
        {
            System.out.print(board1);
            System.out.print(board1.manhattan());
            System.out.print('\n');
        }
//        System.out.print(board.equals(new Board(blocks1)));
//        System.out.print(board.twin());

//        List<Integer>list=new ArrayList<>();
//        for (int i = 0; i < 100; i++)
//            list.add(i);
//        Random random=new Random();
//        for (int i = 0; i < 5; i++)
//        {
//            int n=random.nextInt()%101;
//            list.remove(Integer.valueOf(n));
//            System.out.print(n);
//            System.out.print(list);
//            System.out.print('\n');
//        }
    }
}