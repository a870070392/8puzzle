import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.LinkedList;
import java.util.List;

public class Solver
{
    private Node solution;
    private Node twinsolution;

    private final class Node implements Comparable<Node>
    {
        private final Board cur;
        private final Node pred;
        private final int move;
        private final int manhattan;
        private final boolean twin;

        Node(Board cur, Node pred, int move)
        {
            this.cur = cur;
            this.pred = pred;
            this.move = move;
            twin = pred.twin;
            manhattan = move + cur.manhattan();
        }

        Node(Board cur, boolean twin)
        {
            this.cur = cur;
            this.pred = null;
            this.move = 0;
            this.twin = twin;
            manhattan = cur.manhattan();
        }

        @Override
        public int compareTo(Node node)
        {
            if (manhattan != node.manhattan)
                return manhattan - node.manhattan;
            else return (move + cur.hamming()) - (node.move + node.cur.hamming());
        }
    }

    public Solver(Board initial)           // find a solution to the initial board (using the A* algorithm)
    {
        if (initial == null)
            throw new IllegalArgumentException();
        solution = null;
        twinsolution = null;
        MinPQ<Node> minPQ = new MinPQ<>();
        minPQ.insert(new Node(initial, false));
        minPQ.insert(new Node(initial.twin(), true));
        do
            enquue(minPQ); while (solution == null && twinsolution == null);
    }

    private void enquue(MinPQ<Node> minPQ)
    {
        if (!minPQ.isEmpty())
        {
            Node nod = minPQ.delMin();
            Board cur = nod.cur;
            if (cur.isGoal())
                if (!nod.twin)
                    solution = nod;
                else
                    twinsolution = nod;
            else
            {
                int move = nod.move;
                Node pred = nod.pred;
                Iterable<Board> iterable = cur.neighbors();
                for (Board b : iterable)
                    if (pred == null || !b.equals(pred.cur))
                        minPQ.insert(new Node(b, nod, move + 1));
            }
        }
    }

    public boolean isSolvable()            // is the initial board solvable?
    {
        return solution != null;
    }

    public int moves()                     // min number of moves to solve initial board; -1 if unsolvable
    {
        if (solution == null)
            return -1;
        return solution.move;
    }

    public Iterable<Board> solution()      // sequence of boards in a shortest solution; null if unsolvable
    {
        if (solution == null)
            return null;
        List<Board> boards = new LinkedList<>();
        ((LinkedList<Board>) boards).addFirst(solution.cur);
        Node nod = solution.pred;
        while (nod != null)
        {
            ((LinkedList<Board>) boards).addFirst(nod.cur);
            nod = nod.pred;
        }
        return boards;
    }

    public static void main(String[] args) // solve a slider puzzle (given below)
    {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else
        {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}