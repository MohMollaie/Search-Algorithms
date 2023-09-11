package com.company;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class UCS {
    public static void search(State initialState) {
        if (isGoal(initialState)) {
            result(initialState);
            return;
        }
        Queue<State> frontier = new PriorityQueue<>();
        Hashtable<String, Boolean> explored = new Hashtable<>();
        initialState.setCost(0);
        frontier.add(initialState);
        while (!frontier.isEmpty()) {
            State tempState = frontier.poll();
            if (!explored.containsKey(tempState.hash())) {
                ArrayList<State> children = tempState.successor();
                if (isGoal(tempState)) {
                    result(tempState);
                    return;
                }
                explored.put(tempState.hash(), true);
                for (State child : children) {
                    int currentCosts = tempState.getCost();
                    if (!(explored.containsKey(child.hash()))) {
                        switch (tempState.getGraph().getNode(child.getSelectedNodeId()).getColor()) {
                            case Green -> currentCosts += 3;
                            case Red -> currentCosts++;
                            case Black -> currentCosts += 2;
                        }
                        child.setCost(currentCosts);
                        frontier.add(child);
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
            FileWriter myWriter = new FileWriter("UcsResult.txt");
            System.out.println("initial state : ");
            while (!states.empty()) {
                State tempState = states.pop();
                if (tempState.getSelectedNodeId() != -1) {
                    System.out.println("selected id : " + tempState.getSelectedNodeId());
                }
                count++;
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
