package pu.fmi.wordle.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pu.fmi.wordle.logic.GameService;
import pu.fmi.wordle.logic.UnknownWordException;
import pu.fmi.wordle.model.Game;

import java.util.Collection;

@RestController
@RequestMapping("/api/games")
public class GameApi {

  private final GameService gameService;

  public GameApi(GameService gameService) {
    this.gameService = gameService;
  }

  @GetMapping("/last10")
  public Collection<Game> listLast10() {
    return gameService.listLast10();
  }
  @PostMapping
  public Game startNewGame() {
    return this.gameService.startNewGame();
  }

  @GetMapping("/{gameId}")
  public Game showGame(@PathVariable String gameId) {
    return this.gameService.getGame(gameId);
  }

  @PostMapping(path = "/{gameId}/guesses")
  public ResponseEntity<?> makeGuess(@PathVariable String gameId, @RequestParam String guess) {
    try {
      var game = gameService.makeGuess(gameId, guess);
      return ResponseEntity.ok(game);
    } catch (UnknownWordException e) {
      return ResponseEntity.badRequest().body(new CustomError("unknown-word", guess, e.getMessage()));
    }
  }
}
