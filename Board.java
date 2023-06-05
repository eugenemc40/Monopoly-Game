package boardgame;

class Board {
	private final int N = 12;
	private final Square[] board; //representation of board
	

	//constructor for a new board of squares
	public Board() {
		board = new Square[N];
		//initialize board squares
		for (int i = 0; i < N; i++)
			board[i] = makeSquare(i);

		makeGroups();
		}

	public int size() {
		return N;
	}

	private Area property(String name) {
		for (Square sq : board) {
			if (sq instanceof Area && sq.name().equals(name))
				return (Area) sq;
		}

		return null;
	}

	public Square square(int pos) {
		return board[pos];
	}

	//return an array of the squares on the board
	public Square[] getBoard() {
		return board;
	}

	private Square makeSquare(int pos) {
		switch (pos) {
			case 0:
				return eden(pos);
			case 1:
				return gobi(pos);
			case 2:
				return sahara(pos);
			case 3:
				return indian(pos);
			case 4:
				return pacific(pos);
			case 5:
				return atlantic(pos);
			case 6:
				return greenpeace(pos);
			case 7:
				return amazon(pos);
			case 8:
				return congo(pos);
			case 9:
				return australasian(pos);
			case 10:
				return eurasian(pos);
			case 11:
				return american(pos);
			default:
				return null;
		}
	}

	private void makeGroups() {
		makeGroup("Gobi Desert", "Sahara Desert");
		makeGroup("Indian Ocean", "Pacific Ocean", "Atlantic Ocean");
		makeGroup("Amazon Rainforest", "Congo Rainforest", "Australasian Rainforest");
		makeGroup("Eurasian Steppes", "North American Prairies");
	}

	
	private void makeGroup(String nameA, String nameB) {
		makeGroup(nameA, nameB, null);
	}

	private void makeGroup(String nameA, String nameB, String nameC) {
		Area propA = property(nameA);
		Area propB = property(nameB);
		Area propC = null;
		if (nameC != null)
			propC = property(nameC);

		if (propA == null || propB == null)
			throw new RuntimeException("Bad property");

		propA.setGroup(propB, propC);
		propB.setGroup(propA, propC);
		if (propC != null)
			propC.setGroup(propA, propB);
	}

	
	private Square eden(int pos) {
		return new Inactive("Eden Forest", pos);
	}

	private Square gobi(int pos) {
		int rent = 2;
		int dev1 = 10;
		int dev2 = 30;
		int dev3 = 90;
		int majorDev = 250;
		int cost = 60;
		int devs = 50;
		return new Area("Gobi Desert", pos, rent, dev1, dev2, dev3, majorDev, cost, devs);
	}

	private Square sahara(int pos) {
		int rent = 4;
		int dev1 = 20;
		int dev2 = 60;
		int dev3 = 180;
		int majorDev = 450;
		int cost = 60;
		int devs = 50;
		return new Area("Sahara Desert", pos, rent, dev1, dev2, dev3, majorDev, cost, devs);
	}
	private Square indian(int pos) {
		int rent = 4;
		int dev1 = 20;
		int dev2 = 60;
		int dev3 = 180;
		int majorDev = 450;
		int cost = 60;
		int devs = 50;
		return new Area("Indian Ocean", pos, rent, dev1, dev2, dev3, majorDev, cost, devs);
	}

	private Square pacific(int pos) {
		int rent = 6;
		int dev1 = 30;
		int dev2 = 90;
		int dev3 = 270;
		int majorDev = 550;
		int cost = 100;
		int devs = 50;
		return new Area("Pacific Ocean", pos, rent, dev1, dev2, dev3,majorDev, cost, devs);
	}

	private Square atlantic(int pos) {
		int rent = 6;
		int dev1 = 30;
		int dev2 = 90;
		int dev3 = 270;
		int majorDev = 550;
		int cost = 100;
		int devs = 50;
		return new Area("Atlantic Ocean", pos, rent, dev1, dev2, dev3,majorDev, cost, devs);
	}

	private Square amazon(int pos) {
		int rent = 8;
		int dev1 = 40;
		int dev2 = 100;
		int dev3 = 300;
		int majorDev = 600;
		int cost = 120;
		int devs = 50;
		return new Area("Amazon Rainforest", pos, rent, dev1, dev2, dev3, majorDev, cost, devs);
	}

	private Square congo(int pos) {
		int rent = 10;
		int dev1 = 50;
		int dev2 = 150;
		int dev3 = 450;
		int majorDev = 750;
		int cost = 140;
		int devs = 100;
		return new Area("Congo Rainforest", pos, rent, dev1, dev2, dev3, majorDev, cost, devs);
	}
	
	private Square australasian(int pos) {
		int rent = 10;
		int dev1 = 50;
		int dev2 = 150;
		int dev3 = 450;
		int majorDev = 750;
		int cost = 140;
		int devs = 100;
		return new Area("Australasian Rainforest", pos, rent, dev1, dev2, dev3, majorDev, cost, devs);
	}
	
	private Square eurasian(int pos) {
		int rent = 10;
		int dev1 = 50;
		int dev2 = 150;
		int dev3 = 450;
		int majorDev = 750;
		int cost = 140;
		int devs = 100;
		return new Area("Eurasian Steppes", pos, rent, dev1, dev2, dev3, majorDev, cost, devs);
	}
	
	private Square american(int pos) {
		int rent = 10;
		int dev1 = 50;
		int dev2 = 150;
		int dev3 = 450;
		int majorDev = 750;
		int cost = 140;
		int devs = 100;
		return new Area("North American Prairies", pos, rent, dev1, dev2, dev3, majorDev, cost, devs);
	}

	private Square greenpeace(int pos) {
		return new Inactive("Greenpeace Square", pos);
	}

}