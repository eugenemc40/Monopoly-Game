
package boardgame;



import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;


class Planet {
	private final boolean deterministic;
	private final Dice dice; //two six-sided dice
	private State state;
	private boolean lost = false;
	private Planet() {
		state = new State();
		state.players = new LinkedList<>();
		state.current = null;
		state.state = DecisionState.NONE;
		Input input = new Input();
		
		deterministic = false;
		dice = new ProbDice(); //two dice, six sided



		state.board = new Board(); //create new board
		initialize(input);
	}

	public static void main(String[] args) {
		Planet planet = new Planet();
		planet.run();
	}

	private void run() {
		while (state.players.size() > 1) {
			try {
				state.current = state.players.remove();
				turn();
				if (!lost)
					state.players.add(state.current);
				lost = false;
			} catch (NoSuchElementException e) {
				System.out.println("Early Termination initiated.");
				return;
			} finally {
				printState();
			}
		}

		Player winner = state.players.remove();
		System.out.println("----------------------------------------");
		System.out.print("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
		System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("THE WINNER IS " + winner.name() + "!!!");
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("////////////////////////////////////////");
		System.out.println("----------------------------------------");
	}

	private void initialize(Input input) {
		System.out.println("How many players?");
		int N = input.inputInt();
		
		while (N < 2 || N > 4) {
			System.out.println("Must have between 2 and 4 players. Please try again.");
			N = input.inputInt();
		}
		


		int[] order = new int[N];
		for (int i = 0; i < N; i++) {
			System.out.println("Player " + (i + 1) + " name?");
			state.players.add(new GamePlayer(input.inputString()));
		}

		
		if (deterministic)
			return;

		boolean tie = true;
		boolean[] ties = new boolean[N];
		for (int i = 0; i < N; i++)
			ties[i] = true;
		int first = -1;

		while (tie) {
			for (int i = 0; i < N; i++) {
				if (ties[i])
					order[i] = dice.roll().val;
			}

			int maxRoll = 0;

			for (int i = 0; i < N; i++) {
				if (ties[i]) {
					if (order[i] > maxRoll) {
						maxRoll = order[i];
						first = i;
					}
				}
			}

			tie = false;
			for (int i = 0; i < N; i++)
				ties[i] = false;

			for (int i = 0; i < N; i++) {
				if (order[i] == maxRoll && i != first) {
					ties[i] = true;
					tie = true;
				}
			}
		}

		for (int i = 0; i < first; i++)
			state.players.add(state.players.remove());

		printState();
	}

	private void turn() {
		System.out.println("It's " + state.current.name() + "'s turn");
		int double_count = 0;
		while (true) {
			Dice.Roll roll = dice.roll();
			if (roll.is_double)
				double_count++;

			System.out.print("You rolled a " + roll.val);
			if (roll.is_double)
				System.out.print(" (double)");
			Square[] square = state.board.getBoard();
			System.out.println(" and landed on " + square[(state.current.position() + roll.val) % 11].name());
			state.current.move(roll.val);

			handleSquare(state.current, square[state.current.position()], roll.val);
			
			break;
			
		}
	

		boolean additional = true;
		while (additional && !lost) {
			System.out.println("Would you like to take any additional actions on this turn?");
			System.out.println("Please select choice");
			System.out.println("1) Develop Area");
			System.out.println("2) Nothing");
			state.state = DecisionState.ADDITIONAL;
			int decision = state.current.inputInt(state);

			switch (decision) {
				case 1:
					handleFields(state.current);
					break;
				case 2:
					additional = false;
					break;
				default:
					System.out.println("Please enter a valid decision.");
			}
		}

		System.out.println();
	}

	private void handleSquare(Player player, Square sq, int roll) {
		boolean owned = sq.isOwned();
		boolean ownable = sq.isOwnable();

		if (!owned && ownable)
			unowned(player, sq);
		else if (ownable)
			owned(player, sq, roll);
	}

	private void buyFields(Player player) {
		System.out.println("Expected Values:");
		for (Square sq : player.areas()) {
			Area prop;
			if (sq instanceof Area)
				prop = (Area) sq;
			else
				continue;

			double val = prop.cost();
			/*valueEstimator.expectedValue(sq.position(), prop.rentDiff());*/

			System.out.println(prop.name() + ": " + val);
		}
		do {
			System.out.println("On which area would you like to Develop?");
			Area prop = areaSelect(player);
			
			prop.build(1);
			player.excMoney(-1 * prop.devCost());

			System.out.println("You now have " + prop.numDevs() + " developments on " + prop.name());
			System.out.println("Would you like to buy any more Developments?");
		} while (player.inputBool(state));
	}

	
	private void handleFields(Player player) {
		System.out.println("Would you like to Develop the Area?");
		state.state = DecisionState.BUY_FIELD;
		if (player.inputBool(state))
			buyFields(player);

		
	}
	

		
	private void unowned(Player player, Square square) {
		int cost = square.cost();

		if (player.getMoney() < cost) {
			System.out.println("You cannot afford to purchase " + square.name());
			return;
		}

		boolean additional = false;
		System.out.println("Would you like to purchase " + square.name() + " for " + cost + " (Yes/No)?");
		state.state = DecisionState.PURCHASE;
		if (player.getMoney() < cost) {
			additional = true;
			System.out.println("You do not have enough funds");
		}

		if (player.inputBool(state)) {
			if (!additional)
				player.excMoney(-1 * cost);
			purchase(player, square);
		} else
			purchase(auction(player, square), square);

	}

	private void purchase(Player player, Square square) {
		if (player == null || square == null) return;

		if (!square.isOwnable()) return;

		player.addArea(square);
		square.purchase(player);
	}

	private Player auction(Player player, Square square) {
		System.out.println("Auctioning off " + square.name() + ".");
		int currentBid = -10;
		final int BID_INCREMENT = 10;

		Player winner = null;
		while (true) {
			int minBid = currentBid + BID_INCREMENT;
			System.out.println("Would anyone like to place a bid? Minimum bid: $" + minBid);
			state.state = DecisionState.AUCTION;
			state.val = minBid;
			if (!player.inputBool(state))
				break;

			System.out.println("Please enter player name"); //TODO has to be changed for CPU
			winner = player.inputPlayer(state, player);
			System.out.println(winner.name() + ", please enter your bid.");
			int bid = player.inputInt(state);
			if (bid < minBid) {
				System.out.println("Bid is below minimum bid. Please try again.");
				continue;
			}

			System.out.println("Bid accepted. Current highest bid - " + winner.name() + " for $" + bid);
			currentBid = bid;
		}

		if (winner != null) {
			winner.excMoney(-1 * currentBid);
			System.out.println(winner.name() + " wins auction, for $" + currentBid);
		} else
			System.out.println("No player wins auction.");

		return winner;
	}
	
	private void owned(Player player, Square square, int val) {
		int cost = square.rent(val);
		
		Player owner = square.owner();
		if (player.name().equals(owner.name()))
			return;
		System.out.println("You have landed on " + square.name() + " and owe " + cost + " in rent.");
		if (player.getMoney() < cost) {
			System.out.println("You do not have enough funds.");
		}

		
			player.excMoney(-1 * cost);
			owner.excMoney(cost);
		
	}

	
	private Area areaSelect(Player player) {
		Queue<Square> props = new LinkedList<>();
		for (Square sq : player.areas()) {
			if (!(sq instanceof Area))
				continue;


		}
		return (Area) areaSelect(props, player);
	}


	private Square squareSelect(Player player) {
		return areaSelect(player.areas(), player);
	}

	private Square areaSelect(Iterable<Square> props, Player player) {
		System.out.println("You own the following areas:");

		int counter = 1;
		for (Square sq : props)
			System.out.println(counter++ + ") " + sq.name());

		while (true) {
			int propNum = player.inputInt(state);
			int propState = 1;

			for (Square sq : props) {
				if (propState++ == propNum)
					return sq;
			}

			System.out.println("Please select a valid area.");
		}
	}

	private Queue<Square> availableAssets(Player player) {
		Iterable<Square> props = player.areas();
		Queue<Square> avail = new LinkedList<>();
		for (Square sq : props)
			avail.add(sq);
		return avail;
	}

	private int totalVal(Queue<Square> props) {
		int totalMoney = 0;
		for (Square sq : props) {
			totalMoney += sq.cost();
			if (sq instanceof Area) {
				Area prop = (Area) sq;
				totalMoney += prop.numDevs() * prop.devCost();
			}
		}
		return totalMoney;
	}



	private void lose(Player loser, Player winner) {
		Iterable<Square> squares = loser.areas();
		for (Square sq : squares)
			winner.addArea(sq);
		winner.excMoney(loser.getMoney());

		lost = true;
		System.out.println(loser.name() + " has LOST!");
	}

	private void printState() {
		int counter = 1;
		for (Player player : state.players) {
			System.out.println("--------------------------------------------------");
			System.out.println("Player " + counter++);
			System.out.printf("%-10s%40s%n", "Name", player.name());
			System.out.printf("%-10s%40s%n", "Money", player.getMoney());
			System.out.printf("%-10s%40s%n", "Position", player.position());
			System.out.printf("%-10s", "Areas");
			Iterable<Square> owned = player.areas();

			boolean first = true;
			for (Square s : owned) {
				if (first)
					System.out.printf("%40s%n", s);
				else
					System.out.printf("%50s%n", s);
				first = false;
			}

			if (first)
				System.out.printf("%40s%n", "none");

			System.out.println("--------------------------------------------------");
		}
	}

	public enum DecisionState {
		NONE, BUY_FIELD, DEVELOP,
		 PURCHASE, AUCTION, ADDITIONAL
	}

	public class State {
		public DecisionState state;
		public Queue<Player> players;
		public Board board; //game board
		public Player current;
		public int val = 0;
	}
}
