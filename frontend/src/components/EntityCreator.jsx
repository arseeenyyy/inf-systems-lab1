import React, { useState } from 'react';
import { apiClient } from '../api/client';

const EntityCreator = ({ onEntityCreated }) => {
  const [selectedType, setSelectedType] = useState('coordinates');
  const [formData, setFormData] = useState({});
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({}); // для ошибок

  const entityTypes = [
    { value: 'coordinates', label: 'coordinates' },
    { value: 'cave', label: 'cave' },
    { value: 'person', label: 'person' },
    { value: 'head', label: 'head' }
  ];

  const handleTypeChange = (type) => {
    setSelectedType(type);
    setFormData(getDefaultFormData(type));
    setErrors({}); // очищаем ошибки
  };

  function getDefaultFormData(type) {
    switch (type) {
      case 'coordinates':
        return { x: '', y: '' };
      case 'cave':
        return { numberOfTreasures: '' };
      case 'person':
        return { name: '', eyeColor: '', hairColor: '', height: '', nationality: '' };
      case 'head':
        return { size: '', eyesCount: '' };
      default:
        return {};
    }
  }

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const validateAndSubmit = (data) => {
    const newErrors = {};

    switch (selectedType) {
      case 'coordinates':
        const x = parseFloat(data.x);
        const y = parseFloat(data.y);
        if (isNaN(x) || x > 696 || x < 0) {
          newErrors.x = 'x: 0-696';
        }
        if (isNaN(y) || y > 366 || y < 0) {
          newErrors.y = 'y: 0-366';
        }
        break;

      case 'cave':
        const treasures = data.numberOfTreasures ? parseInt(data.numberOfTreasures) : null;
        if (treasures !== null && (isNaN(treasures) || treasures <= 0)) {
          newErrors.numberOfTreasures = '> 0';
        }
        break;

      case 'person':
        if (!data.name.trim()) {
          newErrors.name = 'required';
        }
        if (!data.eyeColor) {
          newErrors.eyeColor = 'required';
        }
        const height = parseInt(data.height);
        if (isNaN(height) || height <= 0) {
          newErrors.height = '> 0';
        }
        break;

      case 'head':
        const size = parseInt(data.size);
        if (isNaN(size) || size <= 0) {
          newErrors.size = '> 0';
        }
        break;
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setErrors({});

    if (!validateAndSubmit(formData)) {
      setLoading(false);
      return;
    }

    try {
      let created;
      
      switch (selectedType) {
        case 'coordinates':
          created = await apiClient.createCoordinates({
            x: parseFloat(formData.x),
            y: parseFloat(formData.y)
          });
          break;
          
        case 'cave':
          created = await apiClient.createCave({
            numberOfTreasures: formData.numberOfTreasures ? parseInt(formData.numberOfTreasures) : null
          });
          break;
          
        case 'person':
          created = await apiClient.createPerson({
            name: formData.name,
            eyeColor: formData.eyeColor,
            hairColor: formData.hairColor || null,
            height: parseInt(formData.height),
            nationality: formData.nationality || null
          });
          break;
          
        case 'head':
          created = await apiClient.createHead({
            size: parseInt(formData.size),
            eyesCount: formData.eyesCount ? parseInt(formData.eyesCount) : null
          });
          break;
      }
      
      onEntityCreated(selectedType);
      setFormData(getDefaultFormData(selectedType));
      
    } catch (error) {
      console.error(`Failed to create ${selectedType}:`, error);
      setErrors({ submit: `Server error: ${error.message}` });
    } finally {
      setLoading(false);
    }
  };

  const renderForm = () => {
    switch (selectedType) {
      case 'coordinates':
        return (
          <div className="form-row">
            <div className="form-group">
              <label className="form-label">x (max:696)</label>
              <input
                type="text"
                name="x"
                value={formData.x || ''}
                onChange={handleChange}
                className={`form-input ${errors.x ? 'error' : ''}`}
                placeholder="0-696"
                required
              />
              {errors.x && <span className="error-text">{errors.x}</span>}
            </div>
            <div className="form-group">
              <label className="form-label">y (max:366)</label>
              <input
                type="text"
                name="y"
                value={formData.y || ''}
                onChange={handleChange}
                className={`form-input ${errors.y ? 'error' : ''}`}
                placeholder="0-366"
                required
              />
              {errors.y && <span className="error-text">{errors.y}</span>}
            </div>
          </div>
        );
        
      case 'cave':
        return (
          <div className="form-group">
            <label className="form-label">number of treasures</label>
            <input
              type="text"
              name="numberOfTreasures"
              value={formData.numberOfTreasures || ''}
              onChange={handleChange}
              className={`form-input ${errors.numberOfTreasures ? 'error' : ''}`}
              placeholder="> 0"
            />
            {errors.numberOfTreasures && <span className="error-text">{errors.numberOfTreasures}</span>}
          </div>
        );
        
      case 'person':
        return (
          <>
            <div className="form-group">
              <label className="form-label">name</label>
              <input
                type="text"
                name="name"
                value={formData.name || ''}
                onChange={handleChange}
                className={`form-input ${errors.name ? 'error' : ''}`}
                required
              />
              {errors.name && <span className="error-text">{errors.name}</span>}
            </div>
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">eye color *</label>
                <select
                  name="eyeColor"
                  value={formData.eyeColor || ''}
                  onChange={handleChange}
                  className={`form-select ${errors.eyeColor ? 'error' : ''}`}
                  required
                >
                  <option value=""></option>
                  <option value="GREEN">green</option>
                  <option value="BLUE">blue</option>
                  <option value="YELLOW">yellow</option>
                </select>
                {errors.eyeColor && <span className="error-text">{errors.eyeColor}</span>}
              </div>
              <div className="form-group">
                <label className="form-label">hair color</label>
                <select
                  name="hairColor"
                  value={formData.hairColor || ''}
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
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">height *</label>
                <input
                  type="text"
                  name="height"
                  value={formData.height || ''}
                  onChange={handleChange}
                  className={`form-input ${errors.height ? 'error' : ''}`}
                  placeholder="> 0"
                  required
                />
                {errors.height && <span className="error-text">{errors.height}</span>}
              </div>
              <div className="form-group">
                <label className="form-label">nationality</label>
                <select
                  name="nationality"
                  value={formData.nationality || ''}
                  onChange={handleChange}
                  className="form-select"
                >
                  <option value=""></option>
                  <option value="SPAIN">spain</option>
                  <option value="VATICAN">vatican</option>
                  <option value="ITALY">italy</option>
                </select>
              </div>
            </div>
          </>
        );
        
      case 'head':
        return (
          <div className="form-row">
            <div className="form-group">
              <label className="form-label">size *</label>
              <input
                type="text"
                name="size"
                value={formData.size || ''}
                onChange={handleChange}
                className={`form-input ${errors.size ? 'error' : ''}`}
                placeholder="> 0"
                required
              />
              {errors.size && <span className="error-text">{errors.size}</span>}
            </div>
            <div className="form-group">
              <label className="form-label">eyes count</label>
              <input
                type="text"
                name="eyesCount"
                value={formData.eyesCount || ''}
                onChange={handleChange}
                className="form-input"
                placeholder="≥ 0"
              />
            </div>
          </div>
        );
        
      default:
        return null;
    }
  };

  return (
    <div className="panel">
      <div className="panel-title">[create_entities]</div>
      
      <div className="form-row" style={{ marginBottom: '15px' }}>
        {entityTypes.map(type => (
          <button
            key={type.value}
            type="button"
            className={`btn ${selectedType === type.value ? 'btn-primary' : ''}`}
            onClick={() => handleTypeChange(type.value)}
            style={{ padding: '6px 12px' }}
          >
            [{type.label}]
          </button>
        ))}
      </div>

      <form onSubmit={handleSubmit} className="form">
        {renderForm()}
        
        {errors.submit && (
          <div className="error-text" style={{ marginTop: '5px' }}>
            {errors.submit}
          </div>
        )}
        
        <div className="actions">
          <button 
            type="submit" 
            className="btn btn-primary"
            disabled={loading}
          >
            [{loading ? 'creating...' : 'create'}]
          </button>
        </div>
      </form>
    </div>
  );
};

export default EntityCreator;