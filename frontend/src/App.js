import React, { useState, useEffect } from 'react';
import { apiClient } from './api/client';
import { useApi } from './api/useApi';
import DragonTable from './components/DragonTable';
import DragonDetail from './components/DragonDetail';
import CreateDragonForm from './components/CreateDragonForm';
import CreateForm from './components/CreateForm';
import Modal from './components/Modal';
import './App.css';

function App() {
  const [dragons, setDragons] = useState([]);
  const [selectedDragon, setSelectedDragon] = useState(null);
  const [filter, setFilter] = useState('');
  const [showCreateDragon, setShowCreateDragon] = useState(false);
  const [showCreateRelated, setShowCreateRelated] = useState(null);
  const { loading, error, execute } = useApi();

  const loadDragons = async () => {
    const result = await execute(() => apiClient.getDragons());
    if (result) setDragons(result);
  };

  useEffect(() => { loadDragons(); }, []);

  const handleDeleteDragon = async (id) => {
    if (window.confirm('Delete this dragon?')) {
      const success = await execute(() => apiClient.deleteDragon(id));
      if (success !== null) loadDragons();
    }
  };

  return (
    <div className="app">
      <header>
        <h1>Dragon Manager</h1>
        <div className="controls">
          <input
            type="text"
            placeholder="Filter by name or color..."
            value={filter}
            onChange={(e) => setFilter(e.target.value)}
          />
          <button onClick={() => setShowCreateDragon(true)}>Add Dragon</button>
          <select onChange={(e) => setShowCreateRelated(e.target.value)} value="">
            <option value="">Create Related...</option>
            <option value="Coordinates">Coordinates</option>
            <option value="Cave">Cave</option>
            <option value="Head">Head</option>
            <option value="Person">Person</option>
          </select>
        </div>
      </header>

      <main>
        {loading && <div className="loading">Loading...</div>}
        {error && <div className="error">Error: {error}</div>}
        
        <DragonTable
          dragons={dragons}
          onSelect={setSelectedDragon}
          filter={filter}
        />
      </main>

      {selectedDragon && (
        <Modal onClose={() => setSelectedDragon(null)}>
          <DragonDetail
            dragon={selectedDragon}
            onUpdate={() => {/* TODO */}}
            onDelete={handleDeleteDragon}
            onClose={() => setSelectedDragon(null)}
          />
        </Modal>
      )}

      {showCreateDragon && (
        <Modal onClose={() => setShowCreateDragon(false)}>
          <CreateDragonForm onCreated={() => { setShowCreateDragon(false); loadDragons(); }} />
        </Modal>
      )}

      {showCreateRelated && (
        <Modal onClose={() => setShowCreateRelated(null)}>
          <CreateForm
            type={showCreateRelated}
            onCreated={() => { setShowCreateRelated(null); loadDragons(); }}
          />
        </Modal>
      )}
    </div>
  );
}

export default App;