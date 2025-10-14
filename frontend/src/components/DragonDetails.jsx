import React from 'react';

const DragonDetails = ({ dragon, onEdit, onDelete }) => {
  if (!dragon) {
    return <div className="no-selection">NO_SELECTION</div>;
  }

  const formatDragonData = (dragon) => {
    const { id, name, coordinates, creationDate, cave, killer, age, weight, color, character, head } = dragon;
    return JSON.stringify({
      id,
      name,
      coordinates,
      creationDate,
      cave,
      killer,
      age,
      weight,
      color,
      character,
      head
    }, null, 2);
  };

  return (
    <div>
      <div className="json-view">
        {formatDragonData(dragon)}
      </div>
        <div className="actions">
          <button 
            onClick={() => onEdit(dragon)}
            className="btn btn-primary"
          >
            [update]
          </button>
          <button 
            onClick={() => onDelete(dragon.id)}
            className="btn btn-danger"
          >
            [delete]
          </button>
        </div>
    </div>
  );
};

export default DragonDetails;