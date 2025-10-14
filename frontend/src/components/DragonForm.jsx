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
    
    // Валидация
    const age = parseInt(formData.age);
    const weight = parseFloat(formData.weight);
    
    if (age <= 0) {
      alert('Age must be greater than 0');
      return;
    }
    
    if (weight <= 0) {
      alert('Weight must be greater than 0');
      return;
    }

    const data = {
      ...formData,
      coordinatesId: formData.coordinatesId ? parseInt(formData.coordinatesId) : null,
      caveId: formData.caveId ? parseInt(formData.caveId) : null,
      killerId: formData.killerId ? parseInt(formData.killerId) : null,
      headId: formData.headId ? parseInt(formData.headId) : null,
      age: age,
      weight: weight
    };
    onSubmit(data);
  };

  return (
    <form onSubmit={handleSubmit} className="form">
      <div className="form-group">
        <label className="form-label">name</label>
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
        <label className="form-label">coordinates</label>
        <select
          name="coordinatesId"
          value={formData.coordinatesId}
          onChange={handleChange}
          className="form-select"
          required
        >
          <option value=""></option>
          {relatedData.coordinates.map(coord => (
            <option key={coord.id} value={coord.id}>
              ({coord.x};{coord.y})
            </option>
          ))}
        </select>
      </div>

      <div className="form-row">
        <div className="form-group">
          <label className="form-label">age</label>
          <input
            type="text"
            name="age"
            value={formData.age}
            onChange={handleChange}
            className="form-input"
            placeholder="> 0"
            required
          />
        </div>

        <div className="form-group">
          <label className="form-label">weight</label>
          <input
            type="text"
            name="weight"
            value={formData.weight}
            onChange={handleChange}
            className="form-input"
            placeholder="> 0"
            required
          />
        </div>
      </div>

      <div className="form-row">
        <div className="form-group">
          <label className="form-label">cave</label>
          <select
            name="caveId"
            value={formData.caveId}
            onChange={handleChange}
            className="form-select"
          >
            <option value=""></option>
            {relatedData.caves.map(cave => (
              <option key={cave.id} value={cave.id}>
                treasures:{cave.numberOfTreasures || '?'}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label className="form-label">killer</label>
          <select
            name="killerId"
            value={formData.killerId}
            onChange={handleChange}
            className="form-select"
          >
            <option value=""></option>
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
          <label className="form-label">head</label>
          <select
            name="headId"
            value={formData.headId}
            onChange={handleChange}
            className="form-select"
          >
            <option value=""></option>
            {relatedData.heads.map(head => (
              <option key={head.id} value={head.id}>
                ({head.size};{head.eyesCount || '?'})
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label className="form-label">color</label>
          <select
            name="color"
            value={formData.color}
            onChange={handleChange}
            className="form-select"
          >
            <option value=""></option>
            <option value="GREEN">green</option>
            <option value="BLUE">blue</option>
            <option value="YELLOW">yellow</option>
          </select>
        </div>
      </div>

      <div className="form-group">
        <label className="form-label">character</label>
        <select
          name="character"
          value={formData.character}
          onChange={handleChange}
          className="form-select"
        >
          <option value=""></option>
          <option value="EVIL">evil</option>
          <option value="CHAOTIC">chaotic</option>
          <option value="CHAOTIC_EVIL">chaotic_evil</option>
        </select>
      </div>
      
      <div className="actions">
        <button type="submit" className="btn btn-primary">
          [{dragon ? 'update' : 'create'}]
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