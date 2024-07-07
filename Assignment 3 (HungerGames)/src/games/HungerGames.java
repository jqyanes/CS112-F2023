package games;

import java.util.ArrayList;

import org.w3c.dom.Node;

/**
 * This class contains methods to represent the Hunger Games using BSTs.
 * Moves people from input files to districts, eliminates people from the game,
 * and determines a possible winner.
 * 
 * @author Pranay Roni
 * @author Maksims Kurjanovics Kravcenko
 * @author Kal Pandit
 */
public class HungerGames {

    private ArrayList<District> districts;  // all districts in Panem.
    private TreeNode            game;       // root of the BST. The BST contains districts that are still in the game.

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * Default constructor, initializes a list of districts.
     */
    public HungerGames() {
        districts = new ArrayList<>();
        game = null;
        StdRandom.setSeed(2023);
    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * Sets up Panem, the universe in which the Hunger Games takes place.
     * Reads districts and people from the input file.
     * 
     * @param filename will be provided by client to read from using StdIn
     */
    public void setupPanem(String filename) { 
        StdIn.setFile(filename);  // open the file - happens only once here
        setupDistricts(filename); 
        setupPeople(filename);
    }

    /**
     * Reads the following from input file:
     * - Number of districts
     * - District ID's (insert in order of insertion)
     * Insert districts into the districts ArrayList in order of appearance.
     * 
     * @param filename will be provided by client to read from using StdIn
     */
    public void setupDistricts (String filename) {

        // WRITE YOUR CODE HERE
        int distnum = StdIn.readInt();
        for (int i = 0; i < distnum; i++) {
            District insert = new District(StdIn.readInt());
            districts.add(i, insert);
        }
    }

    /**
     * Reads the following from input file (continues to read from the SAME input file as setupDistricts()):
     * Number of people
     * Space-separated: first name, last name, birth month (1-12), age, district id, effectiveness
     * Districts will be initialized to the instance variable districts
     * 
     * Persons will be added to corresponding district in districts defined by districtID
     * 
     * @param filename will be provided by client to read from using StdIn
     */
    public void setupPeople (String filename) {

        // WRITE YOUR CODE HERE
        int peoplenum = StdIn.readInt();
        StdIn.readLine();
        for (int i = 0; i < peoplenum; i++) {
            String line = StdIn.readLine();
            String[] data = line.split(" ");
            Person insert = new Person(Integer.parseInt(data[2]), data[0], data[1], Integer.parseInt(data[3]), Integer.parseInt(data[4]), Integer.parseInt(data[5]));
            if ((insert.getAge() >= 12) && (insert.getAge() < 18)) insert.setTessera(true);
            else insert.setTessera(false);
            int count = 0;
            int distid = insert.getDistrictID();
            while ((districts.get(count)).getDistrictID() != distid) count++;
            if ((insert.getBirthMonth() % 2) == 0) (districts.get(count)).addEvenPerson(insert);
            else (districts.get(count)).addOddPerson(insert);
        }
    }

    /**
     * Adds a district to the game BST.
     * If the district is already added, do nothing
     * 
     * @param root        the TreeNode root which we access all the added districts
     * @param newDistrict the district we wish to add
     */
    public void addDistrictToGame(TreeNode root, District newDistrict) {

        // WRITE YOUR CODE HERE
        game = root;
        TreeNode current = root;
        if (current == null) {
            game = new TreeNode(newDistrict, null, null);
            for (int i = 0; i < districts.size(); i++) {
                if (districts.get(i) == newDistrict) districts.remove(i);
            }
            return;
        }
        TreeNode cL = root.getLeft();
        TreeNode cR = root.getRight();
        TreeNode nNode = new TreeNode(newDistrict, null, null);
        while (current.getDistrict() != newDistrict) {
            if (current.getDistrict().getDistrictID() < newDistrict.getDistrictID()) {
                if (cR == null) {
                    current.setRight(nNode);
                    for (int i = 0; i < districts.size(); i++) {
                        if (districts.get(i) == newDistrict) districts.remove(i);
                    }
                    return;
                }
                else {
                    current = cR;
                    cL = current.getLeft();
                    cR = current.getRight();
                }
            }
            else {
                if (cL == null) {
                    current.setLeft(nNode);
                    for (int i = 0; i < districts.size(); i++) {
                        if (districts.get(i) == newDistrict) districts.remove(i);
                    }
                    return;
                }
                else {
                    current = cL;
                    cL = current.getLeft();
                    cR = current.getRight();
                }
            }
        }
    }

    /**
     * Searches for a district inside of the BST given the district id.
     * 
     * @param id the district to search
     * @return the district if found, null if not found
     */
    public District findDistrict(int id) {

        // WRITE YOUR CODE HERE
        TreeNode current = game;
        if (current.getDistrict().getDistrictID() == id) return current.getDistrict();
        while (current != null) {
            if (current.getDistrict().getDistrictID() == id) return current.getDistrict();
            else {
                if (current.getDistrict().getDistrictID() < id) current = current.getRight();
                else current = current.getLeft();
            }
        }
        return null; // update this line
    }

    /**
     * Selects two duelers from the tree, following these rules:
     * - One odd person and one even person should be in the pair.
     * - Dueler with Tessera (age 12-18, use tessera instance variable) must be
     * retrieved first.
     * - Find the first odd person and even person (separately) with Tessera if they
     * exist.
     * - If you can't find a person, use StdRandom.uniform(x) where x is the respective 
     * population size to obtain a dueler.
     * - Add odd person dueler to person1 of new DuelerPair and even person dueler to
     * person2.
     * - People from the same district cannot fight against each other.
     * 
     * @return the pair of dueler retrieved from this method.
     */

     //tessera odd
     private Person dtOdd(TreeNode root, DuelPair duelPair){
        // DuelPair duelPair = new DuelPair();
        Person person1 = duelPair.getPerson1();
        Person person2 = duelPair.getPerson2();
        if (root != null){
            ArrayList<Person> rootOddPersons = root.getDistrict().getOddPopulation();
            if (person1 == null){
                if (person2 == null){
                    for (int i = 0; i < rootOddPersons.size(); i++){
                        if (rootOddPersons.get(i).getTessera() == true){
                            person1 = rootOddPersons.get(i);
                            rootOddPersons.remove(person1);
                            return person1;
                        }
                    }
                }
                if (person2 != null){
                    for (int i = 0; i < rootOddPersons.size(); i++){
                        if (rootOddPersons.get(i).getTessera() == true && rootOddPersons.get(i).getDistrictID() != person2.getDistrictID()){
                            person1 = rootOddPersons.get(i);
                            rootOddPersons.remove(person1);
                            return person1;
                        }
                    }
                }
            }
            person1 = dtOdd(root.getLeft(), duelPair);
            if (person1 == null){
                person1 = dtOdd(root.getRight(), duelPair);
            }
        }
        return person1;
    }

    //tessera even
    private Person dtEven(TreeNode root, DuelPair duelPair){
        Person person1 = duelPair.getPerson1();
        Person person2 = duelPair.getPerson2();
        if (root != null){
            ArrayList<Person> rootEvenPersons = root.getDistrict().getEvenPopulation();
            if (person2 == null){
                if (person1 == null){
                    for (int i = 0; i < rootEvenPersons.size(); i++){
                    
                        if (rootEvenPersons.get(i).getTessera() == true){
                            person2 = rootEvenPersons.get(i);
                            rootEvenPersons.remove(person2);
                            return person2;
                        }
                    }
                }
                if (person1 != null){
                    for (int i = 0; i < rootEvenPersons.size(); i++){
                        if (rootEvenPersons.get(i).getTessera() == true && rootEvenPersons.get(i).getDistrictID() != person1.getDistrictID()){
                            person2 = rootEvenPersons.get(i);
                            rootEvenPersons.remove(i);
                            return person2;
                        }
                    }
                }
            }
            person2 = dtEven(root.getLeft(), duelPair);
            if (person2 == null){
                person2 = dtEven(root.getRight(), duelPair);
            }
        }
        return person2;
    }

    //non-tessera odd
    private Person dOdd(TreeNode root, Person person2){
        Person person1 = null;
        if (root != null){
            ArrayList<Person> rootOddPersons = root.getDistrict().getOddPopulation();
            int person2DistrictID = person2.getDistrictID();    
            if (person1 == null){
                if (rootOddPersons.get(0).getDistrictID() != person2DistrictID){
                    int randomdInt = StdRandom.uniform(rootOddPersons.size());
                    person1 = (rootOddPersons.get(randomdInt));
                    rootOddPersons.remove(person1);
                    return person1;
                }
            }
            person1 = dOdd((root.getLeft()), person2);
            if (person1 == null){
                person1 = dOdd(root.getRight(), person2);
            }
        }
        return person1;
    }

    //non-tessera even
    private Person dEven(TreeNode root, Person person1){
        Person person2 = null;
        if (root != null){
            ArrayList<Person> rootEvenPersons = root.getDistrict().getEvenPopulation();
            int person1DistrictID = person1.getDistrictID(); 
            if (person2 == null){
                if (rootEvenPersons.get(0).getDistrictID() != person1DistrictID){
                    int randomdInt = StdRandom.uniform(rootEvenPersons.size());
                    person2 = rootEvenPersons.get(randomdInt);
                    rootEvenPersons.remove(person2);
                    return person2;
                }
            }
            person2 = dEven(root.getLeft(), person1);
            if (person2 == null){
                person2 = dEven(root.getRight(), person1);
            }
        }
        return person2;
    }

    //both duelers
    private DuelPair dBoth(TreeNode root, DuelPair duelPair){
        if (root != null){
            ArrayList<Person> rootOddPersons = root.getDistrict().getOddPopulation();
            ArrayList<Person> rootEvenPersons = root.getDistrict().getEvenPopulation();
            if (duelPair.getPerson1() == null && duelPair.getPerson2() == null){
                int randomInt = StdRandom.uniform(rootOddPersons.size());
                duelPair.setPerson1(rootOddPersons.get(randomInt));
                rootOddPersons.remove(duelPair.getPerson1());
            }
            if (duelPair.getPerson1() != null && duelPair.getPerson2() == null){
                if (duelPair.getPerson1().getDistrictID() != root.getDistrict().getDistrictID()){
                    int randomInt = StdRandom.uniform(rootEvenPersons.size());
                    duelPair.setPerson2(rootEvenPersons.get(randomInt));
                    rootEvenPersons.remove(duelPair.getPerson2());
                }
            }
            if (duelPair.getPerson1() != null && duelPair.getPerson2() != null){
                return duelPair;
            }
            duelPair = dBoth(root.getLeft(), duelPair);
            if (duelPair.getPerson1() == null || duelPair.getPerson2() == null){
                duelPair = dBoth(root.getRight(), duelPair);
            }
        }
        return duelPair;
    }


    public DuelPair selectDuelers() {
        DuelPair duel = new DuelPair();
        DuelPair duel2 = new DuelPair();
        //tessera odd
        if (duel2.getPerson1() == null && duel2.getPerson2() == null){
            duel.setPerson1(dtOdd(game, duel2));
            duel.setPerson2(dtEven(game, duel));
        }
        //tessera even
        if (duel.getPerson1() == null || duel.getPerson2() == null){
            // person 1 not found
            if (duel.getPerson1() == null && duel.getPerson2() != null){
                Person person2 = duel.getPerson2();
                duel.setPerson1(dOdd(game, person2));
            }
            // person 2 not found
            if (duel.getPerson1() != null && duel.getPerson2() == null){
                Person person1 = duel.getPerson1();
                duel.setPerson2(dEven(game, person1));
            }
        }
        //no duelers found
        if (duel.getPerson1() == null && duel.getPerson2() == null){
            duel = dBoth(game, duel);
        }
        return duel; // update this line
    }


    /**
     * Deletes a district from the BST when they are eliminated from the game.
     * Districts are identified by id's.
     * If district does not exist, do nothing.
     * 
     * This is similar to the BST delete we have seen in class.
     * 
     * @param id the ID of the district to eliminate
     */
    public void eliminateDistrict(int id) {
        // WRITE YOUR CODE HERE
        TreeNode current = game;
        TreeNode cParent = game;
        TreeNode minN = null;
        TreeNode minPar = null;
        //id matches (game)
        if (current.getDistrict().getDistrictID() == id) {
            //two children
            if ((current.getLeft() != null) && (current.getRight() != null)) {
                minN = current.getRight();
                minPar = current;
                while (minN.getLeft() != null) {
                    minPar = minN;
                    minN = minN.getLeft();
                }
                game = minN;
                minPar.setLeft(null);
                return;
            }
            //one child
            else if (current.getLeft() != null) {
                game = current.getLeft();
                return;
            }
            else if (current.getRight() != null) {
                game = current.getRight();
                return;
            }
            //no children
            else {
                game = null;
                return;
            }
            }
        while (current != null) {
            //id matches (not game)
            if (current.getDistrict().getDistrictID() == id) {
                //two children
                if ((current.getLeft() != null) && (current.getRight() != null)) {
                    minN = current.getRight();
                    minPar = current;
                    while (minN.getLeft() != null) {
                        minPar = minN;
                        minN = minN.getLeft();
                    }
                    game = minN;
                    minPar.setLeft(null);
                    return;
                    }
                //one child
                else if (current.getLeft() != null) {
                    if (cParent.getRight() == current) {
                        cParent.setRight(current.getLeft());
                        return;
                    }
                    else {
                        cParent.setLeft(current.getLeft());
                        return;
                    }
                }
                else if (current.getRight() != null) {
                    if (cParent.getRight() == current) {
                        cParent.setRight(current.getRight());
                        return;
                    }
                    else {
                        cParent.setLeft(current.getRight());
                        return;
                    }
                }
                //no children
                else {
                    if (cParent.getLeft() == current) {
                        cParent.setLeft(null);
                        return;
                    }
                    else {
                        cParent.setRight(null);
                        return;
                    }
                }
            }
            //id doesn't match, move on
            else {
                if (current.getDistrict().getDistrictID() < id) {
                    cParent = current;
                    current = current.getRight();
                }
                else {
                    cParent = current;
                    current = current.getLeft();
                }
            }
        }
    }

    /**
     * Eliminates a dueler from a pair of duelers.
     * - Both duelers in the DuelPair argument given will duel
     * - Winner gets returned to their District
     * - Eliminate a District if it only contains a odd person population or even
     * person population
     * 
     * @param pair of persons to fight each other.
     */
    public void eliminateDueler(DuelPair pair) {
        // WRITE YOUR CODE HERE
        // check if one person in the duel pair
        if (pair.getPerson1() == null || pair.getPerson2() == null){
            // if person 1 is null, send person 2
            if (pair.getPerson1() == null && pair.getPerson2() != null){
                int distId = pair.getPerson2().getDistrictID();
                int birth = pair.getPerson2().getBirthMonth();
                District distInsert = findDistrict(distId);
                if (birth % 2 == 0){
                    distInsert.addEvenPerson(pair.getPerson2());
                }
                else{
                    distInsert.addOddPerson(pair.getPerson2());
                }
            }
            // if person 2 is null, send person 1 back
            if (pair.getPerson2() == null && pair.getPerson1() != null){
                int distId = pair.getPerson1().getDistrictID();
                int birth = pair.getPerson1().getBirthMonth();
                District distInsert = findDistrict(distId);
                if (birth % 2 == 0){
                    distInsert.addEvenPerson(pair.getPerson1());
                }
                else {
                    distInsert.addOddPerson(pair.getPerson1());
                }
            }
        }
        //duel pair is complete
        else {
            //find winner and loser
            Person winner = pair.getPerson1().duel(pair.getPerson2());
            // return winner
            int distIdW = winner.getDistrictID();
            int birthW = winner.getBirthMonth();
            District distToInsert = findDistrict(distIdW);
            if (birthW % 2 == 0){
                distToInsert.addEvenPerson(winner);
            }
            else {
                distToInsert.addOddPerson(winner);
            }
            Person loser = null;
            if (winner == pair.getPerson1()){
                loser = pair.getPerson2();
            }
            else {
                loser = pair.getPerson1();
            }
            // check winner district pop
            District wDist = findDistrict(distIdW);
            if (wDist.getEvenPopulation().size() == 0 || wDist.getOddPopulation().size() == 0) {
                eliminateDistrict(distIdW);
            }
            // check loser district pop
            int lDistId = loser.getDistrictID();
            District lDistrict = findDistrict(lDistId);
            if (lDistrict.getEvenPopulation().size() == 0 || lDistrict.getOddPopulation().size() == 0){
                eliminateDistrict(lDistId);
            }
        }
    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * 
     * Obtains the list of districts for the Driver.
     * 
     * @return the ArrayList of districts for selection
     */
    public ArrayList<District> getDistricts() {
        return this.districts;
    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * 
     * Returns the root of the BST
     */
    public TreeNode getRoot() {
        return game;
    }
}
