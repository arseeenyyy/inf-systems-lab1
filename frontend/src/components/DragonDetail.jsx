import React from 'react';
import '../styles/DragonDetail.css'


const DragonDetail = ({ dragon, onUpdate, onDelete, onClose }) => (
  <div className="detail-modal">
    <h3>Dragon Details</h3>
    <div className="detail-grid">
      <div><strong>ID:</strong> {dragon.id}</div>
      <div><strong>Name:</strong> {dragon.name}</div>
      <div><strong>Age:</strong> {dragon.age}</div>
      <div><strong>Weight:</strong> {dragon.weight}</div>
      <div><strong>Color:</strong> {dragon.color}</div>
      <div><strong>Character:</strong> {dragon.character}</div>
      <div><strong>Coordinates ID:</strong> {dragon.coordinates?.id}</div>
      <div><strong>Cave ID:</strong> {dragon.cave?.id}</div>
      <div><strong>Killer ID:</strong> {dragon.killer?.id}</div>
      <div><strong>Head ID:</strong> {dragon.head?.id}</div>
    </div>
    <div className="detail-actions">
      <button onClick={() => onUpdate(dragon)}>Update</button>
      <button onClick={() => onDelete(dragon.id)}>Delete</button>
      <button onClick={onClose}>Close</button>
    </div>
  </div>
);

export default DragonDetail;