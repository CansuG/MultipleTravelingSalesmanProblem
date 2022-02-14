package edu.anadolu;

import java.util.*;

import static edu.anadolu.TurkishNetwork.distance;
import static edu.anadolu.TurkishNetwork.cities;

public class mTSP {

    int depots;
    int salesmen;
    ArrayList<Integer>[] table;
    ArrayList<Integer> route = new ArrayList<>();   /* for one route with one depot in nearest neighbour */
    ArrayList<Integer>[] rollBack;        /* table for hill climbing to copy */

    static int swapNodesInRoute = 0;
    static int swapHubWithNodeInRoute = 0;
    static int swapNodesBetweenRoutes = 0;
    static int insertNodeInRoute = 0;
    static int insertNodeBetweenRoutes = 0;

    public mTSP(int depots, int salesmen) {

        this.depots = depots;
        this.salesmen = salesmen;
        table = new ArrayList[depots * salesmen];
        rollBack = new ArrayList[depots * salesmen];

    }


    public void randomSolution() {

        ArrayList<Integer> allCities = new ArrayList<>();

        for (int i = 0; i < 81; i++) {

            allCities.add(i, i);

        }

        Collections.shuffle(allCities);

        int allRoutes = salesmen * depots;            /* table.size= allRoutes */
        int division = allCities.size() / allRoutes;
        int remainder = allCities.size() % allRoutes;

        int counter = 0;

        /* divide shuffled cities into routes which have same size */
        for (int i = 0; i < table.length; i++) {

            ArrayList<Integer> routes = new ArrayList<>();

            for (int j = 0; j < division; j++) {

                routes.add(j, allCities.get(counter));

                counter++;
            }

            table[i] = routes;

        }

        /* put left cities into table */
        if (remainder != 0) {

            for (int i = 0; i < remainder; i++) {

                table[i].add(allCities.get(allCities.size() - 1 - i));
            }

        }

        /* select hub */
        ArrayList<Integer> HUBS = new ArrayList();
        while (true) {

            int rnd = (int) (Math.random() * allCities.size());

            if (!HUBS.contains(rnd)) {
                HUBS.add(rnd);
            }

            if (HUBS.size() == depots) {
                break;
            }

        }

        /* remove hubs from table */
        for (int i = 0; i < HUBS.size(); i++) {

            for (int j = 0; j < table.length; j++) {

                for (int k = 0; k < table[j].size(); k++) {

                    if (HUBS.get(i).equals(table[j].get(k))) {

                        table[j].remove(k);

                    }
                }
            }
        }

        /* put hubs into routes' 0. indexes because we use them at printing */
        int counterr = 0;
        for (int i = 0; i < table.length; i++) {

            table[i].add(0, HUBS.get(counterr));

            if (i != 0 && (i + 1) % salesmen == 0) {
                counterr++;
            }

        }

    }

    public void nearestNeighbour(int firstCity) {

        /* search nearest city, and add it to route. then search added city, and ... */

        route.add(firstCity);

        for (int i = 0; i < distance.length; i++) {

            int current_city = route.get(i);

            int found_city = searchNearestCity(current_city);

            route.add(found_city);

            if (route.size() == 81) {
                break;
            }
        }


        int allRoutes = salesmen * depots;            /* table.size= allRoutes */
        int division = (81 - depots) / allRoutes;     /* number of city in a route. table[i].length = division */
        int remainder = (81 - depots) % allRoutes;    /* number of city in last route is division+remainder */


        ArrayList<Integer> hubs = new ArrayList<>();   /* depots */

        /* put depots into hub */
        int count = 0;
        while (true) {

            hubs.add(route.get(count));
            count += division * salesmen + 1;

            if (hubs.size() == depots) {
                break;
            }
        }


        for (int i = 0; i < hubs.size(); i++) {
            route.remove(hubs.get(i));   /* remove depots from route */
        }


        ArrayList<ArrayList<Integer>> table_list = new ArrayList<>();   /* list for routes */

        /* divide route into routes to table_list. except last route, other routes have same number of cities in them */
        for (int i = 0; i < route.size() - remainder; i += division) {

            table_list.add(new ArrayList<Integer>(route.subList(i, Math.min(route.size() - remainder, i + division))));

        }

        /* add left cities into last route  */
        for (int i = route.size() - remainder; i < route.size(); i++) {

            table_list.get(table_list.size() - 1).add(route.get(i));

        }

        /* put depots into 0. indexes in routes according to salesman number */
        int hub_counter = 0;

        for (int i = 0; i < table_list.size(); i++) {

            table_list.get(i).add(0, hubs.get(hub_counter));

            if ((i + 1) % salesmen == 0) {
                hub_counter++;
            }

        }

        /* transfer cities in table_list into table_for_nn */
        for (int i = 0; i < table.length; i++) {
            table[i] = table_list.get(i);
        }


    }

