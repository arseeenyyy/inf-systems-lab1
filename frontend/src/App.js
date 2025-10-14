import React, { useState, useEffect } from 'react';
import { apiClient } from './api/client';
import DragonTable from './components/DragonTable';
import DragonForm from './components/DragonForm';
import DragonDetails from './components/DragonDetails';
import EntityCreator from './components/EntityCreator';
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

  const handleEntityCreated = (entityType, createdEntity) => {
    console.log(`Created ${entityType}:`, createdEntity);
  };

  return (
    <div className="container">
      <div className="header">
      </div>

      <div className="layout">
        <div className="sidebar" style={{ width: '400px' }}>
          <div className="panel">
            <div className="panel-title">[create/update_dragon]</div>
            <DragonForm 
              dragon={editingDragon}
              onSubmit={editingDragon ? handleUpdateDragon : handleCreateDragon}
              onCancel={() => setEditingDragon(null)}
            />
          </div>
          <EntityCreator onEntityCreated={handleEntityCreated} />
          <div className="panel">
            <div className="panel-title">[selected_dragon_data]</div>
            <DragonDetails 
              dragon={selectedDragon}
              onEdit={handleEditDragon}
              onDelete={handleDeleteDragon}
            />
          </div>
        </div>

        <div className="main">
          <div className="panel">
            <div className="panel-title">[dragons_table]</div>
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