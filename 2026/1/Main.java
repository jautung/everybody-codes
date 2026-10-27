import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

// Time since you've opened this puzzle for the first time: 20m 44s 754ms

public class Main {
    public static void main(String[] args) {
        ParseResult parsed1 = parseFromFile("data_1.txt");
        System.out.println("Part 1:");
        runPart1(parsed1);
        System.out.println();

        ParseResult parsed2 = parseFromFile("data_2.txt");
        System.out.println("Part 2:");
        runPart2(parsed2);
        System.out.println();

        ParseResult parsed3 = parseFromFile("data_3.txt");
        System.out.println("Part 3:");
        runPart3(parsed3);
        System.out.println();
    }

    private record ParseResult(List<Integer> cones) {}
    private static ParseResult parseFromFile(String path) {
        List<String> lines = Util.readLines(path);
        if (lines.size() != 1) {
            throw new IllegalStateException("Not exactly one line");
        }
        String line = lines.get(0);
        List<String> rawCones = Util.csvLine(line);
        List<Integer> cones = rawCones.stream().map(c -> Integer.parseInt(c)).collect(Collectors.toList());
        return new ParseResult(cones);
    }

    private static void runPart1(ParseResult parsed) {
        List<Integer> cones = parsed.cones();
        int totalConesNeeded = cones.stream().map(c -> conesToSquare(c)).mapToInt(Integer::intValue).sum();
        System.out.println(totalConesNeeded);
    }

    private static int conesToSquare(int cones) {
        int minSide = (int) Math.ceil(Math.sqrt(cones));
        int conesNeeded = minSide * minSide - cones;
        return conesNeeded;
    }

    private static void runPart2(ParseResult parsed) {
        List<Integer> cones = parsed.cones();
        int totalConesNeeded = cones.stream().map(c -> conesToRing(c)).mapToInt(Integer::intValue).sum();
        System.out.println(totalConesNeeded);
    }

    private static int conesToRing(int cones) {
        if (cones <= 8) {
            return 8 - cones;
        }
        int minSide = (int) Math.ceilDiv(cones, 4);
        int conesNeeded = 4 * minSide - cones;
        return conesNeeded;
    }

    private static void runPart3(ParseResult parsed) {
        List<Integer> cones = parsed.cones();
        int totalConesNeeded = cones.stream().map(c -> conesToWeirdCenter(c)).mapToInt(Integer::intValue).sum();
        System.out.println(totalConesNeeded);
    }

    private static int conesToWeirdCenter(int cones) {
        int minSide = (int) Math.ceil(Math.sqrt(cones + 1));
        if (minSide % 2 == 0) {
            minSide += 1;
        }
        int conesNeeded = minSide * minSide - cones - 1;
        return conesNeeded;
    }
}
