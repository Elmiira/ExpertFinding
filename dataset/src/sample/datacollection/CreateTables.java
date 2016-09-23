package sample.datacollection;

import java.sql.*;

/**
 * Created by elmira on 7/31/2016.
 */
public class CreateTables {

                      // ****************** Tables will be created in order! ******************

    public void makeTable(String tableName) {

        String sql = "";
        if (tableName.equals("users")) {
            sql = "CREATE TABLE Users " +
                    "(id INTEGER not NULL, " +
                    " UserId int, " +
                    " siteName VARCHAR(70), " +
                    " Reputation int, " +
                    " CreationDate DATE , " +
                    " DisplayName VARCHAR(40), " +
                    " LastAccessDate DATE, " +
                    " WebsiteUrl VARCHAR(200), " +
                    " Location VARCHAR(255), " +
                    " Views int, " +
                    " UpVotes int, " +
                    " DownVotes int, " +
                    " EmailHash VARCHAR(32), " +
                    " Age int, " +
                    " AccountId int, " +
                    " PRIMARY KEY ( id ))";

        } else if (tableName.equals("posts")) {
            sql = "CREATE TABLE Posts " +
                    "(id INTEGER not NULL, " +
                    " PostId int, " +
                    " siteName VARCHAR(70), " +
                    " PostTypeId int, " +
                    " CreationDate DATE , " +
                    " Score int, " +
                    " ViewCount int, " +
                    " OwnerUserId int, " +
                    " Tags NVARCHAR(250), " +
                    " AnswerCount int, " +
                    " CommentCount int, " +
                    " FavoriteCount int, " +
                    " PRIMARY KEY ( id ))";
        }
        try {
            Tools.getTools().setPreparedStatement(Tools.getTools().getConnection().prepareStatement(sql));
            Tools.getTools().getPreparedStatement().execute();
            System.out.println(" ** table " + tableName + "  is created ** \n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void makeJoinTable() {

        String sql = "CREATE TABLE UsersPostsJoin " +
                "(id INTEGER not NULL," +
                " UserId int, " +
                " siteName VARCHAR(70), " +
                " PostTypeId int, " +
                " PostScore int, " +
                " PostViewCount int, " +
                " PostTags NVARCHAR(250), " +
                " PostAnswerCount int, " +
                " PostCommentCount int, " +
                " PostFavoriteCount int, " +
                " PostOwnerUserId int, " +
                " UserReputation int, " +
                " UserViews int, " +
                " UserUpVotes int, " +
                " UserDownVotes int, " +
                " PostCreationDate DATE , " +
                " UserCreationDate DATE , " +
                " UserDisplayName VARCHAR(40), " +
                " UserLastAccessDate DATE, " +
                " UserWebsiteUrl VARCHAR(200), " +
                " UserLocation VARCHAR(255), " +
                " UserEmailHash VARCHAR(32), " +
                " UserAge int, " +
                " UserAccountId int, " +
                " PRIMARY KEY ( id ))";
        try {
            Tools.getTools().setPreparedStatement(Tools.getTools().getConnection().prepareStatement(sql));
            Tools.getTools().getPreparedStatement().execute();
            System.out.println(" ** table UsersPostsJoin  is created ** \n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void makeUserProfileTable() {

        String sql = "CREATE TABLE UsersProfile " +
                "(id INTEGER not NULL," +
                " tag TEXT, " +
                " userAccountId int, " +
                " UserReputation int, " +
                " UserAge int, " +
                " UserVots int, " +
                " postRate DOUBLE, " +
                " userAnswers int, " +
                " userQuestions int, " +
                " questionRate DOUBLE, " +
                " answerRate DOUBLE, " +
                " normalizedEntropyMeasure DOUBLE, " +
                " normalizedTopicEntropy DOUBLE, " +
                " topicalReputation DOUBLE , " +
                " UserCreationDate DATE , " +
                " UserLastAccessDate DATE, " +
                " UserDisplayName VARCHAR(40), " +
                " UserWebsiteUrl VARCHAR(200), " +
                " UserLocation VARCHAR(255), " +
                " UserEmailHash VARCHAR(32), " +
                " PRIMARY KEY ( id ))";
        try {
            Tools.getTools().setPreparedStatement(Tools.getTools().getConnection().prepareStatement(sql));
            Tools.getTools().getPreparedStatement().execute();
            System.out.println(" ** table UsersProfile  is created ** \n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