    public Integer searchNearestCity(int currentCity) {

        int best_neighbour = currentCity;
        int best_neighbour_length = Integer.MAX_VALUE;
        int current_neighbour_length;


        for (int i = 0; i < distance.length; i++) {

            if (!route.contains(i)) {    /*  we added cities step by step. so we check found nearest city isn't visited before  */

                current_neighbour_length = distance[currentCity][i];

                if (current_neighbour_length < best_neighbour_length) {
                    best_neighbour_length = current_neighbour_length;
                    best_neighbour = i;
                }

            }
        }

        return best_neighbour;
    }

    public void hillClimbing() {
        int count = 0;
        int newCOST;
        int bestCost = cost(table);

        while (count != 5000000) {

            int rnd = (int) (Math.random() * 5);

            switch (rnd) {

                case 0:

                    rollBack = copyTABLE(table);
                    swapNodesInRoute(table);
                    newCOST = cost(table);
                    if (newCOST < bestCost) {
                        swapNodesInRoute++;
                        bestCost = newCOST;
                    } else {
                        table = copyTABLE(rollBack);
                    }
                    break;

                case 1:

                    rollBack = copyTABLE(table);
                    swapHubWithNodeInRoute(table);
                    newCOST = cost(table);
                    if (newCOST < bestCost) {
                        swapHubWithNodeInRoute++;
                        bestCost = newCOST;
                    } else {
                        table = copyTABLE(rollBack);
                    }
                    break;

                case 2:

                    rollBack = copyTABLE(table);
                    swapNodesBetweenRoutes(table);
                    newCOST = cost(table);
                    if (newCOST < bestCost) {
                        swapNodesBetweenRoutes++;
                        bestCost = newCOST;
                    } else {
                        table = copyTABLE(rollBack);
                    }
                    break;

                case 3:

                    rollBack = copyTABLE(table);
                    insertNodeInRoute(table);
                    newCOST = cost(table);
                    if (newCOST < bestCost) {
                        insertNodeInRoute++;
                        bestCost = newCOST;
                    } else {
                        table = copyTABLE(rollBack);
                    }
                    break;


                case 4:

                    rollBack = copyTABLE(table);

                    insertNodeBetweenRoutes(table);
                    newCOST = cost(table);
                    if (newCOST < bestCost) {
                        insertNodeBetweenRoutes++;
                        bestCost = newCOST;

                    } else {
                        table = copyTABLE(rollBack);
                    }
                    break;
            }

            count++;
        }

        System.out.println("swapNodesInRoute: " + swapNodesInRoute);
        System.out.println("swapHubWithNodeInRoute: " + swapHubWithNodeInRoute);
        System.out.println("swapNodesBetweenRoutes: " + swapNodesBetweenRoutes);
        System.out.println("insertNodeInRoute: " + insertNodeInRoute);
        System.out.println("insertNodeBetweenRoutes: " + insertNodeBetweenRoutes);

    }

