package boardgame;

import java.util.LinkedList;
import java.util.Queue;

public class Area implements Square {
	//costs of rent for all possible Area states
	private final int rent;
	private final int dev1;
	private final int dev2;
	private final int dev3;
	private final int majdev;

	private final int value; //cost to purchase Area
	private final int devs; //cost to purchase one development on Area
	private final int pos;
	private final String name;
	private int devwork;  //building status
	private boolean planet; //does one player own all Areas in group?
	private boolean owned;  //is Area owned?
	private Player owner;
	private Area groupA;
	private Area groupB;

	//construct Area, given its rents
	public Area(String name, int pos, int rent, int dev1, int dev2, int dev3, 
			int majdev, int value, int devs) {
		this.rent = rent;
		this.dev1 = dev1;
		this.dev2 = dev2;
		this.dev3 = dev3;
		this.majdev = majdev;
		this.value = value;
		this.devs = devs;
		devwork = 0;
		planet = false;
		owned = false;

		this.pos = pos;
		this.name = name;
	}

	public void setGroup(Area groupA, Area groupB) {
		this.groupA = groupA;
		this.groupB = groupB;
	}

	public int position() {
		return pos;
	}

	public String name() {
		return name;
	}

	public boolean isOwnable() {
		return true;
	}

	//update status of area to owned
	public void purchase(Player player) {
		owned = true;
		owner = player;

		updateGame(player);
	}

	private void updateGame(Player player) {
		boolean a = false;
		boolean b = false;

		if (groupB == null)
			b = true;

		Queue<Area> props = new LinkedList<>();
		for (Square sq : player.areas())
			if (sq instanceof Area)
				props.add((Area) sq);

		for (Area prop : props) {
			if (prop.name().equals(groupA.name()))
				a = true;
			if (groupB != null && prop.name().equals(groupB.name()))
				b = true;
		}

		if (a && b) {
			setPlanet();
			groupA.setPlanet();
			if (groupB != null)
				groupB.setPlanet();
		} else {
			breakPlanet();
			groupA.breakPlanet();
			if (groupB != null)
				groupB.breakPlanet();
		}
	}

	public boolean isOwned() {
		return owned;
	}

	//update building status by integer input
	public void build(int a) {
		devwork += a;
		if (devwork > 4)
			throw new IllegalArgumentException("Cannot build past majdev!");
		if (devwork < 0)
			throw new IllegalArgumentException("Cannot build negative buildings!");
	}

	//switch status of monopoly
	public boolean planet() {
		return planet;
	}

	//cost to purchase property
	public int cost() {
		return value;
	}

	//return number of buildings owned
	public int numDevs() {
		return devwork;
	}

	//return cost to purchase one development
	public int devCost() {
		return devs;
	}

	//return amount owed
	public int rent(int val) {
		if (!owned)
			return 0;
		switch (devwork) {
			case 0:
				if (planet) return 2 * rent;
				return rent;
			case 1:
				return dev1;
			case 2:
				return dev2;
			case 3:
				return dev3;
			case 4:
				return majdev;
			default:
				return 0;
		}
	}

	public int rentDiff() {
		if (!owned || !planet || (devwork == 5))
			return 0;
		switch (devwork) {
			case 0:
				return dev1 - 2 * rent;
			case 1:
				return dev2 - dev1;
			case 2:
				return dev3 - dev2;
			case 3:
				return majdev - dev3;
			default:
				return 0;
		}
	}

	public Player owner() {
		return owner;
	}

	
	private void setPlanet() {
		planet = true;
	}

	private void breakPlanet() {
		planet = false;
	}

	public boolean groupBuild() {
		if (!planet)
			return false;

		int aDiff = groupA.numDevs() - numDevs();
		boolean aOkay = aDiff == 0 || aDiff == 1;
		if (groupB == null)
			return aOkay;

		int bDiff = groupB.numDevs() - numDevs();
		boolean bOkay = bDiff == 0 || bDiff == 1;

		return aOkay && bOkay;
	}


	public String toString() {
		if (numDevs() == 4)
			return name + " - Major Development";
		if (numDevs() > 0)
			return name + " - " + numDevs() + " Developments";
		return name;
	}
}