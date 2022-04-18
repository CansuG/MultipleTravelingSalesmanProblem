# The Multiple Traveling Salesman Problem

The [Multiple Traveling Salesman Problem](https://neos-guide.org/content/multiple-traveling-salesman-problem-mtsp) (*m*TSP) in which more than one salesman is allowed is a generalization of the Traveling Salesman Problem (TSP).
Given a set of cities, one depot (where *m* salesmen are located), and a cost metric, the objective of the *m*TSP is to determine a set of routes for *m* salesmen so as to minimize the total cost of the *m* routes. 
The cost metric can represent cost, distance, or time. The requirements on the set of routes are:

* All of the routes must start and end at the (same) depot.
* There must be at least one city (except depot) in each route.
* Each city must be visited exactly once by only one salesman.

**Multiple depots**: Instead of one depot, the multi-depot *m*TSP has a set of depots, with m<sub>j</sub> salesmen at each depot *j*. 
In the *fixed destination* version, a salesman returns to the same depot from which he started.

Please keep in mind that this project is based on the 81 cities of **Turkey**.

After 'mvn clean package', 'java -jar target/mTSP.jar -d 5 -s 2 -c r' or 'java -jar target/mTSP.jar -d 5 -s 2 -c R' print city indices according to 
random solution.

'java -jar target/mTSP.jar -d 5 -s 2 -c nn' or 'java -jar target/mTSP.jar -d 5 -s 2 -c NN' print city indices according to nearest neighbour solution.

'java -jar target/mTSP.jar -d 5 -s 2 -c nn -v' prints city names instead od indices.

After random or nearest neighbour solution, hill climbing algorithms are invoked. At the end, cost and time is calculated.

Solution is written into json file which is named according to its depot and salesman number.
