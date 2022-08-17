package com.company;

import java.util.Arrays;
import java.util.Scanner;

public class Game {

    Board board = new Board();
    Scanner scanner = new Scanner(System.in);

    public Game() {
    }

    private Position[] getInput() {
        // get user's input
        // formatted as <xfrom><yfrom> <xto><yto>
        // c<l or r> is used to castle
        while (true) {
            try {
                String s = scanner.nextLine();
                String[] str = s.split(" ");
                if (str.length == 1) {
                    if (str[0].equals("cr")) {
                        return new Position[] {new Position(0, 1)};
                    } else {
                        return new Position[] {new Position(0, -1)};
                    }
                }
                return new Position[]{new Position(Integer.parseInt(str[0].substring(0, 1)), Integer.parseInt(str[0].substring(1, 2))), new Position(Integer.parseInt(str[1].substring(0, 1)), Integer.parseInt(str[1].substring(1, 2)))};
            } catch (Exception ignored) {
            }
        }
    }

    public void play() {
        boolean whiteTurn = true;
        boolean blackHasCastled = false;
        board.drawBoard(null);

        while (true) {

            if (whiteTurn) {
                System.out.print("Your Move: ");
                // check for stalemate or win
                if (board.allPossibleMoves(true).length == 0) {
                    if (board.kingUnderPressure(true)) {
                        System.out.println("Stalemate!");
                    } else {
                        System.out.println("Black Wins!");
                    }
                    break;
                }
                Position[] moves = getInput();
                if (moves.length == 1) {
                    boolean[] castles = board.castles(true);
                    if (moves[0].y() == -1 && castles[0]) {
                        board.castle(true, true);
                        board.drawBoard(null);
                    } else if (moves[0].y() == 1 && castles[1]) {
                        board.castle(true, false);
                        board.drawBoard(null);
                    } else {
                        System.out.println("Invalid Move");
                        continue;
                    }
                }
                // check that the move exists and is valid
                else if (board.getPiece(moves[0]) != null && Arrays.asList(board.possibleMoves(moves[0])).contains(moves[1])) {
                    board.movePiece(moves[0], moves[1], true);
                } else {
                    System.out.println("Invalid Move");
                    continue;
                }
            } else {

                Move m = new MoveSearcher(board).search();
                if (m == null) {
                    if (board.kingUnderPressure(false)) {
                        System.out.println("Stalemate!");
                    } else {
                        System.out.println("White Wins!");
                    }
                    break;
                }
                board.movePiece(m.from(), m.to(), true);
            }
            // alternate turns
            whiteTurn = !whiteTurn;
        }
    }
}
