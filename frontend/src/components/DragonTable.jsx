import React from 'react';
import '../styles/table.css';

const DragonTable = ({ dragons, onDragonClick, sortConfig, onSort }) => {
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

  const getSortIndicator = (column) => {
    if (sortConfig.key !== column) return '↕';
    return sortConfig.direction === 'asc' ? '↑' : '↓';
  };

  const handleSort = (column) => {
    onSort(column);
  };

  return (
    <div className="table-container">
      <table className="data-table">
        <thead>
          <tr>
            <th onClick={() => handleSort('id')}>
              ID {getSortIndicator('id')}
            </th>
            <th onClick={() => handleSort('name')}>
              Name {getSortIndicator('name')}
            </th>
            <th onClick={() => handleSort('coordinates')}>
              Coordinates {getSortIndicator('coordinates')}
            </th>
            <th onClick={() => handleSort('creationDate')}>
              Creation Date {getSortIndicator('creationDate')}
            </th>
            <th onClick={() => handleSort('cave')}>
              Cave {getSortIndicator('cave')}
            </th>
            <th onClick={() => handleSort('killer')}>
              Killer {getSortIndicator('killer')}
            </th>
            <th onClick={() => handleSort('age')}>
              Age {getSortIndicator('age')}
            </th>
            <th onClick={() => handleSort('weight')}>
              Weight {getSortIndicator('weight')}
            </th>
            <th onClick={() => handleSort('color')}>
              Color {getSortIndicator('color')}
            </th>
            <th onClick={() => handleSort('character')}>
              Character {getSortIndicator('character')}
            </th>
            <th onClick={() => handleSort('head')}>
              Head {getSortIndicator('head')}
            </th>
          </tr>
        </thead>
        <tbody>
          {dragons.map(dragon => (
            <tr 
              key={dragon.id} 
              onClick={() => onDragonClick(dragon)}
            >
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