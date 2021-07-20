package com.company;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class Board {
    public final ArrayList<Piece> pieces;
    private static final PieceType[] initialOrder = {PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.KING, PieceType.QUEEN, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK};
    private ArrayList<Piece> rewinds = new ArrayList<>();
    private Position deletionPosition;

    private static final HashMap<PieceType, Integer> mobilityScore = new HashMap<>();
    private static final HashMap<PieceType, Integer> threatenedScore = new HashMap<>();
    private static final HashMap<PieceType, Integer> protectedScore = new HashMap<>();
    private static final HashMap<PieceType, Integer> pieceValueScore = new HashMap<>();
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BOLD = "\u001B[1m";
    private final HashMap<MoveQuery, Position[]> cache = new HashMap<>();

    public Board() {
        pieces = new ArrayList<>();
        for (int side=1; side<7; side+=5) { //Add Pieces
            for (int i = 0; i < 8; i++) {
                pieces.add(new Piece(PieceType.PAWN, new Position(i, side), side == 1));
                pieces.add(new Piece(initialOrder[i], new Position(i, side == 1 ? 0 : 7), side == 1));
            }
        }
        initializeDicts();

    }
    public Board(ArrayList<Piece> pieces) {
        this.pieces = pieces;
        initializeDicts();
    }
    private void initializeDicts() {
        if (!pieceValueScore.isEmpty()) return;

        PieceType[] typeOrder = new PieceType[] {PieceType.PAWN, PieceType.KNIGHT, PieceType.BISHOP, PieceType.ROOK, PieceType.QUEEN, PieceType.KING};
        int[] mobility = new int[] {0, 0, 0, 0, 1, 0};
        int[] threat = new int[] {0, 1, 1, 2, 5, 4};
        int[] protect = new int[] {0, 1, 1, 0, 0, 0};
        int[] piece = new int[] {1, 3, 3, 5, 9, 0};
        for (int i=0; i<6; i++) {
            mobilityScore.put(typeOrder[i], mobility[i]);
            threatenedScore.put(typeOrder[i], threat[i]);
            protectedScore.put(typeOrder[i], protect[i]);
            pieceValueScore.put(typeOrder[i], piece[i]);
        }
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
            if (p.white != white && Arrays.asList(possibleMoves(p, false, false, false)).contains(king.position)) {
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
        return possibleMoves(getPiece(p), true, false, true);
    }

    public Position[] possibleMoves(Piece p, boolean baseMove, boolean includeOwnTeam, boolean canUseCache) {
        if (canUseCache && cache.containsKey(new MoveQuery(p.position, baseMove, includeOwnTeam))) {
            return cache.get(new MoveQuery(p.position, baseMove, includeOwnTeam));
        }

        Position piecePosition = p.position;
        ArrayList<Position> possibleMoves = new ArrayList<>(Arrays.asList(p.possibleMoves()));

        for (int i = 0; i < possibleMoves.size();) {
            boolean increment = true;
            Piece pieceAtLoc = getPiece(possibleMoves.get(i));
            if (pieceAtLoc != null && pieceAtLoc.white == p.white && !includeOwnTeam) {
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
                if (piecePosition.x() != possibleMoves.get(i).x() && pieceAtLoc == null || (piecePosition.x() == possibleMoves.get(i).x() && pieceAtLoc != null)) {
                    possibleMoves.remove(i);
                    increment = false;
                }
            }
            i = increment ? i+1 : i;
        }
        Position[] x;
        if (canUseCache) {
            x = new Position[possibleMoves.size()];
            cache.put(new MoveQuery(p.position, false, includeOwnTeam), possibleMoves.toArray(x));
        }

        if (baseMove) {
            int i = 0;
            while (i < possibleMoves.size()) {
                Position position = possibleMoves.get(i);
                Piece piece = getPiece(position);
                if (piece != null && piece.white == p.white) {
                    i++;
                    continue;
                }
                movePiece(piecePosition, position, false);
                if (kingUnderPressure(p.white)) {
                    possibleMoves.remove(i);
                } else {
                    i++;
                }
                undo();
            }
        }


        Position[] pMoves = new Position[possibleMoves.size()];

        pMoves = possibleMoves.toArray(pMoves);
        if (canUseCache && baseMove) {
            cache.put(new MoveQuery(p.position, true, includeOwnTeam), pMoves);
        }
        return pMoves;
    }

    public void movePiece(Position from, Position to, boolean clearCache) {
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
        if (clearCache) {
            cache.clear();
        }
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


    public int scoreBoard() { // scores board with respect to white
        int score = 0;
        Piece[] copyPieces = copyPieces();
        for (int i=0; i<pieces.size(); i++) {
            Piece p = copyPieces[i];
            int multiplier = p.white ? 1 : -1;
            score += pieceValueScore.get(p.type) * multiplier; // scores piece value

            if (p.type == PieceType.PAWN) { // scores pawn advancement (PAW of 1)
                int baseline = p.white ? 1 : 6;
                int diff = Math.abs(p.position.y() - baseline);
                score += diff * multiplier;
            }

            Position[] totalPossMoves = possibleMoves(p, true, true, true);
            Position[] actualPossMoves = Arrays.stream(totalPossMoves).filter(position -> {
                Piece piece = getPiece(position);
                if (piece != null && piece.white == p.white) {
                    return false;
                }
                return true;
            }).toArray(Position[]::new);

            if (mobilityScore.get(p.type) != 0) { //scores mobility
                int moveCount = actualPossMoves.length;
                score += moveCount * mobilityScore.get(p.type) * multiplier;
            }

            for (Position position : actualPossMoves) {// scores what pieces this piece is threatening
                Piece pieceAtLoc = getPiece(position);
                if (pieceAtLoc != null) {
                    score += threatenedScore.get(pieceAtLoc.type) * multiplier;
                }
            }

            for (Position position : totalPossMoves) {
                Piece piece = getPiece(position);
                if (piece != null && piece.white == p.white) {
                    score += protectedScore.get(piece.type) * multiplier;
                }
            }





        }
        return score;
    }

    public Piece[] copyPieces() {
        Piece[] copyPieces = new Piece[pieces.size()];
        for (int i=0, len=pieces.size(); i<len; i++) {
            Piece p = pieces.get(i);
            copyPieces[i] = new Piece(p.type, new Position(p.position.x(), p.position.y()), p.white);
        }
        return copyPieces;
    }
    public Move[] allPossibleMoves(boolean white) {
        ArrayList<Move> possMoves = new ArrayList<>();
        for (Piece p : pieces) {
            if (p.white == white) {
                for (Position position : possibleMoves(p.position)) {
                    possMoves.add(new Move(p.position, position));
                }
            }
        }
        Move[] moves = new Move[possMoves.size()];
        return possMoves.toArray(moves);
    }


}
