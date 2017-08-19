package prime_gen.backend;

public class PrimeGen extends Thread {  // producer thread

    private final Buffer buffer;
    private int N;
    private static int primeGenID;
    private int count;

    public PrimeGen(int N, Buffer buffer){
        this.N = N;
        this.buffer = buffer;
        primeGenID++;
    }

    @Override
    public void run () {
        synchronized (buffer)
        {
            // generate prime numbers between 0 and N
            if (N <= 1)
            {
                // zero, one or negative integers can not be prime.
                // Thread exits
                buffer.close();
                return; //ends the thread
            }

            // the number 2 is a prime number.
            // However, the following algorithm will not detect it (as we are assuming there that all even numbers cannot be prime).
            // add it manually:
            add(2);

            for (int i = 3; i <= N; i += 2)
            {
                boolean flag = false;
                for (int j = 2; j <= i / 2; j++) {
                    if ((i % j) == 0) {
                        flag = true;
                        break;
                    }
                }
                if (flag == false)
                {
                    // a prime number is found equals (i)
                    // add (i)'s value to the buffer
                    add(i);

                    count++;
                }
            }
            buffer.close();
        }
    }

    /**
     * Add a prime number to buffer.
     * must be called within a synchronized block.
     * @param i A prime number
     */
    private void add(int i) {
        // producer thread waits while buffer is full
        while (buffer.checkFull()) {
            try {
                System.out.println("Buffer is full. Producer is waiting... ");
                buffer.wait();
                System.out.println("Producer resumes.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // add (i)'s value to the buffer
        buffer.addPrimeNumber(i);

        buffer.notify();
    }

    public int getPrimeNumbersCount() {
        return this.count;
    }
}
