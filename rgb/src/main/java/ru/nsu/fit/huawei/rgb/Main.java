package ru.nsu.fit.huawei.rgb;

import ru.nsu.fit.huawei.rgb.control.SimpleController;
import ru.nsu.fit.huawei.rgb.model.Coords;
import ru.nsu.fit.huawei.rgb.model.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int count = scanner.nextInt();
        scanner.nextLine();
        List<SimpleController> gameControllers = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            String s = scanner.nextLine();
            if (!s.equals("")) {
                throw new IllegalStateException();
            }
            List<String> fieldLines = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                fieldLines.add(scanner.nextLine());
            }
            gameControllers.add(new SimpleController(fieldLines));
        }

        for (int i = 1; i <= gameControllers.size(); i++) {
            SimpleController game = gameControllers.get(i - 1);
            if (i != 1) {
                System.out.println();
            }
            System.out.printf("Game %d:%n", i);
            while (game.step()) {
                //game.printField();
                Coords lastStepCoords = game.getLastStepCoords();
                System.out.printf("Move %d at(%d, %d): removed %d balls of color %s, got %d points.%n",
                        game.getStepNumber(),
                        Game.HEIGHT - lastStepCoords.getY(),
                        lastStepCoords.getX() + 1,
                        game.getLastStepBalls(),
                        game.getLastStepColor().getMark(),
                        game.getLastStepScore());
            }
            System.out.printf("Final score: %d, with %d balls remaining%n",
                    game.getTotalScore(),
                    game.getPointsRemaining());
        }
    }
}
