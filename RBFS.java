package com.company;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RBFS {
    public static void search(State initialState) {
        if (isGoal(initialState)) {
            result(initialState);
            return;
        }
        LinkedList<State> frontier = new LinkedList<>();
        initialState.setCutOff(1000000);
        frontier.add(initialState);
        State tempState = initialState;
        boolean firstTime = true;
        while (!frontier.isEmpty()) {
            if (firstTime || Objects.requireNonNull(tempState).getCost() < tempState.getParentState().getCutOff()) {
                firstTime = false;
                ArrayList<State> children = tempState.successor();
                Queue<State> queue = new PriorityQueue<>();
                for (State childState : children) {
                    childState.setCost(gCost(childState) + calculatingHeuristicfuncCost(childState));
                    queue.add(childState);
                }
                tempState = queue.poll();
                int tempStateIndex = frontier.size();
                if (tempState != null) {
                    if (tempState.getCost() < tempState.getParentState().getCutOff()) {
                        frontier.add(tempState);
                        if (queue.size() >= 1) {
                            tempState.setCutOff(Math.min(queue.peek().getCost(), tempState.getParentState().getCutOff()));
                            if (isGoal(tempState)) {
                                result(tempState);
                            }
                        }
                        while (!queue.isEmpty()) {
                            frontier.add(queue.poll());
                        }
                    } else {
                        for (int i = frontier.size() - 1; i >= 0; i--) {
                            if (isEqual(frontier.get(i), tempState.getParentState())) {
                                frontier.get(i).setCost(tempState.getCost());
                                break;
                            }
                            if (frontier.get(i).getDepth() < tempState.getParentState().getDepth()) break;
                        }
                        int indexOfMinCost = frontier.size() - 1;
                        int minCost = frontier.getLast().getCost();
                        for (int i = frontier.size() - 2; i >= 0; i--) {
                            if (frontier.get(i).getDepth() == tempState.getParentState().getDepth()) {
                                if (minCost > frontier.get(i).getCost()) {
                                    minCost = frontier.get(i).getCost();
                                    indexOfMinCost = i;
                                }
                            } else break;
                        }
                        tempState = frontier.get(indexOfMinCost);
                        queue = new PriorityQueue<>();
                        for (int i = frontier.size() - 1; i >= 0; i--) {
                            if (frontier.get(i).getDepth() == tempState.getDepth() && !isEqual(frontier.get(i), tempState)) {
                                queue.add(frontier.get(i));
                            }
                            if (frontier.get(i).getDepth() != tempState.getDepth()) break;
                        }
                        tempState.setCutOff(Math.min(queue.peek().getCost(), tempState.getParentState().getCutOff()));
                        if (isGoal(tempState)) {
                            result(tempState);
                        }
                    }
                }
            } else {
                for (int i = frontier.size() - 1; i >= 0; i--) {
                    if (isEqual(frontier.get(i), tempState.getParentState())) {
                        frontier.get(i).setCost(tempState.getCost());
                        break;
                    }
                }
                int indexOfMinCost = frontier.size() - 1;
                int minCost = frontier.getLast().getCost();
                for (int i = frontier.size() - 2; i >= 0; i--) {
                    if (frontier.get(i).getDepth() == tempState.getParentState().getDepth()) {
                        if (minCost > frontier.get(i).getCost()) {
                            minCost = frontier.get(i).getCost();
                            indexOfMinCost = i;
                        }
                    } else break;
                }
                tempState = frontier.get(indexOfMinCost);
                for (int i = frontier.size() - 1; i >= 0; i--) {
                    if (frontier.get(i).getDepth() > tempState.getDepth()) {
                        frontier.remove(i);
                    } else break;
                }
                Queue<State> queue = new PriorityQueue<>();
                for (int i = frontier.size() - 1; i >= 0; i--) {
                    if (frontier.get(i).getDepth() == tempState.getDepth() && !isEqual(frontier.get(i), tempState)) {
                        queue.add(frontier.get(i));
                    }
                    if (frontier.get(i).getDepth() != tempState.getDepth()) break;
                }
                tempState.setCutOff(Math.min(queue.peek().getCost(), tempState.getParentState().getCutOff()));
                if (isGoal(tempState)) {
                    result(tempState);
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
        if (firstS.getSelectedNodeId() != secondS.getSelectedNodeId()) result = false;
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
            for (int j = 0; j < graph.getNode(i).getNeighborsIds().size(); j++) {
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
