import React from 'react';

const SelectedTeamPanel = ({ selectedTeam, onDeleteTeam }) => {
  return (
    <div className="panel">
      <div className="panel-title">[selected_team_data]</div>
      {selectedTeam ? (
        <div>
          <div className="json-view">
            <pre>{JSON.stringify({
              id: selectedTeam.id,
              name: selectedTeam.name,
              members_count: selectedTeam.memberCount,
              members: selectedTeam.members || []
            }, null, 2)}</pre>
          </div>
          <div className="actions">
            <button 
              onClick={() => onDeleteTeam(selectedTeam.id)}
              className="btn btn-danger"
            >
              [delete]
            </button>
          </div>
        </div>
      ) : (
        <div className="no-selection">NO_SELECTION</div>
      )}
    </div>
  );
};

export default SelectedTeamPanel;