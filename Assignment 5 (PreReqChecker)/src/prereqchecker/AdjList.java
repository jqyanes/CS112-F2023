package prereqchecker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * Steps to implement this class main method:
 * 
 * Step 1:
 * AdjListInputFile name is passed through the command line as args[0]
 * Read from AdjListInputFile with the format:
 * 1. a (int): number of courses in the graph
 * 2. a lines, each with 1 course ID
 * 3. b (int): number of edges in the graph
 * 4. b lines, each with a source ID
 * 
 * Step 2:
 * AdjListOutputFile name is passed through the command line as args[1]
 * Output to AdjListOutputFile with the format:
 * 1. c lines, each starting with a different course ID, then 
 *    listing all of that course's prerequisites (space separated)
 */
public class AdjList {
    public static void main(String[] args) {

        try{

        if ( args.length < 2 ) {
            StdOut.println("Execute: java -cp bin prereqchecker.AdjList <adjacency list INput file> <adjacency list OUTput file>");
            return;
        }

	// WRITE YOUR CODE HERE
    StdIn.setFile(args[0]);
    int courseNum = Integer.parseInt(StdIn.readLine());
    String[] courses = new String[courseNum];
    AdjNode[] preReqs = new AdjNode[courseNum];
    for (int i = 0; i < courseNum; i++) courses[i] = StdIn.readLine();
    int edgeNum = Integer.parseInt(StdIn.readLine());
    for (int j = 0; j < edgeNum; j++) {
        String edge = StdIn.readLine();
        String[] edgeSep = edge.split(" ");
        int courseIndex = 0;
        while (!edgeSep[0].equals(courses[courseIndex])) courseIndex++;
        AdjNode prereq = new AdjNode(edgeSep[1], preReqs[courseIndex]);
        preReqs[courseIndex] = prereq; 
    }
    File output = new File(args[1]);
    FileWriter dataOut = new FileWriter(output);
    for (int line = 0; line < courseNum; line++) {
        String lineData = courses[line];
        AdjNode ptr = preReqs[line];
        while (ptr != null) {
            lineData += (" " + ptr.getPrereq());
            ptr = ptr.getNextAdjNode();
        }
        dataOut.write(lineData + "\n");
    }
    dataOut.close();
    }
catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
}
}
}
