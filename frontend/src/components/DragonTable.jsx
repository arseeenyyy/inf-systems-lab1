import React from 'react';
import '../styles/table.css';

const DragonTable = ({ dragons }) => {
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
    <div className="table-container">
      <table className="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Coordinates</th>
            <th>Creation Date</th>
            <th>Cave</th>
            <th>Killer</th>
            <th>Age</th>
            <th>Weight</th>
            <th>Color</th>
            <th>Character</th>
            <th>Head</th>
          </tr>
        </thead>
        <tbody>
          {dragons.map(dragon => (
            <tr key={dragon.id}>
              <td>{dragon.id}</td>
              <td>{dragon.name}</td>
              <td>{formatCoordinates(dragon.coordinates)}</td>
              <td>{dragon.creationDate}</td>
              <td>{formatCave(dragon.cave)}</td>
              <td>{formatKiller(dragon.killer)}</td>
              <td>{dragon.age}</td>
              <td>{dragon.weight}</td>
              <td>{dragon.color || '-'}</td>
              <td>{dragon.character || '-'}</td>
              <td>{formatHead(dragon.head)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default DragonTable;