    public ArrayList<Integer>[] copyTABLE(ArrayList<Integer>[] table) {

        ArrayList<Integer>[] copied = new ArrayList[salesmen * depots];

        for (int i = 0; i < table.length; i++) {

            ArrayList<Integer> EachRoute = new ArrayList<>();


            for (int j = 0; j < table[i].size(); j++) {

                int value = table[i].get(j);

                EachRoute.add(j, value);

            }
            copied[i] = EachRoute;
        }

        return copied;
    }

    public int[] generateRandomNumber(int lowerBound, int upperBound, boolean canSame) {
        int[] arr = new int[2];
        if (lowerBound == upperBound) {
            throw new RuntimeException("Alt ve üst sınır aynı olamaz!");
        }
        if (!canSame) {
            while (true) {
                int rnd = (int) (Math.random() * (upperBound - lowerBound) + lowerBound);
                int rnd1 = (int) (Math.random() * (upperBound - lowerBound) + lowerBound);
                if (rnd != rnd1) {
                    arr[0] = rnd;
                    arr[1] = rnd1;
                    break;
                }
            }
        } else {
            arr[0] = (int) (Math.random() * (upperBound - lowerBound) + lowerBound);
            arr[1] = (int) (Math.random() * (upperBound - lowerBound) + lowerBound);
        }
        return arr;
    }

    public ArrayList<Integer>[] swapNodesInRoute(ArrayList<Integer>[] table) {

        int selectRoute = (int) (Math.random() * table.length);  // select a random route's index

        if (table[selectRoute].size() >= 3) {

            int[] selectNodes = generateRandomNumber(1, table[selectRoute].size(), false);  // select random two nodes' indexes

            int temp1 = table[selectRoute].get(selectNodes[0]);  // Value of node 1
            int temp2 = table[selectRoute].get(selectNodes[1]);  // Value of node 2

            table[selectRoute].set(selectNodes[0], temp2);
            table[selectRoute].set(selectNodes[1], temp1);
        }
        return table;

    }

    public ArrayList<Integer>[] swapHubWithNodeInRoute(ArrayList<Integer>[] table) {

        int[] selectRoute = generateRandomNumber(0, table.length, true);  // select a random route's index
        int selectNodeIndex;

        while (true) {
            selectNodeIndex = (int) (Math.random() * table[selectRoute[0]].size());  // select random a node's index
            if (selectNodeIndex != 0) {
                break;
            }
        }

        int temp1 = table[selectRoute[0]].get(selectNodeIndex);  // Value of node

        int temp2 = table[selectRoute[1]].get(0);  // Value of hub

        if (App.n.equals("nn") || App.n.equals("NN")) {

            if (depots > 1) {

                for (int i = 0; i < table.length; i++) {

                    if (table[i].get(0) == temp2) {
                        table[i].set(0, temp1);     // make value of hub's routes have value of node as depot
                    }
                }

                table[selectRoute[0]].set(selectNodeIndex, temp2);   // set value of node to value of hub}


            }
        } else {
            for (int i = 0; i < table.length; i++) {

                if (table[i].get(0) == temp2) {

                    table[i].set(0, temp1);     // make value of hub's routes have value of node as depot

                }

            }

            table[selectRoute[0]].set(selectNodeIndex, temp2);   // set value of node to value of hub}
        }
        return table;
    }

    public ArrayList<Integer>[] swapNodesBetweenRoutes(ArrayList<Integer>[] table) {

        if (salesmen * depots > 1) {

            int[] selectRoute = generateRandomNumber(0, table.length, false);  // select random two routes' indexes

            if (table[selectRoute[0]].size() >= 2 & table[selectRoute[1]].size() >= 2) {

                int selectNodeIndex1;
                int selectNodeIndex2;
                while (true) {
                    selectNodeIndex1 = (int) (Math.random() * table[selectRoute[0]].size());
                    selectNodeIndex2 = (int) (Math.random() * table[selectRoute[1]].size());
                    if (selectNodeIndex1 != 0 && selectNodeIndex2 != 0) {
                        break;
                    }
                }
                int temp1 = table[selectRoute[0]].get(selectNodeIndex1);  // Value of node 1
                int temp2 = table[selectRoute[1]].get(selectNodeIndex2);  // Value of node 2

                table[selectRoute[0]].set(selectNodeIndex1, temp2);
                table[selectRoute[1]].set(selectNodeIndex2, temp1);
            }

        }

        return table;
    }

