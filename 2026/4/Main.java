import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

public class Main {
    private static String LINE_REGEX = "id=(\\d+) quest=(\\d+) part=(\\d+) time=(\\d+)";
    private static Pattern LINE_PATTERN = Pattern.compile(LINE_REGEX);

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

    private record ParseResult(List<ParticipantResult> results) {}
    private static ParseResult parseFromFile(String path) {
        List<String> lines = Util.readLines(path);
        List<ParticipantResult> results = lines.stream().map(line -> parseLine(line)).collect(Collectors.toList());
        return new ParseResult(results);
    }

    private record ParticipantResult(int id, int quest, int part, int time) {}
    private static ParticipantResult parseLine(String line) {
        Matcher matcher = LINE_PATTERN.matcher(line);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Line not in the right format");
        }
        return new ParticipantResult(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)));
    }

    private static void runPart1(ParseResult parsed) {
        Map<Integer, Integer> participantTimes = new HashMap<>();
        for (ParticipantResult result : parsed.results()) {
            if (participantTimes.containsKey(result.id())) {
                participantTimes.put(result.id(), participantTimes.get(result.id()) + result.time());
            } else {
                participantTimes.put(result.id(), result.time());
            }
        }

        List<Integer> listOfTimes = new ArrayList<>(participantTimes.values());
        listOfTimes.sort(Comparator.naturalOrder());
        System.out.println(calculateChecksum(listOfTimes));
    }

    private static int calculateChecksum(List<Integer> orderedValues) {
        int checksum = 0;
        Integer previousValue = null; // For tie breaking
        int offset = 0; // For tie breaking
        for (int i = 0; i < orderedValues.size(); i++) {
            int currentValue = orderedValues.get(i);
            if (previousValue != null && previousValue == currentValue) {
                offset += 1;
            } else {
                offset = 0;
            }
            previousValue = currentValue;
            checksum += orderedValues.get(i) * (i + 1 - offset);
        }
        return checksum;
    }

    private record PartsAndTime(int parts, int time) {}
    private static void runPart2(ParseResult parsed) {
        Map<Integer, PartsAndTime> participantTimes = new HashMap<>();
        for (ParticipantResult result : parsed.results()) {
            if (participantTimes.containsKey(result.id())) {
                PartsAndTime existingEntry = participantTimes.get(result.id());
                PartsAndTime newEntry = new PartsAndTime(existingEntry.parts() + 1, existingEntry.time() + result.time());
                participantTimes.put(result.id(), newEntry);
            } else {
                participantTimes.put(result.id(), new PartsAndTime(1, result.time()));
            }
        }

        List<PartsAndTime> listOfPartsAndTimes = new ArrayList<>(participantTimes.values());
        listOfPartsAndTimes.sort(Comparator.comparing(PartsAndTime::parts).reversed().thenComparing(PartsAndTime::time));
        List<Integer> listOfTimes = listOfPartsAndTimes.stream().map(PartsAndTime::time).collect(Collectors.toList());
        System.out.println(calculateChecksum(listOfTimes));
    }

    private record ResultKey(int id, int quest, int part) {}
    private static void runPart3(ParseResult parsed) {
        Map<ResultKey, Integer> lookupResults = new HashMap<>();
        for (ParticipantResult result : parsed.results()) {
            lookupResults.put(new ResultKey(result.id(), result.quest(), result.part()), result.time());
        }

        Set<Integer> participantIds = allParticipantIds(parsed);
        Set<Integer> questsSet = allQuests(parsed);
        List<Integer> quests = new ArrayList<>(questsSet);
        quests.sort(Comparator.naturalOrder());

        Map<Integer, Integer> fullScores = new HashMap<>();
        for (int participantId : participantIds) {
            fullScores.put(participantId, 0);
        }

        for (int quest : quests) {
            Map<Integer, Integer> runningTimes = new HashMap<>();
            for (int participantId : participantIds) {
                runningTimes.put(participantId, 0);
            }

            for (int part = 1; part <= 3; part++) {
                // ARTISANAL DEBUGGING
                // System.out.println();
                // System.out.println("Quest " + quest + ", Part " + part + ":");

                // Check all participants for this part
                for (int participantId : participantIds) {
                    ResultKey keyForThis = new ResultKey(participantId, quest, part);
                    Integer existingRunningTime = runningTimes.get(participantId);
                    if (existingRunningTime == null) {
                        // Did not complete prior parts; so no need to check this part; do nothing
                    } else if (lookupResults.containsKey(keyForThis)) {
                        runningTimes.put(participantId, existingRunningTime + lookupResults.get(keyForThis));
                    } else {
                        // Did not complete this part
                        runningTimes.put(participantId, null);
                    }
                }

                // Updated runningTimes for the part, now tabulate rankings
                List<Map.Entry<Integer, Integer>> entryList = new ArrayList<>(runningTimes.entrySet());
                entryList.removeIf(entry -> entry.getValue() == null);
                entryList.sort(Comparator.comparing(Map.Entry::getValue));

                // Now update scores
                int scoreToAdd = part * 50;
                Integer previousTime = null; // For tie breaking
                int offset = 0; // For tie breaking
                for (Map.Entry entry : entryList) {
                    int scoredParticipantId = (int) entry.getKey();

                    int currentTime = (int) entry.getValue();
                    if (previousTime != null && previousTime == currentTime) {
                        offset += 1;
                    } else {
                        offset = 0;
                    }
                    previousTime = currentTime;

                    if (scoreToAdd + offset <= 0) {
                        break;
                    }
                    fullScores.put(scoredParticipantId, fullScores.get(scoredParticipantId) + scoreToAdd + offset);

                    // ARTISANAL DEBUGGING
                    // System.out.println("id=" + scoredParticipantId + ", time=" + currentTime + ", points=" + (scoreToAdd + offset));

                    scoreToAdd -= 1;
                }
            }
        }

        List<Integer> listOfScores = new ArrayList<>(fullScores.values());
        listOfScores.sort(Comparator.reverseOrder());
        System.out.println(calculateChecksum(listOfScores));
    }

    private static Set<Integer> allParticipantIds(ParseResult parsed) {
        Set<Integer> participantIds = new HashSet<>();
        for (ParticipantResult result : parsed.results()) {
            participantIds.add(result.id());
        }
        return participantIds;
    }

    private static Set<Integer> allQuests(ParseResult parsed) {
        Set<Integer> quests = new HashSet<>();
        for (ParticipantResult result : parsed.results()) {
            quests.add(result.quest());
        }
        return quests;
    }
}
