package io.github.akotu235;

public class Tury {
    public static void main(String args[]) {
        Sync s = new Sync();
        T1 t1 = new T1(s);
        T2 t2 = new T2(s);
        t1.start();
        t2.start();
    }
}