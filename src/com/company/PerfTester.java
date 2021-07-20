package com.company;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class PerfTester implements Runnable {
    static AtomicInteger count = new AtomicInteger(1000000);

    public static void main(String[] args) throws InterruptedException {
        System.out.println(Runtime.getRuntime().availableProcessors());
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i=0; i<Runtime.getRuntime().availableProcessors(); i++) {

            threads.add(new Thread(new PerfTester()));
            threads.get(threads.size() - 1).start();
        }
        Instant start = Instant.now();
        for (Thread t : threads) {
            t.join();
        }
        Instant end = Instant.now();
        System.out.println(Duration.between(start, end).getSeconds());
    }

    @Override
    public void run() {
        Board b = new Board();
        do {
            Piece[] pieces = b.copyPieces();
            for (Piece p : pieces) {
                b.possibleMoves(p.position);
            }
            b.scoreBoard();

        } while (count.decrementAndGet() >= 0);

    }
}
