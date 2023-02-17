package com.msp.model;

import javafx.scene.control.Label;
import javafx.scene.shape.Line;

import java.util.LinkedList;
import java.util.List;

public class EdgeFx implements Comparable<EdgeFx> {
    public NodeFx source, target;
    public double weight;
    public Line line;
    public Label weightLabel;

    public static List<EdgeFx>  edgeList = new LinkedList<>();

    public EdgeFx(NodeFx src, NodeFx dest) {
        source = src;
        target = dest;
        weight = calcManhattanDist(source, target);

        line = new Line(src.x, src.y, dest.x, dest.y);

        weightLabel = new Label();
        weightLabel.setText(String.format("%.2f",weight));
        weightLabel.setLayoutX(((src.x) + (dest.x)) / 2);
        weightLabel.setLayoutY(((src.y) + (dest.y)) / 2);

        if(edgeList.stream()
                .noneMatch(e -> e.source.equals(dest) && e.target.equals(source)))
            edgeList.add(this);
    }

    public static double calcManhattanDist(NodeFx a, NodeFx b) {
        double sum = 0;
        sum += Math.abs(a.x - b.x);
        sum += Math.abs(a.y - b.y);
        return sum;
    }

    //Edge가 교차하는지 판단하는 함수
    public static boolean intersect(EdgeFx a, EdgeFx b){
        return (CCW(a.source, a.target, b.source) * CCW(a.source, a.target, b.target) < 0)
                && CCW(b.source, b.target, a.source) * CCW(b.source, b.target, a.target) < 0;
    }
    //위 함수에 쓰이는 입력된 세 점이 CCW 방향인지 판단하는 함수
    public static int CCW(NodeFx a, NodeFx b, NodeFx c){
        double A1 = a.x * b.y + b.x * c.y + c.x * a.y;
        double A2 = a.y * b.x + b.y * c.x + c.y * a.x;
        double area = (A1 - A2) / 2;
        if (area < 0){
            return -1;
        }
        else{
            return 1;
        }
    }

    public static EdgeFx getEdge(int src, int dest) {
       return edgeList.stream()
               .filter(e -> (e.source.id == src && e.target.id == dest) ||
                e.target.id == src && e.source.id == dest)
               .findFirst()
               .get();
    }

    @Override
    public int compareTo(EdgeFx o) {
        return Double.compare(weight, o.weight);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof EdgeFx)) return false;
        EdgeFx eobj = (EdgeFx) obj;
        return (this.source.equals(eobj.source) && this.target.equals(eobj.target));
    }
}
