package pu.fmi.wordle.logic;

import pu.fmi.wordle.model.Game;

import java.util.Collection;

public interface GameService {

  Game startNewGame();

  Game getGame(String id);

  Game makeGuess(String id, String word);

  Collection<Game> listLast10();
}
