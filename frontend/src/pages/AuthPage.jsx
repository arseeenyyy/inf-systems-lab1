import React, { useState } from 'react';

function AuthPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [isAdmin, setIsAdmin] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log('data:', { username, password, isAdmin });
    
  };

  return (
    <div className="layout">
      <div className="main" style={{ maxWidth: '500px', margin: '100px auto' }}>
        <div className="panel">
          
          <form onSubmit={handleSubmit} className="form">
            <div className="form-group">
              <label className="form-label">[username]</label>
              <input
                type="text"
                className="form-input"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="enter username"
              />
            </div>

            <div className="form-group">
              <label className="form-label">[password]</label>
              <input
                type="password"
                className="form-input"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="enter password"
              />
            </div>

            <div className="form-row" style={{ alignItems: 'center', gap: '10px' }}>
              <input
                type="checkbox"
                id="adminCheckbox"
                checked={isAdmin}
                onChange={(e) => setIsAdmin(e.target.checked)}
                style={{ cursor: 'pointer' }}
              />
              <label htmlFor="adminCheckbox" className="form-label" style={{ margin: 0 }}>
                {isAdmin ? '[admin]' : '[user]'}
              </label>
            </div>

            <div className="form-row">
              <button type="submit" className="btn btn-primary" style={{ flex: 1 }}>
                [register]
              </button>
              <button type="submit" className="btn btn-primary" style={{ flex: 1 }}>
                [login]
              </button>
            </div>

            <div className="error-text" style={{ textAlign: 'center', marginTop: '2px' }}>
              {error}
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default AuthPage;