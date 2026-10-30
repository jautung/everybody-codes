import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

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

    private record ParseResult(List<ParsedLine> parsedLines) {}
    private static ParseResult parseFromFile(String path) {
        List<String> lines = Util.readLines(path);
        List<ParsedLine> parsedLines = lines.stream().map(line -> parseLine(line)).collect(Collectors.toList());
        return new ParseResult(parsedLines);
    }

    private record ParsedLine(long standard, long vote) {}
    private static ParsedLine parseLine(String line) {
        String coreLine;
        if (line.startsWith("ISO-")) {
            coreLine = line.substring(4, line.length());
        } else if (line.startsWith("ISO2-")) {
            coreLine = line.substring(5, line.length());
        } else {
            throw new IllegalArgumentException("Line does not start with ISO- or ISO2-");
        }

        List<String> lineParts = List.of(coreLine.split(" "));
        if (lineParts.size() != 2) {
            throw new IllegalArgumentException("Line does not have two parts");
        }

        long standard = Long.parseLong(lineParts.get(0));
        long vote = Long.parseLong(lineParts.get(1));
        return new ParsedLine(standard, vote);
    }

    private static void runPart1(ParseResult parsed) {
        List<Long> scores = parsed.parsedLines().stream().map(parsedLine -> calcScore(parsedLine)).collect(Collectors.toList());
        long totalScore = scores.stream().mapToLong(Long::longValue).sum();
        System.out.println(totalScore);
    }

    private static long calcScore(ParsedLine parsedLine) {
        long standard = parsedLine.standard();
        long vote = parsedLine.vote();

        List<Long> cycleStandardVals = new ArrayList<>();
        long score = 0;
        long currExponent = 0;
        while (true) {
            long standardVal = standard % 10;
            long voteVal = vote % 10;

            cycleStandardVals.add(standardVal);

            long increment = voteVal * (long) Math.pow(standardVal, currExponent);
            score += increment;

            currExponent += 1;

            standard = standard / 10;
            vote = vote / 10;
            if (vote == 0) {
                break;
            }
            if (standard == 0) {
                standard = cycleStandardVals.remove(0);
            }
        }
        return score;
    }

    private static void runPart2(ParseResult parsed) {
        runPart1(parsed);
    }

    private static void runPart3(ParseResult parsed) {
        List<Long> scores = parsed.parsedLines().stream().map(parsedLine -> calcScoreV2(parsedLine)).collect(Collectors.toList());
        long totalScore = scores.stream().mapToLong(Long::longValue).sum();
        System.out.println(totalScore);
    }

    private static long calcScoreV2(ParsedLine parsedLine) {
        long baseScore = calcScore(parsedLine);

        for (long testCase = 0; testCase < parsedLine.vote(); testCase++) {
            long otherScore = calcScore(new ParsedLine(parsedLine.standard(), testCase));
            if (baseScore == otherScore) {
                return 0;
            }
        }

        return baseScore;
    }
}
