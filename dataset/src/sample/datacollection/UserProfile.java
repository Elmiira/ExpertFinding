package sample.datacollection;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by elmira on 8/2/2016.
 */
public class UserProfile {


    // *** Properties ***
    private ResultSet resultSet;
    private ResultSet userResultSet;
    private String tag;
    private int totalQuestionNumber;
    private int totalAnswerNumber;
    private int userQuestions;
    private int userAnswers;
    private double postRate;
    private double questionRate;
    private double answerRate;
    private int programCounter;
    private double normalizedEntropyMeasure;
    private double normalizedTopicEntropy;
    private double topicalReputation;
    private List<String> tagList;
    Map<String, Integer> countByWords;
    Map<String, Integer> userTags;
    Map<Integer, List<String>> postTags;
    Map<Integer, Integer> postScores;

    // ** Constructor **
    UserProfile() {

        tag = "";
        tagList = new ArrayList<>();
        resultSet = null;
        userResultSet = null;
        totalAnswerNumber = 0;
        totalQuestionNumber = 0;
        userAnswers = 0;
        userQuestions = 0;
        postRate = 0;
        answerRate = 0;
        questionRate = 0;
        normalizedEntropyMeasure = 0;
        normalizedTopicEntropy = 0;
        topicalReputation = 0;
        programCounter = 0;
        countByWords = new HashMap<String, Integer>();
        userTags = new HashMap<>();
        postTags = new HashMap<Integer, List<String>>();
        postScores = new HashMap<Integer, Integer>();
    }

    // ***************************** Master Methods *****************************

    public void run() {
        this.makeTagList();
        System.out.println(tagList);
        this.makeIDList();
    }

