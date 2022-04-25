import * as React from "react";
import { Routes, Route } from "react-router-dom";
import "./App.css";
import Home from "./Home";
import Login from "./Login";

const App = () => {
    return (
        <div className="App">
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="login" element={<Login />} />
            </Routes>
        </div>
    );
}
export default App;