import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiClient } from '../api/client';

function AuthPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [isAdmin, setIsAdmin] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleAuth = async (isRegister) => {
    setLoading(true);
    setError('');

    if (!username.trim() || !password.trim()) {
      setError('Username and password are required');
      setLoading(false);
      return;
    }

    try {
      if (isRegister) {
        await apiClient.register({ 
          username: username.trim(), 
          password: password.trim(), 
          role: isAdmin ? "ADMIN" : "USER" 
        });
      } else {
        await apiClient.login({ 
          username: username.trim(), 
          password: password.trim() 
        });
      }
      navigate('/dragons');
    } catch (err) {
      if (err.message.includes('Username already exists') || 
          err.message.includes('Invalid username or password') ||
          err.message.includes('Authentication failed') ||
          err.message.includes('400') || 
          err.message.includes('401')) {
        setError('Invalid credentials');
      } else {
        setError(`Authentication failed: ${err.message}`);
      }
    } finally {
      setLoading(false);
    }
  };

  const clearError = () => {
    setError('');
  };

  const handleFormSubmit = (e) => {
    e.preventDefault();
    e.stopPropagation();
    return false;
  };

  return (
    <div className="layout">
      <div className="main" style={{ maxWidth: '500px', margin: '100px auto' }}>
        <div className="panel">
          
          <form className="form" onSubmit={handleFormSubmit} noValidate>
            <div className="form-group">
              <label className="form-label">[username]</label>
              <input
                type="text"
                className="form-input"
                value={username}
                onChange={(e) => {
                  setUsername(e.target.value);
                  clearError();
                }}
                placeholder="enter username"
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label className="form-label">[password]</label>
              <input
                type="password"
                className="form-input"
                value={password}
                onChange={(e) => {
                  setPassword(e.target.value);
                  clearError();
                }}
                placeholder="enter password"
                disabled={loading}
              />
            </div>

            <div className="form-row" style={{ alignItems: 'center', gap: '10px' }}>
              <input
                type="checkbox"
                id="adminCheckbox"
                checked={isAdmin}
                onChange={(e) => setIsAdmin(e.target.checked)}
                style={{ cursor: 'pointer' }}
                disabled={loading}
              />
              <label htmlFor="adminCheckbox" className="form-label" style={{ margin: 0 }}>
                {isAdmin ? '[admin]' : '[user]'}
              </label>
            </div>

            <div className="form-row">
              <button 
                type="button" 
                className="btn btn-primary" 
                style={{ flex: 1 }}
                onClick={() => handleAuth(true)}
                disabled={loading}
              >
                {'[register]'}
              </button>
              <button 
                type="button" 
                className="btn btn-primary" 
                style={{ flex: 1 }}
                onClick={() => handleAuth(false)}
                disabled={loading}
              >
                {'[login]'}
              </button>
            </div>

            {error && (
              <div className="error-text" style={{ 
                textAlign: 'center', 
                marginTop: '15px'
              }}>
                {error}
              </div>
            )}
          </form>
        </div>
      </div>
    </div>
  );
}

export default AuthPage;