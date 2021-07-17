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
        p1.add(new Piece(PieceType.KING, new Position(4, 7), false));
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
        assertTrue(Arrays.asList(b2.possibleMoves(new Position(3, 4))).contains(new Position(3, 7)));
        assertTrue(Arrays.asList(b2.possibleMoves(new Position(3, 4))).contains(new Position(0, 4)));
        assertTrue(Arrays.asList(b2.possibleMoves(new Position(3, 4))).contains(new Position(7, 4)));
        assertFalse(Arrays.asList(b2.possibleMoves(new Position(3, 1))).contains(new Position(7, 5)));
        assertTrue(Arrays.asList(b2.possibleMoves(new Position(7, 5))).contains(new Position(3, 1)));
        assertFalse(Arrays.asList(b2.possibleMoves(new Position(7, 5))).contains(new Position(2, 0)));
        assertTrue(Arrays.asList(b2.possibleMoves(new Position(3, 4))).contains(new Position(3, 1)));
        assertFalse(Arrays.asList(b2.possibleMoves(new Position(3, 4))).contains(new Position(3, 0)));
        assertEquals(2, b2.possibleMoves(new Position(4, 7)).length);
        assertTrue(Arrays.asList(b2.possibleMoves(new Position(5, 5))).contains(new Position(4, 6)));
        assertFalse(Arrays.asList(b2.possibleMoves(new Position(5, 5))).contains(new Position(6, 6)));
        assertTrue(Arrays.asList(b2.possibleMoves(new Position(4, 6))).contains(new Position(5, 5)));
        assertFalse(Arrays.asList(b2.possibleMoves(new Position(4, 6))).contains(new Position(3, 5)));



    }

    @Test
    void testRewind() {
        Board b = new Board();
        b.undo();
        b.movePiece(new Position(1, 1), new Position(1, 2));
        assertSame(b.getPiece(new Position(1, 2)).type, PieceType.PAWN);
        b.undo();
        assertSame(b.getPiece(new Position(1, 1)).type, PieceType.PAWN);
        assertNull(b.getPiece(new Position(1, 2)));

        ArrayList<Piece> layout = new ArrayList<>();
        layout.add(new Piece(PieceType.QUEEN, new Position(2, 4), true));
        layout.add(new Piece(PieceType.ROOK, new Position(2, 7), false));
        Board b2 = new Board(layout);
        b2.movePiece(new Position(2, 4), new Position(2, 7));
        assertTrue(b2.getPiece(new Position(2, 4)) == null);
        assertTrue(b2.getPiece(new Position(2, 7)).type == PieceType.QUEEN && b2.getPiece(new Position(2, 7)).white);
        b2.undo();
        assertTrue(b2.getPiece(new Position(2, 4)).type == PieceType.QUEEN && b2.getPiece(new Position(2, 4)).white);
        assertTrue(b2.getPiece(new Position(2, 7)).type == PieceType.ROOK && !b2.getPiece(new Position(2, 7)).white);
    }
    @Test
    void testCheckMate() {
        ArrayList<Piece> layout = new ArrayList<Piece>();
        layout.add(new Piece(PieceType.KING, new Position(5, 7), false));
        layout.add(new Piece(PieceType.KING, new Position(0, 0), true));
        layout.add(new Piece(PieceType.ROOK, new Position(0, 7), true));
        layout.add(new Piece(PieceType.QUEEN, new Position(1, 6), true));
        layout.add(new Piece(PieceType.QUEEN, new Position(3, 2), false));

        Board b = new Board(layout);
        b.drawBoard();
        assertEquals(0, b.possibleMoves(new Position(5, 7)).length);
        assertTrue(Arrays.asList(b.possibleMoves(new Position(3, 2))).contains(new Position(3, 7)));
        assertEquals(1, b.possibleMoves(new Position(3, 2)).length);
    }
}