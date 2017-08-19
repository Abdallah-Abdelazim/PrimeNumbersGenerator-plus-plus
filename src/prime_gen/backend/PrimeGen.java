package prime_gen.backend;

public class PrimeGen extends Thread {  // producer thread

    private Buffer buffer;
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
            if (N < 1)
            {
                // There are no prime numbers between 0 and N
                // Thread exits
                buffer.close();
                return; //ends the thread
            }
            else if ( N % 2 == 0)
            {
                // if N is an even number then it will not be a prime number
                // because all prime numbers are divisible by 2
                N--;
            }

            for (int i = 1; i <= N; i += 2)
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
                    // producer thread waits while buffer is full
                    while (buffer.checkFull())
                    {
                        try {
                            System.out.println("Buffer is full. Producer is waiting... ");
                            buffer.wait();
                            System.out.println("Producer resumes.");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    // a prime number is found equals (i)
                    // add (i)'s value to the buffer
                    buffer.addPrimeNumber(i);

                    buffer.notify();
                    count++;
                }
            }
            buffer.close();
        }
    }

    public int getPrimeNumbersCount() {
        return this.count;
    }
}
