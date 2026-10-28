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

    private record ParseResult(List<GroupNotes> groups) {}
    private static ParseResult parseFromFile(String path) {
        List<String> lines = Util.readLines(path);
        List<GroupNotes> groups = lines.stream().map(line -> parseLine(line)).collect(Collectors.toList());
        return new ParseResult(groups);
    }

    private record GroupNotes(List<OrderingItem> orderings) {}
    private static GroupNotes parseLine(String line) {
        List<OrderingItem> orderings = List.of(line.split(" ")).stream().map(item -> parseItem(item)).collect(Collectors.toList());
        return new GroupNotes(orderings);
    }

    private record OrderingItem(int left, int right, boolean isGreater) {}
    private static OrderingItem parseItem(String line) {
        if (line.length() != 3) {
            throw new IllegalArgumentException("Item is not 3 characters");
        }
        char first = line.charAt(0);
        char op = line.charAt(1);
        char second = line.charAt(2);
        if (op == '>') {
            return new OrderingItem(Character.getNumericValue(first), Character.getNumericValue(second), true);
        } else if (op == '<') {
            return new OrderingItem(Character.getNumericValue(first), Character.getNumericValue(second), false);
        } else {
            throw new IllegalArgumentException("Op is not either > or <");
        }
    }

    private static void runPart1(ParseResult parsed) {
        long checksum = 0;
        for (GroupNotes group : parsed.groups()) {
            List<Integer> finalOrdering = getOrder(group);
            int finalOrderingValue = Integer.parseInt(finalOrdering.stream().map(String::valueOf).collect(Collectors.joining()));
            checksum += finalOrderingValue;
        }
        System.out.println(checksum);
    }

    private static List<Integer> getOrder(GroupNotes group) {
        List<OrderingItem> orderings = group.orderings();
        Set<Integer> allNumbers = new HashSet<>();
        for (OrderingItem order : orderings) {
            allNumbers.add(order.left());
            allNumbers.add(order.right());
        }
        List<Integer> finalOrdering = new ArrayList<>();
        while (allNumbers.size() > 0) {
            Integer hardestNumber = findHardestAmong(allNumbers, orderings);
            allNumbers.remove(hardestNumber);
            finalOrdering.add(hardestNumber);
        }
        return finalOrdering;
    }

    private static Integer findHardestAmong(Set<Integer> allNumbers, List<OrderingItem> orderings) {
        for (Integer number : allNumbers) {
            if (isHardest(number, allNumbers, orderings)) {
                return number;
            }
        }
        throw new IllegalStateException("Inconsistent hardness state");
    }

    private static boolean isHardest(Integer number, Set<Integer> allNumbers, List<OrderingItem> orderings) {
        for (OrderingItem ordering : orderings) {
            if (ordering.left() == number && allNumbers.contains(ordering.right()) && !ordering.isGreater) {
                return false;
            }
            if (ordering.right() == number && allNumbers.contains(ordering.left()) && ordering.isGreater) {
                return false;
            }
        }
        return true;
    }

    private static void runPart2(ParseResult parsed) {
        runPart1(parsed);
    }

    private static void runPart3(ParseResult parsed) {
        runPart1(parsed);
    }
}
