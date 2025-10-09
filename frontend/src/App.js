import React, { useState, useEffect } from 'react';
import { apiClient } from './api/client';
import DragonTable from './components/DragonTable';
import CreateDragonModal from './components/CreateDragonModal';
import CreateRelatedModal from './components/CreateRelatedModal';
import './styles/common.css';

function App() {
  const [dragons, setDragons] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showCreateDragon, setShowCreateDragon] = useState(false);
  const [showCreateRelated, setShowCreateRelated] = useState(false);

  const loadDragons = async () => {
    setLoading(true);
    try {
      const data = await apiClient.getDragons();
      setDragons(data || []);
    } catch (error) {
      console.error('Failed to load dragons:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDragons();
  }, []);

  const addDragon = (newDragon) => {
    setDragons(prev => [...prev, newDragon]);
  };

  const handleRelatedCreated = () => {
    // Просто закрываем модалку
  };

  return (
    <div className="app">
      <header className="app-header">
        <h1 className="app-title">Dragon Manager</h1>
        <div className="controls">
          <button 
            onClick={() => setShowCreateDragon(true)}
            className="btn btn-primary"
          >
            Add Dragon
          </button>
          <button 
            onClick={() => setShowCreateRelated(true)}
            className="btn btn-secondary"
          >
            Create Related Object
          </button>
          <button 
            onClick={loadDragons} 
            disabled={loading}
            className="btn btn-outline"
          >
            Refresh
          </button>
        </div>
      </header>

      <main>
        {loading ? (
          <div className="loading">Loading dragons...</div>
        ) : (
          <DragonTable dragons={dragons} />
        )}
      </main>

      {showCreateDragon && (
        <CreateDragonModal 
          onClose={() => setShowCreateDragon(false)}
          onCreated={addDragon}
        />
      )}

      {showCreateRelated && (
        <CreateRelatedModal 
          onClose={() => setShowCreateRelated(false)}
          onCreated={handleRelatedCreated}
        />
      )}
    </div>
  );
}

export default App;