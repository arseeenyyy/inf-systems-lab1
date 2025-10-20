import React, { useState, useEffect } from 'react';
import { apiClient } from '../api/client';
import CreateTeamForm from '../components/CreateTeamForm';
import SendToCaveForm from '../components/SendToCaveForm';
import SelectedTeamPanel from '../components/SelectedTeamPanel';
import DragonOperationsPanel from '../components/DragonOperationsPanel';
import TeamsTable from '../components/TeamsTable';

const TeamsPage = () => {
  const [teams, setTeams] = useState([]);
  const [persons, setPersons] = useState([]);
  const [caves, setCaves] = useState([]);
  const [totalTreasures, setTotalTreasures] = useState(0); 
  const [selectedTeam, setSelectedTeam] = useState(null);
  const [createFormData, setCreateFormData] = useState({ name: '', personsIds: [] });
  const [sendFormData, setSendFormData] = useState({ teamId: '', caveId: '' });
  const [dragonOperations, setDragonOperations] = useState({ 
    color: '', 
    substring: '', 
    searchResults: [] 
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  const availableColors = ['GREEN', 'BLUE', 'YELLOW'];

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
      console.error('Failed to delete dragons by color:', error.message);
    }
  };

  const handleDeleteOneByColor = async () => {
    if (!dragonOperations.color) return;
    try {
      await apiClient.deleteOneByColor(dragonOperations.color);
      await loadAllData();
      setDragonOperations(prev => ({ ...prev, color: '' }));
    } catch (error) {
      console.error('Failed to delete one dragon by color:', error.message);
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
      await apiClient.createTeam(createFormData);
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
        <CreateTeamForm
          createFormData={createFormData}
          availablePersons={availablePersons}
          errors={errors}
          loading={loading}
          onCreateChange={handleCreateChange}
          onCheckboxChange={handleCheckboxChange}
          onSubmit={handleCreateSubmit}
        />

        <SendToCaveForm
          sendFormData={sendFormData}
          teams={teams}
          caves={caves}
          onSendChange={handleSendChange}
          onSubmit={handleSendSubmit}
        />

        <SelectedTeamPanel
          selectedTeam={selectedTeam}
          onDeleteTeam={handleDeleteTeam}
        />

        <DragonOperationsPanel
          dragonOperations={dragonOperations}
          availableColors={availableColors}
          onOperationChange={handleDragonOperationChange}
          onDeleteAllByColor={handleDeleteAllByColor}
          onDeleteOneByColor={handleDeleteOneByColor}
          onSearchByName={handleSearchByName}
        />
      </div>

      <div className="main">
        <div className="treasures-info">
          [total treasures: {totalTreasures}]
        </div>

        <TeamsTable
          teams={teams}
          selectedTeam={selectedTeam}
          onSelectTeam={setSelectedTeam}
        />
      </div>
    </div>
  );
};

export default TeamsPage;