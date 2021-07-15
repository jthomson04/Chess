package com.company;


import java.util.ArrayList;


public class Board {
    private ArrayList<Piece> pieces = new ArrayList<>();
    private boolean whitesTurn = true;
    private static PieceType[] initialOrder = {PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.KING, PieceType.QUEEN, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK};


    public Board() {

        for (int side=1; side<7; side+=5) { //Add Pieces
            for (int i = 0; i < 8; i++) {
                pieces.add(new Piece(PieceType.PAWN, new Position(i, side), side == 1));
                pieces.add(new Piece(initialOrder[i], new Position(i, side == 1 ? 0 : 7), side == 1));
            }
        }
        drawBoard();

    }

    public Piece getPieceAtPosition(Position position) {
        for (Piece p : pieces) {
            if (p.position.equals(position)) {
                return p;
            }
        }
        return null;
    }

    public void drawBoard() {
        String[][] b = new String[8][8];

        for (int x = 7; x >= 0; x--) {
            String s = "";
            for (int y = 0; y<8; y++) {
                Piece p = getPieceAtPosition(new Position(y, x));
                if (p == null) {
                    s += "  ";
                } else {
                    s += strMap(p);
                }
                s += "|";
                if (y == 0) {
                    s = "|" + s;
                }
            }
            System.out.println(s);
        }
    }

    private static String strMap(Piece p) {
        String team = p.white ? "W" : "B";
        String type;
        if (p.type == PieceType.KING) {
            type = "K";
        } else if (p.type == PieceType.ROOK) {
            type = "R";
        } else if (p.type == PieceType.QUEEN) {
            type = "Q";
        } else if (p.type == PieceType.PAWN) {
            type = "P";
        } else if (p.type == PieceType.KNIGHT) {
            type = "N";
        } else {
            type = "B";
        }
        return type + team;
    }


}
