/**
 * 
 */
package obp.spots;

import org.joda.time.DateTime;

/**
 * @author bbehrens
 *
 */
public abstract class Spot {
	private int id;
	private String name;
	private int room;
	private int floor;
	private int group;
	private int x;
	private int y;
	private DateTime created = DateTime.now();
	private DateTime lastSeen = DateTime.now();
	
	protected Spot(int id, String name, int room, int floor, int group, int x, int y) {
		setId(id);
		setName(name);
		setRoom(room);
		setFloor(floor);
		setGroup(room);
		setX(x);
		setY(y);
	} // Constructor
	
	/**
	 * @return the reader id
	 */
	public int getId() {
		return id;
	} // getId

	/**
	 * @param id the reader id to set
	 */
	public void setId(int id) {
		this.id = id;
	} // setId
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	} // getName

	/**
	 * @param name a name for the reader
	 */
	public void setName(String name) {
		this.name = name;
	} // setName
	
	/**
	 * @return Created joda datetime
	 */
	public DateTime getCreated() {
		return created;
	} // getCreated

	/**
	 * @return the last seen date
	 */
	public DateTime getLastSeen() {
		return lastSeen;
	} // getLastSeen

	/**
	 * @param lastSeen the lastSeen to set
	 */
	public void setLastSeen(DateTime lastSeen) {
		this.lastSeen = lastSeen;
	} // setLastSeen

	/**
	 * @return the room
	 */
	public int getRoom() {
		return room;
	}

	/**
	 * @param room the room to set
	 */
	public void setRoom(int room) {
		this.room = room;
	}

	/**
	 * @return the floor
	 */
	public int getFloor() {
		return floor;
	}

	/**
	 * @param floor the floor to set
	 */
	public void setFloor(int floor) {
		this.floor = floor;
	}

	/**
	 * @return the group
	 */
	public int getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(int group) {
		this.group = group;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}
	
	public abstract SpotType getType();
}
