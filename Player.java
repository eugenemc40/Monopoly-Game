package boardgame;

interface Player {
	/* Player stuff */
	void addArea(Square square);

	void move(int numSpaces);

	void moveTo(int pos);

	int position();

	Iterable<Square> areas();

	String name();

	int getMoney();

	void excMoney(int money);

	int getAssets();

	/* Input stuff */
	boolean inputBool(Planet.State state);

	int inputInt(Planet.State state);

	int inputDecision(Planet.State state, String[] choices);

	Player inputPlayer(Planet.State state, Player notAllowed);
}
