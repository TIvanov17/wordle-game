import React from "react";
import ReactDOM from "react-dom/client";
import { IntlProvider } from "react-intl";
import "react-loading-skeleton/dist/skeleton.css";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import App from "./App";
import Game from "./Game";
import "./index.css";
import reportWebVitals from "./reportWebVitals";

const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
  },
  {
    path: "games/:gameId",
    element: <Game />,
  },
]);

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <React.StrictMode>
    <IntlProvider locale="bg">
      <RouterProvider router={router} />
    </IntlProvider>
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
