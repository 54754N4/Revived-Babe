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
import java.util.Set;
import java.util.Stack;

import javax.imageio.ImageIO;

import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;

import commands.hierarchy.fsm.FSMCommand;
import commands.hierarchy.fsm.State;
import commands.hierarchy.fsm.Transition;
import commands.level.normal.fsm.TicTacToe;

public class FSMVisualizer {
	public static final String GRAPH_FILE_PATH = "src/test/resources/graph.png";
	
	public static Map<State, List<State>> extractAdjacencyList(FSMCommand command) {
		Map<State, List<State>> map = new HashMap<>();
		Set<State> visited = new HashSet<>();
		Stack<State> visit = new Stack<>();
		State current = command.getStart();
		do {
			map.putIfAbsent(current, new ArrayList<>());
			for (Transition edge : current.getTransitions()) {
				if (visited.contains(edge.getNextState()))
					continue;
				map.get(current).add(edge.getNextState()); 
				visit.push(edge.getNextState());
			}
			visited.add(current);
			try { current = visit.pop(); }
			catch (Exception e) { current = null; }
		} while (current != null);
		return map;
	}
	
	public static File visualise(FSMCommand command, int scaling) throws IOException {
		Map<State, List<State>> map = extractAdjacencyList(command);
		// Create graph
		Graph<State, DefaultWeightedEdge> graph = GraphTypeBuilder.<State, DefaultWeightedEdge>undirected()
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
	    mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
	    layout.execute(graphAdapter.getDefaultParent());
	    BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, scaling, new Color(0,0,0,0), true, null);
	    File imgFile = new File(GRAPH_FILE_PATH);
	    ImageIO.write(image, "PNG", imgFile);
		return imgFile;
	}
	
	public static void main(String[] args) throws IOException {
		visualise(new TicTacToe(null, null), 2);
	}
}