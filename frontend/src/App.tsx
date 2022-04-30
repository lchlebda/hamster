import * as React from 'react';
import { Routes, Route } from 'react-router-dom';
import './App.css';
import Home from './Home';
import Login from './Login';
import AuthProvider from './authorization/AuthProvider';
import Layout from './Layout';
import RequireAuth from './authorization/RequireAuth';

const App = () => {
    return (
        <AuthProvider>
            <div className="App">
                <Routes>
                    <Route element={<Layout />}>
                        <Route
                            path="/"
                            element={
                                <RequireAuth>
                                    <Home />
                                </RequireAuth>
                            }
                        />
                        <Route path="login" element={<Login />} />
                    </Route>
                </Routes>
            </div>
        </AuthProvider>
    );
}
export default App;