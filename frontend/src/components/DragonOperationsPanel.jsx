import React from 'react';

const DragonOperationsPanel = ({ 
  dragonOperations, 
  availableColors, 
  onOperationChange, 
  onDeleteAllByColor, 
  onDeleteOneByColor, 
  onSearchByName 
}) => {
  const formatDragonData = (dragon) => {
    return {
      id: dragon.id,
      name: dragon.name,
      age: dragon.age,
      weight: dragon.weight,
      color: dragon.color,
      character: dragon.character,
      coordinates: dragon.coordinates ? `(${dragon.coordinates.x};${dragon.coordinates.y})` : 'null',
      cave: dragon.cave ? `treasures: ${dragon.cave.numberOfTreasures || 0}` : 'null',
      killer: dragon.killer ? dragon.killer.name : 'null',
      head: dragon.head ? `size: ${dragon.head.size}, eyes: ${dragon.head.eyesCount || '?'}` : 'null'
    };
  };

  return (
    <div className="panel">
      <div className="panel-title">[dragon_operations]</div>
      <div className="form">
        <div className="form-group">
          <label className="form-label">color</label>
          <select 
            name="color"
            value={dragonOperations.color}
            onChange={onOperationChange}
            className="form-select"
          >
            <option value="">Select color</option>
            {availableColors.map(color => (
              <option key={color} value={color}>
                {color}
              </option>
            ))}
          </select>
          <div className="actions">
            <button 
              onClick={onDeleteAllByColor} 
              className="btn btn-danger"
              disabled={!dragonOperations.color}
            >
              [delete all by color]
            </button>
            <button 
              onClick={onDeleteOneByColor} 
              className="btn btn-danger"
              disabled={!dragonOperations.color}
            >
              [delete one by color]
            </button>
          </div>
        </div>
        <div className="form-group">
          <label className="form-label">name substring</label>
          <input
            type="text"
            name="substring"
            value={dragonOperations.substring}
            onChange={onOperationChange}
            className="form-input"
            placeholder="Enter name substring"
          />
          <div className="actions">
            <button 
              onClick={onSearchByName} 
              className="btn btn-primary"
              disabled={!dragonOperations.substring}
            >
              [search by name]
            </button>
          </div>
        </div>
        {dragonOperations.searchResults.length > 0 && (
          <div className="search-results">
            <div className="results-count">
              Found: {dragonOperations.searchResults.length} dragons
            </div>
            <div className="results-list">
              {dragonOperations.searchResults.map(dragon => (
                <div key={dragon.id} className="result-item">
                  <div className="dragon-info">
                    <strong>ID:</strong> {dragon.id} | <strong>Name:</strong> {dragon.name}
                  </div>
                  <div className="dragon-details">
                    <strong>Age:</strong> {dragon.age} | <strong>Weight:</strong> {dragon.weight}
                  </div>
                  <div className="dragon-details">
                    <strong>Color:</strong> {dragon.color} | <strong>Character:</strong> {dragon.character || 'null'}
                  </div>
                  <div className="dragon-details">
                    <strong>Coordinates:</strong> {dragon.coordinates ? `(${dragon.coordinates.x};${dragon.coordinates.y})` : 'null'}
                  </div>
                  <div className="dragon-details">
                    <strong>Cave:</strong> {dragon.cave ? `treasures: ${dragon.cave.numberOfTreasures || 0}` : 'null'}
                  </div>
                  <div className="dragon-details">
                    <strong>Killer:</strong> {dragon.killer ? dragon.killer.name : 'null'}
                  </div>
                  <div className="dragon-details">
                    <strong>Head:</strong> {dragon.head ? `size: ${dragon.head.size}, eyes: ${dragon.head.eyesCount || '?'}` : 'null'}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default DragonOperationsPanel;