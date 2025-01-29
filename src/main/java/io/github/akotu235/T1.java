package io.github.akotu235;

class T1 extends Thread {
    private final Sync sync;

    public T1(Sync sync) {
        this.sync = sync;
    }

    public void run() {
        for (int i = 0; i < 10; i++) {
            synchronized (sync) {
                while (sync.tura != 1) {
                    try {
                        sync.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(1);
                sync.tura = 2;
                sync.notify();
            }
        }
    }
}
