package com.company;

public class MoveSearcher {

    static final int RECURSIVE_DEPTH = 3;
    private final Board board;

    public MoveSearcher(Board b) {
        this.board = b;
    }

    public Move search() {

        Move[] possibleMoves = board.allPossibleMoves(false);
        if (possibleMoves.length == 0) {
            return null;
        } else if (possibleMoves.length == 1) {
            return possibleMoves[0];
        } else {
            return possibleMoves[search(Integer.MIN_VALUE, Integer.MAX_VALUE, RECURSIVE_DEPTH, false, true)];
        }
    }


    private int search(int alpha, int beta, int depth, boolean maximize, boolean root) {
        // performs minimax algorithm with alpha-beta pruning
        // if it is the root function call, return the index of the best movef
        // otherwise, return best board score, depending on whether to maximize

        // return board score heuristic if at root call depth
        if (depth == 0) {
            return board.scoreBoard();
        }

        if (maximize) {
            int max = Integer.MIN_VALUE;
            int highestIndex = -1;
            Move[] allPossibleMoves = board.allPossibleMoves(true);
            for (int i = 0, allPossibleMovesLength = allPossibleMoves.length; i < allPossibleMovesLength; i++) {
                Move m = allPossibleMoves[i];
                // move the piece, then recursively call search on subtree
                board.movePiece(m.from(), m.to(), false);
                int val = search(alpha, beta, depth - 1, false, false);
                if (root) {
                    if ((i+1) % 5 == 0) System.out.println((i+1) + " / " + allPossibleMoves.length);
                }
                // undo the move, the compare to best found move
                board.undo();
                max = Math.max(val, max);
                if (max == val) {
                    highestIndex = i;
                }
                alpha = Math.max(val, max);
                if (beta < alpha) {
                    break;
                }
            }

            return root ? highestIndex : max;
        } else {
            // exact same as above, except intending to minimize and using beta instead o0f alpha
            int min = Integer.MAX_VALUE;
            int lowestIndex = -1;
            Move[] allPossibleMoves = board.allPossibleMoves(false);
            for (int i = 0, allPossibleMovesLength = allPossibleMoves.length; i < allPossibleMovesLength; i++) {
                Move m = allPossibleMoves[i];
                board.movePiece(m.from(), m.to(), false);
                int val = search(alpha, beta, depth - 1, true, false);
                if (root) {
                    if ((i+1) % 5 == 0) System.out.println((i+1) + " / " + allPossibleMoves.length);
                }
                board.undo();
                min = Math.min(val, min);
                if (val == min) {
                    lowestIndex = i;
                }
                beta = Math.min(val, beta);
                if (beta < alpha) {
                    break;
                }
            }
            if (root) {
                System.out.println(allPossibleMoves[lowestIndex].from());
            }
            return root ? lowestIndex : min;
        }
    }
}
