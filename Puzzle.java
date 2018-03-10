import java.io.IOException;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.parseInt;

public class Puzzle{

	static int inf = MAX_VALUE;

	public static void main(String args[]) throws IOException{
		//long startTime=System.currentTimeMillis();//

		String[] input = Files.readAllLines(Paths.get(args[0]), StandardCharsets.UTF_8).toArray(new String[0]);

		int num = parseInt(input[0]);
		StringBuilder output = new StringBuilder();
		int[] weights = new int[8];
		String start;
		String goal;
		Graph g;
		GraphNode Node;
		GraphNode[] validmoves = new GraphNode[4];
		Heap<GraphNode> queue = new Heap<GraphNode>();
		Set<String> hmap = new HashSet<String>();
		HashMap<String, GraphNode> qmap = new HashMap<String, GraphNode>();
		Stack<String> stack = new Stack<String>();
		int gap = 0;
		String[] input_str;

		for(int p=1; p<=2*num; p+=2){

			input_str = input[p].split(" ");
			start = input_str[0];
			goal = input_str[1];

			if (!Solvable(start, goal)) {
				output.append("-1 -1\n\n");
				continue;
			}

			input_str = input[p+1].split(" ");
			for(int c=0; c<8; c++)
				weights[c] = parseInt(input_str[c]);

			g = new Graph(start, goal, weights);			
			queue.clear();
			hmap.clear();
			qmap.clear();
			stack.clear();
			Node = new GraphNode();
			int num_validmoves;

			queue.add(g.start);
			qmap.put(g.start.str(), g.start);

			String goal_str = g.goal.str();

			//Dijkstra's algorithm
			while (true){
				Node = queue.poll();
				hmap.add(Node.string);
				if (hmap.contains(goal_str)){break;}
				gap = 0;

				num_validmoves = validmoves(Node, g.weight, validmoves);

				for(int n=0; n<num_validmoves; n++){
					if(!hmap.contains(validmoves[n].string) && !qmap.containsKey(validmoves[n].string) || qmap.get(validmoves[n].string).compareTo(validmoves[n])==1){
						queue.add(validmoves[n]);
						qmap.put(validmoves[n].string, validmoves[n]);
					}
				}

				//System.out.println(Arrays.toString(queue.heap.toArray()));//
			}

			Node = qmap.get(goal_str);
			output.append(Node.num_moves+" "+Node.distance+"\n");

			while(Node!=g.start){
				stack.push(Node.move);
				Node = Node.parent;
			}
			
			while(!stack.empty())
				output.append(stack.pop()+" ");

			output.append("\n");
		}

		//output.append("total time: "+(System.currentTimeMillis() - startTime)+" ms");
		Files.write(Paths.get(args[1]), output.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

	}

	//Puzzle solavble for given inputs
	public static boolean Solvable(String n1, String n2) {
		int[] array1 = new int[9];
		int[] array2 = new int[9];
		for (int i = 0; i < 9; i++) {
			char a = n1.charAt(i);
			char b = n2.charAt(i);
			if(a=='G'){array1[i] = 0;}else{array1[i] = Character.getNumericValue(a);}
			if(b=='G'){array2[i] = 0;}else{array2[i] = Character.getNumericValue(b);}
		}
		int p1 = 0; int p2 = 0;
		for (int i = 0; i < 8; i++) {
			for(int j = i+1; j < 9; j++) {
				if (array1[i] > array1[j]) p1++;
				if (array2[i] > array2[j]) p2++;
			}
			if (array1[i] == 0 && i % 2 == 1) p1++;
			if (array2[i] == 0 && i % 2 == 1) p1++;
		}
		return p1 % 2 == p2 % 2;
	}

	//Determining valid moves for current state
	public static int validmoves(GraphNode Node, int[] weight, GraphNode[]  validmoves){
		int gap = 0;
		for(int j=0; j<4; j++)
			validmoves[j] = new GraphNode();

		for(int j=0; j<3; j++){
			for(int k=0; k<3; k++){
				for(int i=0; i<4; i++)
					validmoves[i].state[j][k] = Node.state[j][k];
				if(Node.state[j][k]==0)	gap = 3*j+k;
			}
		}

		switch(gap){
			case 0:
				move_adder(validmoves[0], Node, weight, 0, 0, 0, 1, "L");
				move_adder(validmoves[1], Node, weight, 0, 0, 1, 0, "U");
				return 2;
			case 1:
				move_adder(validmoves[0], Node, weight, 0, 1, 0, 0, "R");
				move_adder(validmoves[1], Node, weight, 0, 1, 0, 2, "L");
				move_adder(validmoves[2], Node, weight, 0, 1, 1, 1, "U"); 
				return 3;
			case 2:
				move_adder(validmoves[0], Node, weight, 0, 2, 0, 1, "R");
				move_adder(validmoves[1], Node, weight, 0, 2, 1, 2, "U");
				return 2;
			case 3:
				move_adder(validmoves[0], Node, weight, 1, 0, 0, 0, "D");
				move_adder(validmoves[1], Node, weight, 1, 0, 1, 1, "L");
				move_adder(validmoves[2], Node, weight, 1, 0, 2, 0, "U");
				return 3;
			case 4:
				move_adder(validmoves[0], Node, weight, 1, 1, 0, 1, "D");
				move_adder(validmoves[1], Node, weight, 1, 1, 1, 0, "R");
				move_adder(validmoves[2], Node, weight, 1, 1, 1, 2, "L");
				move_adder(validmoves[3], Node, weight, 1, 1, 2, 1, "U");
				return 4;
			case 5:
				move_adder(validmoves[0], Node, weight, 1, 2, 0, 2, "D");
				move_adder(validmoves[1], Node, weight, 1, 2, 1, 1, "R");
				move_adder(validmoves[2], Node, weight, 1, 2, 2, 2, "U"); 
				return 3;
			case 6:
				move_adder(validmoves[0], Node, weight, 2, 0, 1, 0, "D");
				move_adder(validmoves[1], Node, weight, 2, 0, 2, 1, "L");
				return 2;
			case 7:
				move_adder(validmoves[0], Node, weight, 2, 1, 1, 1, "D");
				move_adder(validmoves[1], Node, weight, 2, 1, 2, 0, "R");
				move_adder(validmoves[2], Node, weight, 2, 1, 2, 2, "L");
				return 3;
			case 8:
				move_adder(validmoves[0], Node, weight, 2, 2, 1, 2, "D");
				move_adder(validmoves[1], Node, weight, 2, 2, 2, 1, "R");
				return 2;
		}
		return 0;
	}

	public static void move_adder(GraphNode validmove, GraphNode Node, int[] weight, int i1, int j1, int i2, int j2, String dir){
		validmove.state[i1][j1] = Node.state[i2][j2];
		validmove.distance = Node.distance + weight[Node.state[i2][j2] - 1];
		validmove.move = Node.state[i2][j2] + dir;
		validmove.num_moves = Node.num_moves + 1;
		validmove.state[i2][j2] = 0;
		validmove.parent = Node;
		validmove.str();
	}

	//Graph Class
	public static class Graph{

		GraphNode start;
		GraphNode goal;
		int[] weight;

		public Graph(String start_str, String goal_str, int[] weights){
			start = new GraphNode();
			goal = new GraphNode();
			weight = weights;
			for(int i=0; i<3; i++){
				for(int j=0; j<3; j++){
					char c = start_str.charAt(3*i+j);
					char d = goal_str.charAt(3*i+j);
					if(c=='G')	start.state[i][j] = 0;
					else start.state[i][j] = Character.getNumericValue(c); 
					if(d=='G')	goal.state[i][j] = 0;
					else goal.state[i][j] = Character.getNumericValue(d);
				}
			}
			start.distance = 0;
		}

	}

	//GraphNode class
	public static class GraphNode implements Comparable<GraphNode>{

		int[][] state = new int[3][3];
		int distance = inf;
		GraphNode parent;
		String move = new String("");
		int num_moves = 0;
		String string = "";

		public String str(){
			string = "";
			for(int i=0; i<3; i++){
				for(int j=0; j<3; j++){
					this.string+=this.state[i][j];
				}
			}
			return this.string;
		}

		//public String toString(){
		//	String a = Arrays.toString(state[0])+ Arrays.toString(state[1]) +Arrays.toString(state[2]);
		//	return a;
		//}

		public int compareTo(GraphNode b){
			if(this.distance > b.distance)
				return 1;
			else if (this.distance < b.distance)
				return -1;
			else if(this.num_moves > b.num_moves)
				return 1;
			else if(this.num_moves < b.num_moves)
				return -1;
			else
				return 0;
		}
	}

	//Heap Class
	public static class Heap<E extends Comparable<E>>  {
		public ArrayList<E> heap;

		public Heap() {
			heap = new ArrayList<E>();
		}

		public E poll() {
			if (heap.size() <= 0)
				return null;
			else {
				E minimum = heap.get(0);
				heap.set(0, heap.get(heap.size()-1)); 
				heap.remove(heap.size()-1);
				minHeapify(heap, 0);
				return minimum;
			}
		}

		public void clear(){
			heap.clear();
		}

		public void add(E element) {
			heap.add(element);      
			int location = heap.size()-1;

			while (location > 0 && heap.get(location).compareTo(heap.get((location-1)/2)) < 0) {
				swap(heap, location, (location-1)/2);
				location = (location-1)/2; //parent location
			}
		}

		private static <E extends Comparable<E>> void minHeapify(ArrayList<E> a, int i) {
			int left = 2*i +1; 
			int right = 2*i +2;
			int least;   

			if (left <= a.size()-1 && a.get(left).compareTo(a.get(i)) < 0)
				least = left; 
			else
				least = i;     

			if (right <= a.size()-1 && a.get(right).compareTo(a.get(least)) < 0)
				least = right; 

			if (least != i) {
				swap(a, i, least);
				minHeapify(a, least);
			}
		} 

		private static <E> void swap(ArrayList<E> a, int i, int j) {
			E t = a.get(i);
			a.set(i, a.get(j));
			a.set(j, t);
		}

	}

}
