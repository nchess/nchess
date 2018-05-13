package com.github.elementbound.nchess.montecarlo;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Node;
import com.github.elementbound.nchess.game.Table;
import com.github.elementbound.nchess.game.operator.MoveOperator;
import com.github.elementbound.nchess.game.operator.Operator;
import com.github.elementbound.nchess.montecarlo.agent.AdvisorAgent;
import com.github.elementbound.nchess.montecarlo.agent.montecarlo.MonteCarloUctAgent;
import com.github.elementbound.nchess.net.Client;
import com.github.elementbound.nchess.util.event.client.GameEndEvent;
import com.github.elementbound.nchess.util.event.client.GameStateUpdateEvent;
import com.github.elementbound.nchess.util.event.client.TurnEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class MonteCarloClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonteCarloClient.class);

    private Set<Operator> operators;
    private AdvisorAgent advisorAgent;

    public static void main(String[] args) {
        new MonteCarloClient().run(args);
    }

    private void run(String[] args) {
        Scanner sc = new Scanner(System.in);
        String host = "localhost";
        int port = 60001;

        System.out.println("Host: ");
        //host = sc.nextLine();

        System.out.println("Port: ");
        //port = sc.nextInt();
        sc.close();

        Client client = new Client(host, port);
        client.getGameStateUpdateEventSource().subscribe(this::onGameStateUpdate);
        client.getTurnEventSource().subscribe(this::onTurn);
        client.getGameEndEventEventSource().subscribe(this::onGameEnd);

        client.run();
    }

    private void onGameStateUpdate(GameStateUpdateEvent event) {
        LOGGER.info("Game state update");

        GameState state = event.getGameState();
        Table table = state.getTable();
        Set<Node> nodes = table.getNodes();

        operators = nodes.stream().flatMap(
                from -> nodes.stream()
                        .filter(to -> !to.equals(from))
                        .map(to -> new Move(from, to)))
                .map(MoveOperator::new)
                .collect(Collectors.toSet());

        LOGGER.info("Gathered {} possible operators on {} nodes", operators.size(), nodes.size());

        advisorAgent = new MonteCarloUctAgent();
        LOGGER.info("Monte Carlo agent initialized");
    }

    /**
     * Responds with a random move whenever it's the client's turn.
     *
     * @param event turn event
     */
    private void onTurn(TurnEvent event) {
        Client client = event.getClient();

        if (event.isMyTurn()) {
            LOGGER.info("Agent's turn");

            MoveOperator operator = (MoveOperator) advisorAgent.advise(client.getGameState());

            LOGGER.info("Agent's advice: {}", operator);

            client.move(operator.getMove());
        }
    }

    /**
     * Terminates when the game ends.
     *
     * @param event game end event
     */
    private void onGameEnd(GameEndEvent event) {
        System.exit(0);
    }
}
