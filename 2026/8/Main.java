import java.util.*;
import java.util.function.*;
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

    private record ParseResult(List<List<Character>> charGrid, int height, int width, int startRow) {}
    private static ParseResult parseFromFile(String path) {
        List<String> lines = Util.readLines(path);
        List<List<Character>> charGrid = lines.stream().map(line -> line.chars().mapToObj(c -> (char) c).collect(Collectors.toList())).collect(Collectors.toList());

        int height = charGrid.size();
        if (height == 0) {
            throw new IllegalArgumentException("Grid has 0 rows");
        }
        int width = charGrid.get(0).size();
        if (width == 0) {
            throw new IllegalArgumentException("Grid has 0 columns");
        }

        int startRow = findRowWithNonDotFirstCol(charGrid, height);

        return new ParseResult(charGrid, height, width, startRow);
    }

    private static int findRowWithNonDotFirstCol(List<List<Character>> charGrid, int height) {
        for (int row = 0; row < height; row++) {
            if (charGrid.get(row).get(0) != '.') {
                return row;
            }
        }
        throw new IllegalArgumentException("Grid has no row with non-dot first column");
    }

    private static void runPart1(ParseResult parsed) {
        List<FullLineInfo> lineInfos = getLinesFromTopLeft(parsed);
        List<List<Integer>> lines = lineInfos.stream().map(info -> info.heights()).collect(Collectors.toList());
        int totalEnergy = lines.stream().map(line -> energyForLine(line).numEnergy()).mapToInt(Integer::intValue).sum();
        System.out.println(totalEnergy);
    }

    private record Coordinate(int row, int col) {}

    private static Set<Coordinate> getTopLeftLine(ParseResult parsed) {
        Set<Coordinate> startCoordinates = new HashSet<>();
        for (int startRow = parsed.startRow(); startRow >= 0; startRow--) {
            int startCol = parsed.startRow() - startRow;
            startCoordinates.add(new Coordinate(startRow, startCol));
        }
        return startCoordinates;
    }

    private static Set<Coordinate> getTopLine(ParseResult parsed) {
        Set<Coordinate> startCoordinates = new HashSet<>();
        for (int startCol = parsed.startRow(); startCol < parsed.width() - parsed.startRow(); startCol = startCol + 2) {
            startCoordinates.add(new Coordinate(0, startCol));
        }
        return startCoordinates;
    }

    private static Set<Coordinate> getTopRightLine(ParseResult parsed) {
        Set<Coordinate> startCoordinates = new HashSet<>();
        for (int startRow = 0; startRow <= parsed.startRow(); startRow++) {
            int startCol = parsed.width() - 1 - parsed.startRow() + startRow;
            startCoordinates.add(new Coordinate(startRow, startCol));
        }
        return startCoordinates;
    }

    private static Set<Coordinate> getBottomRightLine(ParseResult parsed) {
        Set<Coordinate> startCoordinates = new HashSet<>();
        for (int startRow = parsed.startRow(); startRow < parsed.height(); startRow++) {
            int startCol = parsed.width() - 1 + parsed.startRow() - startRow;
            startCoordinates.add(new Coordinate(startRow, startCol));
        }
        return startCoordinates;
    }

    private static Set<Coordinate> getBottomLine(ParseResult parsed) {
        Set<Coordinate> startCoordinates = new HashSet<>();
        for (int startCol = parsed.startRow(); startCol < parsed.width() - parsed.startRow(); startCol = startCol + 2) {
            startCoordinates.add(new Coordinate(parsed.height() - 1, startCol));
        }
        return startCoordinates;
    }

    private static Set<Coordinate> getBottomLeftLine(ParseResult parsed) {
        Set<Coordinate> startCoordinates = new HashSet<>();
        for (int startRow = parsed.height() - 1; startRow >= parsed.startRow(); startRow--) {
            int startCol = startRow - parsed.startRow();
            startCoordinates.add(new Coordinate(startRow, startCol));
        }
        return startCoordinates;
    }

    private static List<FullLineInfo> getLinesFromTopLeft(ParseResult parsed) {
        Set<Coordinate> starts = new HashSet<>();
        starts.addAll(getTopLeftLine(parsed));
        starts.addAll(getTopLine(parsed));
        return starts
            .stream()
            .map(
                startCoordinate -> getLineStartingFrom(
                    parsed, startCoordinate,
                    (startRow, distAlongLine) -> startRow + distAlongLine,
                    (startCol, distAlongLine) -> startCol + distAlongLine
                )
            )
            .collect(Collectors.toList());
    }

    private static List<FullLineInfo> getLinesFromTopRight(ParseResult parsed) {
        Set<Coordinate> starts = new HashSet<>();
        starts.addAll(getTopLine(parsed));
        starts.addAll(getTopRightLine(parsed));
        return starts
            .stream()
            .map(
                startCoordinate -> getLineStartingFrom(
                    parsed, startCoordinate,
                    (startRow, distAlongLine) -> startRow + distAlongLine,
                    (startCol, distAlongLine) -> startCol - distAlongLine
                )
            )
            .collect(Collectors.toList());
    }

    private static List<FullLineInfo> getLinesFromRight(ParseResult parsed) {
        Set<Coordinate> starts = new HashSet<>();
        starts.addAll(getTopRightLine(parsed));
        starts.addAll(getBottomRightLine(parsed));
        return starts
            .stream()
            .map(
                startCoordinate -> getLineStartingFrom(
                    parsed, startCoordinate,
                    (startRow, distAlongLine) -> startRow,
                    (startCol, distAlongLine) -> startCol - 2 * distAlongLine
                )
            )
            .collect(Collectors.toList());
    }

    private static List<FullLineInfo> getLinesFromBottomRight(ParseResult parsed) {
        Set<Coordinate> starts = new HashSet<>();
        starts.addAll(getBottomRightLine(parsed));
        starts.addAll(getBottomLine(parsed));
        return starts
            .stream()
            .map(
                startCoordinate -> getLineStartingFrom(
                    parsed, startCoordinate,
                    (startRow, distAlongLine) -> startRow - distAlongLine,
                    (startCol, distAlongLine) -> startCol - distAlongLine
                )
            )
            .collect(Collectors.toList());
    }

    private static List<FullLineInfo> getLinesFromBottomLeft(ParseResult parsed) {
        Set<Coordinate> starts = new HashSet<>();
        starts.addAll(getBottomLine(parsed));
        starts.addAll(getBottomLeftLine(parsed));
        return starts
            .stream()
            .map(
                startCoordinate -> getLineStartingFrom(
                    parsed, startCoordinate,
                    (startRow, distAlongLine) -> startRow - distAlongLine,
                    (startCol, distAlongLine) -> startCol + distAlongLine
                )
            )
            .collect(Collectors.toList());
    }

    private static List<FullLineInfo> getLinesFromLeft(ParseResult parsed) {
        Set<Coordinate> starts = new HashSet<>();
        starts.addAll(getBottomLeftLine(parsed));
        starts.addAll(getTopLeftLine(parsed));
        return starts
            .stream()
            .map(
                startCoordinate -> getLineStartingFrom(
                    parsed, startCoordinate,
                    (startRow, distAlongLine) -> startRow,
                    (startCol, distAlongLine) -> startCol + 2 * distAlongLine
                )
            )
            .collect(Collectors.toList());
    }

    private record FullLineInfo(List<Integer> heights, List<Coordinate> coordinates) {}
    private static FullLineInfo getLineStartingFrom(
        ParseResult parsed,
        Coordinate startCoordinate,
        BiFunction<Integer, Integer, Integer> transformRow,
        BiFunction<Integer, Integer, Integer> transformCol
    ) {
        int startRow = startCoordinate.row();
        int startCol = startCoordinate.col();

        List<Integer> currLine = new ArrayList<>();
        List<Coordinate> lineCoordinates = new ArrayList<>();
        int distAlongLine = 0;

        while (true) {
            int targetRow = transformRow.apply(startRow, distAlongLine);
            int targetCol = transformCol.apply(startCol, distAlongLine);

            if (targetRow < 0 || targetRow >= parsed.height() || targetCol < 0 || targetCol >= parsed.width()) {
                break;
            }

            char newItem = parsed.charGrid().get(targetRow).get(targetCol);
            if (newItem == '.') {
                break;
            }

            if (newItem == 'X' || newItem == 'Y' || newItem == 'Z') {
                currLine.add(null);
            } else {
                currLine.add(Character.getNumericValue(newItem));
            }
            lineCoordinates.add(new Coordinate(targetRow, targetCol));
            distAlongLine++;
        }
        return new FullLineInfo(currLine, lineCoordinates);
    }

    private record EnergyLineResult(int numEnergy, List<Boolean> tookEnergy) {}
    private static EnergyLineResult energyForLine(List<Integer> values) {
        int finalCount = 0;
        List<Boolean> tookEnergy = new ArrayList<>();
        Integer currentShadow = null;

        for (Integer value : values) {
            if (value != null && (currentShadow == null || value > currentShadow)) {
                finalCount++;
                currentShadow = value;
                tookEnergy.add(true);
            } else {
                tookEnergy.add(false);
            }
            if (currentShadow != null) {
                currentShadow--;
            }
        }

        return new EnergyLineResult(finalCount, tookEnergy);
    }

    private static void runPart2(ParseResult parsed) {
        int totalEnergyFromTopLeft = getLinesFromTopLeft(parsed).stream().map(lineInfo -> energyForLine(lineInfo.heights()).numEnergy()).mapToInt(Integer::intValue).sum();
        int totalEnergyFromTopRight = getLinesFromTopRight(parsed).stream().map(lineInfo -> energyForLine(lineInfo.heights()).numEnergy()).mapToInt(Integer::intValue).sum();
        int totalEnergyFromRight = getLinesFromRight(parsed).stream().map(lineInfo -> energyForLine(lineInfo.heights()).numEnergy()).mapToInt(Integer::intValue).sum();
        int totalEnergyFromBottomRight = getLinesFromBottomRight(parsed).stream().map(lineInfo -> energyForLine(lineInfo.heights()).numEnergy()).mapToInt(Integer::intValue).sum();
        int totalEnergyFromBottomLeft = getLinesFromBottomLeft(parsed).stream().map(lineInfo -> energyForLine(lineInfo.heights()).numEnergy()).mapToInt(Integer::intValue).sum();
        int totalEnergyFromLeft = getLinesFromLeft(parsed).stream().map(lineInfo -> energyForLine(lineInfo.heights()).numEnergy()).mapToInt(Integer::intValue).sum();
        int totalEnergy = totalEnergyFromTopLeft + totalEnergyFromTopRight + totalEnergyFromRight + totalEnergyFromBottomRight + totalEnergyFromBottomLeft + totalEnergyFromLeft;
        System.out.println(totalEnergy);
    }

    private static void runPart3(ParseResult parsed) {
        ParseResult currParsed = parsed;
        int round = 0;
        int totalUsedEnergy = 0;
        int totalGrownWarriors = 0;
        while (true) {
            EnergyDirection direction = EnergyDirection.fromValue(round % 6);
            IterateGridResult result = iterateGrid(currParsed, direction);
            currParsed = result.parsed();
            totalUsedEnergy += result.usedEnergy();
            totalGrownWarriors += result.grownWarriors();
            if (totalGrownWarriors >= 100000) {
                break;
            }
            round++;
        }
        System.out.println(totalUsedEnergy);
    }

    private enum EnergyDirection {
        TopLeft(0),
        TopRight(1),
        Right(2),
        BottomRight(3),
        BottomLeft(4),
        Left(5);

        private final int value;

        private EnergyDirection(int value) {
            this.value = value;
        }

        private int getValue() {
            return this.value;
        }

        public static EnergyDirection fromValue(int value) {
            for (EnergyDirection direction : EnergyDirection.values()) {
                if (direction.getValue() == value) {
                    return direction;
                }
            }
            throw new IllegalArgumentException("Unknown numeric value for EnergyDirection: " + value);
        }
    }

    private record IterateGridResult(ParseResult parsed, int usedEnergy, int grownWarriors) {}
    private static IterateGridResult iterateGrid(ParseResult parsed, EnergyDirection direction) {
        List<FullLineInfo> lineInfos = getLinesInDirection(parsed, direction);
        List<LineEnergyReceivedResult> energyResults = lineInfos.stream().map(lineInfo -> calculateEnergyReceived(lineInfo)).collect(Collectors.toList());

        int usedEnergy = energyResults.stream().map(energyResult -> energyResult.numEnergy()).mapToInt(Integer::intValue).sum();
        List<Coordinate> coordinatesReceivingEnergy = energyResults.stream().flatMap(energyResult -> energyResult.coordinatesReceivingEnergy().stream()).collect(Collectors.toList());

        int grownWarriors = 0;
        List<List<Character>> originalGrid = parsed.charGrid();
        List<List<Character>> newGrid = new ArrayList<>();
        for (int row = 0; row < parsed.height(); row++) {
            List<Character> newRow = new ArrayList<>();
            for (int col = 0; col < parsed.width(); col++) {
                char item = originalGrid.get(row).get(col);
                if (item == '.') {
                    newRow.add('.');
                } else if (item == 'X') {
                    newRow.add('Y');
                } else if (item == 'Y') {
                    newRow.add('Z');
                } else if (item == 'Z') {
                    newRow.add('1');
                } else if (coordinatesReceivingEnergy.contains(new Coordinate(row, col))) {
                    int oldValue = Character.getNumericValue(item);
                    if (oldValue != 9) {
                        newRow.add(Character.forDigit(oldValue + 1, 10));
                    } else {
                        grownWarriors++;
                        newRow.add('X');
                    }
                } else {
                    newRow.add(item);
                }
            }
            newGrid.add(newRow);
        }

        return new IterateGridResult(new ParseResult(newGrid, parsed.height(), parsed.width(), parsed.startRow()), usedEnergy, grownWarriors);
    }

    private record LineEnergyReceivedResult(List<Coordinate> coordinatesReceivingEnergy, int numEnergy) {}
    private static LineEnergyReceivedResult calculateEnergyReceived(FullLineInfo lineInfo) {
        List<Coordinate> coordinates = lineInfo.coordinates();
        EnergyLineResult energyResult = energyForLine(lineInfo.heights());
        List<Boolean> tookEnergy = energyResult.tookEnergy();
        if (coordinates.size() != tookEnergy.size()) {
            throw new IllegalStateException("Somehow coordinates length and took energy length mismatched");
        }

        List<Coordinate> coordinatesReceivingEnergy = new ArrayList<>();
        for (int i = 0; i < coordinates.size(); i++) {
            if (tookEnergy.get(i)) {
                coordinatesReceivingEnergy.add(coordinates.get(i));
            }
        }
        return new LineEnergyReceivedResult(coordinatesReceivingEnergy, energyResult.numEnergy());
    }

    private static List<FullLineInfo> getLinesInDirection(ParseResult parsed, EnergyDirection direction) {
        switch (direction) {
            case TopLeft:
                return getLinesFromTopLeft(parsed);
            case TopRight:
                return getLinesFromTopRight(parsed);
            case Right:
                return getLinesFromRight(parsed);
            case BottomRight:
                return getLinesFromBottomRight(parsed);
            case BottomLeft:
                return getLinesFromBottomLeft(parsed);
            case Left:
                return getLinesFromLeft(parsed);
        }
        throw new IllegalArgumentException("Unrecognized direction: " + direction);
    }
}
