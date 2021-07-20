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
        b2.drawBoard();
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
        assertEquals(3, b2.possibleMoves(new Position(4, 7)).length);
        assertTrue(Arrays.asList(b2.possibleMoves(new Position(5, 5))).contains(new Position(4, 6)));
        assertFalse(Arrays.asList(b2.possibleMoves(new Position(5, 5))).contains(new Position(6, 6)));
        assertTrue(Arrays.asList(b2.possibleMoves(new Position(4, 6))).contains(new Position(5, 5)));
        assertFalse(Arrays.asList(b2.possibleMoves(new Position(4, 6))).contains(new Position(3, 5)));

        ArrayList<Piece> layout = new ArrayList<>();
        layout.add(new Piece(PieceType.KING, new Position(0, 0), true));
        layout.add(new Piece(PieceType.KING, new Position(7, 7), false));
        layout.add(new Piece(PieceType.PAWN, new Position(3, 1), true));
        layout.add(new Piece(PieceType.ROOK, new Position(2, 2), false));
        layout.add(new Piece(PieceType.BISHOP, new Position(3, 2), false));
        layout.add(new Piece(PieceType.KNIGHT, new Position(4, 2), true));

        Board b3 = new Board(layout);
        assertEquals(1, b3.possibleMoves(new Position(3, 1)).length);
        assertTrue(Arrays.asList(b3.possibleMoves(new Position(3, 1))).contains(new Position(2, 2)));


    }

    @Test
    void testRewind() {
        Board b = new Board();
        b.undo();
        b.movePiece(new Position(1, 1), new Position(1, 2), true);
        assertSame(b.getPiece(new Position(1, 2)).type, PieceType.PAWN);
        b.undo();
        assertSame(b.getPiece(new Position(1, 1)).type, PieceType.PAWN);
        assertNull(b.getPiece(new Position(1, 2)));

        ArrayList<Piece> layout = new ArrayList<>();
        layout.add(new Piece(PieceType.QUEEN, new Position(2, 4), true));
        layout.add(new Piece(PieceType.ROOK, new Position(2, 7), false));
        Board b2 = new Board(layout);
        b2.movePiece(new Position(2, 4), new Position(2, 7), true);
        assertNull(b2.getPiece(new Position(2, 4)));
        assertTrue(b2.getPiece(new Position(2, 7)).type == PieceType.QUEEN && b2.getPiece(new Position(2, 7)).white);
        b2.undo();
        assertTrue(b2.getPiece(new Position(2, 4)).type == PieceType.QUEEN && b2.getPiece(new Position(2, 4)).white);
        assertTrue(b2.getPiece(new Position(2, 7)).type == PieceType.ROOK && !b2.getPiece(new Position(2, 7)).white);
    }
    @Test
    void testCheckMate() {
        ArrayList<Piece> layout = new ArrayList<>();
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

        ArrayList<Piece> layout2 = new ArrayList<>();
        layout2.add(new Piece(PieceType.KING, new Position(0, 7), false));
        layout2.add(new Piece(PieceType.QUEEN, new Position(3, 7), false));
        layout2.add(new Piece(PieceType.KING, new Position(3, 0), true));
        layout2.add(new Piece(PieceType.QUEEN, new Position(3, 1), true));

        Board b2 = new Board(layout2);
        assertEquals(6, b2.possibleMoves(new Position(3, 1)).length);

        ArrayList<Piece> layout3 = new ArrayList<>();
        layout3.add(new Piece(PieceType.KING, new Position(0, 7), false));
        layout3.add(new Piece(PieceType.QUEEN, new Position(3, 1), false));
        layout3.add(new Piece(PieceType.ROOK, new Position(2, 7), false));
        layout3.add(new Piece(PieceType.ROOK, new Position(4, 7), false));
        layout3.add(new Piece(PieceType.KING, new Position(3, 0), true));

        Board b3 = new Board(layout3);

        assertEquals(1, b3.possibleMoves(new Position(3, 0)).length);
        assertTrue(Arrays.asList(b3.possibleMoves(new Position(3, 0))).contains(new Position(3, 1)));

        ArrayList<Piece> layout4 = new ArrayList<>();
        layout4.add(new Piece(PieceType.KING, new Position(0, 7), false));
        layout4.add(new Piece(PieceType.QUEEN, new Position(3, 1), false));
        layout4.add(new Piece(PieceType.ROOK, new Position(2, 7), false));
        layout4.add(new Piece(PieceType.ROOK, new Position(4, 7), false));
        layout4.add(new Piece(PieceType.KING, new Position(3, 0), true));
        layout4.add(new Piece(PieceType.ROOK, new Position(7, 1), true));

        Board b4 = new Board(layout4);
        assertEquals(1, b4.possibleMoves(new Position(3, 0)).length);
        assertTrue(Arrays.asList(b4.possibleMoves(new Position(3, 0))).contains(new Position(3, 1)));

        assertEquals(1, b4.possibleMoves(new Position(7, 1)).length);
        assertTrue(Arrays.asList(b4.possibleMoves(new Position(7, 1))).contains(new Position(3, 1)));

        ArrayList<Piece> layout5 = new ArrayList<>();
        layout5.add(new Piece(PieceType.KING, new Position(0, 7), false));
        layout5.add(new Piece(PieceType.QUEEN, new Position(7, 1), false));
        layout5.add(new Piece(PieceType.KING, new Position(3, 0), true));
        layout5.add(new Piece(PieceType.ROOK, new Position(0, 1), true));

        Board b5 = new Board(layout5);

        assertEquals(2, b5.possibleMoves(new Position(0, 7)).length);
        assertEquals(1, b5.possibleMoves(new Position(7, 1)).length);
        assertTrue(Arrays.asList(b5.possibleMoves(new Position(7, 1))).contains(new Position(0, 1)));
    }

    @Test
    void testIncludeOwnPieces() {
        ArrayList<Piece> layout = new ArrayList<>();
        layout.add(new Piece(PieceType.KING, new Position(0, 7), false));
        layout.add(new Piece(PieceType.KING, new Position(0, 0), true));
        layout.add(new Piece(PieceType.ROOK, new Position(3, 0), true));
        layout.add(new Piece(PieceType.ROOK, new Position(2, 0), true));
        layout.add(new Piece(PieceType.BISHOP, new Position(1, 0), true));

        Board b = new Board(layout);
        b.drawBoard();
        assertEquals(12, b.possibleMoves(b.getPiece(new Position(3, 0)), true, true, true).length);
        assertEquals(9, b.possibleMoves(b.getPiece(new Position(2, 0)), true, true, true).length);
        assertEquals(3, b.possibleMoves(b.getPiece(new Position(0, 0)), true, true, true).length);
    }

    @Test
    void testScoring() {
        Board b0 = new Board();
        assertEquals(0, b0.scoreBoard());
        assertEquals(0, b0.scoreBoard());
        ArrayList<Piece> layout = new ArrayList<>();
        layout.add(new Piece(PieceType.KING, new Position(0, 0), true));
        layout.add(new Piece(PieceType.KING, new Position(7, 7), false));
        layout.add(new Piece(PieceType.ROOK, new Position(7, 6), false));
        layout.add(new Piece(PieceType.QUEEN, new Position(7, 0), true));
        Board b = new Board(layout);
        b.drawBoard();
    }
}