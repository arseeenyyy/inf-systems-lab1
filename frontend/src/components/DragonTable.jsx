import React, { useMemo, useState } from 'react';

const DragonTable = ({ dragons, selectedDragon, sortConfig, onSelectDragon, onSort }) => {
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 20;

  const sortedDragons = useMemo(() => {
    if (!sortConfig.key) return dragons;
    
    return [...dragons].sort((a, b) => {
      let aVal = a[sortConfig.key];
      let bVal = b[sortConfig.key];

      switch (sortConfig.key) {
        case 'coordinates':
          aVal = a.coordinates ? a.coordinates.x + a.coordinates.y : 0;
          bVal = b.coordinates ? b.coordinates.x + b.coordinates.y : 0;
          break;
        case 'cave':
          aVal = a.cave ? a.cave.numberOfTreasures || 0 : 0;
          bVal = b.cave ? b.cave.numberOfTreasures || 0 : 0;
          break;
        case 'killer':
          aVal = a.killer ? a.killer.name : '';
          bVal = b.killer ? b.killer.name : '';
          break;
        case 'head':
          aVal = a.head ? a.head.size || 0 : 0;
          bVal = b.head ? b.head.size || 0 : 0;
          break;
        case 'creationDate':
          aVal = new Date(a.creationDate);
          bVal = new Date(b.creationDate);
          break;
        default:
          aVal = aVal || '';
          bVal = bVal || '';
      }

      if (aVal < bVal) return sortConfig.direction === 'asc' ? -1 : 1;
      if (aVal > bVal) return sortConfig.direction === 'asc' ? 1 : -1;
      return 0;
    });
  }, [dragons, sortConfig]);

  const totalPages = Math.ceil(sortedDragons.length / itemsPerPage);
  const paginatedDragons = useMemo(() => {
    const startIndex = (currentPage - 1) * itemsPerPage;
    return sortedDragons.slice(startIndex, startIndex + itemsPerPage);
  }, [sortedDragons, currentPage, itemsPerPage]);

  const handleSort = (key) => {
    const direction = sortConfig.key === key && sortConfig.direction === 'asc' ? 'desc' : 'asc';
    onSort({ key, direction });
    setCurrentPage(1); 
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

  const formatName = (name) => {
    if (!name) return 'null';
    const maxLength = 15;
    if (name.length > maxLength) {
      return name.substring(0, maxLength) + '...';
    }
    return name;
  };

  const showPagination = sortedDragons.length > itemsPerPage;

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
            <th onClick={() => handleSort('coordinates')}>
              coordinates<span className="sort-icon">{getSortIcon('coordinates')}</span>
            </th>
            <th onClick={() => handleSort('creationDate')}>
              creation<span className="sort-icon">{getSortIcon('creationDate')}</span>
            </th>
            <th onClick={() => handleSort('cave')}>
              cave<span className="sort-icon">{getSortIcon('cave')}</span>
            </th>
            <th onClick={() => handleSort('killer')}>
              killer<span className="sort-icon">{getSortIcon('killer')}</span>
            </th>
            <th onClick={() => handleSort('age')}>
              age<span className="sort-icon">{getSortIcon('age')}</span>
            </th>
            <th onClick={() => handleSort('weight')}>
              weight<span className="sort-icon">{getSortIcon('weight')}</span>
            </th>
            <th onClick={() => handleSort('color')}>
              color<span className="sort-icon">{getSortIcon('color')}</span>
            </th>
            <th onClick={() => handleSort('character')}>
              character<span className="sort-icon">{getSortIcon('character')}</span>
            </th>
            <th onClick={() => handleSort('head')}>
              head<span className="sort-icon">{getSortIcon('head')}</span>
            </th>
          </tr>
        </thead>
        <tbody>
          {paginatedDragons.map(dragon => (
            <tr 
              key={dragon.id}
              className={selectedDragon?.id === dragon.id ? 'selected' : ''}
              onClick={() => onSelectDragon(dragon)}
            >
              <td>{dragon.id}</td>
              <td title={dragon.name}>{formatName(dragon.name)}</td>
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

      {}
      {showPagination && (
        <div className="pagination">
          <button 
            onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
            disabled={currentPage === 1}
            className="btn btn-secondary"
          >
            ← Prev
          </button>
          
          <span className="pagination-info">
            Page {currentPage} of {totalPages} ({sortedDragons.length} total)
          </span>
          
          <button 
            onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))}
            disabled={currentPage === totalPages}
            className="btn btn-secondary"
          >
            Next →
          </button>
        </div>
      )}
    </div>
  );
};

export default DragonTable;