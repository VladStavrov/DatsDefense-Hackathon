package org.example.scripts;

import org.example.models.play.Build;

import java.util.Comparator;

public class BuildDistanceComparator implements Comparator<Build> {
    private final int startX;
    private final int startY;

    public BuildDistanceComparator(int startX, int startY) {
        this.startX = startX;
        this.startY = startY;
    }

    @Override
    public int compare(Build a, Build b) {
        double distA = calculateDistance(startX, startY, a.getX(), a.getY());
        double distB = calculateDistance(startX, startY, b.getX(), b.getY());
        return Double.compare(distA, distB);
    }

    private double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}