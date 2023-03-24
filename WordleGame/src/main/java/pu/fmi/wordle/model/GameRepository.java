package pu.fmi.wordle.model;

import java.time.LocalDateTime;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import pu.fmi.wordle.model.Game.GameState;

public interface GameRepository extends JpaRepository<Game, String> {

  Collection<Game> findTop10ByStateNotOrderByStartedOnDesc(GameState state);

  Collection<Game> findByStateAndStartedOnBefore(GameState state, LocalDateTime startedOn);

}
