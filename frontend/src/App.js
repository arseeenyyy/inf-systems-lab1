import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link, useNavigate, Navigate } from 'react-router-dom';
import DragonsPage from './pages/DragonsPage';
import TeamsPage from './pages/TeamsPage';
import AuthPage from './pages/AuthPage';
import ImportPage from './pages/ImportPage';
import { apiClient } from './api/client';
import './styles/main.css';

function Header() {
  const navigate = useNavigate();
  const isAuthenticated = apiClient.isAuthenticated();

  const handleLogout = () => {
    apiClient.logout();
    navigate('/auth');
  };

  return (
    <div className="header">
      <nav>
        {isAuthenticated ? (
        <>
          <Link to="/dragons" className="btn btn-primary">[dragons]</Link>
          <Link to="/teams" className="btn btn-primary">[teams]</Link>
          <Link to="/import" className="btn btn-primary">[import]</Link>
          <button onClick={handleLogout} className="btn btn-danger">[logout]</button>
        </>
        ) : (
          <Link to="/auth" className="btn btn-primary">[auth]</Link>
        )}
      </nav>
    </div>
  );
}

const ProtectedRoute = ({ children }) => {
  if (!apiClient.isAuthenticated()) {
    return <Navigate to="/auth" replace />;
  }

  return children;
};

const PublicRoute = ({ children }) => {
  if (apiClient.isAuthenticated()) {
    return <Navigate to="/dragons" replace />;
  }

  return children;
};


function App() {
  return (
    <Router>
      <div className="container">
        <Header />
        
        <Routes>
          <Route path="/auth" element={
            <PublicRoute>
              <AuthPage />
            </PublicRoute>
          } />
          
          <Route path="/dragons" element={
            <ProtectedRoute>
              <DragonsPage />
            </ProtectedRoute>
          } />
          
          <Route path="/teams" element={
            <ProtectedRoute>
              <TeamsPage />
            </ProtectedRoute>
          } />
          <Route path="/import" element={
            <ProtectedRoute>
              <ImportPage />
            </ProtectedRoute>
          } />
                  
          <Route path="/" element={
            apiClient.isAuthenticated() ? 
              <Navigate to="/dragons" replace /> : 
              <Navigate to="/auth" replace />
          } />
          
          <Route path="*" element={
            apiClient.isAuthenticated() ? 
              <Navigate to="/dragons" replace /> : 
              <Navigate to="/auth" replace />
          } />
        </Routes>
      </div>
    </Router>
  );
}

export default App;