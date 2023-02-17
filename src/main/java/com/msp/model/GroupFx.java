package com.msp.model;

import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.*;

//가까이 있는 Node를 묶는 Group
public class GroupFx extends Circle {
    public static int RAD = 95;
    //전체 Group의 list
    public static List<GroupFx> groupList = new ArrayList<>();
    //각 Group을 대표하는 Node와 그 Node들을 잇는 경로의 list
    public static List<NodeFx> representNodeList = new ArrayList<>();
    //각 Group에 속하는 Node와 Edge의 list
    public List<NodeFx> nodeInGroupList = new ArrayList<>();
    public List<EdgeFx> edgeInGroupList = new ArrayList<>();
    //각 Group의 대표 Node
    public NodeFx representNode;

    public GroupFx(double x, double y) {
        super(x, y, RAD);
        setBlendMode(BlendMode.MULTIPLY);
        setOpacity(0.2);
        setFill(Color.GREEN);
        groupList.add(this);
    }

    //모든 Group의 대표 Node를 찾아서 list에 저장하고, 경로를 구성하는 Edge도 찾아서 list에 저장
    public static void findAllRepresentNode() {
        //모든 Group의 대표 Node를 list에 저장
        for (GroupFx g : groupList) {
            representNodeList.add(findRepresentNode(g));
        }
    }

    //입력된 Group에서 Floyd-Warshall로 다른 Node까지의 길이의 합이 가장 작은 Node - 대표 Node를 찾아서 return
    public static NodeFx findRepresentNode(GroupFx g) {
        List<NodeFx> nodes = g.nodeInGroupList;
        List<EdgeFx> edges = g.edgeInGroupList;
        //matrix의 편의를 위해 Node와 index를 mapping
        HashMap<NodeFx, Integer> nodeToIndex = new HashMap<>();
        HashMap<Integer, NodeFx> intToNode = new HashMap<>();
        Integer index = 0;
        for (NodeFx n : nodes) {
            nodeToIndex.put(n, index);
            intToNode.put(index, n);
            index++;
        }
        //matrix 초기화
        int length = nodeToIndex.size();
        double[][][] matrix = new double[length + 1][length][length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                matrix[0][i][j] = 10000;
            }
            matrix[0][i][i] = 0;
        }
        //0번째 matrix
        for (EdgeFx e : edges) {
            matrix[0][nodeToIndex.get(e.source)][nodeToIndex.get(e.target)] = e.weight;
            matrix[0][nodeToIndex.get(e.target)][nodeToIndex.get(e.source)] = e.weight;
        }
        //Floyd-Warshall
        for (int k = 1; k < length + 1; k++) {
            for (int i = 0; i < length; i++) {
                for (int j = 0; j < length; j++) {
                    matrix[k][i][j] = Math.min(
                            matrix[k - 1][i][j], matrix[k - 1][i][k - 1] + matrix[k - 1][k - 1][j]
                    );
                }
            }
        }
        //다른 Node까지의 거리의 합이 가장 작은 Node의 index를 찾음
        int minindex = 0;
        double minsum = 10000;
        for (int i = 0; i < length; i++) {
            double tmp = 0;
            for (int j = 0; j < length; j++) {
                tmp += matrix[length][i][j];
            }
            if (minsum > tmp) {
                minindex = i;
                minsum = tmp;
            }
        }

        intToNode.get(minindex).setFill(Color.GREEN);
        //해당 index를 갖는 Node를 이 Group의 대표 Node로 설정하고 return
        g.representNode = intToNode.get(minindex);
        return intToNode.get(minindex);
    }

}
