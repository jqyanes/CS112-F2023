package prereqchecker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * 
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
 * EligibleInputFile name is passed through the command line as args[1]
 * Read from EligibleInputFile with the format:
 * 1. c (int): Number of courses
 * 2. c lines, each with 1 course ID
 * 
 * Step 3:
 * EligibleOutputFile name is passed through the command line as args[2]
 * Output to EligibleOutputFile with the format:
 * 1. Some number of lines, each with one course ID
 */
public class Eligible {
    public static void main(String[] args) {
        try {

            if ( args.length < 3 ) {
                StdOut.println("Execute: java -cp bin prereqchecker.Eligible <adjacency list INput file> <eligible INput file> <eligible OUTput file>");
                return;
            }

            // reading adjlist.in
            StdIn.setFile(args[0]);
            int courseNum = Integer.parseInt(StdIn.readLine());
            String[] courses = new String[courseNum]; //list of all courses
            AdjNode[] preReqs = new AdjNode[courseNum]; //linked lists of prereqs for corresponding course
            for (int courseInd = 0; courseInd < courseNum; courseInd++) courses[courseInd] = StdIn.readLine();
            int edgeNum = Integer.parseInt(StdIn.readLine());
            for (int edgeInd = 0; edgeInd < edgeNum; edgeInd++) {
                String edge = StdIn.readLine();
                String[] edgeSep = edge.split(" ");
                int courseIndex = 0;
                while (!edgeSep[0].equals(courses[courseIndex])) courseIndex++;
                AdjNode prereq = new AdjNode(edgeSep[1], preReqs[courseIndex]);
                preReqs[courseIndex] = prereq; 
            }

            //reading eligible.in, completed courses queue setup
            StdIn.setFile(args[1]);
            int eLength = Integer.parseInt(StdIn.readLine());
            ArrayList<String> inpath = new ArrayList<String>();
            for (int e = 0; e < eLength; e++) inpath.add(StdIn.readLine());

            //storing completed courses
            ArrayList<String> completed = new ArrayList<String>();
            AdjNode ptr = null;
            while (!inpath.isEmpty()) {
                int ptrInd = 0;
                while (!courses[ptrInd].equals(inpath.get(0))) ptrInd++; //going to prereqs of prereq
                ptr = preReqs[ptrInd];
                while (ptr != null) {
                    inpath.add(ptr.getPrereq());
                    for (int i = 0; i < completed.size(); i++) {
                        if (completed.get(i).equals(ptr.getPrereq())) inpath.remove(inpath.size() - 1);
                    }
                    ptr = ptr.getNextAdjNode();
                }
                completed.add(inpath.remove(0));
                for (int check = 0; check < (completed.size() - 1); check++) {
                    if (completed.get(check).equals(completed.get(completed.size() - 1))) completed.remove(completed.size() - 1);
                }
            }
            
            //making eligible.out
            File output = new File(args[2]);
            FileWriter dataOut = new FileWriter(output);
            //for each course, check if prereqs are fulfilled and if course has been taken
            for (int j = 0; j < courses.length; j++) {
                ptr = preReqs[j];
                boolean allPre = true;
                while (ptr != null) {
                    boolean cPre = false;
                    for (int compInd = 0; compInd < completed.size(); compInd++) {
                        if (ptr.getPrereq().equals(completed.get(compInd))) cPre = true;
                    }
                    if (cPre == false) allPre = false;
                    ptr = ptr.getNextAdjNode();
                }
                boolean courseTaken = false;
                for (int cCourse = 0; cCourse < completed.size(); cCourse++) {
                    if (completed.get(cCourse).equals(courses[j])) courseTaken = true;
                }
                if ((allPre == true) && (courseTaken == false)) dataOut.write(courses[j] + "\n");
            }
            dataOut.close();
        }

        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }
}
