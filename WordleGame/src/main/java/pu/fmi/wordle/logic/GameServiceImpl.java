package pu.fmi.wordle.logic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pu.fmi.wordle.model.*;

@Service
@Transactional
public class GameServiceImpl implements GameService {

  final GameRepository gameRepo;

  final WordRepo wordRepo;

  public GameServiceImpl(GameRepository gameRepo, WordRepo wordRepo) {
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
    updateAlphabetMatches(game);
    gameRepo.save(game);
    System.out.println(game.getWord());
    return game;
  }

  @Override
  public Game getGame(String id) {
    var optionalOfGame = gameRepo.findById(id);
    if (optionalOfGame.isEmpty()) {
        throw new GameNotFoundException(id);
    }
    return optionalOfGame.get();
  }

  @Override
  public Game makeGuess(String id, String word) {
    Game game = this.getGame(id);
    if (game.getState() != Game.GameState.ONGOING) {
      throw new GameOverException();
    }
    this.ensureWordExist(word);

    Guess guess = new Guess();
    guess.setWord(word);
    guess.setMadeAt(LocalDateTime.now());
    guess.setMatches(this.getMatchWordProcess(guess.getWord(), game.getWord()));
     game.getGuesses().add(guess);
    this.checkIsGameEnd(game, word);
    updateAlphabetMatches(game);
    return game;
  }


  @Override
  public Collection<Game> listLast10() {

    // find all ONGOING games started before 24 hours and update the status to LOSS
    this.gameRepo.findByStateAndStartedOnBefore(Game.GameState.ONGOING, LocalDateTime.now().minusHours(24))
            .forEach(g -> g.setState(Game.GameState.LOSS));

    // find the last 10 finished (not ONGOING) games ordered by startedOn descending
    return this.gameRepo.findTop10ByStateNotOrderByStartedOnDesc(Game.GameState.ONGOING);
  }

  private void ensureWordExist(String word){
    if(!wordRepo.exists(word)) {
      throw new UnknownWordException(word);
    }
  }

  private void checkIsGameEnd(Game game, String word){

    boolean areWordsEquals = game.getWord().equals(word);

    if(areWordsEquals && game.getMaxGuesses() >= game.getGuesses().size()){
      game.setState(Game.GameState.WIN);
      return;
    }
    if(!areWordsEquals && game.getMaxGuesses() == game.getGuesses().size()){
      game.setState(Game.GameState.LOSS);
    }

  }

  private String getMatchWordProcess(String guessWord, String gameWord){

    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < guessWord.length(); i++){
      char currentGuessLetter = guessWord.charAt(i);
      char matchSymbol = Guess.NO_MATCH;

      for(int k = 0; k < gameWord.length(); k++){
        char currentGameLetter = gameWord.charAt(k);
        if(currentGuessLetter == currentGameLetter){
          matchSymbol = i == k ? Guess.PLACE_MATCH : Guess.LETTER_MATCH;
          break;
        }
      }
      builder.append(matchSymbol);
    }

    return builder.toString();
  }

  private void updateAlphabetMatches(Game game) {
    StringBuilder result = new StringBuilder();
    game.getAlphabet()
            .chars()
            .map(letter -> getLetterMatch(game.getGuesses(), (char) letter))
            .forEach(letter -> result.append((char) letter));
    game.setAlphabetMatches(result.toString());
  }

  private char getLetterMatch(List<Guess> guesses, char letter) {
    char match = 'U'; // Not used yet
    for (var guess : guesses) {
      var word = guess.getWord();
      var matches = guess.getMatches();
      for (int i = 0; i < word.length(); i++) {
        if (word.charAt(i) == letter && match != Guess.PLACE_MATCH) {
          match = matches.charAt(i);
        }
      }
    }

    return match;
  }
}
