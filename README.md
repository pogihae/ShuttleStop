# **ShuttleStop**

“ShuttleStop” provides the information needed to operate a shuttle bus within a specific area. 
By drawing a graph on the map, you can install a bus stop and provide a route for the shuttle bus.

![image1](https://user-images.githubusercontent.com/76048647/219593307-2d71c37f-c76f-49e1-b9a5-49597797c691.gif)

## Requirement 	

JDK 16


## How to use
```
1. Get map image (by upload file or search in app)
2. Make graph
    a. Draw node: left click on empty space
    b. Draw edge: Select two nodes
    c. Delete node: right click on node (connected edge deleted too)
3. Shuttle stop located and path provided.
```

## Description


### Edge Intersection

While making edge, detect that edge has intersection with other edges. (***By CCW***)


### Node Grouping

Buildings within walking distance will only need one stop. So, group the nearby buildings into a group.

By changing “GroupFx – RAD” value, you can change the extent to which the group is formed.

After the group is complete, select the representative node which is closest with other nodes in group. (***By Floyd-Warshall***)

### Path

Create the shortest path that unconditionally passes through the representative nodes. 

At this time, there is a difference from TSP in that nodes other than representative nodes do not have to pass. 

We created a path by proceeding from the start representative node to the nearest other representative node. (***With Greedy programming***)
