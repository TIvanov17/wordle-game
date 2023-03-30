import classNames from "classnames";
import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { Guess } from "./Guess";
import "./Game.css";

function Game() {

  let { gameId } = useParams();
  const [game, setGame] = useState({
    word: "",
    alphabet: "",
    state: "ONGOING",
    maxGuesses: 0,
    guesses: [],
  });

  useEffect(() => {
    const fetchGame = async () => {
      const response = await fetch(`/api/games/${gameId}`);
      const game = await response.json();
      setGame(game);
    };

    fetchGame();
  }, [gameId]);

  const [word, setWord] = useState("");
  const [error, setError] = useState();

  function handleChange(e) {
    setWord(e.target.value);
  }

  async function makeGuess(e) {
    e.preventDefault();

    const form = e.target;
    const formData = new FormData(form);
    const response = await fetch(`/api/games/${gameId}/guesses`, {
      method: form.method,
      body: formData,
    });

    const json = await response.json();
    if (response.status === 200) {
      setGame(json);
      setError(null);
    } else {
      setError(json);
    }

    setWord("");
  }

  const guessesLeft = game.maxGuesses - game.guesses.length;

  const placeholders = [...Array(guessesLeft).keys()].map((gpIdx) => (
    <div key={`guess-placeholder-${gpIdx}`}>

      {/* TODO: Extract the code between start and end to component ant replace it with this: <Guess /> */}

      {/* --- start --- */}
      <Guess />
      {/* --- end --- */}
    </div>
  ));

  return (
    <div className="Wordle">
      {game.state === "WIN" && (
        <h1 className="Wordle-Message">
          Браво! Позна думата с {game.guesses.length} опита 🎉
          <Link to={"/"}>Играй отновo</Link>
        </h1>
      )}

      {game.state === "LOSS" && (
        <h1 className="Wordle-Message">
          Жалко! Не успя да познаеш думата 😢
          <Link to={"/"}>Опитай пак</Link>
        </h1>
      )}

      <form onSubmit={makeGuess} method="POST">

        <div>
          {game.guesses.map((guess) => (

            <div key={`guess-${guess.id}`}>
              {/* TODO: Extract the code between start and end to component ant replace it with this: <Guess guess={guess} /> */}
              {/* --- start --- */}
              <Guess guess={guess} />
              {/* --- end --- */}
            </div>

          ))}

          {placeholders}
        </div>

        {game.state === "ONGOING" && (
          <>
            <div>
              <input
                name="guess"
                maxlength="5"
                autofocus
                value={word}
                onChange={handleChange}
              />{" "}
              <button>Check</button>
            </div>
            {error?.code === "unknown-word" && (
              <p className="Wordle-Error">
                В речника няма дума "{error.value}"
              </p>
            )}
          </>
        )}
      </form>

      <div>
        <h3>Статистика за използваните букви:</h3>
        <div className="Wordle-LettersStat">
          {[...game.alphabet].map((char, idx) => (
            <span
              className={classNames(
                "Wordle-Match",
                `Wordle-Match_${game.alphabetMatches.charAt(idx)}`
              )}
            >
              {char}
            </span>
          ))}
        </div>
      </div>

    </div>
  );
}

export default Game;