    // ** Makes a List of all Tags among QAC Sites. **
    public void makeTagList() {

        String sql = "SELECT  posttags" +
                " FROM  " +
                "UsersPostsJoin ";
        try {
            Tools.getTools().setPreparedStatement(Tools.getTools().getConnection().prepareStatement(sql));
            ResultSet tempResultSet = Tools.getTools().getPreparedStatement().executeQuery();
            while (tempResultSet.next()) {
                try {
                    String temp = tempResultSet.getString(1);
                    Pattern pattern = Pattern.compile("<(.*?)>");
                    Matcher matcher = pattern.matcher(temp);
                    while (matcher.find()) {

                        tagList.add(temp.substring(matcher.start() + 1, matcher.end() - 1));
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ** Makes a query and fetch all users' IDs,
    // then continue the program based on distinct Fetched IDs. **
    public void makeIDList() {

        System.out.println("*** starts to query the whole userspostsjoin table:\n");
        String sql = "SELECT DISTINCT UserAccountId " +
                " FROM  " +
                "UsersPostsJoin ";
        try {
            Tools.getTools().setPreparedStatement(Tools.getTools().getConnection().prepareStatement(sql));
            resultSet = Tools.getTools().getPreparedStatement().executeQuery();
            // Iteration based on Users DISTINCT IDs.
            while (resultSet.next()) {
                if (resultSet.getInt(1) != 0) {
                    this.reset();
                    makeProfile(resultSet.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ** Gather or Calculate required profiled information about  a User. **
    public void makeProfile(int userID) {

        String sql = "SELECT * " +
                " FROM  " +
                "UsersPostsJoin " +
                " WHERE " +
                " UserAccountId = ?";
        try {
            Tools.getTools().setPreparedStatement(Tools.getTools().getConnection().prepareStatement(sql));
            Tools.getTools().getPreparedStatement().setInt(1, userID);
            userResultSet = Tools.getTools().getPreparedStatement().executeQuery();
            this.initiateVariables(userID);
            this.saveUserProfile(userID);
            this.report();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initiateVariables(int userID) {

        try {
            int recordCounter = 1;
            while (userResultSet.next()) {

                if (userResultSet.getInt("PostTypeId") == 1) {
                    userQuestions++;
                } else if (userResultSet.getInt("PostTypeId") == 2) {
                    userAnswers++;
                }
                this.postScores.put(recordCounter, userResultSet.getInt("PostScore"));
                this.separateTags(userResultSet.getString("PostTags"), recordCounter);
                recordCounter++;
            }
            this.setAnswerNumber();
            this.setQuestionNumber();
            this.setAnswerRate();
            this.setQuestionRate();
            this.setPostRate(userID);
            this.countTagNumber();
            this.setNormalizedEntropyMeasure();
            this.setNormalizedTopicEntropyAndTopicalReputation();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ** Insert a processed record into usersProfile table. **
    public void saveUserProfile(int userID) {

        try {
            String sql = "SELECT * " +
                    " FROM  " +
                    "UsersPostsJoin" +
                    " WHERE UserAccountId = ? ";

            Tools.getTools().setPreparedStatement(Tools.getTools().getConnection().prepareStatement(sql));
            Tools.getTools().getPreparedStatement().setInt(1, userID);
            ResultSet tempResultSet = Tools.getTools().getPreparedStatement().executeQuery();
            // Iteration based on Users DISTINCT IDs.
            if (tempResultSet.next()) {
                sql = "Insert Into usersprofile" +
                        "(id,tag,userAccountId,UserReputation,UserAge,UserVots,postRate,userAnswers,userQuestions,questionRate," +
                        "answerRate,normalizedEntropyMeasure,normalizedTopicEntropy,topicalReputation," +
                        "UserCreationDate,UserLastAccessDate,UserDisplayName,UserWebsiteUrl," +
                        "UserLocation,UserEmailHash)" +
                        " VALUES " +
                        " (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                Tools.getTools().setPreparedStatement(Tools.getTools().getConnection().prepareStatement(sql));
                Tools.getTools().getPreparedStatement().setInt(1, programCounter);
                Tools.getTools().getPreparedStatement().setString(2, tag);
                Tools.getTools().getPreparedStatement().setInt(3, tempResultSet.getInt("UserAccountId"));
                Tools.getTools().getPreparedStatement().setInt(4, tempResultSet.getInt("UserReputation"));
                Tools.getTools().getPreparedStatement().setInt(5, tempResultSet.getInt("UserAge"));
                Tools.getTools().getPreparedStatement().setInt(6, (tempResultSet.getInt("UserUpVotes")) -
                        (tempResultSet.getInt("UserDownVotes")));
                Tools.getTools().getPreparedStatement().setDouble(7, postRate);
                Tools.getTools().getPreparedStatement().setInt(8, userAnswers);
                Tools.getTools().getPreparedStatement().setInt(9, userQuestions);
                Tools.getTools().getPreparedStatement().setDouble(10, questionRate);
                Tools.getTools().getPreparedStatement().setDouble(11, answerRate);
                Tools.getTools().getPreparedStatement().setDouble(12, normalizedEntropyMeasure);
                Tools.getTools().getPreparedStatement().setDouble(13, normalizedTopicEntropy);
                Tools.getTools().getPreparedStatement().setDouble(14, topicalReputation);
                Tools.getTools().getPreparedStatement().setDate(15, getDate(String.valueOf(tempResultSet.getDate("UserCreationDate"))));
                Tools.getTools().getPreparedStatement().setDate(16, getDate(String.valueOf(tempResultSet.getDate("UserLastAccessDate"))));
                Tools.getTools().getPreparedStatement().setString(17, tempResultSet.getString("UserDisplayName"));
                Tools.getTools().getPreparedStatement().setString(18, tempResultSet.getString("UserWebsiteUrl"));
                Tools.getTools().getPreparedStatement().setString(19, tempResultSet.getString("UserLocation"));
                Tools.getTools().getPreparedStatement().setString(20, tempResultSet.getString("UserEmailHash"));
                System.out.println(Tools.getTools().getPreparedStatement() + " ****\n");
                Tools.getTools().getPreparedStatement().executeUpdate();
                programCounter++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ***************************** Others Method *****************************

    public java.sql.Date getDate(String date) {

        String pattern = "yyyy-MM-dd";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setLenient(false);
        try {
            return new java.sql.Date(format.parse(date).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // *** Tokenize user tags  based od <> (in <><><> format) into individual words. ***
    public void separateTags(String tags, int recordCounter) {

        System.out.println("separateTags method is called ****\n");
        List<String> tempList = new ArrayList<>();
        Pattern pattern = Pattern.compile("<(.*?)>");
        Matcher matcher = pattern.matcher(tags);
        while (matcher.find()) {
            String temp = tags.substring(matcher.start() + 1, matcher.end() - 1);

            // ** a Data Structure to capture the number of occurrence of individual Tags
            // IN THE WHOLE CORPUSE. **
            countByWords.put(temp, 0);

            // ** concat the tags together, it then will be used for similarity analysis between
            // Job Post keywords & User Expertise (TAGS). ***
            if (tag.equals("")) {
                tag = temp;
            } else {
                tag = tag + "," + temp;
            }
            tempList.add(temp);

            // ** a Data Structure to capture the number of occurrence of individual Tags
            // in USER'S POSTS. **
            if (userTags.containsKey(temp)) {
                userTags.put(temp,
                        userTags.get(temp) + 1);
            } else {
                userTags.put(temp, 1);
            }
        }
        // ** a Data Structure to capture  individual Tags OF USER'S POSTS. **
        postTags.put(recordCounter, tempList);
    }

    // *** Counting the Number of all Post Answers. ***
    public void setAnswerNumber() {

        if(totalAnswerNumber == 0){
            String sql = "SELECT count(*) " +
                    " FROM  " +
                    "UsersPostsJoin " +
                    " WHERE " +
                    " PostTypeId = 2 ";
            try {
                Tools.getTools().setPreparedStatement(Tools.getTools().getConnection().prepareStatement(sql));
                ResultSet tempResultSet = Tools.getTools().getPreparedStatement().executeQuery();
                if (tempResultSet.next()) {
                    totalAnswerNumber = tempResultSet.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // *** Counting the Number of all Post Questions. ***
    public void setQuestionNumber() {

        if(totalQuestionNumber ==0){
            String sql = "SELECT count(*) " +
                    " FROM  " +
                    "UsersPostsJoin " +
                    " WHERE " +
                    " PostTypeId = 1 ";
            try {
                System.out .println(sql);
                Tools.getTools().setPreparedStatement(Tools.getTools().getConnection().prepareStatement(sql));
                ResultSet tempResultSet = Tools.getTools().getPreparedStatement().executeQuery();
                if (tempResultSet.next()) {
                    totalQuestionNumber = tempResultSet.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void setAnswerRate() {
        answerRate = (double) userAnswers / totalAnswerNumber;
    }

    public void setQuestionRate() {
        questionRate = (double) userQuestions / totalQuestionNumber;
    }

    // ** First, it calculate the  total Scores per day,
    // then makes an average on total score days. **
    public void setPostRate(int userID) {

        String sql = "SELECT DISTINCT PostCreationDate" +
                " FROM  " +
                "UsersPostsJoin " +
                "WHERE " +
                "(" +
                "   UserAccountId = ?" +
                ")";
        try {
            Tools.getTools().setPreparedStatement(Tools.getTools().getConnection().prepareStatement(sql));
            Tools.getTools().getPreparedStatement().setInt(1, userID);
            ResultSet tempResultSet = Tools.getTools().getPreparedStatement().executeQuery();
            int postCounter = 0;
            ResultSet rs = null;
            while (tempResultSet.next()) {
                sql = "SELECT count(*)" +
                        " FROM  " +
                        "UsersPostsJoin " +
                        "WHERE " +
                        "(" +
                        "   PostCreationDate = ?" +
                        ")";
                System.out.println(sql);
                Tools.getTools().setPreparedStatement(Tools.getTools().getConnection().prepareStatement(sql));
                Tools.getTools().getPreparedStatement().setDate(1, tempResultSet.getDate("PostCreationDate"));
                rs = Tools.getTools().getPreparedStatement().executeQuery();
                if (rs.next()) {
                    postRate = postRate + rs.getInt(1);
                }
                postCounter++;
            }
            postRate = (double) postRate / postCounter;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setNormalizedEntropyMeasure() {

        if (userQuestions != 0 && userAnswers != 0) {
            normalizedEntropyMeasure = (double) -0.5 *
                    ((double)
                            (
                                    ((double) userQuestions / totalQuestionNumber) *
                                            (
                                                    (double) Math.log((double) userQuestions / totalQuestionNumber)
                                            )
                            )
                            +
                            (
                                    ((double) userAnswers / totalAnswerNumber) *
                                            (
                                                    (double) Math.log(((double) userAnswers / totalAnswerNumber))

                                            )
                            )
                    );

        } else if (userQuestions != 0 && userAnswers == 0) {
            normalizedEntropyMeasure = (double) -0.5 *
                    (

                            ((double) userQuestions / totalQuestionNumber) *
                                    (
                                            (double) Math.log((double) userQuestions / totalQuestionNumber)
                                    )


                    );
        } else if (userQuestions == 0 && userAnswers != 0) {
            normalizedEntropyMeasure = (double) -0.5 *
                    (
                            ((double) userAnswers / totalAnswerNumber) *
                                    (
                                            (double) Math.log(((double) userAnswers / totalAnswerNumber))

                                    )

                    );

        }
    }

    public void setNormalizedTopicEntropyAndTopicalReputation() {

        if (userTags.size() > 0) {
            Set<String> set = userTags.keySet();
            for (Map.Entry<String, Integer> map : userTags.entrySet()) {

               // System.out.println("users tags: " + map.getKey() + " " + map.getValue());
               // System.out.println("# of " + map.getKey() + " in the whole database is: " + countByWords.get(map.getKey()));
               // System.out.println("so calculation is: " + map.getValue() + " / " + countByWords.get(map.getKey()));
                if (countByWords.get(map.getKey()) != 0) {
                    normalizedTopicEntropy = (double)
                            (
                                    ((double)
                                            ((double) map.getValue() / countByWords.get(map.getKey()))
                                            *
                                            ((double) Math.log(((double) map.getValue() / countByWords.get(map.getKey()))))
                                    )


                            )
                            + normalizedTopicEntropy;
                }

                for (Map.Entry<Integer, List<String>> map2 : postTags.entrySet()) {

                   // System.out.println("check that is is belong to a post");
                    if (map2.getValue().contains(map.getKey())) {

                        /*System.out.println("yes " + map.getKey() + " is there in list of " +
                                map2.getKey() + " th post and also tag list of post is  " + map2.getValue());
                        System.out.println("so the score for it is " + postScores.get(map2.getKey()));*/
                        topicalReputation = topicalReputation +
                                (double) postScores.get(map2.getKey());
                    }

                }
            }
           // System.out.println("normalizedTopicEntropy is: " + normalizedTopicEntropy);
           // System.out.println("userTags size is: " + userTags.size());
            normalizedTopicEntropy = (-1) * normalizedTopicEntropy / userTags.size();
        }
    }

    // ** Counting the  occurence number of Tags in the whole corpuse. **
    public void countTagNumber() {

        for (String tag : tagList) {
            if (countByWords.containsKey(tag)) {
                countByWords.put(tag,
                        countByWords.get(tag) + 1);
            }
        }
    }

    // *** It will make a report of all memberVariables per a User ***
    public void report() {

        System.out.println("userAnswers numbers: " + userAnswers);
        System.out.println("userQuestions numbers: " + userQuestions);
        System.out.println("answerRate numbers: " + answerRate);
        System.out.println("questionRate numbers: " + questionRate);
        System.out.println("totalAnswerNumber numbers: " + totalAnswerNumber);
        System.out.println("totalQuestionNumber numbers: " + totalQuestionNumber);
        System.out.println("user tag : " + tag);
        System.out.println("topicalReputation: " + topicalReputation);
        System.out.println("normalizedEntropyMeasure : " + normalizedEntropyMeasure);
        System.out.println("normalizedTopicEntropyor : " + normalizedTopicEntropy);
        System.out.println("postRate : " + postRate);
    }

    // *** It will be clear memberVariables. ***
    public void reset() {

        tag = "";
        userAnswers = 0;
        userQuestions = 0;
        postRate = 0;
        answerRate = 0;
        questionRate = 0;
        normalizedEntropyMeasure = 0;
        normalizedTopicEntropy = 0;
        topicalReputation = 0;
        countByWords = new HashMap<String, Integer>();
        userTags = new HashMap<>();
        postTags = new HashMap<Integer, List<String>>();
        postScores = new HashMap<Integer, Integer>();
    }
}
