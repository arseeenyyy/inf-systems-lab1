import React, { useState, useEffect, useMemo } from 'react';
import { apiClient } from './api/client';
import DragonTable from './components/DragonTable';
import CreateDragonModal from './components/CreateDragonModal';
import CreateRelatedModal from './components/CreateRelatedModal';
import DragonDetail from './components/DragonDetail';
import UpdateDragonModal from './components/UpdateDragonModal';
import './styles/common.css';

function App() {
  const [dragons, setDragons] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showCreateDragon, setShowCreateDragon] = useState(false);
  const [showCreateRelated, setShowCreateRelated] = useState(false);
  const [selectedDragon, setSelectedDragon] = useState(null);
  const [editingDragon, setEditingDragon] = useState(null);
  const [sortConfig, setSortConfig] = useState({ key: null, direction: 'asc' });

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

  const sortedDragons = useMemo(() => {
    if (!sortConfig.key) return dragons;

    return [...dragons].sort((a, b) => {
      let aValue = a[sortConfig.key];
      let bValue = b[sortConfig.key];

      // Специальная обработка для связанных объектов
      switch (sortConfig.key) {
        case 'coordinates':
          aValue = a.coordinates ? a.coordinates.x + a.coordinates.y : 0;
          bValue = b.coordinates ? b.coordinates.x + b.coordinates.y : 0;
          break;
        case 'cave':
          aValue = a.cave ? a.cave.numberOfTreasures : 0;
          bValue = b.cave ? b.cave.numberOfTreasures : 0;
          break;
        case 'killer':
          aValue = a.killer ? a.killer.name : '';
          bValue = b.killer ? b.killer.name : '';
          break;
        case 'head':
          aValue = a.head ? a.head.size : 0;
          bValue = b.head ? b.head.size : 0;
          break;
        default:
          // Для обычных полей используем как есть
          break;
      }

      // Обработка null/undefined значений
      if (aValue == null) aValue = '';
      if (bValue == null) bValue = '';

      // Сравнение значений
      if (aValue < bValue) {
        return sortConfig.direction === 'asc' ? -1 : 1;
      }
      if (aValue > bValue) {
        return sortConfig.direction === 'asc' ? 1 : -1;
      }
      return 0;
    });
  }, [dragons, sortConfig]);

  const handleSort = (key) => {
    let direction = 'asc';
    if (sortConfig.key === key && sortConfig.direction === 'asc') {
      direction = 'desc';
    }
    setSortConfig({ key, direction });
  };

  const addDragon = (newDragon) => {
    setDragons(prev => [...prev, newDragon]);
  };

  const updateDragon = (updatedDragon) => {
    setDragons(prev => prev.map(dragon => 
      dragon.id === updatedDragon.id ? updatedDragon : dragon
    ));
  };

  const deleteDragon = async (id) => {
    try {
      await apiClient.deleteDragon(id);
      setDragons(prev => prev.filter(dragon => dragon.id !== id));
      setSelectedDragon(null);
    } catch (error) {
      console.error('Failed to delete dragon:', error);
    }
  };

  const handleDragonClick = (dragon) => {
    setSelectedDragon(dragon);
  };

  const handleUpdateClick = (dragon) => {
    setSelectedDragon(null);
    setEditingDragon(dragon);
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
          <DragonTable 
            dragons={sortedDragons} 
            onDragonClick={handleDragonClick}
            sortConfig={sortConfig}
            onSort={handleSort}
          />
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

      {selectedDragon && (
        <DragonDetail
          dragon={selectedDragon}
          onUpdate={handleUpdateClick}
          onDelete={deleteDragon}
          onClose={() => setSelectedDragon(null)}
        />
      )}

      {editingDragon && (
        <UpdateDragonModal
          dragon={editingDragon}
          onClose={() => setEditingDragon(null)}
          onUpdated={updateDragon}
        />
      )}
    </div>
  );
}

export default App;