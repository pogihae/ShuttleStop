package com.msp.model;

import javafx.scene.control.Label;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class NodeFx extends Circle {
    private static final int RAD = 15;

    public static boolean isPrimaryClicked = false;
    public static boolean isSecondaryClicked = false;
    public static NodeFx toRemove = null;

    public static int nodeCnt = 0;
    public static List<NodeFx> selectedNodes = new ArrayList<>(2);

    public Label idLabel;
    public int id;
    public double x,y;
    public boolean selected;

    //어느 Group이던 속해있는지 확인하는 bool
    public boolean grouped;
    //Node의 list
    public static ArrayList<NodeFx> NodeList = new ArrayList<>();
    //각 Node의 adj list
    public ArrayList<EdgeFx> adj = new ArrayList<>();

    public NodeFx(double x, double y) {
        super(x,y,RAD);
        this.x = x;
        this.y = y;
        selected = false;
        grouped = false;

        //set node name
        idLabel = new Label(Integer.toString(nodeCnt++));
        idLabel.setOnMouseClicked(this::handleNodeClick);
        idLabel.setPickOnBounds(false);
        idLabel.setFont(new Font(30));
        idLabel.setLayoutX(x-10);
        idLabel.setLayoutY(y-20);

        id = nodeCnt-1;

        setBlendMode(BlendMode.MULTIPLY);
        setOpacity(0.5);
        setFill(Color.CORAL);

        setOnMouseClicked(this::handleNodeClick);

        //Node가 생성되면 NodeList에 추가
        NodeList.add(this);
    }

    public void handleNodeClick(MouseEvent e) {
        if(e.getButton() == MouseButton.PRIMARY) {
            isPrimaryClicked = true;
            if(selected) {
                selected = false;
                selectedNodes.remove(this);
                setFill(Color.BLUE);
            }
            else {
                selected = true;
                selectedNodes.add(this);
                setFill(Color.RED);
            }
        }
        else if(e.getButton() == MouseButton.SECONDARY) {
            isSecondaryClicked = true;
            toRemove = this;

            //남아있는 Node 중 삭제된 Node보다 숫자가 큰 Node는 전부 숫자를 1씩 낮춤, nodeCnt도 1 감소
            int rmvnum = this.id;
            for (NodeFx n: NodeList){
                int tmp = n.id;
                if(tmp > rmvnum){
                    n.id--;
                    n.idLabel.setText(Integer.toString(n.id));
                }
            }
            nodeCnt--;
            //Node가 지워지면 NodeList에서도 삭제
            NodeList.remove(this);
        }

    }

    public static NodeFx getNode(int ID) {
        return NodeList.stream()
                .filter(n -> n.id == ID)
                .findFirst()
                .get();
    }

    public static void clearSelected() {
        selectedNodes.get(0).unselect();
        selectedNodes.get(1).unselect();
        selectedNodes.clear();
    }

    public void unselect() {
        this.setFill(Color.BLUE);
        selected = false;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof NodeFx human) {
            return human.id == this.id;
        }else {
            return false;
        }
    } // equals 동등성비교

    @Override
    public int hashCode() {
        return id;
    } // hashCode 재정의
}
