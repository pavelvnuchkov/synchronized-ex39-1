import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) {
        List<Thread> threadList = new ArrayList<>();

        Thread thread = new Thread(() -> {
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        System.out.println("Программа завершила свою работу!");
                        break;
                    }
                    int keyMaxValue = 0;
                    int maxValue = 0;
                    for (Integer key : sizeToFreq.keySet()) {
                        if (maxValue < sizeToFreq.get(key)) {
                            maxValue = sizeToFreq.get(key);
                            keyMaxValue = key;
                        }
                    }
                    System.out.println("Частота - " + keyMaxValue + " Максимум - " + sizeToFreq.get(keyMaxValue));

                }
            }
        });
        thread.start();
        for (int i = 0; i < 1000; i++) {
            Thread threadCounter = new Thread(() -> {
                countCommand(generateRoute("RLRFR", 100));
            });
            threadCounter.start();
            threadList.add(threadCounter);
        }


        for (Thread threads : threadList) {
            try {
                threads.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        printMap();
        thread.interrupt();
    }

    public static void countCommand(String route) {
        int count = 0;
        for (int i = 0; i < route.length(); i++) {
            if (route.charAt(i) == 'R') {
                count++;
            }
        }
        synchronized (sizeToFreq) {
            if (sizeToFreq.containsKey(count)) {
                sizeToFreq.put(count, sizeToFreq.get(count) + 1);
            } else {
                sizeToFreq.put(count, 1);
            }
            sizeToFreq.notify();
        }
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static void printMap() {
        int keyMaxValue = 0;
        int maxValue = 0;
        for (Integer key : sizeToFreq.keySet()) {
            if (maxValue < sizeToFreq.get(key)) {
                maxValue = sizeToFreq.get(key);
                keyMaxValue = key;
            }
        }
        System.out.println("Самое частое количество повторений " + keyMaxValue +
                " (встретилось " + sizeToFreq.get(keyMaxValue) + " раз)");
        System.out.println("Другие размеры:");
        for (Integer key : sizeToFreq.keySet()) {
            if (key != keyMaxValue) {
                System.out.println(" - " + key + " (" + sizeToFreq.get(key) + ") раз");
            }
        }
    }

}
