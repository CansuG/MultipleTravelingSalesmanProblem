package edu.anadolu;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import static edu.anadolu.TurkishNetwork.cities;

/**
 * Hello world!
 */
public class App {

    static String n = "";

    public static void main(String[] args) throws Exception {

        long start = System.currentTimeMillis();

        Params params;
        try {
            params = CliFactory.parseArguments(Params.class, args);
        } catch (ArgumentValidationException e) {
            System.out.println(e.getMessage());
            return;
        }

        int limit = params.getNumDepots() * params.getNumSalesmen() + params.getNumDepots();
        if (limit > 81 || params.getNumDepots() < 0 || params.getNumSalesmen() < 0) {
            throw new Exception("Invalid input.Depot or Hub number is out of bound!!");
        }

        if (params.getInitial() < 1 || params.getInitial() > 81) {
            throw new Exception("The initial city that you choose for nearest neighbour solution is invalid. Please enter an integer value that is between 1-81 ");
        }

        if (params.choose().equals("r") || params.choose().equals("R")) {

            mTSP best = null;
            int minCost = Integer.MAX_VALUE;

            for (int i = 0; i < 100_000; i++) {

                mTSP mTSP = new mTSP(params.getNumDepots(), params.getNumSalesmen());

                mTSP.randomSolution();

                final int cost = mTSP.cost();

                if (cost < minCost) {
                    best = mTSP;
                    minCost = cost;
                }
            }


            if (best != null) {
                best.print(params.getVerbose());
                System.out.println("Random Solution **Total cost is " + best.cost());
            }

            best.hillClimbing();
            best.print(params.getVerbose());
            System.out.println("Hill Climbing **Total cost is " + best.cost());

            JsonPart(best, params);

        } else if (params.choose().equals("nn") || params.choose().equals("NN")) {

            mTSP mTSP_nn = new mTSP(params.getNumDepots(), params.getNumSalesmen());
            n = params.choose();
            mTSP_nn.nearestNeighbour(params.getInitial() - 1);

            mTSP_nn.print(params.getVerbose());
            System.out.println("Nearest Neighbour **Total cost is " + mTSP_nn.cost());

            mTSP_nn.hillClimbing();
            mTSP_nn.print(params.getVerbose());
            System.out.println("Hill Climbing **Total cost is " + mTSP_nn.cost());

            JsonPart(mTSP_nn, params);

        } else {
            throw new Exception("Please choose random or nearest random solution and enter R or NN. ");
        }

        long finish = System.currentTimeMillis();

        System.out.println("Time: " + (finish - start) + " milliseconds");

    }

    public static void JsonPart(mTSP best, Params params) {

        JSONObject mainObject = new JSONObject();
        JSONArray solutions = new JSONArray();

        JSONObject solutionsObject = new JSONObject();
        JSONArray routes = new JSONArray();
        for (int i = 0; i < best.table.length; i++) {
            String s = "";
            for (int j = 1; j < best.table[i].size(); j++) {
                if (params.getVerbose()) s += cities[best.table[i].get(j)] + " ";
                else s += best.table[i].get(j).toString() + " ";
            }
            s = s.substring(0, s.length() - 1);
            routes.add(s);

            if (routes.size() == params.getNumSalesmen()) {
                if (params.getVerbose()) solutionsObject.put("depot", cities[best.table[i].get(0)]);
                else solutionsObject.put("depot", best.table[i].get(0).toString());
                solutionsObject.put("routes", routes);
                solutions.add(solutionsObject);
                solutionsObject = new JSONObject();
                routes = new JSONArray();
            }
        }
        mainObject.put("solutions", solutions);

        try {
            Files.write(Paths.get("solution_d" + params.getNumDepots() + "s" + params.getNumSalesmen() + ".json"), mainObject.toJSONString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
