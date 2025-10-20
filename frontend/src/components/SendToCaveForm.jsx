import React from 'react';

const SendToCaveForm = ({ sendFormData, teams, caves, onSendChange, onSubmit }) => {
  return (
    <div className="panel">
      <div className="panel-title">[send_to_cave]</div>
      <form onSubmit={onSubmit} className="form">
        <div className="form-group">
          <label className="form-label">team</label>
          <select name="teamId" value={sendFormData.teamId} onChange={onSendChange} className="form-select">
            <option value="">Select team</option>
            {teams.map(team => (
              <option key={team.id} value={team.id}>
                {team.id}; {team.name} ({team.memberCount} members)
              </option>
            ))}
          </select>
        </div>
        <div className="form-group">
          <label className="form-label">cave</label>
          <select name="caveId" value={sendFormData.caveId} onChange={onSendChange} className="form-select">
            <option value="">Select cave</option>
            {caves.map(cave => (
              <option key={cave.id} value={cave.id}>
                {cave.id}; treasures: {cave.numberOfTreasures || '?'}
              </option>
            ))}
          </select>
        </div>
        <div className="actions">
          <button type="submit" className="btn btn-primary">[send to cave]</button>
        </div>
      </form>
    </div>
  );
};

export default SendToCaveForm;