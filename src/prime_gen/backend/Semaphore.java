package prime_gen.backend;

public class Semaphore {

    protected int value = 0 ;

    protected Semaphore() { value = 0 ; }

    protected Semaphore(int initial) { value = initial ; }

    public synchronized void P() {  // acquire  // lock

        value-- ;
        if (value < 0)
            try { wait() ; } catch(  InterruptedException e ) { }
    }

    public synchronized void V() {  //release  // unlock
        value++ ; if (value <= 0) notify() ;
    }
}
