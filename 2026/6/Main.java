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

    private record ParseResult(List<IngredientSpec> ingredientSpecs, Map<String, List<String>> ingredientLookup) {}
    private static ParseResult parseFromFile(String path) {
        List<String> lines = Util.readLines(path);
        List<IngredientSpec> ingredientSpecs = lines.stream().map(line -> parseLine(line)).collect(Collectors.toList());

        Map<String, List<String>> ingredientLookup = new HashMap<>();
        for (IngredientSpec ingredientSpec : ingredientSpecs) {
            ingredientLookup.put(ingredientSpec.output(), ingredientSpec.inputs());
        }

        return new ParseResult(ingredientSpecs, ingredientLookup);
    }

    private record IngredientSpec(String output, List<String> inputs) {}
    private static IngredientSpec parseLine(String line) {
        List<String> lineParts = List.of(line.split(":"));
        if (lineParts.size() != 2) {
            throw new IllegalArgumentException("Line did not have two parts: " + line);
        }

        String output = lineParts.get(0);
        String csvInputs = lineParts.get(1);
        List<String> inputs = List.of(csvInputs.split(","));

        return new IngredientSpec(output, inputs);
    }

    private static void runPart1(ParseResult parsed) {
        Set<String> finalAllInputs = runWithStart(parsed, "NULL");
        System.out.println(finalAllInputs.size());
    }

    private static Set<String> runWithStart(ParseResult parsed, String start) {
        Map<String, List<String>> ingredientLookup = parsed.ingredientLookup();

        Set<String> finalAllInputs = new HashSet<>();
        Set<String> checked = new HashSet<>();
        Stack<String> toCheck = new Stack<>();
        toCheck.push(start);

        while (toCheck.size() > 0) {
            String current = toCheck.pop();
            if (checked.contains(current)) {
                continue;
            }
            if (!ingredientLookup.containsKey(current)) {
                continue;
            }
            for (String input : ingredientLookup.get(current)) {
                if (!input.equals(start)) {
                    finalAllInputs.add(input);
                }
                if (!checked.contains(input)) {
                    toCheck.push(input);
                }
            }
            checked.add(current);
        }

        return finalAllInputs;
    }

    private static void runPart2(ParseResult parsed) {
        Set<String> finalAllInputs = runWithEnd(parsed, "NULL");
        System.out.println(finalAllInputs.size());
    }

    private static Set<String> runWithEnd(ParseResult parsed, String end) {
        List<IngredientSpec> ingredientSpecs = parsed.ingredientSpecs();

        Set<String> finalAllInputs = new HashSet<>();
        Set<String> checked = new HashSet<>();
        Stack<String> toCheck = new Stack<>();
        toCheck.push(end);

        while (toCheck.size() > 0) {
            String current = toCheck.pop();
            if (checked.contains(current)) {
                continue;
            }
            for (IngredientSpec ingredientSpec : ingredientSpecs) {
                if (ingredientSpec.inputs().contains(current)) {
                    String upstream = ingredientSpec.output();
                    if (!upstream.equals(end)) {
                        finalAllInputs.add(upstream);
                    }
                    if (!checked.contains(upstream)) {
                        toCheck.push(upstream);
                    }
                }
            }
            checked.add(current);
        }

        return finalAllInputs;
    }

    private static void runPart3(ParseResult parsed) {
        Set<String> finalAllInputs1 = runWithEnd(parsed, "NULLv1");
        Set<String> finalAllInputs2 = runWithEnd(parsed, "NULLv2");
        Set<String> finalAllInputs3 = runWithEnd(parsed, "NULLv3");

        HashMap<String, Integer> countsPerInput = new HashMap<>();
        for (String input : finalAllInputs1) {
            countsPerInput.merge(input, 1, (a, b) -> a + b);
        }
        for (String input : finalAllInputs2) {
            countsPerInput.merge(input, 1, (a, b) -> a + b);
        }
        for (String input : finalAllInputs3) {
            countsPerInput.merge(input, 1, (a, b) -> a + b);
        }

        int finalCount = 0;
        for (Map.Entry<String, Integer> entry : countsPerInput.entrySet()) {
            if (entry.getValue() > 1) {
                finalCount++;
            }
        }
        System.out.println(finalCount);
    }
}
