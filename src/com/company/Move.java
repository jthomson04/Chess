package com.company;

public record Move(Position from, Position to) implements Copyable<Move> {
    @Override
    public Move copy() {
        return new Move(from(), to());
    }
}
