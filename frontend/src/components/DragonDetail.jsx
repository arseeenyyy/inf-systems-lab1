import React from 'react';
import '../styles/modal.css';
import '../styles/forms.css';

const DragonDetail = ({ dragon, onUpdate, onDelete, onClose }) => {
  const formatCoordinates = (coordinates) => {
    if (!coordinates) return '-';
    return `(${coordinates.x};${coordinates.y})`;
  };

  const formatCave = (cave) => {
    if (!cave) return '-';
    return cave.numberOfTreasures;
  };

  const formatKiller = (killer) => {
    if (!killer) return '-';
    return killer.name;
  };

  const formatHead = (head) => {
    if (!head) return '-';
    return `(${head.size};${head.eyesCount || 'null'})`;
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="modal-header">
          <h2 className="modal-title">Dragon Details</h2>
        </div>

        <div className="form">
          <div className="form-group">
            <label className="form-label">ID</label>
            <div>{dragon.id}</div>
          </div>

          <div className="form-group">
            <label className="form-label">Name</label>
            <div>{dragon.name}</div>
          </div>

          <div className="form-group">
            <label className="form-label">Coordinates</label>
            <div>{formatCoordinates(dragon.coordinates)}</div>
          </div>

          <div className="form-group">
            <label className="form-label">Creation Date</label>
            <div>{dragon.creationDate}</div>
          </div>

          <div className="form-group">
            <label className="form-label">Cave</label>
            <div>{formatCave(dragon.cave)}</div>
          </div>

          <div className="form-group">
            <label className="form-label">Killer</label>
            <div>{formatKiller(dragon.killer)}</div>
          </div>

          <div className="form-group">
            <label className="form-label">Age</label>
            <div>{dragon.age}</div>
          </div>

          <div className="form-group">
            <label className="form-label">Weight</label>
            <div>{dragon.weight}</div>
          </div>

          <div className="form-group">
            <label className="form-label">Color</label>
            <div>{dragon.color || '-'}</div>
          </div>

          <div className="form-group">
            <label className="form-label">Character</label>
            <div>{dragon.character || '-'}</div>
          </div>

          <div className="form-group">
            <label className="form-label">Head</label>
            <div>{formatHead(dragon.head)}</div>
          </div>

          <div className="form-actions">
            <button onClick={onClose}>Close</button>
            <button onClick={() => onUpdate(dragon)}>Update</button>
            <button onClick={() => onDelete(dragon.id)}>Delete</button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DragonDetail;