package cf4j.algorithms.knn.userToUser.similarities;

import cf4j.data.DataModel;
import cf4j.data.TestUser;
import cf4j.data.User;
import cf4j.process.TestUserPartible;

/**
 * <p>This class process the similarity measure between two users. If you want to define your own similarity
 * metric implementation, you must extend this class and implements the abstract method similarity (...).</p>
 * 
 * <p>When the execution of the similarity metric is complete, all the test users will have a double array
 * on his map saved with the key "similarities". The similarities can be retrieved using getSimilarities() 
 * method of TestUser class. The positions of this array overlaps with the array returned by the method 
 * getUsers() of the Kernel class. For example, testUser.getSimilarities()[i] will contains the similarity 
 * between testUser and Kernel.getInstance().getUsers()[i] user.</p>
 * 
 * @author Fernando Ortega
 */
abstract public class UserSimilarities extends TestUserPartible {

	public UserSimilarities(DataModel dataModel) {
		super(dataModel);
	}

	/**
	 * <p>This method must returns the similarity between two users.</p> 
	 * <p>If two users do not have a similarity value, the method must return Double.NEGATIVE_INIFINITY.</p>
	 * <p>The value returned by this method should be higher the higher the similarity between users.</p>
	 * @param activeUser Active user
	 * @param targetUser User with which the similarity is computed
	 * @return Similarity between activeUser and targetUser
	 */
	abstract public double similarity (TestUser activeUser, User targetUser);

	@Override
	public void beforeRun () { }

	@Override
	public void run (int testUserIndex) {
		TestUser activeUser = dataModel.getTestUserAt(testUserIndex);
		
		int numUsers = dataModel.getNumberOfUsers();
		Double [] similarities = new Double [numUsers];
		
		for (int u = 0; u < similarities.length; u++) {
			User targetUser = dataModel.getUserAt(u);
			if (activeUser.getUserCode() == targetUser.getUserCode()) {
				similarities[u] = Double.NEGATIVE_INFINITY;
			} else { 
				similarities[u] = this.similarity(activeUser, targetUser);
			}
		}
		
		activeUser.getStoredData().setDoubleArray(TestUser.SIMILARITIES_KEY,similarities);
	}
	
	@Override
	public void afterRun () { }
}
