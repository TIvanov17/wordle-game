package pu.fmi.wordle.logic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    updateAlphabetMatches(game);
    gameRepo.save(game);
    System.out.println(game.getWord());
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
    Game game = this.getGame(id);
    this.ensureWordExist(word);

    Guess guess = new Guess();
    guess.setWord(word);
    guess.setMadeAt(LocalDateTime.now());
    guess.setMatches(this.getMatchWordProcess(guess.getWord(), game.getWord()));
    game.setGuessesMade(game.getGuessesMade() + 1);
    game.getGuesses().add(guess);
    this.checkIsGameEnd(game, word);
    updateAlphabetMatches(game);
    return game;
  }

  private void ensureWordExist(String word){
    if(!wordRepo.exists(word)) {
      throw new UnknownWordException(word);
    }
  }

  private void checkIsGameEnd(Game game, String word){

    boolean areWordsEquals = game.getWord().equals(word);

    if(areWordsEquals && game.getMaxGuesses() >= game.getGuessesMade()){
      game.setGameWin(true);
      return;
    }
    if(!areWordsEquals && game.getMaxGuesses() == game.getGuessesMade()){
      game.setGameLost(true);
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
