// src/App.jsx (добавляем роутинг)
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
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
          <Route path="/dragons" element={<DragonsPage />} />
          <Route path="/teams" element={<TeamsPage />} />
          <Route path="*" element={<DragonsPage />} /> {/* default */}
        </Routes>
      </div>
    </Router>
  );
}

export default App;