package com.msp;

import com.msp.model.EdgeFx;
import com.msp.model.Graph;
import com.msp.model.GroupFx;
import com.msp.model.NodeFx;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class CanvasController implements Initializable {
    @FXML
    private Button btnDone;
    @FXML
    private Group grpCanvas;
    @FXML
    private BorderPane borderPane;
    @FXML
    private VBox vBox;

    private Queue<NodeFx> busPath;
    private double totalDist = 0;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        grpCanvas.setOnMouseClicked(this::handleCanvasClick);
        btnDone.setOnAction(e -> handleBtnDone());
        vBox.setPrefHeight(StartController.height);
        setBackGround(StartController.imageUrl);
    }

    /*--------handler----------*/

    public void handleCanvasClick(MouseEvent e) {
        //left click on existed node
        if (NodeFx.isPrimaryClicked) {
            if (NodeFx.selectedNodes.size() == 2) {
                drawEdge();
            }
            return;
        }
        //right click on existed node
        if (NodeFx.isSecondaryClicked && NodeFx.toRemove != null) {
            remove();
            return;
        }
        //right click on empty space
        if (e.getButton() == MouseButton.SECONDARY) {
            return;
        }
        //left click on empty space
        //draw node
        NodeFx node = new NodeFx(e.getX(), e.getY());
        if (!grpCanvas.getChildren().contains(node)) {
            grpCanvas.getChildren().add(node);
            grpCanvas.getChildren().add(node.idLabel);
        }
    }

    public void handleBtnDone() {
        grouping();
        btnDone.setText("Next");
        btnDone.setOnAction(e -> handlePath());
    }

    /**
     * make bus path
     *
     * make path with must visit bus stop node
     * path start: bottom node
     * */
    public void handlePath() {
        GroupFx.groupList.forEach(g -> g.setFill(Color.TRANSPARENT));

        Graph graph = new Graph();

        //1. floyd-warshall with whole graph
        double[][] dist = graph.floydwarshall();

        GroupFx.representNodeList = GroupFx.representNodeList.stream()
                .distinct()
                .collect(Collectors.toList());

        //2. sort represent node list by y coordinate
        List<Integer> repNodeL = GroupFx.representNodeList.stream()
                .sorted(Comparator.comparing(n -> n.y))
                .mapToInt(n -> n.id)
                .boxed()
                .collect(Collectors.toList());


        List<Integer> path = new LinkedList<>();
        Map<Integer, Boolean> visited = new HashMap<>();

        for (int r : repNodeL) {
            visited.put(r, false);
        }

        int cur = repNodeL.get(0);  //id, not list index
        visited.put(cur, true);
        path.add(cur);

        //3. make represent node visit order [GREEDY]
        while (!visited.values()
                .stream()
                .allMatch(b -> b)) {
            double min = Double.MAX_VALUE;
            int minIdx = -1;
            for (int i = 0; i < dist.length; i++) {

                if (min > dist[cur][i] && i != cur &&
                        repNodeL.contains(i) &&
                        !visited.get(i)) {

                    min = dist[cur][i];
                    minIdx = i;
                }
            }
            if (minIdx < 0) break;
            path.add(minIdx);
            visited.put(minIdx, true);
            cur = minIdx;
        }

        busPath = new LinkedList<>();

        //4. make bus path in whole graph with visit order
        for (int i = 0; i < path.size() - 1; i++) {
            int start = path.get(i);
            int end = path.get(i + 1);

            Queue<Integer> graphPath = graph.getPath(start, end);

            if (graphPath.size() < 2) return;

            int src = graphPath.poll();
            busPath.add(NodeFx.getNode(src));
            while (!graphPath.isEmpty()) {
                int dest = graphPath.poll();

                EdgeFx tmp = EdgeFx.getEdge(src, dest);
                tmp.line.setStroke(Color.RED);
                tmp.line.setStrokeWidth(10);

                busPath.add(NodeFx.getNode(dest));
                src = dest;
            }
        }

        btnDone.setText("Play");
        btnDone.setOnAction(e -> handlePlay());
    }

    public void handlePlay() {
        moveThrough();
        btnDone.setText("End");
        btnDone.setOnAction(e -> handleEnd());
    }

    public void handleEnd() {
        Stage total = new Stage();
        VBox pane = new VBox();
        Scene scene = new Scene(pane, 300, 100);
        String distString = "Total travel distance: " + String.format("%.2f", totalDist);
        Pane emptyPane = new Pane();
        emptyPane.setPrefSize(80, 80);
        Label dist = new Label(distString);
        dist.setAlignment(Pos.CENTER);
        dist.setFont(new Font(20));
        Button endBtn = new Button("END");
        endBtn.setPrefSize(100, 100);
        endBtn.setOnAction(e -> {
            Window window = btnDone
                    .getScene()
                    .getWindow();
            window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
            total.close();
        });
        pane.getChildren().add(dist);
        pane.getChildren().add(emptyPane);
        pane.getChildren().add(endBtn);
        total.setScene(scene);
        total.show();
    }

    /*--------handler----------*/

    /*--------result----------*/
    private void moveThrough() {
        Rectangle rectPath = new Rectangle(0, 0, 40, 40);
        rectPath.setArcHeight(10);
        rectPath.setArcWidth(10);
        //rectPath.setFill(Color.ORANGE);
        Image image = null;
        URI uri = null;
        try {
            uri = Class.forName("gachon.algorithm.CanvasController")
                    .getResource("/gachon/algorithm/icon/ladybug.png").toURI();
        } catch (ClassNotFoundException | URISyntaxException e) {
            e.printStackTrace();
        }
        image = new Image(uri.toString());
        rectPath.setFill(new ImagePattern(image));

        NodeFx first = busPath.poll();
        Path path = new Path();
        path.getElements().add(new MoveTo(first.x, first.y));

        for (NodeFx ef : busPath) {
            double x = ef.x;
            double y = ef.y;
            path.getElements().add(new LineTo(x, y));
            totalDist += EdgeFx.calcManhattanDist(first, ef);
            first = ef;
        }

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(8000));
        pathTransition.setPath(path);
        pathTransition.setNode(rectPath);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(Timeline.INDEFINITE);
        pathTransition.setAutoReverse(true);

        pathTransition.play();
        grpCanvas.getChildren().add(rectPath);
    }

    public void grouping() {
        //Dijkstra를 위해 adj list 구성
        for (EdgeFx e : EdgeFx.edgeList) {
            e.source.adj.add(e);
            e.target.adj.add(e);
        }
        //각 Node에 대해
        for (NodeFx n1 : NodeFx.NodeList) {
            if (!n1.grouped) {
                n1.grouped = true;
                //새 Group에 속할 Node의 list
                ArrayList<NodeFx> nearNodeList = new ArrayList<>();
                nearNodeList.add(n1);
                //다른 각 Node에 대해
                for (NodeFx n2 : NodeFx.NodeList) {
                    //앞의 Node와의 거리가 Group의 반지름보다 짧을 경우 Group에 포함
                    double dist = (n1.x - n2.x) * (n1.x - n2.x) + (n1.y - n2.y) * (n1.y - n2.y);
                    if (dist < GroupFx.RAD * GroupFx.RAD && !n2.grouped) {
                        nearNodeList.add(n2);
                        n2.grouped = true;
                    }
                }
                //지금까지 포함한 Node의 좌표의 평균을 새 Group의 중심으로 정함
                double centerX = 0, centerY = 0;
                for (NodeFx n : nearNodeList) {
                    centerX += n.x;
                }
                for (NodeFx n : nearNodeList) {
                    centerY += n.y;
                }
                centerX = centerX / nearNodeList.size();
                centerY = centerY / nearNodeList.size();

                GroupFx g = new GroupFx(centerX, centerY);
                grpCanvas.getChildren().add(g);

                //아직 Group에 속하지 않은 Node가 있을 경우 포함함
                for (NodeFx n : NodeFx.NodeList) {
                    double dist = (centerX - n.x) * (centerX - n.x) + (centerY - n.y) * (centerY - n.y);
                    if (dist < GroupFx.RAD * GroupFx.RAD && !n.grouped) {
                        nearNodeList.add(n);
                        n.grouped = true;
                    }
                }
                //Group의 list에 지금 Group을 포함
                GroupFx.groupList.add(g);
                g.nodeInGroupList = nearNodeList;
                //Group에 속하는 Node사이의 Edge를 찾아서 Group에 포함
                ArrayList<EdgeFx> nearEdgeList = new ArrayList<>();
                for (EdgeFx e : EdgeFx.edgeList) {
                    if (nearNodeList.contains(e.target) && nearNodeList.contains(e.source)) {
                        nearEdgeList.add(e);
                    }
                }
                g.edgeInGroupList = nearEdgeList;
            }
            //현재 확인하는 Node가 이미 어느 Group에 속해있을 경우 continue
        }
        //모든 대표 Node와 그 사이의 경로를 찾음
        GroupFx.findAllRepresentNode();
    }

    private void setBackGround(String imageUrl) {
        borderPane.setPrefSize(StartController.width + 160, StartController.height + 160);
        borderPane.getCenter().setStyle("-fx-background-image: url('" + imageUrl + "'); " +
                "-fx-background-repeat: no-repeat; ");
    }
    /*--------result----------*/

    /*--------canvas control----------*/

    private void drawEdge() {
        NodeFx src = NodeFx.selectedNodes.get(0);
        NodeFx dest = NodeFx.selectedNodes.get(1);
        EdgeFx edge = new EdgeFx(src, dest);

        grpCanvas.getChildren().add(edge.weightLabel);
        grpCanvas.getChildren().add(edge.line);

        NodeFx.clearSelected();

        NodeFx.isPrimaryClicked = false;

        //교차하는지 확인하고 교차하면 분할
        int flag = 0;
        //교차하는 Edge를 저장할 List
        ArrayList<EdgeFx> interList = new ArrayList<>();
        //교차하는 Edge의 양쪽 Node사이에 새로 생성하는 Node를 저장할 List
        ArrayList<NodeFx> interNodeList = new ArrayList<>();
        for (EdgeFx e : EdgeFx.edgeList) {
            //Edge가 교차하고, 같은 Node를 갖지 않을 때
            if (EdgeFx.intersect(edge, e)) {
                if (edge.source != e.source &&
                        edge.source != e.target && edge.target != e.source &&
                        edge.target != e.target) {
                    flag = 1;
                    interList.add(e);
                }
            }
        }
        //교차하면 실행
        if (flag == 1) {
            //새로 생성한 Edge가 교차하므로 화면에서 삭제, edgeList에서도 삭제
            grpCanvas.getChildren().remove(edge.line);
            grpCanvas.getChildren().remove(edge.weightLabel);
            EdgeFx.edgeList.remove(edge);
            //새로 생성한 Edge edge와 교차하는 모든 Edge에 대해
            for (EdgeFx e : interList) {
                //두 선분의 교점의 좌표를 찾는 공식
                NodeFx a1, a2, b1, b2;
                a1 = e.source;
                a2 = e.target;
                b1 = edge.source;
                b2 = edge.target;
                double t1 = (b2.y - b1.y) * (a2.x - a1.x) - (b2.x - b1.x) * (a2.y - a1.y);
                double t2 = (b2.x - b1.x) * (a1.y - b1.y) - (b2.y - b1.y) * (a1.x - b1.x);
                double t = t2 / t1;
                double x = a1.x + t * (a2.x - a1.x);
                double y = a1.y + t * (a2.y - a1.y);
                //교점의 위치에 새 Node 생성
                NodeFx n = new NodeFx(x, y);
                grpCanvas.getChildren().add(n);
                grpCanvas.getChildren().add(n.idLabel);
                //새로 생긴 Node의 List에 추가
                interNodeList.add(n);
                //교차하는 기존의 Edge를 화면에서 삭제하고 edgeList에서도 삭제
                grpCanvas.getChildren().remove(e.line);
                grpCanvas.getChildren().remove(e.weightLabel);
                EdgeFx.edgeList.remove(e);
                //새로 생성한 Node와 기존의 Node 2개를 Edge로 연결
                EdgeFx e1 = new EdgeFx(e.source, n);
                grpCanvas.getChildren().add(e1.line);
                grpCanvas.getChildren().add(e1.weightLabel);
                EdgeFx e2 = new EdgeFx(e.target, n);
                grpCanvas.getChildren().add(e2.line);
                grpCanvas.getChildren().add(e2.weightLabel);
            }
            //새로 생성한 Node를 x값에 따라 오름차순 정렬
            interNodeList.sort(new NodeXComparator());
            //x값이 가장 낮은 Node를 start에 저장
            NodeFx start = interNodeList.get(0);
            NodeFx last = interNodeList.get(0);
            //x값의 순서대로 Edge로 연결
            for (NodeFx n : interNodeList) {
                if (n == interNodeList.get(0)) continue;
                EdgeFx e = new EdgeFx(last, n);
                grpCanvas.getChildren().add(e.line);
                grpCanvas.getChildren().add(e.weightLabel);
                last = n;
            }
            //x값이 가장 큰 Node가 last에 저장됨
            //start와 last를 새로 생성한 Edge edge의 source와 거리를 구해서 비교
            double dx1 = (edge.source.x - last.x) * (edge.source.x - last.x);
            double dy1 = (edge.source.y - last.y) * (edge.source.y - last.y);
            double dist1 = dx1 + dy1;
            double dx2 = (edge.source.x - start.x) * (edge.source.x - start.x);
            double dy2 = (edge.source.y - start.y) * (edge.source.y - start.y);
            double dist2 = dx2 + dy2;
            //edge의 source와 target을 가까운 쪽과 Edge로 연결
            if (dist1 > dist2) {
                EdgeFx e1 = new EdgeFx(edge.source, start);
                grpCanvas.getChildren().add(e1.line);
                grpCanvas.getChildren().add(e1.weightLabel);
                EdgeFx e2 = new EdgeFx(edge.target, last);
                grpCanvas.getChildren().add(e2.line);
                grpCanvas.getChildren().add(e2.weightLabel);
            } else {
                EdgeFx e1 = new EdgeFx(edge.source, last);
                grpCanvas.getChildren().add(e1.line);
                grpCanvas.getChildren().add(e1.weightLabel);
                EdgeFx e2 = new EdgeFx(edge.target, start);
                grpCanvas.getChildren().add(e2.line);
                grpCanvas.getChildren().add(e2.weightLabel);
            }
        }
    }

    private void remove() {
        //remove node
        grpCanvas.getChildren().remove(NodeFx.toRemove);
        grpCanvas.getChildren().remove(NodeFx.toRemove.idLabel);

        //remove edge
        List<EdgeFx> removedEdgeList = EdgeFx.edgeList.stream()
                .filter(edge -> edge.source.idLabel == NodeFx.toRemove.idLabel ||
                        edge.target.idLabel == NodeFx.toRemove.idLabel)
                .collect(Collectors.toList());

        for (EdgeFx ef : removedEdgeList) {
            grpCanvas.getChildren().remove(ef.weightLabel);
            grpCanvas.getChildren().remove(ef.line);
            EdgeFx.edgeList.remove(ef);
        }

        NodeFx.isSecondaryClicked = false;
        NodeFx.toRemove = null;
    }

    //정렬하는데 사용하는 Comparator
    public static class NodeXComparator implements Comparator<NodeFx> {
        @Override
        public int compare(NodeFx n1, NodeFx n2) {
            if (n1.x > n2.x) {
                return 1;
            } else if (n1.x < n2.x) {
                return -1;
            }
            return 0;
        }
    }
    /*--------canvas control----------*/
}
