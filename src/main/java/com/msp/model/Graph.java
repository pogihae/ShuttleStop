package com.msp.model;

import java.util.*;

public class Graph {
    public final Map<Integer, List<EdgeFx>> adj;
    public int[][] path;

    public Graph() {
        adj = new HashMap<>();

        for (EdgeFx e : EdgeFx.edgeList) {
            int src = e.source.id;
            int dest = e.target.id;

            EdgeFx inverseE = new EdgeFx(e.target, e.source);

            if(!adj.containsKey(src)) {
                adj.put(src, new LinkedList<>());
            }

            if(!adj.containsKey(dest)) {
                adj.put(dest, new LinkedList<>());
            }

            adj.get(src).add(e);
            adj.get(dest).add(inverseE);
        }
    }

    public double[][] floydwarshall() {
        int size = adj.size();

        double[][] dp = new double[size][size];
        path = new int[size][size];

        for(int i = 0; i < size; i++) {
            Arrays.fill(path[i], -1);
        }

        for (int i = 0; i < size; i++) {
            List<EdgeFx> temp = adj.getOrDefault(i, new LinkedList<>());
            for (int j = 0; j < size; j++) {
                for (EdgeFx e : temp) {
                    if (e.target.id == j) {
                        dp[i][j] = e.weight;
                        path[i][j] = j;
                        break;
                    }
                }
                if(i == j) path[i][j] = i;
                if(i != j && dp[i][j] == 0) dp[i][j] = Double.MAX_VALUE;
            }
        }
        for (int k = 0; k < size; ++k) {
            for (int i = 0; i < size; ++i) {
                if (i == k) continue;
                for (int j = 0; j < size; ++j) {
                    if (j == k || i == j) continue;
                    if (dp[i][k] + dp[k][j] < dp[i][j]) {
                        dp[i][j] = dp[i][k] + dp[k][j];

                        path[i][j] = path[i][k];
                    }
                }
            }
        }
        return dp;
    }

    public Queue<Integer> getPath(int start, int end) {
        Queue<Integer> queue = new LinkedList<>();
        queue.add(start);
        getPath(queue, start, end);
        return queue;
    }

    public void getPath(Queue<Integer> queue, int start, int end) {
        if(start != end) {
            start = path[start][end];
            queue.add(start);
            getPath(queue, start, end);
        }
    }

    public Iterable<EdgeFx> dijkstra(int start, int end) {
        Queue<EdgeFx> pq = new PriorityQueue<>();
        boolean[] visited = new boolean[adj.size()];
        Stack<EdgeFx> path = new Stack<>();
        double[] distTo = new double[adj.size()];

        for(int i=0; i<adj.size(); i++) {
            distTo[i] = Double.POSITIVE_INFINITY;
        }

        distTo[start] = 0;
        pq.addAll(adj.get(start));

        while(!pq.isEmpty()) {
            relax(pq.poll());
        }

        return path;
    }

    private void relax(EdgeFx e) {

    }
}