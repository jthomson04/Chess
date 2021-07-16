package com.company;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Board {
    private final ArrayList<Piece> pieces;
    private static final PieceType[] initialOrder = {PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.KING, PieceType.QUEEN, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK};


    public Board() {
        pieces = new ArrayList<Piece>();
        for (int side=1; side<7; side+=5) { //Add Pieces
            for (int i = 0; i < 8; i++) {
                pieces.add(new Piece(PieceType.PAWN, new Position(i, side), side == 1));
                pieces.add(new Piece(initialOrder[i], new Position(i, side == 1 ? 0 : 7), side == 1));
            }
        }
        drawBoard();
    }
    public Board(ArrayList<Piece> pieces) {
        this.pieces = pieces;
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
        System.out.println();
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

    public Position[] possibleMoves(Position p) {
        return possibleMoves(getPieceAtPosition(p));
    }
    public Position[] possibleMoves(Piece p) {
        ArrayList<Position> possibleMoves = new ArrayList<>(Arrays.asList(p.possibleMoves()));

        for (int i = 0; i < possibleMoves.size();) {
            boolean increment = true;
            Piece pieceAtLoc = getPieceAtPosition(possibleMoves.get(i));
            if (pieceAtLoc != null && pieceAtLoc.white == p.white) {
                possibleMoves.remove(pieceAtLoc.position);
                increment = false;
            } else if (p.type != PieceType.KNIGHT) {
                Position[] spacesBetween = spacesBetween(p.position, possibleMoves.get(i));
                if (spacesBetween.length > 0) {
                    for (Position b : spacesBetween) {
                        if (getPieceAtPosition(b) != null) {
                            possibleMoves.remove(i);
                            increment = false;
                            break;
                        }
                    }
                }
            }
            if (p.type == PieceType.PAWN) {
                if (p.position.x() != possibleMoves.get(i).x() && pieceAtLoc == null) {
                    possibleMoves.remove(i);
                    increment = false;
                }
            }
            i = increment ? i+1 : i;
        }

        Position[] pMoves = new Position[possibleMoves.size()];
        return possibleMoves.toArray(pMoves);
    }
    private Position[] spacesBetween(Position p1, Position p2) {
        int xShift = Integer.compare(p2.x(), p1.x());
        int yShift = Integer.compare(p2.y(), p1.y());
        int currentX = p1.x() + xShift;
        int currentY = p1.y() + yShift;
        ArrayList<Position> positions = new ArrayList<>();
        while (currentX != p2.x() || currentY != p2.y()) {
            positions.add(new Position(currentX, currentY));
            currentX += xShift;
            currentY += yShift;
        }

        Position[] p = new Position[positions.size()];
        return positions.toArray(p);
    }
    private int pieceValue(Piece p) {
        PieceType type = p.type;
        if (type == PieceType.QUEEN) {
            return 9;
        } else if (type == PieceType.ROOK) {
            return 5;
        } else if (type == PieceType.BISHOP || type == PieceType.KNIGHT) {
            return 3;
        } else {
            return 1;
        }
    }
    private int scoreBoard() { // scores board with respect to white
        int score = 0;
        for (Piece p : pieces) {
            score += p.white ? pieceValue(p) : -pieceValue(p);
        }

        return 0;
    }


}
