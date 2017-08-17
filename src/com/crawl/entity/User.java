package com.crawl.entity;

/**
 * 知乎用户资料
 * 
 * @author hjg
 *
 */
public class User {
	/**
	 * 用户Id，由urlToken取得
	 */
	private String userId;
	/**
	 * 用户hashId(用户唯一标识)
	 */
	private String hashId;
	/**
	 * 用户名
	 */
	private String name;
	/**
	 * 位置
	 */
	private String location;
	/**
	 * 行业
	 */
	private String business;
	/**
	 * 性别
	 */
	private String gender;
	/**
	 * 赞同数
	 */
	private int voteupCount;
	/**
	 * 感谢数
	 */
	private int thankedCount;
	/**
	 * 提问数
	 */
	private int questionCount;
	/**
	 * 回答数
	 */
	private int answerCount;
	/**
	 * 文章数
	 */
	private int articlesCount;
	/**
	 * 关注人数
	 */
	private int followingCount;
	/**
	 * 粉丝人数
	 */
	private int followerCount;

	private String school;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getHashId() {
		return hashId;
	}

	public void setHashId(String hashId) {
		this.hashId = hashId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getBusiness() {
		return business;
	}

	public void setBusiness(String business) {
		this.business = business;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getVoteupCount() {
		return voteupCount;
	}

	public void setVoteupCount(int voteupCount) {
		this.voteupCount = voteupCount;
	}

	public int getThankedCount() {
		return thankedCount;
	}

	public void setThankedCount(int thankedCount) {
		this.thankedCount = thankedCount;
	}

	public int getQuestionCount() {
		return questionCount;
	}

	public void setQuestionCount(int questionCount) {
		this.questionCount = questionCount;
	}

	public int getAnswerCount() {
		return answerCount;
	}

	public void setAnswerCount(int answerCount) {
		this.answerCount = answerCount;
	}

	public int getArticlesCount() {
		return articlesCount;
	}

	public void setArticlesCount(int articlesCount) {
		this.articlesCount = articlesCount;
	}

	public int getFollowingCount() {
		return followingCount;
	}

	public void setFollowingCount(int followingCount) {
		this.followingCount = followingCount;
	}

	public int getFollowerCount() {
		return followerCount;
	}

	public void setFollowerCount(int followerCount) {
		this.followerCount = followerCount;
	}

	@Override
	public String toString() {
		return "User{" + "id=" + userId + ",hashId=" + hashId + ",name=" + name + ",location=" + location + ",business="
				+ business + ",gender=" + gender + ",voteupCount=" + voteupCount + ",thankedCount=" + thankedCount
				+ ",questionCount=" + questionCount + ",answerCount=" + answerCount + ",articlesCount=" + articlesCount
				+ ",followingCount=" + followingCount + ",followerCount=" + followerCount + "}";
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}
}
