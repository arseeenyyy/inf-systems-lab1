// src/components/TeamsTable.jsx
import React from 'react';

const TeamsTable = ({ teams, selectedTeam, onSelectTeam }) => {
  return (
    <div className="panel">
      <div className="panel-title">[teams_table]</div>
      <div className="table-container">
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Members Count</th>
            </tr>
          </thead>
          <tbody>
            {teams.map(team => (
              <tr 
                key={team.id}
                className={selectedTeam?.id === team.id ? 'selected' : ''}
                onClick={() => onSelectTeam(team)}
              >
                <td>{team.id}</td>
                <td>{team.name}</td>
                <td>{team.memberCount}</td>
              </tr>
            ))}
          </tbody>
        </table>
        {teams.length === 0 && <div className="no-selection">no_teams_found</div>}
      </div>
    </div>
  );
};

export default TeamsTable;