package ru.nsu.fit.huawei.rgb.control;

import ru.nsu.fit.huawei.rgb.model.Coords;
import ru.nsu.fit.huawei.rgb.model.Game;
import ru.nsu.fit.huawei.rgb.model.Point;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SimpleController {

    private final Game game;

    private int stepNumber = 0;

    private int totalScore = 0;

    private int lastStepScore = 0;

    private Point.Color lastStepColor = null;

    private Coords lastStepCoords = null;

    private int lastStepBalls = 0;

    public SimpleController(List<String> textField) {
        if (textField.size() != Game.HEIGHT) {
            throw new IllegalArgumentException("Field height != " + Game.HEIGHT);
        }

        List<List<Point>> rows = new ArrayList<>(Game.HEIGHT);
        for (String line : textField) {
            if (line.length() != Game.WIDTH) {
                throw new IllegalArgumentException("Row width != " + Game.WIDTH);
            }

            List<Point> row = new ArrayList<>(Game.WIDTH);
            for (int i = 0; i < line.length(); i++) {
                char ColorSymbol = line.charAt(i);
                Point.Color color;
                switch (ColorSymbol) {
                    case 'R' -> color = Point.Color.Red;
                    case 'G' -> color = Point.Color.Green;
                    case 'B' -> color = Point.Color.Blue;
                    default -> throw new IllegalArgumentException("Unexpected color symbol: " + ColorSymbol);
                }
                row.add(new Point(color));
            }
            rows.add(row);
        }
        game = new Game(rows);
    }

    private Coords findBiggestCluster() {
        List<Coords> visited = new ArrayList<>(Game.WIDTH * Game.HEIGHT);
        int maxSize = 0;
        Coords refCoords = null;
        for (int i = 0; i < Game.WIDTH; i++) {
            for (int j = Game.HEIGHT - 1; j >= 0; j--) {
                if (visited.contains(new Coords(i, j))) {
                    continue;
                }
                List<Coords> cluster = game.getCluster(i, j);
                cluster.sort(Comparator.comparing(Coords::getY).reversed().thenComparing(Coords::getX));
                if (cluster.size() > 1 && cluster.size() > maxSize) {
                    maxSize = cluster.size();
                    refCoords = cluster.get(0);
                }
                visited.addAll(cluster);
            }
        }
        return refCoords;
    }

    public boolean step() {
        Coords biggestClusterPoint = findBiggestCluster();
        if (biggestClusterPoint == null) {
            return false;
        }

        Point.Color color = game.getColor(biggestClusterPoint.getX(), biggestClusterPoint.getY());

        int ballsPopped = game.pop(biggestClusterPoint.getX(), biggestClusterPoint.getY());
        if (ballsPopped < 2) {
            return false;
        }

        stepNumber++;
        lastStepCoords = biggestClusterPoint;
        lastStepColor = color;
        lastStepBalls = ballsPopped;
        lastStepScore = (ballsPopped - 2) * (ballsPopped - 2);
        totalScore += lastStepScore;

        if (game.getPointsCount() == 0) {
            totalScore += 1000;
        }
        return true;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getLastStepScore() {
        return lastStepScore;
    }

    public Point.Color getLastStepColor() {
        return lastStepColor;
    }

    public int getLastStepBalls() {
        return lastStepBalls;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public Coords getLastStepCoords() {
        return lastStepCoords;
    }

    public long getPointsRemaining() {
        return game.getPointsCount();
    }

    public void printField() {
        for (int i = 0; i < Game.HEIGHT; i++) {
            for (int j = 0; j < Game.WIDTH; j++) {
                System.out.print(game.getColor(j, i).getMark());
            }
            System.out.println();
        }
    }
}
