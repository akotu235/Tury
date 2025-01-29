package io.github.akotu235;

class T2 extends Thread {
    private final Sync sync;

    public T2(Sync sync) {
        this.sync = sync;
    }

    public void run() {
        for (int i = 0; i < 10; i++) {
            synchronized (sync) {
                while (sync.tura != 2) {
                    try {
                        sync.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(2);
                sync.tura = 1;
                sync.notify();
            }
        }
    }
}
