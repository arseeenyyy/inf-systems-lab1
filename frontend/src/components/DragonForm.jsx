import React, { useState, useEffect } from 'react';
import { apiClient } from '../api/client';

const DragonForm = ({ dragon, onSubmit, onCancel }) => {
  const [formData, setFormData] = useState({
    name: '',
    coordinatesId: '',
    caveId: '',
    killerId: '',
    headId: '',
    age: '',
    weight: '',
    color: '',
    character: ''
  });

  const [relatedData, setRelatedData] = useState({
    coordinates: [],
    caves: [],
    persons: [],
    heads: []
  });

  useEffect(() => {
    loadRelatedData();
  }, []);

  useEffect(() => {
    if (dragon) {
      setFormData({
        name: dragon.name || '',
        coordinatesId: dragon.coordinates?.id || '',
        caveId: dragon.cave?.id || '',
        killerId: dragon.killer?.id || '',
        headId: dragon.head?.id || '',
        age: dragon.age || '',
        weight: dragon.weight || '',
        color: dragon.color || '',
        character: dragon.character || ''
      });
    } else {
      setFormData({
        name: '', coordinatesId: '', caveId: '', killerId: '', headId: '',
        age: '', weight: '', color: '', character: ''
      });
    }
  }, [dragon]);

  const loadRelatedData = async () => {
    try {
      const [coordinates, caves, persons, heads] = await Promise.all([
        apiClient.getCoordinates(),
        apiClient.getCaves(),
        apiClient.getPersons(),
        apiClient.getHeads()
      ]);
      setRelatedData({ coordinates, caves, persons, heads });
    } catch (error) {
      console.error('Failed to load related data:', error);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const data = {
      ...formData,
      coordinatesId: formData.coordinatesId ? parseInt(formData.coordinatesId) : null,
      caveId: formData.caveId ? parseInt(formData.caveId) : null,
      killerId: formData.killerId ? parseInt(formData.killerId) : null,
      headId: formData.headId ? parseInt(formData.headId) : null,
      age: parseInt(formData.age),
      weight: parseFloat(formData.weight)
    };
    onSubmit(data);
  };

  return (
    <form onSubmit={handleSubmit} className="form">
      <div className="form-group">
        <label className="form-label">NAME</label>
        <input
          type="text"
          name="name"
          value={formData.name}
          onChange={handleChange}
          className="form-input"
          required
        />
      </div>

      <div className="form-group">
        <label className="form-label">COORDINATES</label>
        <select
          name="coordinatesId"
          value={formData.coordinatesId}
          onChange={handleChange}
          className="form-select"
          required
        >
          <option value="">SELECT_COORDINATES</option>
          {relatedData.coordinates.map(coord => (
            <option key={coord.id} value={coord.id}>
              COORD_{coord.id}
            </option>
          ))}
        </select>
      </div>

      <div className="form-row">
        <div className="form-group">
          <label className="form-label">AGE</label>
          <input
            type="number"
            name="age"
            value={formData.age}
            onChange={handleChange}
            className="form-input"
            min="1"
            required
          />
        </div>

        <div className="form-group">
          <label className="form-label">WEIGHT</label>
          <input
            type="number"
            name="weight"
            value={formData.weight}
            onChange={handleChange}
            className="form-input"
            min="0.1"
            step="0.1"
            required
          />
        </div>
      </div>

      <div className="form-row">
        <div className="form-group">
          <label className="form-label">CAVE</label>
          <select
            name="caveId"
            value={formData.caveId}
            onChange={handleChange}
            className="form-select"
          >
            <option value="">NO_CAVE</option>
            {relatedData.caves.map(cave => (
              <option key={cave.id} value={cave.id}>
                CAVE_{cave.id}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label className="form-label">KILLER</label>
          <select
            name="killerId"
            value={formData.killerId}
            onChange={handleChange}
            className="form-select"
          >
            <option value="">NO_KILLER</option>
            {relatedData.persons.map(person => (
              <option key={person.id} value={person.id}>
                {person.name}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="form-row">
        <div className="form-group">
          <label className="form-label">HEAD</label>
          <select
            name="headId"
            value={formData.headId}
            onChange={handleChange}
            className="form-select"
          >
            <option value="">NO_HEAD</option>
            {relatedData.heads.map(head => (
              <option key={head.id} value={head.id}>
                HEAD_{head.id}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label className="form-label">COLOR</label>
          <select
            name="color"
            value={formData.color}
            onChange={handleChange}
            className="form-select"
          >
            <option value="">NO_COLOR</option>
            <option value="GREEN">GREEN</option>
            <option value="BLUE">BLUE</option>
            <option value="YELLOW">YELLOW</option>
          </select>
        </div>
      </div>

      <div className="form-group">
        <label className="form-label">CHARACTER</label>
        <select
          name="character"
          value={formData.character}
          onChange={handleChange}
          className="form-select"
        >
          <option value="">NO_CHARACTER</option>
          <option value="EVIL">EVIL</option>
          <option value="CHAOTIC">CHAOTIC</option>
          <option value="CHAOTIC_EVIL">CHAOTIC_EVIL</option>
        </select>
      </div>
      <div className="actions">
        <button type="submit" className="btn btn-primary">
          [create]
        </button>
        {dragon && (
          <button type="button" className="btn" onClick={onCancel}>
            [cancel]
          </button>
        )}
      </div>

    </form>
  );
};

export default DragonForm;