package commands.level.normal;

import bot.hierarchy.UserBot;
import commands.hierarchy.FSMCommand;
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
	private User first, second;
	
	public TicTacToe(UserBot bot, Message message) {
		super(bot, message, Command.TIC_TAC_TOE.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("", "Play tic tac toe!");
	}

	@Override
	protected void setup() {
		if (mentioned.users.size() == 0) {
			println("You need to mention another user to play with =v");
			return;
		}
		initialise();
		createFSM();
		printState(null);
	}
	
	@Override
	protected boolean shouldHandleEvent(GuildMessageReceivedEvent event) {
		long id = event.getAuthor().getIdLong();
		return first.getIdLong() == id || second.getIdLong() == id;
	}
	
	private void initialise() {
		game = new Game(3);
		if (rand.nextBoolean()) {
			first = message.getAuthor();
			second = mentioned.users.iterator().next();
		} else {
			first = mentioned.users.iterator().next();
			second = message.getAuthor();
		}
		printable = channel.sendMessage("Loading..").complete();
	}
	
	private void createFSM() {
		State secondTurn = new State.Builder()
				.addTransition(new Transition.Builder()
						.setCondition(this::isSecond)
						.setAction(this::handleSecond)
						.setNextState(start)
						.build())
				.build();
		start.addTransition(new Transition.Builder()
				.setCondition(this::isFirst)
				.setAction(this::handleFirst)
				.setNextState(secondTurn)
				.build());
	}
	
	private boolean isFirst(GuildMessageReceivedEvent event) {
		return game.isValid(event.getMessage().getContentDisplay()) 
				&& event.getAuthor().getIdLong() == first.getIdLong();
	}
	
	private boolean isSecond(GuildMessageReceivedEvent event) {
		return game.isValid(event.getMessage().getContentDisplay()) 
				&& event.getAuthor().getIdLong() == second.getIdLong();
	}
	
	private void handleFirst(GuildMessageReceivedEvent event) {
		game.set(event.getMessage().getContentDisplay(), Type.CROSS);
		printState(null);
		event.getMessage().delete().queue();
	}
	
	private void handleSecond(GuildMessageReceivedEvent event) {
		game.set(event.getMessage().getContentDisplay(), Type.CIRCLE);
		printState(null);
		event.getMessage().delete().queue();
	}
	
	private void printState(GuildMessageReceivedEvent event) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(String.format("%s vs %s", first.getName(), second.getName()));
		for (int x = 0; x < game.map.length; x++)
			for (int y = 0; y < game.map[x].length; y++) 
				builder.addField(game.map[x][y].toString(), "", y != game.size);
		printable.editMessage(builder.build()).queue();
	}

	public static void main(String[] args) {
		Game game = new Game(3);
		game.forEach(c -> System.out.print(c+" "), v -> System.out.println());
		System.out.println(game.checkWinner());
	}
}