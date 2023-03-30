package pu.fmi.wordle.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import pu.fmi.wordle.logic.GameNotFoundException;

@ControllerAdvice
public class RestErrorHandler {

	@ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
	public CustomError handeError(GameNotFoundException e) {
		return new CustomError("game-over", e.getGameId(), e.getMessage());
	}


}
