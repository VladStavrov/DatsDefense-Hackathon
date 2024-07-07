package org.example.scripts;

public class Script2 implements Runnable {
    @Override
    public void run() {
        System.out.println("Script2 is running...");
        // Симуляция работы скрипта
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("Script2 interrupted.");
        }
    }
}
