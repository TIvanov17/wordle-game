package pu.fmi.wordle.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pu.fmi.wordle.logic.GameService;
import pu.fmi.wordle.model.Game;

@Controller
public class HomeController {

  private final GameService gameService;

  public HomeController(GameService gameService){
    this.gameService = gameService;
  }

  @GetMapping({"/", "/games/{gameId}"})
  public String welcome(Model model) {
    return "/index.html";
  }

}
