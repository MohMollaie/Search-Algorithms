package com.company;
import java.util.ArrayList;
import java.util.LinkedList;

public class State implements Comparable<State> {

    private Graph graph;
    private int selectedNodeId;
    private State parentState;
    private int cost;
    private int depth;
    private int cutOff;

    public State(Graph graph, int selectedNodeId, State parentState, int depth) {
        this.graph = graph.copy();
        this.selectedNodeId = selectedNodeId;
        if (parentState != null) {
            this.parentState = parentState;
        } else {
            this.parentState = null;
        }
        this.depth = depth;
    }

    public ArrayList<State> successor() {
        ArrayList<State> children = new ArrayList<State>();
        for (int i = 0; i < this.graph.size(); i++) {
            int nodeId = this.graph.getNode(i).getId();
            if (nodeId != selectedNodeId) {
                State newState = new State(this.graph.copy(), nodeId, this,this.depth + 1);
                LinkedList<Integer> nodeNeighbors = newState.getGraph().getNode(nodeId).getNeighborsIds();
                for (int j = 0; j < nodeNeighbors.size(); j++) {
                    int neighborId = nodeNeighbors.get(j);
                    newState.getGraph().getNode(neighborId).reverseNodeColor();
                }
                if (newState.getGraph().getNode(nodeId).getColor() == Color.Black) {
                    int greenNeighborsCount = 0;
                    int redNeighborsCount = 0;
                    int blackNeighborcount = 0;
                    for (int j = 0; j < nodeNeighbors.size(); j++) {
                        int neighborId = nodeNeighbors.get(j);
                        switch (newState.getGraph().getNode(neighborId).getColor()) {
                            case Green -> greenNeighborsCount++;
                            case Red -> redNeighborsCount++;
                            case Black -> blackNeighborcount++;
                        }
                    }
                    if (greenNeighborsCount > redNeighborsCount && greenNeighborsCount > blackNeighborcount) {
                        newState.getGraph().getNode(nodeId).changeColorTo(Color.Green);
                    } else if (redNeighborsCount > greenNeighborsCount && redNeighborsCount > blackNeighborcount) {
                        newState.getGraph().getNode(nodeId).changeColorTo(Color.Red);
                    }
                } else {
                    newState.getGraph().getNode(nodeId).reverseNodeColor();
                }
                children.add(newState);
            }
        }
        return children;
    }

    public String hash() {
        String result = "";
        for (int i = 0; i < graph.size(); i++) {
            switch (graph.getNode(i).getColor()) {
                case Green -> result += "g";
                case Red -> result += "r";
                case Black -> result += "b";
            }
        }
        return result;
    }

    public String outputGenerator() {
        String result = "";
        for (int i = 0; i < graph.size(); i++) {
            String color = switch (graph.getNode(i).getColor()) {
                case Red -> "R";
                case Green -> "G";
                case Black -> "B";
            };
            result += graph.getNode(i).getId() + color + " ";
            for (int j = 0; j < graph.getNode(i).getNeighborsIds().size(); j++) {
                int neighborId = graph.getNode(i).getNeighborsId(j);
                String neighborColor = switch (graph.getNode(neighborId).getColor()) {
                    case Red -> "R";
                    case Green -> "G";
                    case Black -> "B";
                };
                result += neighborId + neighborColor + " ";
            }
            result += ",";
        }
        return result;
    }

    @Override
    public int compareTo(State input) {
        return input.cost < this.cost ? 1 : -1;
    }

    public Graph getGraph() {
        return graph;
    }

    public State getParentState() {
        return parentState;
    }

    public int getSelectedNodeId() {
        return selectedNodeId;
    }

    public int getCost(){ return cost; }

    public void setCost(int cost){ if(cost>0) this.cost = cost; }

    public int getDepth(){ return depth; }

    public void setDepth(int depth){ if(depth>=0) this.depth = depth; }

    public void setCutOff(int cutOff){ this.cutOff = cutOff; }

    public int getCutOff(){return cutOff;}
}
