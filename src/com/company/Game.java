package com.company;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Game {

    Board board = new Board();
    Scanner scanner = new Scanner(System.in);

    public Game() {
    }

    private Position[] getInput() {
        while (true) {
            try {
                String s = scanner.nextLine();
                String[] str = s.split(" ");
                return new Position[]{new Position(Integer.parseInt(str[0].substring(0, 1)), Integer.parseInt(str[0].substring(1, 2))), new Position(Integer.parseInt(str[1].substring(0, 1)), Integer.parseInt(str[1].substring(1, 2)))};
            } catch (Exception ignored) {
            }
        }
    }

    public void play() throws ExecutionException, InterruptedException {
        boolean whiteTurn = true;
        board.drawBoard();

        while (true) {

            if (whiteTurn) {
                System.out.print("Your Move: ");
                Position[] moves = getInput();
                if (board.allPossibleMoves(true).length == 0) {
                    System.out.println("Black Wins!");
                    break;
                }
                if (board.getPiece(moves[0]) != null && Arrays.asList(board.possibleMoves(moves[0])).contains(moves[1])) {
                    board.movePiece(moves[0], moves[1], true);
                } else {
                    System.out.println("Invalid Move");
                    continue;
                }
            } else {
                Move m = new MoveSearcher(board).search();
                if (m == null) {
                    System.out.println("White Wins!");
                    break;
                }
                board.movePiece(m.from(), m.to(), true);
            }
            board.drawBoard();
            whiteTurn = !whiteTurn;
        }
    }
}
