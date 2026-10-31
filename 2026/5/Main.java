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

    private record ParseResult(List<List<Character>> grid, int height, int width) {}
    private static ParseResult parseFromFile(String path) {
        List<String> lines = Util.readLines(path);
        List<List<Character>> grid = lines.stream().map(line -> line.chars().mapToObj(c -> (char) c).collect(Collectors.toList())).collect(Collectors.toList());
        int height = grid.size();
        if (height == 0) {
            throw new IllegalArgumentException("Grid has 0 rows");
        }
        int width = grid.get(0).size();
        return new ParseResult(grid, height, width);
    }

    private static void runPart1(ParseResult parsed) {
        List<List<Character>> gridWithLand = constructLand(parsed.grid(), parsed.height(), parsed.width());
        System.out.println(countLand(gridWithLand));
    }

    private static List<List<Character>> constructLand(List<List<Character>> grid, int height, int width) {
        List<List<Character>> currGrid = grid.stream().map(ArrayList::new).collect(Collectors.toList());
        currGrid.get(height / 2).set(width / 2, '#');

        String previousHash = hashGrid(currGrid);
        while (true) {
            currGrid = iterateGridForLand(currGrid, height, width);

            String newHash = hashGrid(currGrid);
            if (newHash.equals(previousHash)) {
                break;
            }
            previousHash = newHash;
        }

        return currGrid;
    }

    private static String hashGrid(List<List<Character>> grid) {
        return grid.stream().map(row -> row.stream().map(String::valueOf).collect(Collectors.joining())).collect(Collectors.joining());
    }

    private static List<List<Character>> iterateGridForLand(List<List<Character>> originalGrid, int height, int width) {
        List<List<Character>> nextGrid = originalGrid.stream().map(ArrayList::new).collect(Collectors.toList());
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                List<Character> neighbors = getNeighbors(originalGrid, height, width, row, col);
                if (!neighbors.contains('#')) {
                    continue;
                }
                boolean becomeLand = neighbors.stream().filter(n -> n == '#' || n == '1').collect(Collectors.toList()).size() >= 5;
                if (becomeLand) {
                    nextGrid.get(row).set(col, '#');
                }
            }
        }
        return nextGrid;
    }

    private static List<Character> getNeighbors(List<List<Character>> grid, int height, int width, int row, int col) {
        List<Character> neighbors = new ArrayList<>(
            Arrays.asList(
                getGridValue(grid, height, width, row - 1, col - 1),
                getGridValue(grid, height, width, row - 1, col),
                getGridValue(grid, height, width, row - 1, col + 1),
                getGridValue(grid, height, width, row, col - 1),
                getGridValue(grid, height, width, row, col + 1),
                getGridValue(grid, height, width, row + 1, col - 1),
                getGridValue(grid, height, width, row + 1, col),
                getGridValue(grid, height, width, row + 1, col + 1)
            )
        );
        return neighbors.stream().filter(n -> n != null).collect(Collectors.toList());
    }

    private static Character getGridValue(List<List<Character>> grid, int height, int width, int row, int col) {
        if (row < 0 || row >= height) {
            return null;
        }
        if (col < 0 || col >= width) {
            return null;
        }
        return grid.get(row).get(col);
    }

    private static int countLand(List<List<Character>> grid) {
        int count = 0;
        for (List<Character> row : grid) {
            for (Character item : row) {
                if (item == '#') {
                    count++;
                }
            }
        }
        return count;
    }

    private static void runPart2(ParseResult parsed) {
        List<List<Character>> gridWithLand = constructLand(parsed.grid(), parsed.height(), parsed.width());
        List<List<Character>> gridWithSand = constructSand(gridWithLand, parsed.height(), parsed.width());
        System.out.println(countSand(gridWithSand));
    }

    private static List<List<Character>> constructSand(List<List<Character>> grid, int height, int width) {
        List<List<Character>> currGrid = grid.stream().map(ArrayList::new).collect(Collectors.toList());
        currGrid = setInitialSand(currGrid, height, width);

        String previousHash = hashGrid(currGrid);
        while (true) {
            currGrid = iterateGridForSand(currGrid, height, width);

            String newHash = hashGrid(currGrid);
            if (newHash.equals(previousHash)) {
                break;
            }
            previousHash = newHash;
        }

        return currGrid;
    }

    private static List<List<Character>> setInitialSand(List<List<Character>> originalGrid, int height, int width) {
        List<List<Character>> finalGrid = originalGrid.stream().map(ArrayList::new).collect(Collectors.toList());
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (originalGrid.get(row).get(col) != '#') {
                    continue;
                }
                List<Character> neighbors = getNeighbors(originalGrid, height, width, row, col);
                boolean isInnerLand = neighbors.stream().filter(n -> n == '#').collect(Collectors.toList()).size() == 8;
                if (!isInnerLand) {
                    finalGrid.get(row).set(col, '=');
                }
            }
        }
        return finalGrid;
    }

    private static List<List<Character>> iterateGridForSand(List<List<Character>> originalGrid, int height, int width) {
        List<List<Character>> nextGrid = originalGrid.stream().map(ArrayList::new).collect(Collectors.toList());
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (originalGrid.get(row).get(col) != '#') {
                    continue;
                }
                List<Character> neighbors = getNeighbors(originalGrid, height, width, row, col);
                boolean becomeSand = neighbors.stream().filter(n -> n == '=').collect(Collectors.toList()).size() >= 5;
                if (becomeSand) {
                    nextGrid.get(row).set(col, '=');
                }
            }
        }
        return nextGrid;
    }

    private static int countSand(List<List<Character>> grid) {
        int count = 0;
        for (List<Character> row : grid) {
            for (Character item : row) {
                if (item == '=') {
                    count++;
                }
            }
        }
        return count;
    }

    private static void runPart3(ParseResult parsed) {
        List<List<Character>> gridWithLand = constructLand(parsed.grid(), parsed.height(), parsed.width());
        List<List<Character>> gridWithSand = constructSand(gridWithLand, parsed.height(), parsed.width());
        List<List<Character>> gridWithTrees = constructTrees(gridWithSand, parsed.height(), parsed.width());
        System.out.println(countTrees(gridWithTrees));
    }

    private static List<List<Character>> constructTrees(List<List<Character>> grid, int height, int width) {
        List<List<Character>> currGrid = grid.stream().map(ArrayList::new).collect(Collectors.toList());
        currGrid = setInitialTrees(currGrid, height, width);

        for (int iter = 0; iter < 2; iter++) {
            currGrid = iterateGridForTrees(currGrid, height, width);
        }

        return currGrid;
    }

    private static List<List<Character>> setInitialTrees(List<List<Character>> originalGrid, int height, int width) {
        List<List<Character>> finalGrid = originalGrid.stream().map(ArrayList::new).collect(Collectors.toList());
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (originalGrid.get(row).get(col) != '#') {
                    continue;
                }
                List<Character> stampedNeighbors = getStampedNeighbors(originalGrid, height, width, row, col);
                boolean canBeTree = stampedNeighbors.stream().filter(n -> n == '#' || n == '@').collect(Collectors.toList()).size() == 20;
                if (canBeTree) {
                    finalGrid.get(row).set(col, '@');
                }
            }
        }
        return finalGrid;
    }

    private static List<Character> getStampedNeighbors(List<List<Character>> grid, int height, int width, int row, int col) {
        List<Character> neighbors = new ArrayList<>(
            Arrays.asList(
                getGridValue(grid, height, width, row - 2, col - 1),
                getGridValue(grid, height, width, row - 2, col),
                getGridValue(grid, height, width, row - 2, col + 1),
                getGridValue(grid, height, width, row - 1, col - 2),
                getGridValue(grid, height, width, row - 1, col - 1),
                getGridValue(grid, height, width, row - 1, col),
                getGridValue(grid, height, width, row - 1, col + 1),
                getGridValue(grid, height, width, row - 1, col + 2),
                getGridValue(grid, height, width, row, col - 2),
                getGridValue(grid, height, width, row, col - 1),
                getGridValue(grid, height, width, row, col + 1),
                getGridValue(grid, height, width, row, col + 2),
                getGridValue(grid, height, width, row + 1, col - 2),
                getGridValue(grid, height, width, row + 1, col - 1),
                getGridValue(grid, height, width, row + 1, col),
                getGridValue(grid, height, width, row + 1, col + 1),
                getGridValue(grid, height, width, row + 1, col + 2),
                getGridValue(grid, height, width, row + 2, col - 1),
                getGridValue(grid, height, width, row + 2, col),
                getGridValue(grid, height, width, row + 2, col + 1)
            )
        );
        return neighbors.stream().filter(n -> n != null).collect(Collectors.toList());
    }


    private static List<List<Character>> iterateGridForTrees(List<List<Character>> originalGrid, int height, int width) {
        List<List<Character>> nextGrid = originalGrid.stream().map(ArrayList::new).collect(Collectors.toList());
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (originalGrid.get(row).get(col) != '#') {
                    continue;
                }
                List<Character> neighbors = getNeighbors(originalGrid, height, width, row, col);
                boolean becomeTree = neighbors.stream().filter(n -> n == '@').collect(Collectors.toList()).size() >= 4 && neighbors.stream().filter(n -> n == '=').collect(Collectors.toList()).size() == 0;
                if (becomeTree) {
                    nextGrid.get(row).set(col, '@');
                }
            }
        }
        return nextGrid;
    }

    private static int countTrees(List<List<Character>> grid) {
        int count = 0;
        for (List<Character> row : grid) {
            for (Character item : row) {
                if (item == '@') {
                    count++;
                }
            }
        }
        return count;
    }
}
