import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Road {
    String name;
    double distance; // in kilometers
    double qualityScore; // road quality score (0 to 10)
    double safetyLevel; // safety score (0 to 10)

    public Road(String name, double distance, double qualityScore, double safetyLevel) {
        this.name = name;
        this.distance = distance;
        this.qualityScore = qualityScore;
        this.safetyLevel = safetyLevel;
    }

    @Override
    public String toString() {
        return "Road: " + name + ", Distance: " + distance + " km, Quality Score: " + qualityScore + ", Safety Level: "
                + safetyLevel;
    }
}

class Route {
    String name; // Route name
    List<Road> roads;
    double totalDistance;
    double averageQualityScore;
    double averageSafetyLevel;

    public Route(String name) {
        this.name = name;
        this.roads = new ArrayList<>();
    }

    public void calculateTotals() {
        totalDistance = 0;
        averageQualityScore = 0;
        averageSafetyLevel = 0;

        for (Road road : roads) {
            totalDistance += road.distance;
            averageQualityScore += road.qualityScore;
            averageSafetyLevel += road.safetyLevel;
        }

        // Calculate averages
        if (roads.size() > 0) {
            averageQualityScore /= roads.size(); // Average quality score
            averageSafetyLevel /= roads.size(); // Average safety level
        }
    }

    @Override
    public String toString() {
        return name + " - Route with " + roads.size() + " roads, Total Distance: " + totalDistance
                + " km, Average Quality Score: " + averageQualityScore + ", Average Safety Level: "
                + averageSafetyLevel;
    }
}

class OptimizedPathSelection {

    // Method to filter routes where distance is more than 30% longer than the shortest route
    public List<Route> filterLongRoutes(List<Route> routes) {
        // Find the shortest route
        double minDistance = Double.MAX_VALUE;

        for (Route route : routes) {
            if (route.totalDistance < minDistance) {
                minDistance = route.totalDistance;
            }
        }

        // Filter out routes more than 30% longer than the shortest route
        List<Route> filteredRoutes = new ArrayList<>();
        for (Route route : routes) {
            if (route.totalDistance <= minDistance * 1.3) {
                filteredRoutes.add(route);
            }
        }
        return filteredRoutes;
    }

    public Route findOptimalRoute(List<Route> routes, double weightDistance, double weightQuality,
                                  double weightSafety) {
        Route optimalRoute = null;
        double bestScore = Double.MIN_VALUE;

        for (Route route : routes) {
            route.calculateTotals(); // Calculate totals before scoring
            double score = (weightDistance * route.totalDistance) +
                    (weightQuality * route.averageQualityScore) +
                    (weightSafety * route.averageSafetyLevel);

            if (score > bestScore) {
                bestScore = score;
                optimalRoute = route;
            } else if (score == bestScore) {
                // Tie-breaking based on distance (or any other criteria you prefer)
                if (optimalRoute.totalDistance > route.totalDistance) {
                    optimalRoute = route; // Choose the route with lesser distance
                }
            }
        }
        return optimalRoute;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Route> routes = new ArrayList<>();

        // Loop to collect multiple routes
        while (true) {
            System.out.println("Enter route name (or type 'exit' to finish):");
            String routeName = scanner.nextLine();
            if (routeName.equalsIgnoreCase("exit")) {
                break;
            }

            Route route = new Route(routeName); // Pass the route name to the Route constructor

            while (true) {
                System.out.println("Enter road name (or type 'done' to finish this route):");
                String roadName = scanner.nextLine();
                if (roadName.equalsIgnoreCase("done")) {
                    break;
                }

                System.out.println("Enter distance (in km):");
                double distance = scanner.nextDouble();

                System.out.println("Enter road quality score (0 to 10):");
                double qualityScore = scanner.nextDouble();

                System.out.println("Enter safety level (0 to 10):");
                double safetyLevel = scanner.nextDouble();
                scanner.nextLine(); // Consume the newline character

                // Validation: Ensure distance, quality, and safety are within acceptable range
                if (distance <= 0 || qualityScore < 0 || qualityScore > 10 || safetyLevel < 0 || safetyLevel > 10) {
                    System.out.println("Invalid input, please enter valid values.");
                    continue; // Skip invalid input
                }

                // Create a new Road object
                Road road = new Road(roadName, distance, qualityScore, safetyLevel);
                route.roads.add(road);
            }

            routes.add(route);
        }

        // User-defined weight adjustment
        System.out.println("Enter weight for Distance (0 to 1):");
        double weightDistance = scanner.nextDouble();
        System.out.println("Enter weight for Quality (0 to 1):");
        double weightQuality = scanner.nextDouble();
        System.out.println("Enter weight for Safety (0 to 1):");
        double weightSafety = scanner.nextDouble();

        // Ensure the weights add up to 1.0
        double totalWeight = weightDistance + weightQuality + weightSafety;
        if (Math.abs(totalWeight - 1.0) > 0.0001) {
            System.out.println("Warning: The total weight should sum up to 1. Adjusting weights to balance.");
            double adjustmentFactor = 1.0 / totalWeight;
            weightDistance *= adjustmentFactor;
            weightQuality *= adjustmentFactor;
            weightSafety *= adjustmentFactor;
        }

        // Filter routes that are too long
        OptimizedPathSelection ops = new OptimizedPathSelection();
        routes = ops.filterLongRoutes(routes); // Updated to return filtered routes

        // Find the optimal route
        Route optimalRoute = ops.findOptimalRoute(routes, weightDistance, weightQuality, weightSafety);

        if (optimalRoute != null) {
            System.out.println("Optimal Route: " + optimalRoute);
        } else {
            System.out.println("No routes available.");
        }

        scanner.close();
    }
}
