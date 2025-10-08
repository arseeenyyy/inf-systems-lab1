import React, { useState, useEffect } from 'react';
import { apiClient } from '../api/client';
import { Color, DragonCharacter } from '../enums/enums';
import '../styles/CreateDragonForm.css'

const CreateDragonForm = ({ onCreated }) => {
  const [formData, setFormData] = useState({});
  const [relatedObjects, setRelatedObjects] = useState({ coordinates: [], caves: [], persons: [], heads: [] });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadRelatedObjects();
  }, []);

  const loadRelatedObjects = async () => {
    const [coordinates, caves, persons, heads] = await Promise.all([
      apiClient.getCoordinates(),
      apiClient.getCaves(),
      apiClient.getPersons(),
      apiClient.getHeads()
    ]);
    setRelatedObjects({ coordinates, caves, persons, heads });
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await apiClient.createDragon(formData);
      onCreated();
      setFormData({});
    } catch (error) {
      alert('Error creating dragon');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form className="dragon-form" onSubmit={handleSubmit}>
      <h4>Create Dragon</h4>
      <input name="name" placeholder="Name" onChange={handleChange} required />
      <input name="age" type="number" placeholder="Age" onChange={handleChange} required />
      <input name="weight" type="number" placeholder="Weight" onChange={handleChange} required />
      
      <select name="coordinatesId" onChange={handleChange} required>
        <option value="">Select Coordinates</option>
        {relatedObjects.coordinates.map(coord => <option key={coord.id} value={coord.id}>Coordinates {coord.id}</option>)}
      </select>
      
      <select name="caveId" onChange={handleChange}>
        <option value="">Select Cave (optional)</option>
        {relatedObjects.caves.map(cave => <option key={cave.id} value={cave.id}>Cave {cave.id}</option>)}
      </select>
      
      <select name="killerId" onChange={handleChange}>
        <option value="">Select Killer (optional)</option>
        {relatedObjects.persons.map(person => <option key={person.id} value={person.id}>{person.name}</option>)}
      </select>
      
      <select name="headId" onChange={handleChange}>
        <option value="">Select Head (optional)</option>
        {relatedObjects.heads.map(head => <option key={head.id} value={head.id}>Head {head.id}</option>)}
      </select>
      
      <select name="color" onChange={handleChange}>
        <option value="">Select Color (optional)</option>
        {Object.values(Color).map(color => <option key={color} value={color}>{color}</option>)}
      </select>
      
      <select name="character" onChange={handleChange}>
        <option value="">Select Character (optional)</option>
        {Object.values(DragonCharacter).map(char => <option key={char} value={char}>{char}</option>)}
      </select>
      
      <button type="submit" disabled={loading}>{loading ? 'Creating...' : 'Create Dragon'}</button>
    </form>
  );
};

export default CreateDragonForm;