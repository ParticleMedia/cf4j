package es.upm.etsisi.cf4j.recommender.matrixFactorization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.util.Parallelizer;
import es.upm.etsisi.cf4j.util.Partible;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.util.Maths;

import java.util.Random;

/**
 * Implements Lee, D. D., &amp;  Seung, H. S. (2001). Algorithms for non-negative matrix factorization. In Advances
 * in neural information processing systems (pp. 556-562).
 * @author Fernando Ortega
 */
public class NMF extends Recommender {

	/**
	 * User factors
	 */
	private double[][] w;

	/**
	 * Item factors
	 */
	private double[][] h;

	/**
	 * Number of factors
	 */
	private int numFactors;

	/**
	 * Number of iterations
	 */
	private int numIters;

	/**
	 * Model constructor
	 * @param datamodel DataModel instance
	 * @param numFactors Number of factors
	 * @param numIters Number of iterations
	 */
	public NMF(DataModel datamodel, int numFactors, int numIters) {
		this(datamodel, numFactors, numIters, System.currentTimeMillis());
	}

	/**
	 * Model constructor
	 * @param datamodel DataModel instance
	 * @param numFactors Number of factors
	 * @param numIters Number of iterations
	 * @param seed Seed for random numbers generation
	 */
	public NMF(DataModel datamodel, int numFactors, int numIters, long seed) {
		super(datamodel);

		this.numFactors = numFactors;
		this.numIters = numIters;

		Random rand = new Random(seed);

		// Users initialization
		this.w = new double[datamodel.getNumberOfUsers()][numFactors];
		for (int u = 0; u < datamodel.getNumberOfUsers(); u++) {
			for (int k = 0; k < numFactors; k++) {
				this.w[u][k] = 1 - rand.nextDouble();
			}
		}

		// Items initialization
		this.h = new double[datamodel.getNumberOfItems()][numFactors];
		for (int i = 0; i < datamodel.getNumberOfItems(); i++) {
			for (int k = 0; k < numFactors; k++) {
				this.h[i][k] = 1 - rand.nextDouble();
			}
		}
	}

	/**
	 * Get the number of factors of the model
	 * @return Number of factors
	 */
	public int getNumFactors() {
		return this.numFactors;
	}

	/**
	 * Get the number of iterations
	 * @return Number of iterations
	 */
	public int getNumIters() {
		return this.numIters;
	}

	@Override
	public void fit() {

		System.out.println("\nProcessing NMF...");

		for (int iter = 1; iter <= this.numIters; iter++) {
			Parallelizer.exec(this.datamodel.getUsers(), new UpdateUsersFactors());
			Parallelizer.exec(this.datamodel.getItems(), new UpdateItemsFactors());
		}
	}

	@Override
	public double predict(int userIndex, int itemIndex) {
		return Maths.dotProduct(this.w[userIndex], this.h[itemIndex]);
	}

	/**
	 * Auxiliary inner class to parallelize user factors computation
	 */
	private class UpdateUsersFactors implements Partible<User> {

		@Override
		public void beforeRun() { }

		@Override
		public void run(User user) {
			int userIndex = user.getUserIndex();

			double [] wu = w[userIndex];

			double [] predictions = new double [user.getNumberOfRatings()];
			for (int i = 0; i < user.getNumberOfRatings(); i++) {
				int itemIndex = user.getItemAt(i);
				predictions[i] = predict(userIndex, itemIndex);
			}

			for (int k = 0; k < NMF.this.numFactors; k++) {

				double sumRatings = 0;
				double sumPredictions = 0;

				for (int i = 0; i < user.getNumberOfRatings(); i++) {
					int itemIndex = user.getItemAt(i);
					double [] hi = h[itemIndex];
					sumRatings += hi[k] * user.getRatingAt(i);
					sumPredictions += hi[k] * predictions[i];
				}

				wu[k] = wu[k] * sumRatings / (sumPredictions + 1E-10);
			}
		}

		@Override
		public void afterRun() { }
	}

	/**
	 * Auxiliary inner class to parallelize item factors computation
	 */
	private class UpdateItemsFactors implements Partible<Item> {

		@Override
		public void beforeRun() { }

		@Override
		public void run(Item item) {
			int itemIndex = item.getItemIndex();

			double [] hi = h[itemIndex];

			double [] predictions = new double [item.getNumberOfRatings()];
			for (int u = 0; u < item.getNumberOfRatings(); u++) {
				int userIndex = item.getUserAt(u);
				predictions[u] = predict(userIndex, itemIndex);
			}

			for (int k = 0; k < NMF.this.numFactors; k++) {

				double sumRatings = 0;
				double sumPredictions = 0;

				for (int u = 0; u < item.getNumberOfRatings(); u++) {
					int userIndex = item.getUserAt(u);
					double [] wu = w[userIndex];
					sumRatings += wu[k] * item.getRatingAt(u);
					sumPredictions += wu[k] * predictions[u];
				}

				hi[k] = hi[k] * sumRatings / (sumPredictions + 1E-10);
			}
		}

		@Override
		public void afterRun() { }
	}
}
