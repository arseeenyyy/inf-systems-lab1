// src/App.js
import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, Navigate } from 'react-router-dom';
import AuthPage from './pages/AuthPage';
import DragonsPage from './pages/DragonsPage';
import TeamsPage from './pages/TeamsPage';
import './styles/main.css';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [userRole, setUserRole] = useState('');

  useEffect(() => {
    const token = localStorage.getItem('authToken');
    const role = localStorage.getItem('userRole');
    if (token) {
      setIsAuthenticated(true);
      setUserRole(role || 'USER');
    }
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userRole');
    localStorage.removeItem('username');
    setIsAuthenticated(false);
    setUserRole('');
  };

  return (
    <Router>
      <div className="container">
        <div className="header">
          <nav>
            {isAuthenticated ? (
              <>
                <Link to="/dragons" className="btn btn-primary">[Dragons]</Link>
                <Link to="/teams" className="btn btn-primary">[Teams]</Link>
                <span style={{ color: '#00ffff', margin: '0 15px' }}>
                  [{localStorage.getItem('username')}]
                </span>
                <button onClick={handleLogout} className="btn btn-danger">
                  [logout]
                </button>
              </>
            ) : (
              <Link to="/auth" className="btn btn-primary">[Auth]</Link>
            )}
          </nav>
        </div>

        <Routes>
          <Route 
            path="/" 
            element={isAuthenticated ? <Navigate to="/dragons" /> : <AuthPage />} 
          />
          <Route 
            path="/auth" 
            element={isAuthenticated ? <Navigate to="/dragons" /> : <AuthPage />} 
          />
          <Route 
            path="/dragons" 
            element={isAuthenticated ? <DragonsPage /> : <Navigate to="/auth" />} 
          />
          <Route 
            path="/teams" 
            element={isAuthenticated ? <TeamsPage /> : <Navigate to="/auth" />} 
          />
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;