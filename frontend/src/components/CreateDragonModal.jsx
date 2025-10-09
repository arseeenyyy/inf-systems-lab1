import React, { useState, useEffect } from 'react';
import { apiClient } from '../api/client';
import '../styles/modal.css';
import '../styles/forms.css';

const CreateDragonModal = ({ onClose, onCreated }) => {
  const [formData, setFormData] = useState({
    name: '',
    coordinatesId: '',
    age: '',
    weight: '',
    color: '',
    character: '',
    caveId: '',
    killerId: '',
    headId: ''
  });
  
  const [relatedObjects, setRelatedObjects] = useState({
    coordinates: [],
    caves: [],
    persons: [],
    heads: []
  });
  
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadRelatedObjects();
  }, []);

  const loadRelatedObjects = async () => {
    try {
      const [coordinates, caves, persons, heads] = await Promise.all([
        apiClient.getCoordinates(),
        apiClient.getCaves(),
        apiClient.getPersons(),
        apiClient.getHeads()
      ]);
      
      setRelatedObjects({
        coordinates: coordinates || [],
        caves: caves || [],
        persons: persons || [],
        heads: heads || []
      });
    } catch (error) {
      console.error('Failed to load related objects:', error);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.name.trim()) {
      newErrors.name = 'Name is required';
    }
    
    if (!formData.coordinatesId) {
      newErrors.coordinatesId = 'Coordinates are required';
    }
    
    if (!formData.age || formData.age < 1) {
      newErrors.age = 'Age must be greater than 0';
    }
    
    if (!formData.weight || formData.weight <= 0) {
      newErrors.weight = 'Weight must be greater than 0';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setLoading(true);
    try {
      const dragonData = {
        name: formData.name,
        coordinatesId: parseInt(formData.coordinatesId),
        age: parseInt(formData.age),
        weight: parseFloat(formData.weight),
        ...(formData.color && { color: formData.color }),
        ...(formData.character && { character: formData.character }),
        ...(formData.caveId && { caveId: parseInt(formData.caveId) }),
        ...(formData.killerId && { killerId: parseInt(formData.killerId) }),
        ...(formData.headId && { headId: parseInt(formData.headId) })
      };

      const newDragon = await apiClient.createDragon(dragonData);
      onCreated(newDragon);
      onClose();
    } catch (error) {
      console.error('Failed to create dragon:', error);
      setErrors({ submit: 'Failed to create dragon' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="modal-header">
          <h2 className="modal-title">Create Dragon</h2>
        </div>
        
        {errors.submit && (
          <div style={{ color: 'red', marginBottom: '10px' }}>{errors.submit}</div>
        )}

        <form className="form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label required">Name</label>
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              className="form-input"
            />
            {errors.name && <div className="form-error">{errors.name}</div>}
          </div>

          <div className="form-group">
            <label className="form-label required">Coordinates</label>
            <select
              name="coordinatesId"
              value={formData.coordinatesId}
              onChange={handleChange}
              className="form-select"
            >
              <option value="">Select Coordinates</option>
              {relatedObjects.coordinates.map(coord => (
                <option key={coord.id} value={coord.id}>
                  ({coord.x};{coord.y})
                </option>
              ))}
            </select>
            {errors.coordinatesId && <div className="form-error">{errors.coordinatesId}</div>}
          </div>

          <div className="form-group">
            <label className="form-label required">Age</label>
            <input
              type="number"
              name="age"
              value={formData.age}
              onChange={handleChange}
              min="1"
              className="form-input"
            />
            {errors.age && <div className="form-error">{errors.age}</div>}
          </div>

          <div className="form-group">
            <label className="form-label required">Weight</label>
            <input
              type="number"
              name="weight"
              value={formData.weight}
              onChange={handleChange}
              step="0.1"
              min="0.1"
              className="form-input"
            />
            {errors.weight && <div className="form-error">{errors.weight}</div>}
          </div>

          <div className="form-group">
            <label className="form-label">Cave</label>
            <select
              name="caveId"
              value={formData.caveId}
              onChange={handleChange}
              className="form-select"
            >
              <option value="">No Cave</option>
              {relatedObjects.caves.map(cave => (
                <option key={cave.id} value={cave.id}>
                  {cave.numberOfTreasures} treasures
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Killer</label>
            <select
              name="killerId"
              value={formData.killerId}
              onChange={handleChange}
              className="form-select"
            >
              <option value="">No Killer</option>
              {relatedObjects.persons.map(person => (
                <option key={person.id} value={person.id}>
                  {person.name}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Head</label>
            <select
              name="headId"
              value={formData.headId}
              onChange={handleChange}
              className="form-select"
            >
              <option value="">No Head</option>
              {relatedObjects.heads.map(head => (
                <option key={head.id} value={head.id}>
                  (size: {head.size}, eyes: {head.eyesCount || 'null'})
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Color</label>
            <select
              name="color"
              value={formData.color}
              onChange={handleChange}
              className="form-select"
            >
              <option value="">Select Color</option>
              <option value="GREEN">GREEN</option>
              <option value="BLUE">BLUE</option>
              <option value="YELLOW">YELLOW</option>
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Character</label>
            <select
              name="character"
              value={formData.character}
              onChange={handleChange}
              className="form-select"
            >
              <option value="">Select Character</option>
              <option value="EVIL">EVIL</option>
              <option value="CHAOTIC">CHAOTIC</option>
              <option value="CHAOTIC_EVIL">CHAOTIC_EVIL</option>
            </select>
          </div>

          <div className="form-actions">
            <button type="button" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" disabled={loading}>
              {loading ? 'Creating...' : 'Create'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateDragonModal;