package sample.datacollection;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Scanner;

/**
 * Created by Mina on 7/31/2016.
 */
public class RunJoinTable {

    public static void main(String[] args) {

        // *** siteName, users.xml and Posts.xml addresses are needed! ***
        // so the user should enter them in console.

        int executionNumber = 1;
        String userCommand ="";
        Scanner scanner = new Scanner(System.in);

        // ** Programmer Defined Class Instantiation **
        CreateTables createTables = new CreateTables();
        ImportTables importTables = new ImportTables();
        JoinTable joinTable = new JoinTable();
        UserProfile userProfile = new UserProfile();

        // *** Running ***
        while(userCommand.equalsIgnoreCase("ok") || executionNumber == 1){
            try {
                // ** User Commands **
                System.out.println("Please enter STACK EXCHANGE SITE NAME: ");
                String siteName = scanner.nextLine().toLowerCase();
                System.out.println("Please enter users.xml adrress file: ");
                String usersFileAddress = scanner.nextLine();
                System.out.println("Please enter posts.xml adrress file: ");
                String postsFileAddress = scanner.nextLine();

                //   *************************
                File userFile = new File(usersFileAddress + "\\Users.xml");
                File postFile = new File(postsFileAddress + "\\Posts.xml");
                DocumentBuilderFactory dbFactory
                        = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(userFile);
                doc.getDocumentElement().normalize();
                NodeList nList = doc.getElementsByTagName("row");
                System.out.println("----------------------------");

                // ************************* Starting Creating Tables Files *****************************

                // First of all, Tables should be created!
                // **** In this case, we create Posts & Users tables according to Stack Exchange Schema. ***

                //  *** Users Table. ***
                if(!Tools.getTools().isExistTable(doc.getDocumentElement().getNodeName()))
                 createTables.makeTable(doc.getDocumentElement().getNodeName());

                //  *** Posts Table. ***
                doc = dBuilder.parse(postFile);
                doc.getDocumentElement().normalize();
                nList = doc.getElementsByTagName("row");
                if(!Tools.getTools().isExistTable(doc.getDocumentElement().getNodeName()))
                    createTables.makeTable(doc.getDocumentElement().getNodeName());

                //  *** Posts Users Joint Table. ***
                if(!Tools.getTools().isExistTable("UsersPostsJoin"))
                    createTables.makeJoinTable();


                //  *** UsersProfile Table. ***
                if(!Tools.getTools().isExistTable("UsersProfile"))
                    createTables.makeUserProfileTable();

                // ************************* Starting Importing Files into Tables  *****************************

                // *** Importing Users.xlm to Users Table ***
                doc = dBuilder.parse(postFile);
                doc.getDocumentElement().normalize();
                nList = doc.getElementsByTagName("row");
                importTables.createNewRecord(nList, doc.getDocumentElement().getNodeName(), siteName);

                // *** Importing Posts.xlm to Posts Table ***
                doc = dBuilder.parse(userFile);
                doc.getDocumentElement().normalize();
                nList = doc.getElementsByTagName("row");
                importTables.createNewRecord(nList, doc.getDocumentElement().getNodeName(), siteName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("If there are some left files to import into DB , please enter OK, otherwise " +
                    "push any keyboard to continue the program! ");
            userCommand = scanner.nextLine();
            executionNumber++;
        }
        // ************************* Joining between Users and Posts Tables *****************************
        joinTable.makeJointTable();
    }
}
