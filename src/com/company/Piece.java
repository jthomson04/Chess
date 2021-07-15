package com.company;

import java.util.ArrayList;

public class Piece {

    public PieceType type;
    public Position position;
    public boolean white;

    public Piece(PieceType type, Position position, boolean white) {
        this.type = type;
        this.position = position;
        this.white = white;
    }

    public Position[] possibleMoves() {
        if (type == PieceType.PAWN) {
            int mult = white ? 1 : -1;
            if ((white && position.y() == 1) || (!white && position.y() == 6)) {
                return new Position[]{new Position(position.x(), position.y() + mult), new Position(position.x(), position.y() + 2*mult)};
            } else {
                if (!(white && position.y() == 7) && !(!white && position.y() == 0)) {
                    return new Position[]{new Position(position.x(), position.y() + mult)};
                }
            }
        } else if (type == PieceType.ROOK) {
            return xyPositions();
        } else if (type == PieceType.QUEEN) {
            Position[] xy = xyPositions();
            Position[] diagonals = diagonalPositions();
            Position[] positions = new Position[xy.length + diagonals.length];
            System.arraycopy(xy, 0, positions, 0, xy.length);
            System.arraycopy(diagonals, 0, positions, xy.length, diagonals.length);
            return positions;
        } else if (type == PieceType.BISHOP) {
            return diagonalPositions();
        } else if (type == PieceType.KNIGHT) {
            ArrayList<Position> positions = new ArrayList<>();
            for (int i=0; i<2; i++) {
                int xVal = i == 0 ? 2 : 1;
                int yVal = i == 0 ? 1 : 2;
                for (int xShift=-xVal; xShift<=xVal; xShift+=xVal*2) {
                    for (int yShift=-yVal; yShift<=yVal; yShift+=2*yVal) {
                        Position p = new Position(position.x() + xShift, position.y() + yShift);
                        if (validPos(p)) {
                            positions.add(p);
                        }
                    }
                }
            }
            Position[] p = new Position[positions.size()];
            return positions.toArray(p);
        } else {
            ArrayList<Position> positions = new ArrayList<>();
            for (int offset=-1; offset<2; offset+=2) {
                Position p1 = new Position(position.x() + offset, position.y());
                Position p2 = new Position(position.x(), position.y() + offset);
                if (validPos(p1)) {
                    positions.add(p1);
                }
                if (validPos(p2)) {
                    positions.add(p2);
                }
            }

            Position[] p = new Position[positions.size()];
            return positions.toArray(p);
        }
        return new Position[] {};
    }

    private Position[] xyPositions() {
        Position[] positions = new Position[14];
        int i = 0;
        for (int j=0; j<8; j++) {
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
        for (int yShift=-1; yShift <= 1; yShift += 2) {

            for (int xShift=-1; xShift <= 1; xShift += 2) {
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

    private static boolean validPos(Position position) {
        return position.x() <= 7 && position.x() >= 0 && position.y() <= 7 && position.y() >= 0;
    }

}
