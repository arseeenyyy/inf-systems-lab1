import React, { useState, useEffect } from 'react';
import { apiClient } from '../api/client';

const TeamsPage = () => {
  const [teams, setTeams] = useState([]);
  const [persons, setPersons] = useState([]);
  const [caves, setCaves] = useState([]);
  const [totalTreasures, setTotalTreasures] = useState(0); 
  const [selectedTeam, setSelectedTeam] = useState(null);
  const [createFormData, setCreateFormData] = useState({ name: '', personsIds: [] });
  const [sendFormData, setSendFormData] = useState({ teamId: '', caveId: '' });
  const [dragonOperations, setDragonOperations] = useState({ color: '', substring: '', searchResults: [] });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadAllData();
  }, []);

  const loadAllData = async () => {
    try {
      const [teamsData, personsData, cavesData] = await Promise.all([
        apiClient.getTeams(),
        apiClient.getPersons(),
        apiClient.getCaves()
      ]);
      setTeams(teamsData);
      setPersons(personsData);
      setCaves(cavesData);
      
      const total = cavesData.reduce((sum, cave) => sum + (cave.numberOfTreasures || 0), 0);
      setTotalTreasures(total);
    } catch (error) {
      console.error('Failed to load data:', error);
    }
  };

  const handleDeleteTeam = async (teamId) => {
    try {
      await apiClient.deleteTeam(teamId);
      if (selectedTeam && selectedTeam.id === teamId) {
        setSelectedTeam(null);
      }
      await loadAllData();
    } catch (error) {
      console.error('Failed to delete team:', error);
    }
  };

  const handleDragonOperationChange = (e) => {
    const { name, value } = e.target;
    setDragonOperations(prev => ({ ...prev, [name]: value }));
  };

  const handleDeleteAllByColor = async () => {
    if (!dragonOperations.color) return;
    try {
      await apiClient.deleteAllByColor(dragonOperations.color);
      await loadAllData();
      setDragonOperations(prev => ({ ...prev, color: '' }));
    } catch (error) {
      console.error('Failed to delete dragons by color:', error);
    }
  };

  const handleDeleteOneByColor = async () => {
    if (!dragonOperations.color) return;
    try {
      await apiClient.deleteOneByColor(dragonOperations.color);
      await loadAllData();
      setDragonOperations(prev => ({ ...prev, color: '' }));
    } catch (error) {
      console.error('Failed to delete one dragon by color:', error);
    }
  };

  const handleSearchByName = async () => {
    if (!dragonOperations.substring) return;
    try {
      const results = await apiClient.findByNameStartingWith(dragonOperations.substring);
      setDragonOperations(prev => ({ ...prev, searchResults: results }));
    } catch (error) {
      console.error('Failed to search dragons:', error);
    }
  };

  const availablePersons = persons.filter(p => 
    !teams.some(t => t.members && t.members.some(m => m.id === p.id))
  );

  const handleCreateChange = (e) => {
    const { name, value } = e.target;
    setCreateFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleCheckboxChange = (personId) => {
    setCreateFormData(prev => {
      const personsIds = prev.personsIds.includes(personId)
        ? prev.personsIds.filter(id => id !== personId)
        : [...prev.personsIds, personId];
      return { ...prev, personsIds };
    });
    if (errors.personsIds) setErrors(prev => ({ ...prev, personsIds: '' }));
  };

  const validateCreate = () => {
    const newErrors = {};
    if (!createFormData.name.trim()) newErrors.name = 'required';
    if (createFormData.personsIds.length < 1) newErrors.personsIds = 'at least 1';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleCreateSubmit = async (e) => {
    e.preventDefault();
    if (!validateCreate()) return;
    setLoading(true);

    try {
      const result = await apiClient.createTeam(createFormData);
      setCreateFormData({ name: '', personsIds: [] });
      await loadAllData();
    } catch (error) {
      setErrors({ submit: error.message });
    } finally {
      setLoading(false);
    }
  };

  const handleSendChange = (e) => {
    const { name, value } = e.target;
    setSendFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSendSubmit = async (e) => {
    e.preventDefault();
    if (!sendFormData.teamId || !sendFormData.caveId) return;
    
    try {
      await apiClient.sendTeamToCave(sendFormData);
      await loadAllData();
    } catch (error) {
      console.error('Failed to send team:', error);
    }
  };

  return (
    <div className="layout">
      <div className="sidebar">
        <div className="panel">
          <div className="panel-title">[create_team]</div>
          <form onSubmit={handleCreateSubmit} className="form" noValidate>
            <div className="form-group">
              <label className="form-label">name</label>
              <input
                type="text"
                name="name"
                value={createFormData.name}
                onChange={handleCreateChange}
                className={`form-input ${errors.name ? 'error' : ''}`}
              />
              {errors.name && <span className="error-text">name: {errors.name}</span>}
            </div>
            <div className="form-group">
              <label className="form-label">members</label>
              <div className="checkbox-container">
                {availablePersons.length > 0 ? (
                  availablePersons.map(person => (
                    <div key={person.id} className="checkbox-item">
                      <input
                        type="checkbox"
                        id={`person-${person.id}`}
                        checked={createFormData.personsIds.includes(person.id)}
                        onChange={() => handleCheckboxChange(person.id)}
                      />
                      <label htmlFor={`person-${person.id}`}>
                        {person.name}
                      </label>
                    </div>
                  ))
                ) : (
                  <p className="no-data">No available persons</p>
                )}
              </div>
              {errors.personsIds && <span className="error-text">members: {errors.personsIds}</span>}
            </div>
            {errors.submit && <div className="error-text">{errors.submit}</div>}
            <div className="actions">
              <button type="submit" className="btn btn-primary" disabled={loading}>
                [{loading ? 'creating...' : 'create'}]
              </button>
            </div>
          </form>
        </div>

        <div className="panel">
          <div className="panel-title">[send_to_cave]</div>
          <form onSubmit={handleSendSubmit} className="form">
            <div className="form-group">
              <label className="form-label">team</label>
              <select name="teamId" value={sendFormData.teamId} onChange={handleSendChange} className="form-select">
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
              <select name="caveId" value={sendFormData.caveId} onChange={handleSendChange} className="form-select">
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
                  onClick={() => handleDeleteTeam(selectedTeam.id)}
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

        <div className="panel">
          <div className="panel-title">[dragon_operations]</div>
          <div className="form">
            <div className="form-group">
              <label className="form-label">color</label>
              <input
                type="text"
                name="color"
                value={dragonOperations.color}
                onChange={handleDragonOperationChange}
                className="form-input"
                placeholder="Enter color"
              />
              <div className="actions">
                <button 
                  onClick={handleDeleteAllByColor} 
                  className="btn btn-danger"
                  disabled={!dragonOperations.color}
                >
                  [delete all by color]
                </button>
                <button 
                  onClick={handleDeleteOneByColor} 
                  className="btn btn-danger"
                  disabled={!dragonOperations.color}
                >
                  [delete one by color]
                </button>
              </div>
            </div>
            <div className="form-group">
              <label className="form-label">name substring</label>
              <input
                type="text"
                name="substring"
                value={dragonOperations.substring}
                onChange={handleDragonOperationChange}
                className="form-input"
                placeholder="Enter name substring"
              />
              <div className="actions">
                <button 
                  onClick={handleSearchByName} 
                  className="btn btn-primary"
                  disabled={!dragonOperations.substring}
                >
                  [search by name]
                </button>
              </div>
            </div>
            {dragonOperations.searchResults.length > 0 && (
              <div className="search-results">
                <div className="results-count">
                  Found: {dragonOperations.searchResults.length} dragons
                </div>
                <div className="results-list">
                  {dragonOperations.searchResults.map(dragon => (
                    <div key={dragon.id} className="result-item">
                      {dragon.id}: {dragon.name} ({dragon.color})
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>

      <div className="main">
        <div className="treasures-info">
          [total treasures: {totalTreasures}]
        </div>

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
                    onClick={() => setSelectedTeam(team)}
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
      </div>
    </div>
  );
};

export default TeamsPage;