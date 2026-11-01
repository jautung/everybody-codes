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

        ParseResult3 parsed3 = parseFromFile3("data_3.txt");
        System.out.println("Part 3:");
        runPart3(parsed3);
        System.out.println();
    }

    private record ParseResult(List<Long> teethsPerGear, long firstNumTeeth, long lastNumTeeth) {}
    private static ParseResult parseFromFile(String path) {
        List<String> lines = Util.readLines(path);
        List<Long> teethsPerGear = lines.stream().map(line -> Long.parseLong(line)).collect(Collectors.toList());
        if (teethsPerGear.size() == 0) {
            throw new IllegalArgumentException("0 lines in file");
        }
        long firstNumTeeth = teethsPerGear.get(0);
        long lastNumTeeth = teethsPerGear.get(teethsPerGear.size() - 1);
        return new ParseResult(teethsPerGear, firstNumTeeth, lastNumTeeth);
    }

    private static void runPart1(ParseResult parsed) {
        long DESIRED_INPUT = 2025;
        long numRotations = DESIRED_INPUT * parsed.firstNumTeeth() / parsed.lastNumTeeth();
        System.out.println(numRotations);
    }

    private static void runPart2(ParseResult parsed) {
        long DESIRED_OUTPUT = 10000000000000L;
        long numRotations = Math.ceilDiv(DESIRED_OUTPUT * parsed.lastNumTeeth(), parsed.firstNumTeeth());
        System.out.println(numRotations);
    }

    private record ParseResult3(List<GearConfig3> gearConfigs) {}
    private static ParseResult3 parseFromFile3(String path) {
        List<String> lines = Util.readLines(path);
        List<GearConfig3> gearConfigs = IntStream.range(0, lines.size()).mapToObj(i -> parseLine3(lines.get(i), i == 0, i == lines.size() - 1)).collect(Collectors.toList());
        return new ParseResult3(gearConfigs);
    }

    private record GearConfig3(Long first, Long second) {}
    private static GearConfig3 parseLine3(String line, boolean isFirst, boolean isLast) {
        if (isFirst) {
            return new GearConfig3(null, Long.parseLong(line));
        } else if (isLast) {
            return new GearConfig3(Long.parseLong(line), null);
        } else {
            List<String> parts = List.of(line.split("\\|"));
            if (parts.size() != 2) {
                throw new IllegalArgumentException("Line does not have two parts: " + line);
            }
            return new GearConfig3(Long.parseLong(parts.get(0)), Long.parseLong(parts.get(1)));
        }
    }

    private static void runPart3(ParseResult3 parsed) {
        // Doing * then / individually will lose rounding error...
        // Doing all the * up front exceeds the integer limit...
        // ==> So... we do something a bit fancier here...
        long currNumerator = 100;
        long currDenominator = 1;
        for (int i = 0; i < parsed.gearConfigs().size() - 1; i++) {
            long toMultiply = parsed.gearConfigs().get(i).second();
            long toDivide = parsed.gearConfigs().get(i+1).first();

            currNumerator *= toMultiply;
            currDenominator *= toDivide;

            long currGcd = gcd(currNumerator, currDenominator);
            currNumerator /= currGcd;
            currDenominator /= currGcd;
        }
        System.out.println(currNumerator / currDenominator);
    }

    private static long gcd(long a, long b) {
        return b == 0 ? a : gcd(b, a % b);
    }
}
