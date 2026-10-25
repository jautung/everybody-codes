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

    private record ParseResult(List<Integer> crateNums, List<Integer> sortedCrateNums) {}
    private static ParseResult parseFromFile(String path) {
        List<String> lines = Util.readLines(path);
        if (lines.size() != 1) {
            throw new IllegalArgumentException("File should have only one line!");
        }
        String crates = lines.get(0);

        List<String> crateList = Util.csvLine(crates);
        List<Integer> crateNums = crateList.stream().map(c -> Integer.parseInt(c)).collect(Collectors.toList());

        Set<Integer> crateNumsSet = new HashSet<>(crateNums);
        List<Integer> sortedCrateNums = new ArrayList<>(crateNumsSet);
        sortedCrateNums.sort(Comparator.reverseOrder());

        return new ParseResult(crateNums, sortedCrateNums);
    }

    private static void runPart1(ParseResult parsed) {
        List<Integer> sortedCrateNums = parsed.sortedCrateNums();
        int sumOfSizes = sortedCrateNums.stream().mapToInt(Integer::intValue).sum();
        System.out.println(sumOfSizes);
    }

    private static void runPart2(ParseResult parsed) {
        List<Integer> sortedCrateNums = parsed.sortedCrateNums();
        if (sortedCrateNums.size() < 20) {
            throw new IllegalArgumentException("Not enough crates for 20 nests");
        }
        List<Integer> lastTwenty = sortedCrateNums.subList(sortedCrateNums.size() - 20, sortedCrateNums.size());
        int sumOfSizes = lastTwenty.stream().mapToInt(Integer::intValue).sum();
        System.out.println(sumOfSizes);
    }

    private static void runPart3(ParseResult parsed) {
        List<Integer> crateNums = parsed.crateNums();
        Map<Integer, Integer> counterMap = new HashMap<>();
        for (Integer num : crateNums) {
            counterMap.merge(num, 1, (oldVal, newVal) -> oldVal + newVal);
        }
        System.out.println(Collections.max(counterMap.values()));
    }
}
