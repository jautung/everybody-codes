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

    private record ParseResult(List<CloudSpec> cloudSpecs) {}
    private static ParseResult parseFromFile(String path) {
        List<String> lines = Util.readLines(path);
        List<CloudSpec> cloudSpecs = lines.stream().map(line -> parseLine(line)).collect(Collectors.toList());
        return new ParseResult(cloudSpecs);
    }

    private record CloudSpec(int id, int energy, List<Integer> connections) {}
    private static CloudSpec parseLine(String line) {
        List<String> lineParts = List.of(line.split(", "));
        if (lineParts.size() != 3) {
            throw new IllegalArgumentException("Line does not have 3 parts: " + line);
        }

        String idPart = lineParts.get(0);
        String energyPart = lineParts.get(1);
        String connectionsPart = lineParts.get(2);

        String idStr = getValueWithAssignment(idPart, "id");
        String energyStr = getValueWithAssignment(energyPart, "energy");
        String connectionsStr = getValueWithAssignment(connectionsPart, "connections");

        int id = Integer.parseInt(idStr);
        int energy = Integer.parseInt(energyStr);
        List<Integer> connections = List.of(connectionsStr.split(",")).stream().map(connection -> Integer.parseInt(connection)).collect(Collectors.toList());

        return new CloudSpec(id, energy, connections);
    }

    private static String getValueWithAssignment(String assignment, String prefix) {
        if (!assignment.startsWith(prefix)) {
            throw new IllegalArgumentException("String " + assignment + " does not start with " + prefix);
        }
        return assignment.substring(prefix.length() + 1);
    }

    private static void runPart1(ParseResult parsed) {
        int STARTING_IMPULSE = 5;
        int startingId = idWithMostConnections(parsed);
        List<ImpulseEvent> allImpulses = simulateImpulses(parsed, new ImpulseEvent(startingId, STARTING_IMPULSE));
        int totalOriginal = parsed.cloudSpecs().stream().map(cloud -> cloud.energy()).mapToInt(Integer::intValue).sum();
        int totalAdded = allImpulses.stream().map(event -> event.impulse()).mapToInt(Integer::intValue).sum();
        System.out.println(totalOriginal + totalAdded);
    }

    private record ImpulseEvent(int id, int impulse) {}
    private static List<ImpulseEvent> simulateImpulses(ParseResult parsed, ImpulseEvent initialEvent) {
        List<ImpulseEvent> allEvents = new ArrayList<>();
        Queue<ImpulseEvent> toProcess = new LinkedList<>();

        allEvents.add(initialEvent);
        toProcess.add(initialEvent);

        while (!toProcess.isEmpty()) {
            ImpulseEvent currEvent = toProcess.remove();
            if (currEvent.impulse() == 1) {
                continue;
            }
            List<Integer> immediateConnections = connectionsForId(currEvent.id(), parsed);
            for (int immediateConnection : immediateConnections) {
                ImpulseEvent nextEvent = new ImpulseEvent(immediateConnection, currEvent.impulse() - 1);
                allEvents.add(nextEvent);
                toProcess.add(nextEvent);
            }
        }

        return allEvents;
    }

    private static int idWithMostConnections(ParseResult parsed) {
        Integer returnId = null;
        Integer maxNumberConnections = null;
        for (CloudSpec cloudSpec : parsed.cloudSpecs()) {
            if (maxNumberConnections == null || cloudSpec.connections().size() > maxNumberConnections) {
                returnId = cloudSpec.id();
                maxNumberConnections = cloudSpec.connections().size();
            }
        }
        if (returnId == null) {
            throw new IllegalArgumentException("No cloud specs at all!");
        }
        return returnId;
    }

    private static List<Integer> connectionsForId(int id, ParseResult parsed) {
        for (CloudSpec cloudSpec : parsed.cloudSpecs()) {
            if (cloudSpec.id() == id) {
                return cloudSpec.connections();
            }
        }
        throw new IllegalArgumentException("No connections for ID: " + id);
    }

    private static void runPart2(ParseResult parsed) {
        int STARTING_IMPULSE = 10;
        int startingId = idWithMostConnections(parsed);

        // Old version before part 3
        // List<ImpulseEvent> allImpulses = simulateImpulses(parsed, new ImpulseEvent(startingId, STARTING_IMPULSE));
        // int totalOriginal = parsed.cloudSpecs().stream().map(cloud -> cloud.energy()).mapToInt(Integer::intValue).sum();
        // int totalAdded = allImpulses.stream().map(event -> event.impulse()).mapToInt(Integer::intValue).sum();
        // System.out.println(totalOriginal + totalAdded);

        // New version that is faster from part 3 anyway
        int remainingEnergy = remainingTotalEnergy(parsed, startingId, STARTING_IMPULSE, -1, false);
        System.out.println(remainingEnergy);
    }

    private static void runPart3(ParseResult parsed) {
        int EXPLOSION_LIMIT = 1000000;
        // int EXPLOSION_LIMIT = 100;
        int STARTING_IMPULSE = 10;

        Integer bestStartId = null;
        Integer bestFinalEnergy = null;

        for (int testStartId : allIds(parsed)) {
            int remainingEnergy = remainingTotalEnergy(parsed, testStartId, STARTING_IMPULSE, EXPLOSION_LIMIT, false);
            if (bestFinalEnergy == null || remainingEnergy > bestFinalEnergy) {
                bestStartId = testStartId;
                bestFinalEnergy = remainingEnergy;
            }
        }

        System.out.println(bestFinalEnergy);
    }

    private static List<Integer> allIds(ParseResult parsed) {
        return parsed.cloudSpecs().stream().map(cloudSpec -> cloudSpec.id()).collect(Collectors.toList());
    }

    private static int remainingTotalEnergy(ParseResult parsed, int startingId, int startingImpulse, int explosionLimit, boolean toPrintDebug) {
        // Set up initial energy totals
        HashMap<Integer, Integer> energyTotals = new HashMap<>();
        for (CloudSpec cloudSpec : parsed.cloudSpecs()) {
            energyTotals.put(cloudSpec.id(), cloudSpec.energy());
        }

        // Send the first pulse
        ImpulseEvent firstImpulse = new ImpulseEvent(startingId, startingImpulse);
        HashMap<ImpulseEvent, Integer> impulsesToCarry = new HashMap<>();
        impulsesToCarry.put(firstImpulse, 1);
        Set<Integer> previousNewlyExplodedIds = new HashSet<>();
        energyTotals.put(startingId, energyTotals.get(startingId) + startingImpulse);
        Set<Integer> explodedIds = new HashSet<>();

        // Special print stage 1
        if (toPrintDebug) {
            System.out.println("Stage 1:");
            prettyPrintImpulsesMap(impulsesToCarry);
            prettyPrintEnergyTotals(energyTotals, explodedIds);
            System.out.println();
        }

        int i = 0;
        while (true) {
            // Print stage
            if (toPrintDebug) {
                System.out.println("Stage " + (i+2) + ":");
            }

            // Simulate full stage
            SimulateStageResult simulateStageResult = simulateStage(impulsesToCarry, previousNewlyExplodedIds, energyTotals, explodedIds, startingImpulse, explosionLimit, toPrintDebug, parsed);
            impulsesToCarry = simulateStageResult.nextEvents();
            previousNewlyExplodedIds = simulateStageResult.newlyExplodedIds();

            if (impulsesToCarry.entrySet().size() == 0 && previousNewlyExplodedIds.size() == 0) {
                if (toPrintDebug) {
                    System.out.println("No more changes! 🎉");
                }
                break;
            }

            // Print stage
            if (toPrintDebug) {
                prettyPrintImpulsesMap(impulsesToCarry);
                prettyPrintEnergyTotals(energyTotals, explodedIds);
                System.out.println();
            }

            i++;
        }

        return energyTotals.entrySet().stream().filter(entry -> !explodedIds.contains(entry.getKey())).map(entry -> entry.getValue()).mapToInt(Integer::intValue).sum();
    }

    private static void prettyPrintImpulsesMap(HashMap<ImpulseEvent, Integer> impulsesToCarry) {
        for (Map.Entry<ImpulseEvent, Integer> currEntry : impulsesToCarry.entrySet()) {
            ImpulseEvent currEvent = currEntry.getKey();
            int numMultiple = currEntry.getValue();
            System.out.println("⚡️ cloud " + currEvent.id() + " received " + numMultiple + " impulse(s) with strength " + currEvent.impulse());
        }
    }

    private static void prettyPrintEnergyTotals(HashMap<Integer, Integer> energyTotals, Set<Integer> explodedIds) {
        Map<Integer, Integer> filteredTotals = energyTotals.entrySet().stream().filter(entry -> !explodedIds.contains(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        System.out.println("🔋 " + filteredTotals);
    }

    private record SimulateStageResult(HashMap<ImpulseEvent, Integer> nextEvents, Set<Integer> newlyExplodedIds) {}
    private static SimulateStageResult simulateStage(HashMap<ImpulseEvent, Integer> initialEvents, Set<Integer> previousNewlyExplodedIds, HashMap<Integer, Integer> energyTotals, Set<Integer> explodedIds, int startingImpulse, int explosionLimit, boolean toPrintDebug, ParseResult parsed) {
        HashMap<ImpulseEvent, Integer> nextEvents = new HashMap<>();

        // Do initial propagation
        for (Map.Entry<ImpulseEvent, Integer> currEntry : initialEvents.entrySet()) {
            ImpulseEvent currEvent = currEntry.getKey();
            int numMultiple = currEntry.getValue();

            if (currEvent.impulse() == 1) {
                continue;
            }

            List<Integer> immediateConnections = connectionsForId(currEvent.id(), parsed);
            for (int immediateConnection : immediateConnections) {
                if (explodedIds.contains(immediateConnection)) {
                    continue;
                }
                ImpulseEvent nextEvent = new ImpulseEvent(immediateConnection, currEvent.impulse() - 1);
                nextEvents.merge(nextEvent, numMultiple, (a, b) -> a + b);
            }

        }

        // Handle fan out from previous newly exploded IDs
        for (int newlyExplodedId : previousNewlyExplodedIds) {
            List<Integer> immediateConnections = connectionsForId(newlyExplodedId, parsed);
            for (int immediateConnection : immediateConnections) {
                if (explodedIds.contains(immediateConnection)) {
                    continue;
                }
                ImpulseEvent nextEvent = new ImpulseEvent(immediateConnection, startingImpulse);
                nextEvents.merge(nextEvent, 1, (a, b) -> a + b);
            }
        }

        // Update energy totals, record new explosions
        Set<Integer> newlyExplodedIds = new HashSet<>();
        for (Map.Entry<ImpulseEvent, Integer> nextEntry : nextEvents.entrySet()) {
            ImpulseEvent currEvent = nextEntry.getKey();
            int numMultiple = nextEntry.getValue();
            int newEnergy = energyTotals.get(currEvent.id()) + currEvent.impulse() * numMultiple;
            energyTotals.put(currEvent.id(), newEnergy);
        }
        for (Map.Entry<Integer, Integer> energyTotal : energyTotals.entrySet()) {
            int currId = energyTotal.getKey();
            int newEnergy = energyTotal.getValue();
            if (explosionLimit > 0 && newEnergy >= explosionLimit && !explodedIds.contains(currId)) {
                explodedIds.add(currId);
                newlyExplodedIds.add(currId);
                if (toPrintDebug) {
                    System.out.println("💥 cloud " + currId + " exploded at energy " + newEnergy);
                }
            }
        }

        return new SimulateStageResult(nextEvents, newlyExplodedIds);
    }
}
