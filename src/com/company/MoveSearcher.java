package com.company;

import java.util.HashMap;

public class MoveSearcher implements Runnable {
    MoveSearcher root;
    Board board;
    static final int RECURSIVE_DEPTH = 5;
    private Move moveCheck;
    private HashMap<Move, Integer> bestMoves;


    public MoveSearcher(Board board, MoveSearcher root, Move moveCheck) {
        this.board = board;
        this.root = root;
        this.moveCheck = moveCheck;
        if (root == null) {
            bestMoves = new HashMap<>();
        }
    }

    public Move search() {
        return null;
    }

    public int search(int alpha, int beta, int depth, boolean maximize) {
        if (depth == 0) {
            return board.scoreBoard();
        }

        if (maximize) {
            int max = Integer.MIN_VALUE;
            for (Move m : board.allPossibleMoves(true)) {
                board.movePiece(m.from(), m.to(), true);
                int val = search(alpha, beta, depth - 1, false);
                board.undo();
                max = Math.max(val, max);
                alpha = Math.max(val, max);
                if (beta <= alpha) {
                    break;
                }
            }
            return max;
        } else {
            int min = Integer.MAX_VALUE;
            for (Move m : board.allPossibleMoves(false)) {
                int val = search(alpha, beta, depth-1, true);
                min = Math.min(val, min);
                beta = Math.min(val, beta);
                if (beta <= alpha) {
                    break;
                }
            }
            return min;
        }
    }




    @Override
    public void run() {
        int best = search(Integer.MIN_VALUE, Integer.MAX_VALUE, RECURSIVE_DEPTH, false);


    }
}
