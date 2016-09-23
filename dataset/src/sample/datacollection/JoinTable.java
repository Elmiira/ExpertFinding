package sample.datacollection;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by elmira on 7/31/2016.
 */
public class JoinTable {


    // *** Properties ***
    private ResultSet resultSet;
    private HashMap<String, String> reputationAVGValue;
    private HashMap<String, String> viewCountAVGValue;
    private List<String> siteNameList;
    private List<Integer> userIDList;


    // *** Constructor ***
    JoinTable() {

        reputationAVGValue = new HashMap<>();
        viewCountAVGValue = new HashMap<>();
        siteNameList = new ArrayList<>();
        resultSet = null;
        userIDList = new ArrayList<>();
    }

    // We consider data of 3 Sites are imported into Databases; if You
    // want to import more files from other's STACKEXCHANGE SITES, please enter similar codes below.
    public void initialization() {

        siteNameList = Tools.getTools().getSiteNames();

        for (String string : siteNameList) {

            if (string.contains("stackoverflow") || string.contains("stack overflow")) {
                reputationAVGValue.put(string, "121");
                viewCountAVGValue.put(string, "350");
            } else if (string.contains("serverfault") || string.contains("server fault")) {
                reputationAVGValue.put(string, "82");
                viewCountAVGValue.put(string, "324");
            } else if (string.contains("datascience") || string.contains("data science")) {
                reputationAVGValue.put(string, "47");
                viewCountAVGValue.put(string, "324");
            }else if (string.contains("firstSite") || string.contains("first site")) {
                reputationAVGValue.put(string, "22");
                viewCountAVGValue.put(string, "324");
            }else if (string.contains("secondSite") || string.contains("second site")) {
                reputationAVGValue.put(string, "22");
                viewCountAVGValue.put(string, "324");
            }
        }
    }


    public void makeJointTable() {

        this.initialization();

        System.out.println("Wait please till all records retrieve from USERS & POSTS tables and then  imported to " +
                " UsersPostsJoin table. :)" + "\n...\n");
        String sql = "";

        for (String siteName : siteNameList) {

            sql = "SELECT u.* , p.* " +
                    " FROM  " +
                    "Posts AS p " +
                    "INNER JOIN " +
                    "Users AS u" +
                    " ON " +
                    "(" +
                    "  p.OwnerUserId = u.UserId " +
                    "   AND " +
                    "   p.siteName = u.siteName" +
                    ") " +
                    "WHERE " +
                    "(" +
                    "   (" +
                    "   p.score>2" +
                    "   OR " +
                    "   (p.viewCount>" + viewCountAVGValue.get(siteName) + " AND  p.commentcount>1 AND  p.AnswerCount>1)" +
                    "    OR" +
                    "    u.reputation> " + reputationAVGValue.get(siteName) +
                    "    )" +
                    "    AND " +
                    " p.siteName='" + siteName + "'" +
                    ")";
            try {
                Tools.getTools().setPreparedStatement(Tools.getTools().getConnection().prepareStatement(sql));
                resultSet = Tools.getTools().getPreparedStatement().executeQuery();
                this.listTravers();
                System.out.println(sql);
                Tools.getTools().report("UsersPostsJoin");
                this.makeIDList();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        this.makeReport();
    }

    public void listTravers() {

        try {
            int autoID = Tools.getTools().isEmptyTable("UsersPostsJoin");
            autoID++;
            while (resultSet.next()) {
                String sql = "Insert Into UsersPostsJoin" +
                        "(id,UserId, siteName,PostTypeId,PostScore,PostViewCount,PostTags,PostAnswerCount,PostCommentCount," +
                        "PostFavoriteCount,PostOwnerUserId,UserReputation,UserViews,UserUpVotes,UserDownVotes," +
                        "PostCreationDate,UserCreationDate,UserDisplayName,UserLastAccessDate,UserWebsiteUrl," +
                        "UserLocation,UserEmailHash,UserAge,UserAccountId)" +
                        " VALUES " +
                        " (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                Tools.getTools().setPreparedStatement(Tools.getTools().getConnection().prepareStatement(sql));
                Tools.getTools().getPreparedStatement().setInt(1, autoID);
                Tools.getTools().getPreparedStatement().setInt(2, resultSet.getInt("UserId"));
                Tools.getTools().getPreparedStatement().setString(3, resultSet.getString("siteName"));
                Tools.getTools().getPreparedStatement().setInt(4, resultSet.getInt("PostTypeId"));
                Tools.getTools().getPreparedStatement().setInt(5, resultSet.getInt("Score"));
                Tools.getTools().getPreparedStatement().setInt(6, resultSet.getInt("ViewCount"));
                Tools.getTools().getPreparedStatement().setString(7, resultSet.getString("Tags"));
                Tools.getTools().getPreparedStatement().setInt(8, resultSet.getInt("AnswerCount"));
                Tools.getTools().getPreparedStatement().setInt(9, resultSet.getInt("CommentCount"));
                Tools.getTools().getPreparedStatement().setInt(10, resultSet.getInt("FavoriteCount"));
                Tools.getTools().getPreparedStatement().setInt(11, resultSet.getInt("OwnerUserId"));
                Tools.getTools().getPreparedStatement().setInt(12, resultSet.getInt("Reputation"));
                Tools.getTools().getPreparedStatement().setInt(13, resultSet.getInt("Views"));
                Tools.getTools().getPreparedStatement().setInt(14, resultSet.getInt("UpVotes"));
                Tools.getTools().getPreparedStatement().setInt(15, resultSet.getInt("DownVotes"));
                Tools.getTools().getPreparedStatement().setDate(16, getDate(String.valueOf(resultSet.getDate("CreationDate"))));
                Tools.getTools().getPreparedStatement().setDate(17, getDate(String.valueOf(resultSet.getDate("CreationDate"))));
                Tools.getTools().getPreparedStatement().setString(18, resultSet.getString("DisplayName"));
                Tools.getTools().getPreparedStatement().setDate(19, getDate(String.valueOf(resultSet.getDate("LastAccessDate"))));
                Tools.getTools().getPreparedStatement().setString(20, resultSet.getString("WebsiteUrl"));
                Tools.getTools().getPreparedStatement().setString(21, resultSet.getString("Location"));
                Tools.getTools().getPreparedStatement().setString(22, resultSet.getString("EmailHash"));
                Tools.getTools().getPreparedStatement().setInt(23, resultSet.getInt("Age"));
                Tools.getTools().getPreparedStatement().setInt(24, resultSet.getInt("AccountId"));
                Tools.getTools().getPreparedStatement().executeUpdate();
                autoID++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void makeIDList(){

        String  sql = "SELECT DISTINCT UserId" +
                " FROM  " +
                "UsersPostsJoin " ;
        try {
            Tools.getTools().setPreparedStatement(Tools.getTools().getConnection().prepareStatement(sql));
            resultSet = Tools.getTools().getPreparedStatement().executeQuery();
            while (resultSet.next())
                userIDList.add(resultSet.getInt(1));
            System.out.println("number of Unique users are: " + userIDList.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void makeReport() {

        Tools.getTools().report("UsersPostsJoin");
        Tools.getTools().report("USers");
        Tools.getTools().report("Posts");
    }

    public Date getDate(String date) {

        String pattern = "yyyy-MM-dd";
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
