import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ParseResult parsed1 = parseFromFile("data_1.txt");
        runPart1(parsed1.nameList(), parsed1.instructionList());

        ParseResult parsed2 = parseFromFile("data_2.txt");
        runPart2(parsed2.nameList(), parsed2.instructionList());

        ParseResult parsed3 = parseFromFile("data_3.txt");
        runPart3(parsed3.nameList(), parsed3.instructionList());
    }

    private record ParseResult(List<String> nameList, List<String> instructionList) {}
    private static ParseResult parseFromFile(String path) {
        List<List<String>> lineGroups = Util.readLinesSplitByNewlines(path);

        if (lineGroups.size() != 2) {
            throw new IllegalStateException("Expected two line groups");
        }
        if (lineGroups.get(0).size() != 1) {
            throw new IllegalStateException("Expected first line group to have one line");
        }
        if (lineGroups.get(1).size() != 1) {
            throw new IllegalStateException("Expected second line group to have one line");
        }

        String names = lineGroups.get(0).get(0);
        String instructions = lineGroups.get(1).get(0);

        List<String> nameList = Util.csvLine(names);
        List<String> instructionList = Util.csvLine(instructions);

        return new ParseResult(nameList, instructionList);
    }

    private static void runPart1(List<String> nameList, List<String> instructionList) {
        int numNames = nameList.size();

        int currPos = 0;
        for (String instruction : instructionList) {
            if (instruction.startsWith("L")) {
                int amount = Integer.parseInt(instruction.substring(1));
                currPos -= amount;
                currPos = Math.max(currPos, 0);
            } else if (instruction.startsWith("R")) {
                int amount = Integer.parseInt(instruction.substring(1));
                currPos += amount;
                currPos = Math.min(currPos, numNames - 1);
            } else {
                throw new IllegalStateException("Instruction doesn't start with either L or R");
            }
        }

        System.out.println("Part 1:");
        System.out.println(nameList.get(currPos));
        System.out.println();
    }

    private static void runPart2(List<String> nameList, List<String> instructionList) {
        int numNames = nameList.size();

        int currPos = 0;
        for (String instruction : instructionList) {
            if (instruction.startsWith("L")) {
                int amount = Integer.parseInt(instruction.substring(1));
                currPos -= amount;
            } else if (instruction.startsWith("R")) {
                int amount = Integer.parseInt(instruction.substring(1));
                currPos += amount;
            } else {
                throw new IllegalStateException("Instruction doesn't start with either L or R");
            }
        }

        System.out.println("Part 2:");
        System.out.println(nameList.get(Math.floorMod(currPos, numNames)));
        System.out.println();
    }

    private static void runPart3(List<String> nameList, List<String> instructionList) {
        int numNames = nameList.size();

        List<String> mutableNameList = new ArrayList<>(nameList);

        for (String instruction : instructionList) {
            if (instruction.startsWith("L")) {
                int offset = Integer.parseInt(instruction.substring(1));
                int swapIndex = Math.floorMod(offset, numNames);
                String rootName = mutableNameList.get(0);
                mutableNameList.set(0, mutableNameList.get(swapIndex));
                mutableNameList.set(swapIndex, rootName);
            } else if (instruction.startsWith("R")) {
                int offset = Integer.parseInt(instruction.substring(1));
                int swapIndex = Math.floorMod(numNames - offset, numNames);
                String rootName = mutableNameList.get(0);
                mutableNameList.set(0, mutableNameList.get(swapIndex));
                mutableNameList.set(swapIndex, rootName);
            } else {
                throw new IllegalStateException("Instruction doesn't start with either L or R");
            }
        }

        System.out.println("Part 3:");
        System.out.println(mutableNameList.get(0));
        System.out.println();
    }
}
