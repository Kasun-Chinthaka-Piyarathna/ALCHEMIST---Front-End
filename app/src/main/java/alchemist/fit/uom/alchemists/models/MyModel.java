package alchemist.fit.uom.alchemists.models;

public class MyModel {
    private String currentContext;
    private String userProfileImageUrl;
    private String userName;
    private String userNearestCity;
    private String postId;
    private String postText;
    private String postType;
    private String postFileUrl;
    private String postTimeStamp;

    public MyModel(String currentContext, String userProfileImageUrl, String userName, String userNearestCity,
                   String postId, String postText, String postType, String postFileUrl, String postTimeStamp) {
        this.currentContext = currentContext;
        this.userProfileImageUrl = userProfileImageUrl;
        this.userName = userName;
        this.userNearestCity = userNearestCity;
        this.postId = postId;
        this.postText = postText;
        this.postType = postType;
        this.postFileUrl = postFileUrl;
        this.postTimeStamp = postTimeStamp;
    }

    public String getCurrentContext() {
        return currentContext;
    }

    public String getUserProfileImageUrl() {
        return userProfileImageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserNearestCity() {
        return userNearestCity;
    }

    public String getPostId() {
        return postId;
    }

    public String getPostText() {
        return postText;
    }

    public String getPostType() {
        return postType;
    }

    public String getPostFileUrl() {
        return postFileUrl;
    }

    public String getPostTimeStamp() {
        return postTimeStamp;
    }

}

