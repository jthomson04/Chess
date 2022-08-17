package com.company;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void possibleMoves() {

        // test the basics of generating possible moves for a given team

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
        b2.drawBoard(null);
        assertArrayEquals(b1.possibleMoves(new Position(1, 1)), new Position[]{new Position(1, 2), new Position(1, 3)});
        assertEquals(0, b1.possibleMoves(new Position(3, 0)).length);
        assertArrayEquals(b1.possibleMoves(new Position(1, 0)), new Position[]{new Position(0, 2), new Position(2, 2)});
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

        // test rewinding of the board state

        Board b = new Board();
        b.undo();
        b.movePiece(new Position(1, 1), new Position(1, 2), false);
        assertSame(b.getPiece(new Position(1, 2)).type, PieceType.PAWN);
        b.undo();
        assertSame(b.getPiece(new Position(1, 1)).type, PieceType.PAWN);
        assertNull(b.getPiece(new Position(1, 2)));

        ArrayList<Piece> layout = new ArrayList<>();
        layout.add(new Piece(PieceType.QUEEN, new Position(2, 4), true));
        layout.add(new Piece(PieceType.ROOK, new Position(2, 7), false));
        Board b2 = new Board(layout);
        b2.movePiece(new Position(2, 4), new Position(2, 7), false);
        assertNull(b2.getPiece(new Position(2, 4)));
        assertTrue(b2.getPiece(new Position(2, 7)).type == PieceType.QUEEN && b2.getPiece(new Position(2, 7)).white);
        b2.undo();
        assertTrue(b2.getPiece(new Position(2, 4)).type == PieceType.QUEEN && b2.getPiece(new Position(2, 4)).white);
        assertTrue(b2.getPiece(new Position(2, 7)).type == PieceType.ROOK && !b2.getPiece(new Position(2, 7)).white);

        ArrayList<Piece> layout2 = new ArrayList<>();
        layout2.add(new Piece(PieceType.ROOK, new Position(3, 3), true));
        layout2.add(new Piece(PieceType.QUEEN, new Position(3, 6), false));

        Board b3 = new Board(layout2);
        b3.movePiece(new Position(3, 3), new Position(3, 6), false);
        b3.movePiece(new Position(3, 6), new Position(0, 6), false);

        assertNull(b3.getPiece(new Position(3, 6)));
        assertSame(b3.getPiece(new Position(0, 6)).type, PieceType.ROOK);

        b3.undo();

        assertEquals(1, b3.pieces.size());
        assertSame(b3.getPiece(new Position(3, 6)).type, PieceType.ROOK);
        assertNull(b3.getPiece(new Position(3, 3)));

        b3.movePiece(new Position(3, 6), new Position(7, 6), false);
        assertEquals(1, b3.pieces.size());
        assertSame(b3.getPiece(new Position(7, 6)).type, PieceType.ROOK);


        b3.undo();
        b3.undo();

        assertSame(b3.getPiece(new Position(3, 3)).type, PieceType.ROOK);
        assertSame(b3.getPiece(new Position(3, 6)).type, PieceType.QUEEN);

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
        b.drawBoard(null);
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
        b.drawBoard(null);
        assertEquals(12, b.possibleMoves(b.getPiece(new Position(3, 0)), true, true).length);
        assertEquals(9, b.possibleMoves(b.getPiece(new Position(2, 0)), true, true).length);
        assertEquals(3, b.possibleMoves(b.getPiece(new Position(0, 0)), true, true).length);
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
        b.drawBoard(null);
    }

    @Test
    void testPawnAdvancement() {

        // check that pawns get converting to queens when they get to the other side

        ArrayList<Piece> layout = new ArrayList<>();
        layout.add(new Piece(PieceType.KING, new Position(2, 2), true));
        layout.add(new Piece(PieceType.KING, new Position(6, 6), false));
        layout.add(new Piece(PieceType.PAWN, new Position(3, 6), true));
        layout.add(new Piece(PieceType.PAWN, new Position(1, 1), false));
        Board b = new Board(layout);
        b.drawBoard(null);
        b.movePiece(new Position(3, 6), new Position(3, 7), false);
        b.movePiece(new Position(1, 1), new Position(1, 0), false);

        assertSame(b.getPiece(new Position(3, 7)).type, PieceType.QUEEN);
        assertSame(b.getPiece(new Position(1, 0)).type, PieceType.QUEEN);

        b.undo();
        assertNull(b.getPiece(new Position(1, 0)));
        assertSame(b.getPiece(new Position(3, 7)).type, PieceType.QUEEN);
        b.undo();
        assertNull(b.getPiece(new Position(1, 0)));
        assertNull(b.getPiece(new Position(3, 7)));
        assertSame(b.getPiece(new Position(3, 6)).type, PieceType.PAWN);
        assertSame(b.getPiece(new Position(1, 1)).type, PieceType.PAWN);
    }

    @Test
    void testCastleMoveCheck() {

        // test castling, and verify that if the king or rook has already been moved, castling isn't possible

        ArrayList<Piece> layout = new ArrayList<>();
        layout.add(new Piece(PieceType.KING, new Position(3, 0), true));
        layout.add(new Piece(PieceType.ROOK, new Position(0, 0), true));
        layout.add(new Piece(PieceType.ROOK, new Position(7, 0), true));
        layout.add(new Piece(PieceType.KING, new Position(3, 7), false));
        layout.add(new Piece(PieceType.ROOK, new Position(0, 7), false));
        layout.add(new Piece(PieceType.ROOK, new Position(7, 7), false));
        Board b1 = new Board(layout);
        assertArrayEquals(new boolean[]{true, true}, b1.castles(true));
        assertArrayEquals(new boolean[]{true, true}, b1.castles(false));
        b1.drawBoard(null);

        b1.movePiece(new Position(0, 0), new Position(0, 1), false);
        assertTrue(b1.hasMoved.get(new Position(0, 0)));
        assertArrayEquals(new boolean[]{false, true}, b1.castles(true));
        b1.movePiece(new Position(3, 0), new Position(3, 1), false);
        assertArrayEquals(new boolean[]{false, false}, b1.castles(true));
        b1.undo();
        assertArrayEquals(new boolean[]{false, true}, b1.castles(true));
        b1.undo();
        assertArrayEquals(new boolean[]{true, true}, b1.castles(true));
        assertFalse(b1.hasMoved.get(new Position(0, 0)));

        b1.movePiece(new Position(0, 0), new Position(0, 1), false);
        assertTrue(b1.hasMoved.get(new Position(0, 0)));
        b1.movePiece(new Position(7, 0), new Position(0, 0), false);
        assertTrue(b1.hasMoved.get(new Position(0, 0)));
        b1.movePiece(new Position(0, 0), new Position(1, 0), false);
        assertTrue(b1.hasMoved.get(new Position(0, 0)));

        b1.undo();
        assertTrue(b1.hasMoved.get(new Position(0, 0)));
        b1.undo();
        assertTrue(b1.hasMoved.get(new Position(0, 0)));
        b1.undo();
        assertFalse(b1.hasMoved.get(new Position(0, 0)));
    }

    @Test
    void testCastle() {
        ArrayList<Piece> layout = new ArrayList<>();
        layout.add(new Piece(PieceType.KING, new Position(3, 0), true));
        layout.add(new Piece(PieceType.ROOK, new Position(0, 0), true));
        layout.add(new Piece(PieceType.ROOK, new Position(7, 0), true));
        layout.add(new Piece(PieceType.KING, new Position(3, 7), false));
        layout.add(new Piece(PieceType.ROOK, new Position(0, 7), false));
        layout.add(new Piece(PieceType.ROOK, new Position(7, 7), false));
        Board b1 = new Board(layout);
        b1.castle(true, true);
        b1.castle(false, true);

        assertSame(b1.getPiece(new Position(1, 7)).type, PieceType.KING);
        assertSame(b1.getPiece(new Position(1, 0)).type, PieceType.KING);
        assertSame(b1.getPiece(new Position(2, 7)).type, PieceType.ROOK);
        assertSame(b1.getPiece(new Position(2, 0)).type, PieceType.ROOK);

        b1.undo();
        b1.undo();

        assertNull(b1.getPiece(new Position(1, 7)));
        assertNull(b1.getPiece(new Position(2, 7)));
        assertNull(b1.getPiece(new Position(1, 0)));
        assertNull(b1.getPiece(new Position(2, 0)));


    }

}