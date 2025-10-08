import React, { useState, useMemo } from 'react';
import '../styles/DragonDetail.css'

const DragonTable = ({ dragons, onSelect, filter, sortField }) => {
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 10;

  const filteredAndSorted = useMemo(() => {
    let result = dragons.filter(d => 
      !filter || d.name.includes(filter) || d.color?.includes(filter)
    );
    
    if (sortField) {
      result.sort((a, b) => (a[sortField] > b[sortField] ? 1 : -1));
    }
    
    return result;
  }, [dragons, filter, sortField]);

  const paginated = useMemo(() => {
    const start = (currentPage - 1) * pageSize;
    return filteredAndSorted.slice(start, start + pageSize);
  }, [filteredAndSorted, currentPage]);

  const totalPages = Math.ceil(filteredAndSorted.length / pageSize);

  return (
    <div className="table-container">
      <table className="dragon-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Age</th>
            <th>Weight</th>
            <th>Color</th>
            <th>Character</th>
            <th>Coordinates ID</th>
            <th>Cave ID</th>
            <th>Killer ID</th>
            <th>Head ID</th>
          </tr>
        </thead>
        <tbody>
          {paginated.map(dragon => (
            <tr key={dragon.id} onClick={() => onSelect(dragon)}>
              <td>{dragon.id}</td>
              <td>{dragon.name}</td>
              <td>{dragon.age}</td>
              <td>{dragon.weight}</td>
              <td>{dragon.color}</td>
              <td>{dragon.character}</td>
              <td>{dragon.coordinates?.id}</td>
              <td>{dragon.cave?.id}</td>
              <td>{dragon.killer?.id}</td>
              <td>{dragon.head?.id}</td>
            </tr>
          ))}
        </tbody>
      </table>
      
      {totalPages > 1 && (
        <div className="pagination">
          {Array.from({ length: totalPages }, (_, i) => (
            <button
              key={i + 1}
              className={currentPage === i + 1 ? 'active' : ''}
              onClick={() => setCurrentPage(i + 1)}
            >
              {i + 1}
            </button>
          ))}
        </div>
      )}
    </div>
  );
};

export default DragonTable;