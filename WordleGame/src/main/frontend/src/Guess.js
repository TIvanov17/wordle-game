import classNames from "classnames";

import "./Game.css";

export function Guess({ guess }) {

    function renderElement(value, key, ...classNames) {
        return <span
            key={key}
            className={classNames}
        >
            {value}
        </span>
    }

    return (
        <>
            {
                guess ?
                    [...guess.word].map((char, idx) => (
                        renderElement(char, `guess-${guess.id}-${idx}`, classNames(
                            "Wordle-Match",
                            `Wordle-Match_${guess.matches.charAt(idx)}`
                        ))
                    ))
                    : [1, 2, 3, 4, 5].map((gpIdx, cpIdx) => (
                        renderElement('1', `guess-placeholder-${gpIdx}-${cpIdx}`, "Wordle-Match")
                    ))
            }
        </>
    )
}