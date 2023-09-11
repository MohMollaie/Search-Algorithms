package com.company;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class AStar {
    public static void search(State initialState) {
        if (isGoal(initialState)) {
            result(initialState);
            return;
        }
        Queue<State> frontier = new PriorityQueue<>();
        Hashtable<String, Boolean> explored = new Hashtable<>();
        frontier.add(initialState);
        while (!frontier.isEmpty()) {
            State tempState = frontier.poll();
            if(!(explored.containsKey(tempState.hash()))) {
                ArrayList<State> children = tempState.successor();
                if (isGoal(tempState)) {
                    result(tempState);
                    return;
                }
                explored.put(tempState.hash(), true);
                for (State childState : children) {
                    if (!(explored.containsKey(childState.hash()))) {
                        childState.setCost(childState.getDepth() + calculatingHeuristicfuncCost(childState));
                        frontier.add(childState);
                    }
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
/*

    private static int gCost(State childrenState) {
        int numberOfRedNodes = 0;
        for (int i = 0; i < childrenState.getGraph().size(); i++) {
            if (childrenState.getGraph().getNode(i).getColor() == Color.Red) {
                numberOfRedNodes++;
            }
        }
        for (int i = 0; i < childrenState.getParentState().getGraph().size(); i++) {
            if (childrenState.getParentState().getGraph().getNode(i).getColor() == Color.Red) {
                numberOfRedNodes--;
            }
        }
        if (numberOfRedNodes < 0) numberOfRedNodes = 1 + childrenState.getParentState().getCost();
        else numberOfRedNodes += childrenState.getParentState().getCost() + 1;
        return numberOfRedNodes;
    }
*/

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
            FileWriter myWriter = new FileWriter("AstarResult.txt");
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
            System.out.println("Successfully wrote to the file.");
            System.out.println(count);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}
