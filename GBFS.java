package com.company;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GBFS {
    public static void search(State initialState) {
        if (isGoal(initialState)) {
            result(initialState);
            return;
        }
        Queue<State> frontier = new PriorityQueue<>();
        Hashtable<String, Boolean> inFrontier = new Hashtable<>();
        Hashtable<String, Boolean> explored = new Hashtable<>();
        initialState.setCost(calculatingHeuristicfuncCost(initialState));
        frontier.add(initialState);
        inFrontier.put(initialState.hash(), true);
        while (!frontier.isEmpty()) {
            State tempState = frontier.poll();
            inFrontier.remove(tempState.hash());
            ArrayList<State> children = tempState.successor();
            if (isGoal(tempState)) {
                result(tempState);
                return;
            }
            explored.put(tempState.hash(), true);
            for (State child : children){
                if (!(inFrontier.containsKey(child.hash()))
                        && !(explored.containsKey(child.hash()))) {
                    child.setCost(calculatingHeuristicfuncCost(child));
                    frontier.add(child);
                    inFrontier.put(child.hash(), true);
                }
            }
        }
    }

    private static boolean isGoal(State state) {
        for (int i = 0; i < state.getGraph().size(); i++) {
            if (state.getGraph().getNode(i).getColor() == Color.Red
                    || state.getGraph().getNode(i).getColor() == Color.Black) {
                return false;
            }
        }
        return true;
    }


    private static int calculatingHeuristicfuncCost(State state) {
        int heuristicCost = 0;
        int numberOfNonGreenNodes = 0;
        Graph graph = state.getGraph().copy();
        Queue<Node> queueOfNeighborsNumbers = new PriorityQueue<Node>();
        for (int i = 0; i < graph.size(); i++) {
            int numberOfNGN = 0;
            if (graph.getNode(i).getColor() != Color.Green) {
                numberOfNonGreenNodes++;
                numberOfNGN++;
            }
            for (int j = 0; j <graph.getNode(i).getNeighborsIds().size(); j++) {
                if (graph.getNode(graph.getNode(i).getNeighborsId(j)).getColor() != Color.Green) {
                    numberOfNGN++;
                }
            }
            graph.getNode(i).setNumberOfNGN(numberOfNGN);
            queueOfNeighborsNumbers.add(graph.getNode(i));
        }
        while (numberOfNonGreenNodes > 0) {
            numberOfNonGreenNodes -= Objects.requireNonNull(queueOfNeighborsNumbers.poll()).getNumberOfNGN();
            heuristicCost++;
        }
        return heuristicCost;
    }



    private static void result(State state) {
        Stack<State> states = new Stack<State>();
        while (true) {
            states.push(state);
            if (state.getParentState() == null) {
                break;
            } else {
                state = state.getParentState();
            }
        }
        try {
            int count = 0;
            FileWriter myWriter = new FileWriter("GbfsResult.txt");
            System.out.println("initial state : ");
            while (!states.empty()) {
                count++;
                State tempState = states.pop();
                if (tempState.getSelectedNodeId() != -1) {
                    System.out.println("selected id : " + tempState.getSelectedNodeId());
                }
                tempState.getGraph().print();

                myWriter.write(tempState.getSelectedNodeId() + " ,");
                myWriter.write(tempState.outputGenerator() + "\n");
            }
            myWriter.close();
            System.out.println(count);
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}