    public ArrayList<Integer>[] insertNodeBetweenRoutes(ArrayList<Integer>[] table) {

        if (table.length >= 2) {

            int[] selectRoutes = generateRandomNumber(0, table.length, false);
            if (table[selectRoutes[0]].size() >= 3) {

                int selectNodeIndex1;
                int selectNodeIndex2;
                while (true) {
                    selectNodeIndex1 = (int) (Math.random() * table[selectRoutes[0]].size());
                    selectNodeIndex2 = (int) (Math.random() * table[selectRoutes[1]].size());
                    if (selectNodeIndex1 != 0 && selectNodeIndex2 != 0) {
                        if (selectNodeIndex1 != selectNodeIndex2)
                            break;
                    }
                }
                int Node1Value = table[selectRoutes[0]].get(selectNodeIndex1);
                table[selectRoutes[1]].add(selectNodeIndex2 + 1, Node1Value);  // add node 1 to right of node 2
                table[selectRoutes[0]].remove(selectNodeIndex1);             // delete node 1 from the beginning place
            }
        }
        return table;
    }

    public ArrayList<Integer>[] insertNodeInRoute(ArrayList<Integer>[] table) {
        int selectRoute = (int) (Math.random() * table.length);

        if (table[selectRoute].size() >= 3) {

            int[] selectNodeIndexes = generateRandomNumber(1, table[selectRoute].size(), false); // Indexes of two nodes
            int Node1Value = table[selectRoute].get(selectNodeIndexes[0]);

            if (selectNodeIndexes[0] < selectNodeIndexes[1]) {
                table[selectRoute].add(selectNodeIndexes[1] + 1, Node1Value);
                table[selectRoute].remove(selectNodeIndexes[0]);
            } else {
                table[selectRoute].add(selectNodeIndexes[1] + 1, Node1Value);
                table[selectRoute].remove(selectNodeIndexes[0] + 1);
            }
        }
        return table;
    }

    public int cost(ArrayList<Integer>[] table) {
        int cost = 0;

        /* add depots to routes' last indexes */
        for (int i = 0; i < table.length; i++) {

            table[i].add(table[i].size(), table[i].get(0));

        }

        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].size(); j++) {

                if (j + 1 != table[i].size()) {

                    cost += distance[table[i].get(j)][table[i].get(j + 1)];
                }
            }
        }

        /* remove depots from routes' last indexes */
        for (int i = 0; i < table.length; i++) {

            table[i].remove(table[i].size() - 1);

        }

        return cost;
    }

    public int cost() {
        int cost = 0;

        /* add depots to routes' last indexes */
        for (int i = 0; i < table.length; i++) {

            table[i].add(table[i].size(), table[i].get(0));

        }

        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].size(); j++) {

                if (j + 1 != table[i].size()) {

                    cost += distance[table[i].get(j)][table[i].get(j + 1)];
                }
            }
        }

        /* remove depots from routes' last indexes */
        for (int i = 0; i < table.length; i++) {

            table[i].remove(table[i].size() - 1);

        }

        return cost;
    }

    public void print(boolean verbose) {

        int counter = 1;

        for (int i = 0; i < table.length; i++) {
            if (i % salesmen == 0) {

                if (verbose) System.out.println("HUB Number: " + (cities[table[i].get(0)] + 1));
                else System.out.println("HUB Number: " + (table[i].get(0) + 1));

            }

            System.out.print("     Route" + counter + ": ");

            for (int j = 1; j < table[i].size(); j++) {

                if (verbose) System.out.print((cities[table[i].get(j)] + 1) + " ");
                else System.out.print((table[i].get(j) + 1) + " ");

            }
            System.out.println();

            if (counter == salesmen) {
                counter = 0;
            }
            counter++;
        }


    }
}
