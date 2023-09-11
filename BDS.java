package com.company;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class BDS {

    public static void search(State initialState) {
        if (isGoal(initialState)) {
            result(initialState, initialState);
            return;
        }
        Queue<State> forwardFrontier = new LinkedList<State>();
        Hashtable<String, Boolean> inForwardFrontier = new Hashtable<>();
        Hashtable<String, Boolean> forwardExplored = new Hashtable<>();
        Queue<State> backwardFrontier = new LinkedList<State>();
        Hashtable<String, Boolean> inBackwardFrontier = new Hashtable<>();
        Hashtable<String, Boolean> backwardExplored = new Hashtable<>();
        forwardFrontier.add(initialState);
        inForwardFrontier.put(initialState.hash(), true);
        State goalState = new State(initialState.getGraph().copy(), -1, null, 0);
        for (int i = 0; i < goalState.getGraph().size(); i++) {
            goalState.getGraph().getNode(i).changeColorTo(Color.Green);
        }
        backwardFrontier.add(goalState);
        inBackwardFrontier.put(goalState.hash(), true);
        while (!forwardFrontier.isEmpty() && !backwardFrontier.isEmpty()) {
            State forwardTempState = forwardFrontier.poll();
            inForwardFrontier.remove(forwardTempState.hash());
            forwardExplored.put(forwardTempState.hash(), true);
            ArrayList<State> forwardChildren = forwardTempState.successor();
            State backwardTempState = backwardFrontier.poll();
            inBackwardFrontier.remove(backwardTempState.hash());
            backwardExplored.put(backwardTempState.hash(), true);
            ArrayList<State> backwardChildren = backwardTempState.successor();
            for (State forwardChild : forwardChildren) {
                if (!(inForwardFrontier.containsKey(forwardChild.hash()))
                        && !(forwardExplored.containsKey(forwardChild.hash()))) {
                    if (inBackwardFrontier.containsKey(forwardChild.hash())) {
                        for (State backstate : backwardFrontier) {
                            if (isEqual(backstate, forwardChild)) {
                                result(forwardChild, backstate);
                                return;
                            }
                        }
                    }
                    forwardFrontier.add(forwardChild);
                    inForwardFrontier.put(forwardChild.hash(), true);
                }
            }
            for (State backChild : backwardChildren) {
                if (!(inBackwardFrontier.containsKey(backChild.hash()))
                        && !(backwardExplored.containsKey(backChild.hash()))) {
                    if (inForwardFrontier.containsKey(backChild.hash())) {
                        for (State forstate : forwardFrontier) {
                            if (isEqual(backChild, forstate)) {
                                result(forstate, backChild);
                                return;
                            }
                        }
                    }
                    backwardFrontier.add(backChild);
                    inBackwardFrontier.put(backChild.hash(), true);
                }
            }
        }
    }

    private static boolean isEqual(State firstS, State secondS) {
        boolean result = true;
        for (int i = 0; i < firstS.getGraph().size(); i++) {
            if (firstS.getGraph().getNode(i).getColor() != secondS.getGraph().getNode(i).getColor()) {
                result = false;
            }
        }
        return result;
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

    private static void result(State forwardState, State backwardState) {
        Stack<State> forwardstates = new Stack<State>();
        Queue<State> backwardstates = new LinkedList<State>();
        while (true) {
            forwardstates.push(forwardState);
            if (forwardState.getParentState() == null) {
                break;
            } else {
                forwardState = forwardState.getParentState();
            }
        }
        while (true) {
            backwardstates.add(backwardState);
            if (backwardState.getParentState() == null) {
                break;
            } else {
                backwardState = backwardState.getParentState();
            }
        }
        try {
            int count = 0;
            FileWriter myWriter = new FileWriter("BdsResult.txt");
            System.out.println("initial state : ");
            while (!forwardstates.empty()) {
                count++;
                State tempState = forwardstates.pop();
                if (tempState.getSelectedNodeId() != -1) {
                    System.out.println("selected id : " + tempState.getSelectedNodeId());
                }
                tempState.getGraph().print();

                myWriter.write(tempState.getSelectedNodeId() + " ,");
                myWriter.write(tempState.outputGenerator() + "\n");
            }
            while (backwardstates.peek().getSelectedNodeId() != -1) {
                count++;
                State tempState = backwardstates.poll();
                System.out.println("selected id : " + tempState.getSelectedNodeId());
                tempState.getParentState().getGraph().print();
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


