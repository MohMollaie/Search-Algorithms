package com.company;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class IDS {
    public static void search(State initialState) {
        if (isGoal(initialState)) {
            result(initialState);
            return;
        }
        int k = 0;
        while (true) {
            Stack<State> frontier = new Stack<State>();
            Hashtable<String, Boolean> inFrontier = new Hashtable<>();
            Hashtable<String, Boolean> explored = new Hashtable<>();
            frontier.push(initialState);
            inFrontier.put(initialState.hash(), true);
            while (!frontier.empty()) {
                State tempState = frontier.pop();
                inFrontier.remove(tempState.hash());
                if (tempState.getDepth() < k) {
                explored.put(tempState.hash(), true);
                ArrayList<State> children = tempState.successor();
                    for (State child : children) {
                        if (!(inFrontier.containsKey(child.hash()))
                                && !(explored.containsKey(child.hash()))) {
                            if (isGoal(child)) {
                                result(child);
                                return;
                            }
                            frontier.add(child);
                            inFrontier.put(child.hash(), true);
                        }
                    }
                }
            }
            k++;
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
            FileWriter myWriter = new FileWriter("IdsResult.txt");
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
            System.out.println(count);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}
