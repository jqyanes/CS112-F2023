package prereqchecker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import prereqchecker.AdjList;

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
 * ValidPreReqInputFile name is passed through the command line as args[1]
 * Read from ValidPreReqInputFile with the format:
 * 1. 1 line containing the proposed advanced course
 * 2. 1 line containing the proposed prereq to the advanced course
 * 
 * Step 3:
 * ValidPreReqOutputFile name is passed through the command line as args[2]
 * Output to ValidPreReqOutputFile with the format:
 * 1. 1 line, containing either the word "YES" or "NO"
 */
public class ValidPrereq {
    public static void main(String[] args) {
        try {

        if ( args.length < 3 ) {
            StdOut.println("Execute: java -cp bin prereqchecker.ValidPrereq <adjacency list INput file> <valid prereq INput file> <valid prereq OUTput file>");
            return;
        }

        // reading adjlist.in
        StdIn.setFile(args[0]);
        int courseNum = Integer.parseInt(StdIn.readLine());
        String[] courses = new String[courseNum]; //list of all courses
        AdjNode[] preReqs = new AdjNode[courseNum]; //linked lists of prereqs for corresponding course
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

        //reading validprereq.in
        StdIn.setFile(args[1]);
        String advanced = StdIn.readLine();
        String proposedPre = StdIn.readLine();

        //setup
        ArrayList<String> inpath = new ArrayList<String>(); //unchecked prereqs (direct + indirect) for proposedPre
        ArrayList<String> visited = new ArrayList<String>(); //checked prereqs
        AdjNode ptr = null;
        int proposedInd = 0;
        while (!courses[proposedInd].equals(proposedPre)) proposedInd++; //finding proposed prereq course index
        File output = new File(args[2]);
        FileWriter dataOut = new FileWriter(output);

        //checking if class1 and class2 are the same
        if (advanced.equals(proposedPre)) {
            dataOut.write("NO");
            dataOut.close();
            return;
        }

        //adding direct prereqs to inpath
        if (preReqs[proposedInd] == null) {
            dataOut.write("YES");
            dataOut.close();
            return;
        }
        else {
            ptr = preReqs[proposedInd];
            while (ptr != null) {
                inpath.add(ptr.getPrereq());
                ptr = ptr.getNextAdjNode();
            }
        }

        //checking prereqs (+ adding indirect prereqs to inpath)
        while (!inpath.isEmpty()) {
            if (inpath.get(0).equals(advanced)) {
                dataOut.write("NO");
                dataOut.close();
                return;            
            }
            int ptrInd = 0;
            while (!courses[ptrInd].equals(inpath.get(0))) ptrInd++; //going to prereqs of prereq
            ptr = preReqs[ptrInd];
            while (ptr != null) {
                inpath.add(ptr.getPrereq());
                for (int i = 0; i < visited.size(); i++) {
                    if (visited.get(i).equals(ptr.getPrereq())) inpath.remove(inpath.size() - 1);
                }
                ptr = ptr.getNextAdjNode();
            }
            visited.add(inpath.remove(0));
        }
        dataOut.write("YES");
        dataOut.close();
    }
    
    catch (IOException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
    }
}

}
