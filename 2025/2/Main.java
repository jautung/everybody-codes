import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private record ParseResult(long x, long y) {}
    private static ParseResult parseFromFile(String path) {
        List<String> lines = Util.readLines(path);
        if (lines.size() != 1) {
            throw new IllegalStateException("Expected two line groups");
        }
        String inputLine = lines.get(0);

        Pattern pattern = Pattern.compile("^A=\\[(-?\\d+),(-?\\d+)\\]$");
        Matcher matcher = pattern.matcher(inputLine);
        if (matcher.find()) {
            String xStr = matcher.group(1);
            String yStr = matcher.group(2);
            long x = Integer.parseInt(xStr);
            long y = Integer.parseInt(yStr);
            return new ParseResult(x, y);
        } else {
            throw new IllegalStateException("File did not match regex");
        }
    }

    private static void runPart1(ParseResult parsed) {
        ComplexNumber result = new ComplexNumber(0, 0);

        for (int i = 0; i < 3; i++) {
            result = multComplex(result, result);
            result = divComplex(result, new ComplexNumber(10, 10));
            result = addComplex(result, toComplex(parsed));
        }

        System.out.println(displayComplex(result));
    }

    private static void runPart2(ParseResult parsed) {
        runEngravingSuite(parsed, 10);
    }

    private static void runPart3(ParseResult parsed) {
        runEngravingSuite(parsed, 1);
    }

    private static void runEngravingSuite(ParseResult parsed, int stepSize) {
        ComplexNumber topLeft = toComplex(parsed);
        int rowOrColCount = 1000 / stepSize;

        int engraveCount = 0;
        for (int i = 0; i <= rowOrColCount; i++) {
            for (int j = 0; j <= rowOrColCount; j++) {
                ComplexNumber toCheck = addComplex(topLeft, new ComplexNumber(i * stepSize, j * stepSize));
                if (shouldEngrave(toCheck)) {
                    engraveCount++;
                }
            }
        }

        System.out.println(engraveCount);
    }

    private static boolean shouldEngrave(ComplexNumber toCheck) {
        ComplexNumber result = new ComplexNumber(0, 0);

        for (int i = 0; i < 100; i++) {
            result = multComplex(result, result);
            result = divComplex(result, new ComplexNumber(100_000, 100_000));
            result = addComplex(result, toCheck);
            if (result.x() > 1_000_000 || result.x() < -1_000_000 || result.y() > 1_000_000 || result.y() < -1_000_000) {
                return false;
            }
        }

        return true;
    }

    // Complex number operations

    private record ComplexNumber(long x, long y) {}

    private static ComplexNumber toComplex(ParseResult result) {
        return new ComplexNumber(result.x(), result.y());
    }

    private static ComplexNumber addComplex(ComplexNumber a, ComplexNumber b) {
        return new ComplexNumber(a.x() + b.x(), a.y() + b.y());
    }

    private static ComplexNumber multComplex(ComplexNumber a, ComplexNumber b) {
        return new ComplexNumber(a.x() * b.x() - a.y() * b.y(), a.x() * b.y() + a.y() * b.x());
    }

    private static ComplexNumber divComplex(ComplexNumber a, ComplexNumber b) {
        return new ComplexNumber(a.x() / b.x(), a.y() / b.y());
    }

    private static String displayComplex(ComplexNumber c) {
        return "[" + c.x() + "," + c.y() + "]";
    }
}
