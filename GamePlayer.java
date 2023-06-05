package boardgame;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

public class GamePlayer implements Player {
	private final Input input;
	private final Queue<Square> areas;
	private final String playerName;
	private int money;
	private int position;
	

	public GamePlayer(String playerName) {
		input = new Input();
		money = 1500;
		areas = new LinkedList<>();
		position = 0;
		this.playerName = playerName;
	}

	public void addArea(Square square) {
		if (!square.isOwnable())
			throw new IllegalArgumentException("This area cannot be purchased!");
		areas.add(square);
		square.purchase(this);
	}

	public void move(int numSpaces) {
		position += numSpaces;
		int BOARD_SIZE = 11;
		if (position >= BOARD_SIZE) {
			position -= BOARD_SIZE;
			excMoney(200);
		}

	}

	public void moveTo(int pos) {
		if (pos < position)
			excMoney(200);
		position = pos;

	}

	public int position() {
		return position;
	}

	public Queue<Square> areas() {
		return areas.stream().collect(Collectors.toCollection(LinkedList::new));
	}

	public String name() {
		return playerName;
	}

	public int getMoney() {
		return money;
	}

	public void excMoney(int money) {
		this.money += money;
	}


	public void sellProp(Square sq) {
		areas.remove(sq);
	}

	public int getAssets() {
		int assets = this.money;
		for (Square s : areas) {
			assets += s.cost();
			if (s instanceof Area)
				assets += getHouseVal((Area) s);
		}
		return assets;
	}

	private int getHouseVal(Area area) {
		int numDevs = area.numDevs();
		int areaCost = area.devCost();

		return numDevs * areaCost;
	}

	public boolean inputBool(Planet.State state) {
		return input.inputBool();
	}

	public int inputInt(Planet.State state) {
		return input.inputInt();
	}

	public int inputDecision(Planet.State state, String[] choices) {
		return input.inputDecision(choices);
	}

	public Player inputPlayer(Planet.State state, Player notAllowed) {
		return input.inputPlayer(state.players, notAllowed);
	}
}