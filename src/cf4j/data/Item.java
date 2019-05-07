package cf4j.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cf4j.data.types.DynamicArray;
import cf4j.data.types.DynamicSortedArray;
import cf4j.utils.Methods;

/**
 * <p>Defines an item. An item is composed by:</p>
 * <ul>
 *  <li>Item code</li>
 *  <li>Item index in the items array</li>
 *  <li>A map where we can save any type of information</li>
 *  <li>Array of users who have rated the item</li>
 *  <li>Array of ratings that the item have received</li>
 * </ul>
 * @author Fernando Ortega
 */
public class Item implements Serializable, Comparable<Item> {

	private static final long serialVersionUID = 20171018L;

	/**
	 * Item code
	 */
	protected String itemCode;
	
	/**
	 * Map of the item
	 */
	protected Map <String, Object> map;

	/**
	 * Users that have rated this item
	 */
	protected DynamicSortedArray<String> users;

	/**
	 * Ratings of the users
	 */
	protected DynamicArray<Double> ratings;

	/**
	 * Rating average of the item
	 */
	protected double ratingAverage;
	
	/**
	 * Standard deviation of this item
	 */
	protected double ratingStandardDeviation;
	
	/**
	 * Creates a new instance of an item. This constructor should not be users by developers.
	 * @param itemCode Item code
	 */
	public Item (String itemCode) {
		this.itemCode = itemCode;
		this.map = new HashMap<String, Object>();
		this.users = new DynamicSortedArray<String>();
		this.ratings = new DynamicArray<Double>();
		//TODO: Metrics?
		//this.ratingAverage = Methods.arrayAverage(ratings);
		//this.ratingStandardDeviation = Methods.arrayStandardDeviation(ratings);
	}

	/**
	 * Write a data in the item map.
	 * @param key Key associated to the value
	 * @param value Value to be written in the map
	 * @return Previously value of the key if exists or null
	 */
	public synchronized Object put (String key, Object value) {		
		return map.put(key, value);
	}
	
	/**
	 * Retrieves a value from a key.
	 * @param key Key of the saved object
	 * @return The value associated to the key if exists or null
	 */
	public synchronized Object get(String key) {
		return map.get(key);
	}

	/**
	 * Average of the ratings received by the item.
	 * @return Rating average
	 */
	public double getRatingAverage() {
		return this.ratingAverage;
	}

	/**
	 * Standard deviation of the ratings received by the item.
	 * @return Rating standard deviation
	 */
	public double getRatingStandardDeviation() {
		return this.ratingStandardDeviation;
	}

	/**
	 * Return the item code.
	 * @return Item code
	 */
	public String getItemCode() {
		return this.itemCode;
	}
	
	/**
	 * Get the map of the item. It is recommended using put(...) and get(...) instead of
	 * this method.
	 * @return Map of the item
	 */
	public Map<String, Object> getMap() {
		return map;
	}
	
	/**
	 * Get the users that have rated the item.
	 * @return Test users codes sorted from low to high. 
	 */
	public DynamicSortedArray<String> getUsers() {
		return this.users;
	}
	
	/**
	 * Returns the user code at index position. 
	 * @param index Index.
	 * @return User code at index. 
	 */
	public String getUserAt(int index) {
		return this.getUsers().get(index);
	}

	/**
	 * Get the ratings of the users to the item. The indexes of the array overlaps
	 * with indexes of the getUsers() array.
	 * @return Training users ratings
	 */
	public DynamicArray<Double> getRatings() {
		return this.ratings;
	}
	
	/**
	 * Returns the rating at index position. 
	 * @param index Index.
	 * @return Rating at index. 
	 */
	public double getRatingAt(int index) {
		return this.getRatings().get(index);
	}
	
	/**
	 * Get the index of an user code at the user's item array.
	 * @param user_code User code
	 * @return User index in the user's item array if the user has rated the item or -1 if not
	 */
	public int getUserIndex (String user_code) {
		return users.get(user_code);
	}
	
	/**
	 * Get the number of ratings that the item have received.
	 * @return Number of ratings received
	 */
	public int getNumberOfRatings () {
		return this.ratings.size();
	}

	public void addRating(String userCode, double rating){
		ratings.add(users.add(userCode), new Double(rating));
	}

	@Override
	public int compareTo(Item o) {
		return this.itemCode.compareTo(o.itemCode);
	}
}
