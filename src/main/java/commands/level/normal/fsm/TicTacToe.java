package commands.level.normal.fsm;

import bot.hierarchy.UserBot;
import commands.hierarchy.fsm.FSMCommand;
import commands.hierarchy.fsm.State;
import commands.hierarchy.fsm.Transition;
import commands.name.Command;
import game.tictactoe.Game;
import game.tictactoe.Type;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class TicTacToe extends FSMCommand {
	private Game game;
	private Message printable;
	private boolean isFirst;
	private User first, second;
	private State secondTurn;
	
	public TicTacToe(UserBot bot, Message message) {
		super(bot, message, Command.TIC_TAC_TOE.names);
		createFSM();
	}

	@Override
	public String helpMessage() {
		return helpBuilder("", 
			"Play tic tac toe! Give any combination of (a,b,c) for row and (0,1,2) for cols [e.g. a0 or b2 etc]",
			"You can also type 'reset' and 'exit' to reset or stop the game respectively");
	}

	@Override
	protected void setup() {
		if (mentioned.users.size() == 0) {
			println("You need to mention another user to play with =v");
			return;
		}
		initialise();
		printState();
	}
	
	@Override
	protected boolean shouldHandleEvent(GuildMessageReceivedEvent event) {
		long id = event.getAuthor().getIdLong();
		String input = event.getMessage().getContentDisplay();
		if (input.startsWith("Invalid coordinates"))	// don't handle printed errors
			return false;
		else if (input.startsWith("Game has"))			// don't handle game state messages
			return false;
		if (first == null && second == null) {
			removeListener();									// prevent memory leaks on failed attempts
			return false;
		}
		boolean isPlayer = first.getIdLong() == id || second.getIdLong() == id;
		return isPlayer;
	}
	
	private void initialise() {
		game = new Game(3);
		isFirst = true;
		if (rand.nextBoolean()) {
			first = message.getAuthor();
			second = mentioned.users.iterator().next();
		} else {
			first = mentioned.users.iterator().next();
			second = message.getAuthor();
		}
		printable = channel.sendMessage(String.format("%s vs %s", first.getName(), second.getName())).complete();
	}
	
	private void createFSM() {
		secondTurn = new State.Builder("2ND").build();
		Transition reset = new Transition.Builder()
				.setPriority(1)
				.setCondition(this::isReset)
				.setAction(this::reset)
				.setNextState(start)
				.build(),
			exit = EXIT_TRANSITION
				.setPriority(2)
				.setAction(event -> printable.delete().queue())
				.build();
		start.addTransition(new Transition.Builder()
				.setCondition(this::isFirst)
				.setAction(event -> handle(event, Type.CROSS))
				.setNextState(secondTurn)
				.build())
			.addTransition(reset)
			.addTransition(exit);
		secondTurn.addTransition(new Transition.Builder()
				.setCondition(this::isSecond)
				.setAction(event -> handle(event, Type.CIRCLE))
				.setNextState(start)
				.build())
			.addTransition(reset)
			.addTransition(exit);
	}
	
	private void toggleFirst() {
		isFirst = !isFirst;
	}
	
	private boolean isReset(GuildMessageReceivedEvent event) {
		return event.getMessage().getContentDisplay().toLowerCase().startsWith("reset");
	}
	
	private void reset(GuildMessageReceivedEvent event) {
		destructibleMessage("Game has been reset").queue();
		game.reset();
		printState();
		event.getMessage().delete().queue();
	}
	
	private boolean isFirst(GuildMessageReceivedEvent event) {
		return isFirst && event.getAuthor().getIdLong() == first.getIdLong() && !isError(event) && !matches(event, "reset") && !matches(event, "exit");
	}
	
	private boolean isSecond(GuildMessageReceivedEvent event) {
		return !isFirst && event.getAuthor().getIdLong() == second.getIdLong() && !isError(event) && !matches(event, "reset") && !matches(event, "exit");
	}
	
	private void handle(GuildMessageReceivedEvent event, Type type) {
		game.set(event.getMessage().getContentDisplay(), type);
		event.getMessage().delete().queue();
		verifyDraw(verifyWinner());
		toggleFirst();
		printState();
	}
	
	private boolean isError(GuildMessageReceivedEvent event) {
		return !event.getMessage().getContentDisplay().matches("[a-zA-Z]+[0-9]+") 
				|| !game.isValid(event.getMessage().getContentDisplay());
	}
	
	private boolean verifyWinner() {
		Type type = game.checkWinner();
		String who = "WTF";
		switch (type) {
			case NONE: return false;
			case CIRCLE: who = second.getName(); break;
			case CROSS: who = first.getName(); break;
		}
		printable.editMessage(String.format("%s won!", who)).queue(); 
		return true;
	}
	
	private void verifyDraw(boolean won) {
		if (game.checkFull() && !won)
			printable.editMessage("It's a draw!").queue();
	}
	
	private void printState() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(String.format("It's %s's turn!", (isFirst ? first : second).getName()));
		for (int x = 0; x < game.map.length; x++)
			for (int y = 0; y < game.map[x].length; y++) 
				builder.addField(game.map[x][y].toString(), "", y != game.size);
		printable.editMessage(builder.build()).queue();
	}
}