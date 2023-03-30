import { useEffect, useState } from "react";
import { FormattedDate } from "react-intl";
import Skeleton from "react-loading-skeleton";
import { useNavigate } from "react-router-dom";
import "./App.css";

function App() {
  const [last10, setLast10] = useState();

  useEffect(() => {
    const fetchLast10 = async () => {
      const response = await fetch("/api/games/last10");
      const last10 = await response.json();
      setLast10(last10);
    };

    fetchLast10();
  }, []);

  const navigate = useNavigate();

  async function startNewGame() {
    const response = await fetch("/api/games", { method: "POST" });
    const game = await response.json();
    navigate(`/games/${game.id}`);
  }

  return (
    <div className="Index">

      <h1>Добре дошъл в Wordle 2023</h1>
      <button onClick={startNewGame}>Започни нова игра</button>

      <div>
        <table className="Index-Last10">

          <caption>Резултати от последните 10 игри</caption>

          <thead>
            <th>Дата</th>
            <th>Резултат</th>
            <th>Опити</th>
            <th>Дума</th>
          </thead>

          <tbody>
            {last10
              ? last10.map((game) => (
                  <tr key={game.id}>
                    <td>
                      <FormattedDate
                        value={game.startedOn}
                        dateStyle="short"
                        timeStyle="short"
                      />
                    </td>
                    <td>{game.state}</td>
                    <td>{game.guesses.length}</td>
                    <td>{game.word}</td>
                  </tr>
                ))
              : [0, 1, 2, 3, 4, 5, 6, 7, 8, 9].map((pos) => (
                  <tr key={pos}>
                    <td>
                      <Skeleton width="10rem" />
                    </td>
                    <td>
                      <Skeleton />
                    </td>
                    <td>
                      <Skeleton />
                    </td>
                    <td>
                      <Skeleton />
                    </td>
                  </tr>
                ))}
          </tbody>

        </table>

      </div>
    </div>
  );
}

export default App;
