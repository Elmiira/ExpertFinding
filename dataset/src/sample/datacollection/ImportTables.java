package sample.datacollection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by elmira on 7/27/2016.
 */
public class ImportTables {


    public void createNewRecord(NodeList nList, String tableName, String siteName) {

        System.out.println("Now table " + tableName + " is going to updated! \n");
        System.out.println("Wait please till all record will be imported to " +tableName + " table. :)" +  "...\n");
        int autoID = Tools.getTools().isEmptyTable(tableName);
        autoID++;

        // *** Users Table. ***
        if (tableName.equals("users")) {
            String sql = "Insert Into users" +
                    "(id,UserId,siteName,Reputation,CreationDate,DisplayName,LastAccessDate,WebsiteUrl,Location," +
                    "Views,UpVotes,DownVotes,EmailHash,Age,AccountId)" +
                    " VALUES " +
                    " (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            try {
                Tools.getTools().setPreparedStatement(Tools.getTools().getConnection().prepareStatement(sql));

                // *** Import each file row as a new record into users Table. ***
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        Tools.getTools().getPreparedStatement().setInt(1, autoID);
                        Tools.getTools().getPreparedStatement().setInt(2, Integer.parseInt(makeColumn(eElement, "Id")));
                        Tools.getTools().getPreparedStatement().setString(3, siteName);
                        Tools.getTools().getPreparedStatement().setInt(4, Integer.parseInt(makeColumn(eElement, "Reputation")));
                        Tools.getTools().getPreparedStatement().setDate(5, getDate(makeColumn(eElement, "CreationDate")));
                        Tools.getTools().getPreparedStatement().setString(6, makeColumn(eElement, "DisplayName"));
                        Tools.getTools().getPreparedStatement().setDate(7, getDate(makeColumn(eElement, "LastAccessDate")));
                        Tools.getTools().getPreparedStatement().setString(8, makeColumn(eElement, "WebsiteUrl"));
                        Tools.getTools().getPreparedStatement().setString(9, makeColumn(eElement, "Location"));
                        Tools.getTools().getPreparedStatement().setInt(10, Integer.parseInt(makeColumn(eElement, "Views")));
                        Tools.getTools().getPreparedStatement().setInt(11, Integer.parseInt(makeColumn(eElement, "UpVotes")));
                        Tools.getTools().getPreparedStatement().setInt(12, Integer.parseInt(makeColumn(eElement, "DownVotes")));
                        Tools.getTools().getPreparedStatement().setString(13, makeColumn(eElement, "EmailHash"));
                        Tools.getTools().getPreparedStatement().setInt(14, Integer.parseInt(makeColumn(eElement, "age")));
                        Tools.getTools().getPreparedStatement().setInt(15, Integer.parseInt(makeColumn(eElement, "AccountId")));
                        Tools.getTools().getPreparedStatement().executeUpdate();
                        autoID++;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // *** Posts Table. ***
        } else if (tableName.equals("posts")) {
            String sql = "Insert Into Posts" +
                    "(id,PostId,siteName,PostTypeId,CreationDate,Score,ViewCount,OwnerUserId,Tags," +
                    "AnswerCount,CommentCount,FavoriteCount)" +
                    " VALUES " +
                    " (?,?,?,?,?,?,?,?,?,?,?,?)";
            try {
                Tools.getTools().setPreparedStatement(Tools.getTools().getConnection().prepareStatement(sql));

                // *** Import each file row as a new record into posts Table. ***
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        int postTypeId = Integer.parseInt(makeColumn(eElement, "PostTypeId"));

                        // ** We just need to import posts that are question OR answer. **
                        if (postTypeId == 1 || postTypeId == 2) {
                            Tools.getTools().getPreparedStatement().setInt(1, autoID);
                            Tools.getTools().getPreparedStatement().setInt(2, Integer.parseInt(makeColumn(eElement, "Id")));
                            Tools.getTools().getPreparedStatement().setString(3, siteName);
                            Tools.getTools().getPreparedStatement().setInt(4, postTypeId);
                            Tools.getTools().getPreparedStatement().setDate(5, getDate(makeColumn(eElement, "CreationDate")));
                            Tools.getTools().getPreparedStatement().setInt(6, Integer.parseInt(makeColumn(eElement, "Score")));
                            Tools.getTools().getPreparedStatement().setInt(7, Integer.parseInt(makeColumn(eElement, "ViewCount")));
                            Tools.getTools().getPreparedStatement().setInt(8, Integer.parseInt(makeColumn(eElement, "OwnerUserId")));
                            Tools.getTools().getPreparedStatement().setString(9, makeColumn(eElement, "Tags"));
                            Tools.getTools().getPreparedStatement().setInt(10, Integer.parseInt(makeColumn(eElement, "AnswerCount")));
                            Tools.getTools().getPreparedStatement().setInt(11, Integer.parseInt(makeColumn(eElement, "CommentCount")));
                            Tools.getTools().getPreparedStatement().setInt(12, Integer.parseInt(makeColumn(eElement, "FavoriteCount")));
                            Tools.getTools().getPreparedStatement().executeUpdate();
                            autoID++;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public String makeColumn(Element eElement, String tag) {

        if (eElement.getAttributeNode(tag) != null) {
            if (tag.equals("CreationDate") || tag.equals("LastAccessDate")) {
                return String.valueOf(eElement.getAttributeNode(tag).getValue()).replace("T", " ");
            }
            return String.valueOf(eElement.getAttributeNode(tag).getValue());
        }
        return "0";
    }

    public Date getDate(String date) {

        String pattern = "yyyy-MM-dd HH:mm:ss.SSS";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setLenient(false);
        try {
            return new Date(format.parse(date).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
