package prime_gen.backend;

import java.io.FileWriter;
import java.io.IOException;

public class OutputWriter extends Thread {  // consumer thread

    private Buffer buffer;
    private static int outputWriterID;
    private FileWriter fWriter;

    public OutputWriter(FileWriter fWriter, Buffer buffer) {
        this.fWriter = fWriter;
        this.buffer = buffer;
    }

    @Override
    public void run() {
        synchronized (buffer)
        {
            while (true) {

                if (buffer.checkEmpty() && buffer.isClosed()) {  // this is the condition that indicates that this thread has finished.
                    return;            // The condition explanation is: if the buffer is closed then we don't expect any more items to be added
                    // , however if it is empty then this is the end and there is no any more prime numbers and there will not be.
                }

                // consumer thread waits while list
                // is empty
                while (buffer.checkEmpty()){
                    try {
                        System.out.println("Buffer is empty. Consumer is waiting... ");
                        buffer.wait();
                        System.out.println("Consumer resumes.");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                //to retrieve number from buffer to write to file
                int aPrime = buffer.removePrimeNumber();  // there's a race condition that happens here and causes the program to fail for N > 600

                // write the prime number to the file
                try {
                    fWriter.write("\"" + aPrime + "\",");
                    // System.out.println("\"" + aPrime + "\",");  // test
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Wake up producer thread
                buffer.notify();
            }
        }
    }

}
