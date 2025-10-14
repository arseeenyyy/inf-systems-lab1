import React, { useState, useEffect } from 'react';
import { apiClient } from './api/client';
import DragonTable from './components/DragonTable';
import DragonForm from './components/DragonForm';
import DragonDetails from './components/DragonDetails';
import './styles/main.css';

function App() {
  const [dragons, setDragons] = useState([]);
  const [selectedDragon, setSelectedDragon] = useState(null);
  const [editingDragon, setEditingDragon] = useState(null);
  const [sortConfig, setSortConfig] = useState({ key: null, direction: 'asc' });

  useEffect(() => {
    loadDragons();
  }, []);

  const loadDragons = async () => {
    try {
      const data = await apiClient.getDragons();
      setDragons(data);
    } catch (error) {
      console.error('Failed to load dragons:', error);
    }
  };

  const handleCreateDragon = async (dragonData) => {
    try {
      await apiClient.createDragon(dragonData);
      await loadDragons();
    } catch (error) {
      console.error('Failed to create dragon:', error);
    }
  };

  const handleUpdateDragon = async (dragonData) => {
    try {
      await apiClient.updateDragon(editingDragon.id, dragonData);
      setEditingDragon(null);
      await loadDragons();
    } catch (error) {
      console.error('Failed to update dragon:', error);
    }
  };

  const handleDeleteDragon = async (id) => {
    try {
      await apiClient.deleteDragon(id);
      if (selectedDragon?.id === id) setSelectedDragon(null);
      if (editingDragon?.id === id) setEditingDragon(null);
      await loadDragons();
    } catch (error) {
      console.error('Failed to delete dragon:', error);
    }
  };

  const handleEditDragon = (dragon) => {
    setEditingDragon(dragon);
  };

  return (
    <div className="container">
      <div className="header">
        <div className="title"></div>
        <div className="subtitle"></div>
      </div>

      <div className="layout">
        <div className="sidebar">
          <div className="panel">
            <div className="panel-title">[CREATE/UPDATE_DRAGON]</div>
            <DragonForm 
              dragon={editingDragon}
              onSubmit={editingDragon ? handleUpdateDragon : handleCreateDragon}
              onCancel={() => setEditingDragon(null)}
            />
          </div>

          <div className="panel">
            <div className="panel-title">[SELECTED_DRAGON_DATA]</div>
            <DragonDetails 
              dragon={selectedDragon}
              onEdit={handleEditDragon}
              onDelete={handleDeleteDragon}
            />
          </div>
        </div>

        <div className="main">
          <div className="panel">
            <div className="panel-title">[DRAGONS_TABLE]</div>
            <DragonTable 
              dragons={dragons}
              selectedDragon={selectedDragon}
              sortConfig={sortConfig}
              onSelectDragon={setSelectedDragon}
              onSort={setSortConfig}
            />
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;