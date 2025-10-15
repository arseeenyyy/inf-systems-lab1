import React, { useState, useEffect } from 'react';
import { apiClient } from '../api/client';

const TeamsPage = () => {
  const [teams, setTeams] = useState([]);
  const [persons, setPersons] = useState([]);
  const [caves, setCaves] = useState([]);
  const [totalTreasures, setTotalTreasures] = useState(0); // ✅ НОВОЕ!
  const [selectedTeam, setSelectedTeam] = useState(null);
  const [createFormData, setCreateFormData] = useState({ name: '', personsIds: [] });
  const [sendFormData, setSendFormData] = useState({ teamId: '', caveId: '' });
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
      
      // ✅ СЧИТАЕМ treasures
      const total = cavesData.reduce((sum, cave) => sum + (cave.numberOfTreasures || 0), 0);
      setTotalTreasures(total);
    } catch (error) {
      console.error('Failed to load data:', error);
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

  // ✅ УБРАЛИ sendResult - просто обновляем данные
  const handleSendSubmit = async (e) => {
    e.preventDefault();
    if (!sendFormData.teamId || !sendFormData.caveId) return;
    
    try {
      await apiClient.sendTeamToCave(sendFormData);
      // ✅ ПЕЩЕРА УДАЛЯЕТСЯ - обновляем всё
      await loadAllData();
    } catch (error) {
      console.error('Failed to send team:', error);
    }
  };

  return (
    <div className="layout">
      <div className="sidebar" style={{ width: '400px' }}>
        {/* CREATE TEAM - БЕЗ ИЗМЕНЕНИЙ */}
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
              <div style={{ 
                maxHeight: '200px', overflowY: 'auto', 
                border: '1px solid #444', padding: '10px',
                minHeight: '200px'
              }}>
                {availablePersons.length > 0 ? (
                  availablePersons.map(person => (
                    <div key={person.id} style={{ marginBottom: '8px' }}>
                      <input
                        type="checkbox"
                        id={`person-${person.id}`}
                        checked={createFormData.personsIds.includes(person.id)}
                        onChange={() => handleCheckboxChange(person.id)}
                      />
                      <label htmlFor={`person-${person.id}`} style={{ marginLeft: '8px' }}>
                        {person.name}
                      </label>
                    </div>
                  ))
                ) : (
                  <p style={{ color: '#888' }}>No available persons</p>
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

        {/* SEND TO CAVE - УБРАЛИ JSON! */}
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
            {/* ✅ УБРАЛИ json-view! */}
          </form>
        </div>

        {/* SELECTED TEAM - БЕЗ ИЗМЕНЕНИЙ */}
        <div className="panel">
          <div className="panel-title">[selected_team_data]</div>
          {selectedTeam ? (
            <div className="json-view">
              <pre>{JSON.stringify({
                id: selectedTeam.id,
                name: selectedTeam.name,
                members_count: selectedTeam.memberCount,
                members: selectedTeam.members || []
              }, null, 2)}</pre>
            </div>
          ) : (
            <div className="no-selection">NO_SELECTION</div>
          )}
        </div>
      </div>

      <div className="main">
        {}
        <div style={{ 
          padding: '10px', 
          background: '#333', 
          color: '#ffd700', 
          fontSize: '12px', 
          fontWeight: 'bold',
          marginBottom: '10px',
          textAlign: 'center'
        }}>
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