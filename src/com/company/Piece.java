package com.company;

import java.util.ArrayList;
import java.util.HashMap;

public class Piece implements Copyable<Piece> {

    private static final HashMap<PreComputeMove, Position[]> moveCache = new HashMap<>();
    public PieceType type;
    public Position position;
    public boolean white;

    public Piece(PieceType type, Position position, boolean white) {
        this.type = type;
        this.position = position;
        this.white = white;
    }

    private static boolean validPos(Position position) {
        return position.x() <= 7 && position.x() >= 0 && position.y() <= 7 && position.y() >= 0;
    }

    public Position[] possibleMoves() {

        if (type == PieceType.PAWN) {
            int mult = white ? 1 : -1;
            Position[] forward;
            if ((white && position.y() == 1) || (!white && position.y() == 6)) {
                forward = new Position[]{new Position(position.x(), position.y() + mult), new Position(position.x(), position.y() + 2 * mult)};
            } else {
                if (!(white && position.y() == 7) && !(!white && position.y() == 0)) {
                    forward = new Position[]{new Position(position.x(), position.y() + mult)};
                } else {
                    forward = new Position[0];
                }
            }
            Position[] diagonals = new Position[position.x() == 0 || position.x() == 7 ? 1 : 2];
            int i = 0;
            if (position.x() != 7) {
                diagonals[i] = new Position(position.x() + 1, position.y() + mult);
                i++;
            }
            if (position.x() != 0) {
                diagonals[i] = new Position(position.x() - 1, position.y() + mult);
            }
            Position[] moves = new Position[forward.length + diagonals.length];
            System.arraycopy(forward, 0, moves, 0, forward.length);
            System.arraycopy(diagonals, 0, moves, forward.length, diagonals.length);
            return moves;
        } else {
            PreComputeMove move = new PreComputeMove(position, type);
            if (moveCache.containsKey(move)) {
                return moveCache.get(move);
            }
            Position[] positions;
            if (type == PieceType.ROOK) {
                return xyPositions();
            } else if (type == PieceType.QUEEN) {
                Position[] xy = xyPositions();
                Position[] diagonals = diagonalPositions();
                positions = new Position[xy.length + diagonals.length];
                System.arraycopy(xy, 0, positions, 0, xy.length);
                System.arraycopy(diagonals, 0, positions, xy.length, diagonals.length);
            } else if (type == PieceType.BISHOP) {
                positions = diagonalPositions();
            } else if (type == PieceType.KNIGHT) {
                ArrayList<Position> positionsArr = new ArrayList<>();
                for (int i = 0; i < 2; i++) {
                    int xVal = i == 0 ? 2 : 1;
                    int yVal = i == 0 ? 1 : 2;
                    for (int xShift = -xVal; xShift <= xVal; xShift += xVal * 2) {
                        for (int yShift = -yVal; yShift <= yVal; yShift += 2 * yVal) {
                            Position p = new Position(position.x() + xShift, position.y() + yShift);
                            if (validPos(p)) {
                                positionsArr.add(p);
                            }
                        }
                    }
                }
                Position[] p = new Position[positionsArr.size()];
                positions = positionsArr.toArray(p);
            } else {
                ArrayList<Position> positionsArr = new ArrayList<>();
                for (int xOffset = -1; xOffset < 2; xOffset += 1) {
                    for (int yOffset = -1; yOffset < 2; yOffset += 1) {
                        if (xOffset == 0 && yOffset == 0) {
                            continue;
                        }
                        Position p1 = new Position(position.x() + xOffset, position.y() + yOffset);
                        if (validPos(p1)) {
                            positionsArr.add(p1);
                        }
                    }


                }

                Position[] p = new Position[positionsArr.size()];
                positions = positionsArr.toArray(p);
            }
            moveCache.put(move, positions);
            return positions;
        }
    }

    private Position[] xyPositions() {
        Position[] positions = new Position[14];
        int i = 0;
        for (int j = 0; j < 8; j++) {
            if (j != position.x()) {
                positions[i] = new Position(j, position.y());
                i++;
            }
            if (j != position.y()) {
                positions[i] = new Position(position.x(), j);
                i++;
            }
        }
        return positions;
    }

    private Position[] diagonalPositions() {

        ArrayList<Position> positions = new ArrayList<>();
        int currentX = position.x();
        int currentY = position.y();
        for (int yShift = -1; yShift <= 1; yShift += 2) {

            for (int xShift = -1; xShift <= 1; xShift += 2) {
                while (true) {
                    currentX += xShift;
                    currentY += yShift;
                    if (currentX == 8 || currentX == -1 || currentY == 8 || currentY == -1) {
                        currentX = position.x();
                        currentY = position.y();
                        break;
                    } else {
                        positions.add(new Position(currentX, currentY));
                    }
                }
            }
        }
        Position[] p = new Position[positions.size()];
        return positions.toArray(p);
    }

    @Override
    public Piece copy() {
        return new Piece(type, new Position(position.x(), position.y()), white);
    }
}
