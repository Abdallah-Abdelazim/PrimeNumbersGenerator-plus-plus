package prime_gen.backend;

import java.util.ArrayList;
import java.util.List;

public class Buffer {

    private List<Integer> primeNumbers;
    public int BUFFER_SIZE = 100;  // default buffer size is 100
    private boolean closed = false;

    public Buffer() {
        this.primeNumbers = new ArrayList<>(BUFFER_SIZE);
    }

    public Buffer(int bufferSize) {
        this.BUFFER_SIZE = bufferSize;
        this.primeNumbers = new ArrayList<>(BUFFER_SIZE);
    }

    public int currentSize() {
        return primeNumbers.size();
    }

    public boolean checkFull() {
        return currentSize() == BUFFER_SIZE;
    }

    public boolean checkEmpty() {
        return primeNumbers.isEmpty();
    }

    /**
     * adds items at the end of the buffer
     */
    public void addPrimeNumber(int aPrimeNumber) {
        primeNumbers.add(aPrimeNumber);
    }

    /**
     * removes Items from the beginning of the buffer
     */
    public int removePrimeNumber() {
        if (!primeNumbers.isEmpty()) {
            return primeNumbers.remove(0);
        }
        return -1;

    }

    public void close() {
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }
}
