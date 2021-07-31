package com.company;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class Board {
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BOLD = "\u001B[1m";
    private static final PieceType[] initialOrder = {PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.KING, PieceType.QUEEN, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK};
    private static final HashMap<PieceType, Integer> mobilityScore = new HashMap<>();
    private static final HashMap<PieceType, Integer> threatenedScore = new HashMap<>();
    private static final HashMap<PieceType, Integer> protectedScore = new HashMap<>();
    private static final HashMap<PieceType, Integer> pieceValueScore = new HashMap<>();
    public final ArrayList<Piece> pieces;
    private final ArrayList<ArrayList<Piece>> rewinds = new ArrayList<>();
    private final ArrayList<Position> deletionPosition = new ArrayList<>();
    private final HashMap<MoveQuery, Position[]> cache = new HashMap<>();


    public Board() {
        pieces = new ArrayList<>();
        for (int side = 1; side < 7; side += 5) { //Add Pieces
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
        return ANSI_BOLD + (p.white ? ANSI_RED : ANSI_BLUE) + " " + type + " " + ANSI_RESET;
    }

    public static <T extends Copyable<T>> ArrayList<T> deepCopyArrayList(ArrayList<T> arr) {
        ArrayList<T> newArr = new ArrayList<>();
        newArr.ensureCapacity(arr.size());
        for (T p : arr) {
            newArr.add(p.copy());
        }
        return newArr;
    }

    private void initializeDicts() {
        if (!pieceValueScore.isEmpty()) return;

        PieceType[] typeOrder = new PieceType[]{PieceType.PAWN, PieceType.KNIGHT, PieceType.BISHOP, PieceType.ROOK, PieceType.QUEEN, PieceType.KING};
        int[] mobility = new int[]{0, 0, 0, 0, 1, 0};
        int[] threat = new int[]{0, 1, 1, 2, 5, 4};
        int[] protect = new int[]{0, 1, 1, 0, 0, 0};
        int[] piece = new int[]{1, 3, 3, 5, 9, 0};
        for (int i = 0; i < 6; i++) {
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
        /*for (Piece p : pieces) {
            if (p.position.equals(position)) {
                return p;
            }
        }
        return null;*/
        return pieces.stream().filter(p -> p.position.equals(position)).findFirst().orElse(null);


    }

    public void drawBoard() {
        boolean white = true;
        for (int x = 7; x >= 0; x--) {
            StringBuilder s = new StringBuilder(x + "  ");

            for (int y = 0; y < 8; y++) {
                Piece p = getPiece(new Position(y, x));
                if (p == null) {
                    s.append(white ? ANSI_WHITE_BACKGROUND : "").append("   ").append(ANSI_RESET);
                } else {
                    s.append(white ? ANSI_WHITE_BACKGROUND : "").append(strMap(p)).append(ANSI_RESET);
                }
                white = (y == 7) == white;

            }
            System.out.println(s);
        }
        StringBuilder bottom = new StringBuilder("   ");
        for (int i = 0; i < 8; i++) {
            bottom.append(" ").append(i).append(" ");
        }
        System.out.println(bottom);

        System.out.println();
    }

    public boolean kingUnderPressure(boolean white) {
        //can disregard all pieces except opponent's bishop, rook and queen if the king wasn't moved, otherwise have to check all
        Piece king = getPiece(PieceType.KING, white);
        ArrayList<Piece> copy = deepCopyArrayList(pieces);
        for (Piece p : copy) {
            assert p.possibleMoves() != null;
            if (p.white != white && Arrays.asList(p.possibleMoves()).contains(king.position) && Arrays.asList(possibleMoves(p, false, false, false)).contains(king.position)) {
                return true;
            }
        }
        return false;
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

        for (int i = 0; i < possibleMoves.size(); ) {
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
            if (p.type == PieceType.PAWN && increment) {
                if (piecePosition.x() != possibleMoves.get(i).x() && pieceAtLoc == null || (piecePosition.x() == possibleMoves.get(i).x() && pieceAtLoc != null)) {
                    possibleMoves.remove(i);
                    increment = false;
                }
            }
            i = increment ? i + 1 : i;
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
        rewinds.add(new ArrayList<>());
        rewinds.get(rewinds.size() - 1).add(pieceFrom.copy());
        Piece pieceTo = getPiece(to);
        if (pieceTo != null) {
            assert (pieceTo.type != PieceType.KING);
            rewinds.get(rewinds.size() - 1).add(pieceTo.copy());
            pieces.remove(pieceTo);
            deletionPosition.add(null);
        } else {
            deletionPosition.add(to);
        }
        pieceFrom.position = to;
        if (clearCache) {
            cache.clear();
        }
    }

    public void undo() {
        if (deletionPosition.size() == 0) {
            return;
        }
        for (Piece p : rewinds.get(rewinds.size() - 1)) {
            Piece atLoc = getPiece(p.position);
            if (atLoc != null) {
                pieces.remove(atLoc);
            }
            pieces.add(p);
        }
        if (deletionPosition.get(deletionPosition.size() - 1) != null) {
            pieces.remove(getPiece(deletionPosition.get(deletionPosition.size() - 1)));
        }
        deletionPosition.remove(deletionPosition.size() - 1);
        rewinds.remove(rewinds.size() - 1);
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
        ArrayList<Piece> copyPieces = deepCopyArrayList(pieces);
        for (int i = 0; i < pieces.size(); i++) {
            Piece p = copyPieces.get(i);
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
                return piece == null || piece.white != p.white;
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

    public Move[] allPossibleMoves(boolean white) {
        ArrayList<Move> possMoves = new ArrayList<>();
        ArrayList<Piece> copy = deepCopyArrayList(pieces);
        for (Piece p : copy) {
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
