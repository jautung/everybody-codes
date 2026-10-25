import java.util.*;
import java.util.regex.*;

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

    private record ParseResult() {}
    private static ParseResult parseFromFile(String path) {
        return new ParseResult();
    }

    private static void runPart1(ParseResult parsed) {
        return;
    }

    private static void runPart2(ParseResult parsed) {
        return;
    }

    private static void runPart3(ParseResult parsed) {
        return;
    }
}
