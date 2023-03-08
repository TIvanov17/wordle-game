package pu.fmi.wordle.logic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import org.springframework.stereotype.Component;
import pu.fmi.wordle.model.Game;
import pu.fmi.wordle.model.GameRepo;
import pu.fmi.wordle.model.Guess;
import pu.fmi.wordle.model.WordRepo;

@Component
public class GameServiceImpl implements GameService {

  final GameRepo gameRepo;

  final WordRepo wordRepo;

  public GameServiceImpl(GameRepo gameRepo, WordRepo wordRepo) {
    this.gameRepo = gameRepo;
    this.wordRepo = wordRepo;
  }

  @Override
  public Game startNewGame() {
    var game = new Game();
    game.setId(UUID.randomUUID().toString());
    game.setStartedOn(LocalDateTime.now());
    game.setWord(wordRepo.getRandom());
    game.setGuesses(new ArrayList<>(game.getMaxGuesses()));
    return game;
  }

  @Override
  public Game getGame(String id) {
    var game = gameRepo.get(id);
    if (game == null) throw new GameNotFoundException(id);
    return game;
  }

  @Override
  public Game makeGuess(String id, String word) {

    this.ensureWordExist(word);

    Game game = this.ensureGameExist(id);

    Guess guess = new Guess();
    guess.setWord(word);
    guess.setMadeAt(LocalDateTime.now());

    String guessWord = guess.getWord();
    String gameWord = game.getWord();

    String matchesWord = this.checkGuessAndGameWordsProcess(guessWord, gameWord);
    guess.setMatches(matchesWord);
    game.getGuesses().add(guess);
    return game;
  }

  private void ensureWordExist(String word){
    if(!wordRepo.exists(word)) {
      throw new UnknownWordException(word);
    }
  }

  private Game ensureGameExist(String id){
    Game game = gameRepo.get(id);
    if(game == null){
      throw new GameNotFoundException(id);
    }
    return game;
  }

  private String checkGuessAndGameWordsProcess(String guessWord, String gameWord){

    StringBuilder builder = new StringBuilder();

    for(int i = 0; i < guessWord.length(); i++){

      char currentGuessLetter = guessWord.charAt(i);

      for(int k = 0; k < gameWord.length(); k++){

        char currentGameLetter = gameWord.charAt(k);

        boolean areLettersEqual = currentGuessLetter == currentGameLetter;
        boolean areLettersEqualAndPositionEqual = areLettersEqual && i == k;
        boolean areLettersEqualAndPositionDifferent = areLettersEqual && i != k;

        if(areLettersEqualAndPositionEqual){
          builder.append(Guess.PLACE_MATCH);
          break;
        }
        if(areLettersEqualAndPositionDifferent){
          builder.append(Guess.LETTER_MATCH);
          break;
        }
        if(k == gameWord.length() - 1){
          builder.append(Guess.NO_MATCH);
        }
      }
    }

    return builder.toString();
  }
}
