package gabs.domain.entity;

import java.util.concurrent.atomic.AtomicLong;

public class ProductNumberGenerator {

    private static final AtomicLong savingsSeq = new AtomicLong(1000000);
    private static final AtomicLong checkingSeq = new AtomicLong(1000000);

    public static String generateSavingsAccountNumber() {
        return "53" + String.format("%08d", savingsSeq.getAndIncrement());
    }
    public static String generateCheckingAccountNumber() {
        return "33" + String.format("%08d", checkingSeq.getAndIncrement());
    }
}
