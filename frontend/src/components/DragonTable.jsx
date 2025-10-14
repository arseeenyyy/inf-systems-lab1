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

  const formatCoordinates = (coord) => {
    if (!coord) return 'null';
    return `(${coord.x};${coord.y})`;
  };

  const formatCave = (cave) => {
    if (!cave) return 'null';
    return cave.numberOfTreasures ? `t:${cave.numberOfTreasures}` : 'null';
  };

  const formatHead = (head) => {
    if (!head) return 'null';
    const eyes = head.eyesCount !== null ? head.eyesCount : '?';
    return `(${head.size};${eyes})`;
  };

  return (
    <div className="table-container">
      <table className="table">
        <thead>
          <tr>
            <th onClick={() => handleSort('id')}>
              id<span className="sort-icon">{getSortIcon('id')}</span>
            </th>
            <th onClick={() => handleSort('name')}>
              name<span className="sort-icon">{getSortIcon('name')}</span>
            </th>
            <th>coordinates</th>
            <th onClick={() => handleSort('creationDate')}>
              creation<span className="sort-icon">{getSortIcon('creationDate')}</span>
            </th>
            <th>cave</th>
            <th>killer</th>
            <th onClick={() => handleSort('age')}>
              age<span className="sort-icon">{getSortIcon('age')}</span>
            </th>
            <th onClick={() => handleSort('weight')}>
              weight<span className="sort-icon">{getSortIcon('weight')}</span>
            </th>
            <th>color</th>
            <th>character</th>
            <th>head</th>
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
              <td>{formatCoordinates(dragon.coordinates)}</td>
              <td>{dragon.creationDate}</td>
              <td>{formatCave(dragon.cave)}</td>
              <td>{dragon.killer?.name || 'null'}</td>
              <td>{dragon.age}</td>
              <td>{dragon.weight}</td>
              <td>{dragon.color || 'null'}</td>
              <td>{dragon.character || 'null'}</td>
              <td>{formatHead(dragon.head)}</td>
            </tr>
          ))}
        </tbody>
      </table>
      {dragons.length === 0 && (
        <div className="no-selection">no_dragons_found</div>
      )}
    </div>
  );
};

export default DragonTable;