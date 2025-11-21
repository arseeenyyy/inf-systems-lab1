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

  const handleRegister = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    if (!username.trim() || !password.trim()) {
      setError('username_and_password_required');
      setLoading(false);
      return;
    }

    try {
      await apiClient.register({ username, password, isAdmin });
      setError('registration_successful');
      setTimeout(() => {
        setError('');
        handleLogin(e);
      }, 1000);
    } catch (err) {
      setError(`registration_failed: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    if (!username.trim() || !password.trim()) {
      setError('username_and_password_required');
      setLoading(false);
      return;
    }

    try {
      const authResponse = await apiClient.login({ username, password });
      
      if (authResponse.jwt) {
        localStorage.setItem('authToken', authResponse.jwt);
        localStorage.setItem('userRole', authResponse.role || (isAdmin ? 'ADMIN' : 'USER'));
        localStorage.setItem('username', username);
        
        setError('login_successful');
        setTimeout(() => {
          navigate('/dragons');
        }, 1000);
      } else {
        setError('invalid_credentials');
      }
    } catch (err) {
      setError(`login_failed: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  const clearError = () => {
    setError('');
  };

  return (
    <div className="layout">
      <div className="main" style={{ maxWidth: '500px', margin: '100px auto' }}>
        <div className="panel">
          
          <form className="form">
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
                onClick={handleRegister}
                disabled={loading}
              >
                {loading ? '[processing...]' : '[register]'}
              </button>
              <button 
                type="button" 
                className="btn btn-primary" 
                style={{ flex: 1 }}
                onClick={handleLogin}
                disabled={loading}
              >
                {loading ? '[processing...]' : '[login]'}
              </button>
            </div>

            <div className="error-text" style={{ 
              minHeight: '20px', 
              textAlign: 'center', 
              marginTop: '15px',
              color: error && error.includes('successful') ? '#00ff00' : '#ff4444'
            }}>
              {error}
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default AuthPage;