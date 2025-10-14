import React, { useMemo } from 'react';

const DragonTable = ({ dragons, selectedDragon, sortConfig, onSelectDragon, onSort }) => {
  const sortedDragons = useMemo(() => {
    if (!sortConfig.key) return dragons;
    
    return [...dragons].sort((a, b) => {
      const aVal = a[sortConfig.key];
      const bVal = b[sortConfig.key];
      if (aVal < bVal) return sortConfig.direction === 'asc' ? -1 : 1;
      if (aVal > bVal) return sortConfig.direction === 'asc' ? 1 : -1;
      return 0;
    });
  }, [dragons, sortConfig]);

  const handleSort = (key) => {
    const direction = sortConfig.key === key && sortConfig.direction === 'asc' ? 'desc' : 'asc';
    onSort({ key, direction });
  };

  const getSortIcon = (key) => {
    if (sortConfig.key !== key) return '↕';
    return sortConfig.direction === 'asc' ? '↑' : '↓';
  };

  const formatValue = (value) => {
    if (value === null || value === undefined) return 'null';
    if (typeof value === 'object') return value.name || value.id || 'object';
    return value.toString();
  };

  return (
    <div className="table-container">
      <table className="table">
        <thead>
          <tr>
            <th onClick={() => handleSort('id')}>ID {getSortIcon('id')}</th>
            <th onClick={() => handleSort('name')}>NAME {getSortIcon('name')}</th>
            <th>COORDINATES</th>
            <th onClick={() => handleSort('age')}>AGE {getSortIcon('age')}</th>
            <th onClick={() => handleSort('weight')}>WEIGHT {getSortIcon('weight')}</th>
            <th>COLOR</th>
            <th>CHARACTER</th>
          </tr>
        </thead>
        <tbody>
          {sortedDragons.map(dragon => (
            <tr 
              key={dragon.id}
              className={selectedDragon?.id === dragon.id ? 'selected' : ''}
              onClick={() => onSelectDragon(dragon)}
            >
              <td>{dragon.id}</td>
              <td>{dragon.name}</td>
              <td>{formatValue(dragon.coordinates)}</td>
              <td>{dragon.age}</td>
              <td>{dragon.weight}</td>
              <td>{formatValue(dragon.color)}</td>
              <td>{formatValue(dragon.character)}</td>
            </tr>
          ))}
        </tbody>
      </table>
      {dragons.length === 0 && (
        <div className="no-selection">NO_DRAGONS_FOUND</div>
      )}
    </div>
  );
};

export default DragonTable;