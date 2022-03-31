package lib;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;

import javax.imageio.ImageIO;

import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.util.mxCellRenderer;

import commands.hierarchy.fsm.FSMCommand;
import commands.hierarchy.fsm.State;
import commands.hierarchy.fsm.Transition;
import commands.level.normal.fsm.TicTacToe;

public class FSMVisualizer {
	public static final String GRAPH_FILE_PATH = "src/test/resources/graph.png";
	
	public static Map<State, List<State>> extractAdjacencyList(FSMCommand command) {
		Map<State, List<State>> map = new HashMap<>();
		Set<State> visited = extractAllStates(command);
		for (State state : visited) {
			map.putIfAbsent(state, new ArrayList<>());
			for (Transition edge : state.getTransitions())
				map.get(state).add(edge.getNextState());
		}
		return map;
	}
	
	public static Set<State> extractAllStates(FSMCommand command) {
		Set<State> visited = new HashSet<>();
		Queue<State> queue = new LinkedBlockingDeque<>();
		State current = command.getStart(), next;
		do {
			for (Transition edge : current.getTransitions()) {
				next = edge.getNextState();
				if (!visited.contains(next)) {
					queue.add(next);
					visited.add(next);
				}
			}
			visited.add(current);
			current = queue.poll();
		} while (!queue.isEmpty());
		return visited;
	}
	
	public static File visualise(FSMCommand command, int scaling) throws IOException {
		Map<State, List<State>> map = extractAdjacencyList(command);
		// Create graph
		Graph<State, DefaultWeightedEdge> graph = GraphTypeBuilder.<State, DefaultWeightedEdge>directed()
				.allowingMultipleEdges(true)
			    .allowingSelfLoops(true)
			    .edgeClass(DefaultWeightedEdge.class)
			    .weighted(true)
			    .buildGraph();
		for (State vertex : map.keySet())
			graph.addVertex(vertex);
		for (Entry<State, List<State>> entry : map.entrySet()) 
			for (State neighbour : entry.getValue())
				graph.addEdge(entry.getKey(), neighbour);
		// Draw graph
		return drawGraph(graph, scaling);
	}
	
	public static <V, E> File drawGraph(Graph<V, E> graph, int scaling) throws IOException {
		JGraphXAdapter<V, E> graphAdapter = new JGraphXAdapter<V, E>(graph);
		mxHierarchicalLayout layout = new mxHierarchicalLayout(graphAdapter);	// mxIGraphLayout 
	    layout.setIntraCellSpacing(200);
	    layout.setInterHierarchySpacing(100);
	    layout.setInterRankCellSpacing(100);
	    layout.setParallelEdgeSpacing(10000);
	    layout.execute(graphAdapter.getDefaultParent());
	    BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, scaling, new Color(0,0,0,0), true, null);
	    File imgFile = new File(GRAPH_FILE_PATH);
	    ImageIO.write(image, "PNG", imgFile);
		return imgFile;
	}
	
	public static void main(String[] args) throws IOException {
		FSMCommand fsm = new TicTacToe(null, null);
		// {2ND=[], START=[2ND, START, END, 2ND], END=[]}
//		System.out.println(extractAllStates(fsm));
//		System.out.println(extractAdjacencyList(fsm));
		visualise(fsm, 2);
	}
}