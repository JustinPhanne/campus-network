import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class CampusNetworkPlanner {

    // Attributes
    private int connectionCount;
    private String fileName;
    private List<Edge> edges = new ArrayList<>();
    private Set<String> campusSet = new HashSet<>();

    // Constructor
    public CampusNetworkPlanner(int connectionCount, String fileName) throws FileNotFoundException {
        this.connectionCount = connectionCount;
        this.fileName = fileName;
        readInputFile();
    }

    // Read file and store connections
    private void readInputFile() throws FileNotFoundException {
        Scanner fileScanner = new Scanner(new File(fileName));
        while (fileScanner.hasNextLine()) {
            String[] parts = fileScanner.nextLine().split("\\s+");
            String campusA = parts[0];
            String campusB = parts[1];
            int cost = Integer.parseInt(parts[2]);

            // Create edge and add to list
            edges.add(new Edge(campusA, campusB, cost));
            campusSet.add(campusA);
            campusSet.add(campusB);
        }
        fileScanner.close();
    }

    // Kruskal's Algorithm to build MST
    public String buildNetwork() {
        List<Edge> result = new ArrayList<>();
        int totalCost = 0;

        // Sort edges by cost
        Collections.sort(edges);

        // Initialize disjoint set
        UnionFind uf = new UnionFind(campusSet);

        // Process edges in sorted order
        for (Edge edge : edges) {
            // Find roots of the campuses
            String rootA = uf.find(edge.campusA);
            String rootB = uf.find(edge.campusB);

            // If they are in different sets, include this edge in the result
            if (!rootA.equals(rootB)) {
                result.add(edge);
                totalCost += edge.cost;
                uf.union(rootA, rootB);
            }
        }

        // Sort result lexicographically
        result.sort((e1, e2) -> {
            int cmp = e1.campusA.compareTo(e2.campusA);
            return (cmp != 0) ? cmp : e1.campusB.compareTo(e2.campusB);
        });

        // Format output
        StringBuilder sb = new StringBuilder();
        for (Edge edge : result) {
            sb.append(edge.campusA).append("---").append(edge.campusB).append(" $").append(edge.cost).append("\n");
        }
        sb.append("Total Cost: $").append(totalCost);
        return sb.toString();
    }

    // Edge class with comparison logic
    private static class Edge implements Comparable<Edge> {
        String campusA, campusB;
        int cost;

        Edge(String a, String b, int c) {
            // Store lexicographically ordered campuses
            if (a.compareTo(b) < 0) {
                this.campusA = a;
                this.campusB = b;
            } else {
                this.campusA = b;
                this.campusB = a;
            }
            this.cost = c;
        }

        // Compare edges based on cost
        @Override
        public int compareTo(Edge other) {
            return Integer.compare(this.cost, other.cost);
        }
    }

    // Union-Find (Disjoint Set) class
    private static class UnionFind {
        private Map<String, String> parent;

        // Constructor initializes each campus as its own parent
        UnionFind(Set<String> campuses) { 
            parent = new HashMap<>(); 
            for (String campus : campuses) { // Initialize each campus
                parent.put(campus, campus);
            }
        }

        // Find with path compression
        String find(String x) {
            if (!parent.get(x).equals(x)) { // If x is not its own parent
                parent.put(x, find(parent.get(x))); // Path compression
            }
            return parent.get(x);
        }

        // Union by rank (not implemented here, but can be added for optimization)
        void union(String a, String b) { // Union two sets
            String rootA = find(a); 
            String rootB = find(b);
            if (!rootA.equals(rootB)) { // If they are in different sets
                parent.put(rootA, rootB); // Make rootA point to rootB
            }
        }
    }
}
