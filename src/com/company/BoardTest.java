package com.company;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void possibleMoves() {
        ArrayList<Piece> p1 = new ArrayList<>();
        p1.add(new Piece(PieceType.KING, new Position(3, 0), true));
        p1.add(new Piece(PieceType.BISHOP, new Position(3, 1), true));
        p1.add(new Piece(PieceType.QUEEN, new Position(0, 4), true));
        p1.add(new Piece(PieceType.ROOK, new Position(0, 5), true));
        p1.add(new Piece(PieceType.KNIGHT, new Position(5, 1), true));
        p1.add(new Piece(PieceType.PAWN, new Position(7, 1), true));
        p1.add(new Piece(PieceType.ROOK, new Position(7, 3), true));
        p1.add(new Piece(PieceType.PAWN, new Position(6, 1), true));
        p1.add(new Piece(PieceType.PAWN, new Position(4, 6), false));
        p1.add(new Piece(PieceType.KING, new Position(3, 7), false));
        p1.add(new Piece(PieceType.BISHOP, new Position(7, 5), false));
        p1.add(new Piece(PieceType.ROOK, new Position(3, 4), false));
        p1.add(new Piece(PieceType.PAWN, new Position(1, 1), false));
        p1.add(new Piece(PieceType.PAWN, new Position(5, 2), false));
        p1.add(new Piece(PieceType.PAWN, new Position(5, 5), true));

        Board b1 = new Board();
        Board b2 = new Board(p1);

        assertArrayEquals(b1.possibleMoves(new Position(1, 1)), new Position[] {new Position(1, 2), new Position(1, 3)});
        assertEquals(0, b1.possibleMoves(new Position(3, 0)).length);
        assertArrayEquals(b1.possibleMoves(new Position(1, 0)), new Position[] {new Position(0, 2), new Position(2, 2)});
        assertEquals(0, b1.possibleMoves(new Position(7, 7)).length);

        assertEquals(1, b2.possibleMoves(new Position(1, 1)).length);
        assertFalse(Arrays.asList(b2.possibleMoves(new Position(0, 4))).contains(new Position(0, 7)));
        assertTrue(Arrays.asList(b2.possibleMoves(new Position(0, 4))).contains(new Position(3, 4)));
        assertFalse(Arrays.asList(b2.possibleMoves(new Position(3, 4))).contains(new Position(3, 7)));
        assertTrue(Arrays.asList(b2.possibleMoves(new Position(3, 4))).contains(new Position(0, 4)));
        assertTrue(Arrays.asList(b2.possibleMoves(new Position(3, 4))).contains(new Position(7, 4)));
        assertTrue(Arrays.asList(b2.possibleMoves(new Position(3, 1))).contains(new Position(7, 5)));
        assertTrue(Arrays.asList(b2.possibleMoves(new Position(7, 5))).contains(new Position(3, 1)));
        assertFalse(Arrays.asList(b2.possibleMoves(new Position(7, 5))).contains(new Position(2, 0)));
        assertTrue(Arrays.asList(b2.possibleMoves(new Position(3, 4))).contains(new Position(3, 1)));
        assertFalse(Arrays.asList(b2.possibleMoves(new Position(3, 4))).contains(new Position(3, 0)));
        assertEquals(3, b2.possibleMoves(new Position(3, 7)).length);
        assertTrue(Arrays.asList(b2.possibleMoves(new Position(5, 5))).contains(new Position(4, 6)));
        assertFalse(Arrays.asList(b2.possibleMoves(new Position(5, 5))).contains(new Position(6, 6)));
        assertTrue(Arrays.asList(b2.possibleMoves(new Position(4, 6))).contains(new Position(5, 5)));
        assertFalse(Arrays.asList(b2.possibleMoves(new Position(4, 6))).contains(new Position(3, 5)));



    }
}