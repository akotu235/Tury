### [Laboratorium 4](https://artemis.wszib.edu.pl/~funika/pwir/tw/lab4/)
# Temat: Współbieżność w Javie
### **Andrzej Kotulski**
#### 24.11.2024r.



---

## 1. Co było do zrobienia

W ramach laboratorium należało rozwiązać dwa problemy związane z programowaniem współbieżnym w Javie:

1. **Problem „Tury”** — należało uruchomić dwa wątki (T1 i T2), które wypisują na ekran odpowiednie numery. Kolejność wypisywania miała być naprzemienna: 1,2,1,2, …
2. **Problem producentów i konsumentów** — należało zaimplementować mechanizm współdzielonego bufora dla jednego producenta i jednego konsumenta, wykorzystując mechanizmy z pakietu Java Concurrency Utilities (JCU). Dodatkowo należało zmierzyć czas wykonania za pomocą `System.nanoTime()`, wstawić do kodu `Thread.sleep()` oraz porównać wydajność rozwiązania względem implementacji opartej na mechanizmach `wait()` i `notify()`.

## 2. Podejście do rozwiązania problemu

1. **Problem „Tury”** został rozwiązany poprzez wykorzystanie klasy synchronizującej `Sync`, która przechowuje aktualną turę. Wątki `T1` i `T2` działają w pętli, sprawdzając czy ich tura jest aktywna. Synchronizacja odbywa się za pomocą `synchronized`, `wait()` i `notify()`.
2. **Problem producentów i konsumentów** został rozwiązany na dwa sposoby:
    - Tradycyjna implementacja z `wait()` i `notify()`, która wymaga ręcznej obsługi synchronizacji.
    - Implementacja z wykorzystaniem `BlockingQueue`, która upraszcza obsługę współdzielonego bufora.

## 3. Fragmenty kodu

## Program `Tury`:

### Klasa `Sync`:

```java
class Sync {
    public int tura = 1;
}
```

### Klasa `T1`:

```java
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
```

### Klasa `T2`:

```java
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
```

### Klasa `Tury`:

```java
public class Tury {
    public static void main(String[] args) {
        Sync s = new Sync();
        T1 t1 = new T1(s);
        T2 t2 = new T2(s);
        t1.start();
        t2.start();
    }
}
```

---

## Program `PKmon`:

### Klasa `BuforV2` (BlockingQueue):

```java
public class BuforV2 implements Bufor {
    private final BlockingQueue<Integer> queue;

    public BuforV2(int size) {
        this.queue = new LinkedBlockingQueue<>(size);
    }

    @Override
    public void put(int i) {
        try {
            queue.put(i);
            System.out.println("P -> " + i);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Wątek przerwany podczas dodawania do kolejki.");
        }
    }

    @Override
    public int get() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Wątek przerwany podczas pobierania z kolejki.");
            return -1;
        }
    }
}
```

*Pełny kod programów znajduje się w [załącznikach](#7-załączniki)*

## 4. Wyniki

1. **Problem „Tury”**:
   - Wątki poprawnie wypisywały naprzemiennie liczby `1` i `2`.
   - Mechanizm `wait()` i `notify()` zapewnił synchronizację między wątkami.

2. **Problem producentów i konsumentów**:
   - **BuforV1** (oparty na `wait()/notify()`) wymagał precyzyjnej synchronizacji i był podatny na błędy.
   - **BuforV2** (oparty na `BlockingQueue`) był bardziej niezawodny i prostszy w implementacji.
   - Pomiar czasu wykazał, że `BuforV2` działa wydajniej, szczególnie przy dużej liczbie operacji.

### Pomiar czasu w implementacji producent-konsument

Poniższa tabela przedstawia zależność między czasem `sleep` a całkowitym czasem wykonania programu dla dwóch implementacji bufora:

| `sleepTime` (ms) | Czas całkowity (ms) – **BuforV1** (`wait()/notify()`) | Czas całkowity (ms) – **BuforV2** (`BlockingQueue`) | 
|------------------|-------------------------------------------------------|-----------------------------------------------------|
| 0                | **35**                                                | **26**                                              | 
| 10               | **1734**                                              | **1662**                                            | 
| 50               | **6316**                                              | **5986**                                            |
| 100              | **10962**                                             | **10903**                                           | 

### Opis implementacji

- **BuforV1** – implementacja oparta na `wait()` i `notify()`, wymagająca synchronizacji na obiekcie monitora. Jest trudniejsza w zarządzaniu i może prowadzić do zakleszczeń lub nieefektywnego działania przy nieodpowiedniej synchronizacji.
- **BuforV2** – implementacja wykorzystująca `BlockingQueue`, zapewniająca automatyczną synchronizację i lepszą wydajność w środowisku wielowątkowym.

## 5. Wnioski

Eksperymenty pokazały, że wykorzystanie `BlockingQueue` upraszcza implementację i poprawia stabilność programu w porównaniu do mechanizmu `wait()/notify()`. Pomiar czasu wykazał niewielką przewagę wydajności `BlockingQueue`, zwłaszcza przy dużej liczbie operacji. Dodatkowo, dla obu implementacji bufora, przy wyższych wartościach sleep, wątki wykonywały operacje naprzemiennie.

## 6. Bibliografia

1. **Java Platform, Standard Edition Documentation** - Oracle. Dostępne online: <https://docs.oracle.com/javase/8/docs/>
2. **Java Concurrency Documentation** - Oracle. Dostępne online: <https://docs.oracle.com/javase/1.5.0/docs/guide/concurrency/overview.html>

## 7. Załączniki

1. **Repozytorium kodu źródłowego** – Program `PKmon`. Dostępne online: <https://github.com/akotu235/PKmon>

2. **Repozytorium kodu źródłowego** – Program `Tury`. Dostępne online: <https://github.com/akotu235/Tury>

3. **Wersja online sprawozdania** – Bieżąca wersja dokumentu. Dostępne online: <https://github.com/akotu235/Tury/blob/master/report/report.md>


