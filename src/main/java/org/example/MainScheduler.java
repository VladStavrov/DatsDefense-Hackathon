package org.example;

import org.example.scripts.Script1;
import org.example.scripts.Script2;


import java.util.HashMap;
import java.util.Map;

public class MainScheduler {
    //TODO Решить, что будем делать с интервалом
    private static final long INTERVAL = 5000;
    private final Map<String, ScriptRunner> scripts = new HashMap<>();

    public static void main(String[] args) {
        MainScheduler manager = new MainScheduler();
        manager.addScript("Script1", new Script1());
        manager.addScript("Script2", new Script2());
        MainCommands mainCommands = new MainCommands();
        mainCommands.zombieDef();
        mainCommands.fetchWorldData();

        manager.startAll();
    }

    public void addScript(String name, Runnable script) {
        scripts.put(name, new ScriptRunner(name, script));
    }

    public void startAll() {
        for (ScriptRunner runner : scripts.values()) {
            new Thread(runner).start();
        }
    }

    private record ScriptRunner(String name, Runnable script) implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    script.run();
                    Thread.sleep(INTERVAL);
                } catch (InterruptedException e) {
                    System.out.println("Script " + name + " interrupted. Restarting...");
                } catch (Exception e) {
                    System.out.println("Script " + name + " failed with exception: " + e.getMessage());
                }
                System.out.println("Restarting script " + name);
            }
        }
    }
}
