package com.company;


import java.util.ArrayList;
import java.util.Arrays;


public class Board {
    private final ArrayList<Piece> pieces;
    private static final PieceType[] initialOrder = {PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.KING, PieceType.QUEEN, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK};
    private ArrayList<Piece> rewinds = new ArrayList<>();
    private Position deletionPosition;
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BOLD = "\u001B[1m";

    public Board() {
        pieces = new ArrayList<Piece>();
        for (int side=1; side<7; side+=5) { //Add Pieces
            for (int i = 0; i < 8; i++) {
                pieces.add(new Piece(PieceType.PAWN, new Position(i, side), side == 1));
                pieces.add(new Piece(initialOrder[i], new Position(i, side == 1 ? 0 : 7), side == 1));
            }
        }

    }
    public Board(ArrayList<Piece> pieces) {
        this.pieces = pieces;

    }
    public Piece getPiece(PieceType type, boolean white) {
        return pieces.stream().filter(p -> p.type == type && p.white == white).findFirst().get();
    }

    public Piece getPiece(Position position) {
        for (Piece p : pieces) {
            if (p.position.equals(position)) {
                return p;
            }
        }
        return null;
    }

    public void drawBoard() {
        boolean white = true;
        for (int x = 7; x >= 0; x--) {
            String s = x + "  ";

            for (int y = 0; y<8; y++) {
                Piece p = getPiece(new Position(y, x));
                if (p == null) {
                    s += (white ? ANSI_WHITE_BACKGROUND : "") + "   " + ANSI_RESET;
                } else {
                    s += (white ? ANSI_WHITE_BACKGROUND : "") + strMap(p) + ANSI_RESET;
                }
                white = (y == 7) == white;

            }
            System.out.println(s);
        }
        String bottom = "   ";
        for (int i=0; i<8; i++) {
            bottom += " " + i + " ";
        }
        System.out.println(bottom);

        System.out.println();
    }

    public boolean kingUnderPressure(boolean white) {
        Piece king = getPiece(PieceType.KING, white);
        for (Piece p : pieces) {

            if (p.white != white && Arrays.asList(possibleMoves(p, false)).contains(king.position)) {
                return true;
            }
        }
        return false;
    }

    private static String strMap(Piece p) {

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
        return ANSI_BOLD + (p.white ? ANSI_GREEN : ANSI_BLUE) + " " + type + " " + ANSI_RESET;
    }

    public Position[] possibleMoves(Position p) {
        return possibleMoves(getPiece(p), true);
    }

    public Position[] possibleMoves(Piece p, boolean baseMove) {
        Position piecePosition = p.position;
        ArrayList<Position> possibleMoves = new ArrayList<>(Arrays.asList(p.possibleMoves()));

        for (int i = 0; i < possibleMoves.size();) {
            boolean increment = true;
            Piece pieceAtLoc = getPiece(possibleMoves.get(i));
            if (pieceAtLoc != null && pieceAtLoc.white == p.white) {
                possibleMoves.remove(pieceAtLoc.position);
                increment = false;
            } else if (p.type != PieceType.KNIGHT) {
                Position[] spacesBetween = spacesBetween(piecePosition, possibleMoves.get(i));
                if (spacesBetween.length > 0) {
                    for (Position b : spacesBetween) {
                        if (getPiece(b) != null) {
                            possibleMoves.remove(i);
                            increment = false;
                            break;
                        }
                    }
                }
            }
            if (p.type == PieceType.PAWN) {
                if (piecePosition.x() != possibleMoves.get(i).x() && pieceAtLoc == null) {
                    possibleMoves.remove(i);
                    increment = false;
                }
            }
            i = increment ? i+1 : i;
        }

        if (baseMove) {
            int i = 0;
            while (i < possibleMoves.size()) {
                Position position = possibleMoves.get(i);
                movePiece(piecePosition, position);
                if (kingUnderPressure(p.white)) {
                    possibleMoves.remove(i);
                } else {
                    i++;
                }
                undo();
                /*
                ArrayList<Piece> newPieces = new ArrayList<>(pieces);
                Board tester = new Board(newPieces);
                if (tester.kingUnderPressure(p.white)) {
                    possibleMoves.remove(i);
                } else {
                    i++;
                }*/
            }
        }


        Position[] pMoves = new Position[possibleMoves.size()];
        return possibleMoves.toArray(pMoves);
    }
    public void movePiece(Position from, Position to) {
        Piece pieceFrom = getPiece(from);
        rewinds.clear();
        rewinds.add(new Piece(pieceFrom));
        Piece pieceTo = getPiece(to);
        if (pieceTo != null) {
            rewinds.add(new Piece(pieceTo));
            pieces.remove(pieceTo);
            deletionPosition = null;
        } else {
            deletionPosition = to;
        }
        pieceFrom.position = to;
    }
    public void undo() {
        for (Piece p : rewinds) {
            Piece atLoc = getPiece(p.position);
            if (atLoc != null) {
                pieces.remove(atLoc);
            }
            pieces.add(p);
        }
        if (deletionPosition != null) {
            pieces.remove(getPiece(deletionPosition));
        }
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
