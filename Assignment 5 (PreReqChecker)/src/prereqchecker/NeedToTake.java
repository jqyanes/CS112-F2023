package prereqchecker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

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
 * NeedToTakeInputFile name is passed through the command line as args[1]
 * Read from NeedToTakeInputFile with the format:
 * 1. One line, containing a course ID
 * 2. c (int): Number of courses
 * 3. c lines, each with one course ID
 * 
 * Step 3:
 * NeedToTakeOutputFile name is passed through the command line as args[2]
 * Output to NeedToTakeOutputFile with the format:
 * 1. Some number of lines, each with one course ID
 */
public class NeedToTake {
    public static void main(String[] args) {

        try{
            if ( args.length < 3 ) {
                StdOut.println("Execute: java NeedToTake <adjacency list INput file> <need to take INput file> <need to take OUTput file>");
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

            //reading needtotake.in
            StdIn.setFile(args[1]);
            String target = StdIn.readLine();
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

            //storing all prereqs of target
                //direct
            int targetInd = 0;
            ArrayList<String> inpath2 = new ArrayList<String>();
            ArrayList<String> allPreList = new ArrayList<String>();
            ptr = null;
            while (!target.equals(courses[targetInd])) targetInd++;
            ptr = preReqs[targetInd];
            while (ptr != null) {
                inpath2.add(ptr.getPrereq());
                ptr = ptr.getNextAdjNode();
            }
                //indirect
            while (!inpath2.isEmpty()) {
                int ptrInd = 0;
                while (!courses[ptrInd].equals(inpath2.get(0))) ptrInd++; //going to prereqs of prereq
                ptr = preReqs[ptrInd];
                while (ptr != null) {
                    inpath2.add(ptr.getPrereq());
                    for (int i = 0; i < allPreList.size(); i++) {
                        if (allPreList.get(i).equals(ptr.getPrereq())) inpath2.remove(inpath2.size() - 1);
                    }
                    ptr = ptr.getNextAdjNode();
                }
                allPreList.add(inpath2.remove(0));
                for (int check = 0; check < (allPreList.size() - 1); check++) {
                    if (allPreList.get(check).equals(allPreList.get(allPreList.size() - 1))) allPreList.remove(allPreList.size() - 1);
                }
            }

            //making needtotake.out
            File output = new File(args[2]);
            FileWriter dataOut = new FileWriter(output);
                //was target course completed?
            for (int i = 0; i < completed.size(); i++) {
                if (target.equals(completed.get(i))) return;
            }
                //target not completed
            for (int all = 0; all < allPreList.size(); all++) {
                boolean prereqComp = false;
                for (int check = 0; check < completed.size(); check++) {
                    if (allPreList.get(all).equals(completed.get(check))) prereqComp = true;
                }
                if (prereqComp == false) dataOut.write(allPreList.get(all) + "\n");
            }
            dataOut.close();
        }

        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
