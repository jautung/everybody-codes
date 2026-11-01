import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class Util {
    public static List<String> readLines(String path) {
        Path fullPath = Path.of(path);

        try {
            return Files.readAllLines(fullPath);
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static List<List<String>> readLinesSplitByNewlines(String path) {
        List<List<String>> lineGroups = new ArrayList<>();
        List<String> currentGroup = new ArrayList<>();

        List<String> lines = readLines(path);
        for (String line : lines) {
            if (!line.isBlank()) {
                currentGroup.add(line);
            } else {
                maybeAddCurrentGroup(currentGroup, lineGroups);
            }
        }
        maybeAddCurrentGroup(currentGroup, lineGroups);

        return lineGroups;
    }

    private static void maybeAddCurrentGroup(List<String> currentGroup, List<List<String>> lineGroups) {
        if (currentGroup.isEmpty()) {
            return;
        }
        lineGroups.add(new ArrayList<>(currentGroup));
        currentGroup.clear();
    }

    public static List<List<String>> readGrid(String path) {
        List<String> lines = readLines(path);
        return lines.stream().map(line -> csvLine(line)).collect(Collectors.toList());
    }

    public static List<String> csvLine(String line) {
        return List.of(line.split(","));
    }

    public static void print2D(List<List<Character>> grid) {
        for (List<Character> row : grid) {
            System.out.println(row.stream().map(String::valueOf).collect(Collectors.joining()));
        }
    }
}
