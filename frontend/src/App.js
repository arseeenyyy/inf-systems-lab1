// src/App.js
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import AuthPage from './pages/AuthPage';
import DragonsPage from './pages/DragonsPage';
import TeamsPage from './pages/TeamsPage';
import './styles/main.css';

function App() {
  return (
    <Router>
      <div className="container">
        <div className="header">
          <nav>
            <Link to="/dragons" className="btn btn-primary">[Dragons]</Link>
            <Link to="/teams" className="btn btn-primary">[Teams]</Link>
          </nav>
        </div>

        <Routes>
          <Route path="/" element={<AuthPage />} />
          <Route path="/auth" element={<AuthPage />} />
          <Route path="/dragons" element={<DragonsPage />} />
          <Route path="/teams" element={<TeamsPage />} />
          <Route path="*" element={<AuthPage />} /> {/* default to auth */}
        </Routes>
      </div>
    </Router>
  );
}

export default App;