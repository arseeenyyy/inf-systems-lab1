import React, { useState } from 'react';
import { apiClient } from '../api/client';

const EntityCreator = ({ onEntityCreated }) => {
  const [selectedType, setSelectedType] = useState('coordinates');
  const [formData, setFormData] = useState({});
  const [loading, setLoading] = useState(false);

  const entityTypes = [
    { value: 'coordinates', label: 'coordinates' },
    { value: 'cave', label: 'cave' },
    { value: 'person', label: 'person' },
    { value: 'head', label: 'head' }
  ];

  const handleTypeChange = (type) => {
    setSelectedType(type);
    setFormData(getDefaultFormData(type));
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
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      let created;
      
      switch (selectedType) {
        case 'coordinates':
          const x = parseFloat(formData.x);
          const y = parseFloat(formData.y);
          if (isNaN(x) || isNaN(y) || x > 696 || x < 0 || y > 366 || y < 0) {
            alert('Invalid coordinates: x ≤ 696, y ≤ 366, both must be numbers');
            return;
          }
          created = await apiClient.createCoordinates({ x, y });
          break;
          
        case 'cave':
          const treasures = formData.numberOfTreasures ? parseInt(formData.numberOfTreasures) : null;
          if (treasures !== null && (isNaN(treasures) || treasures <= 0)) {
            alert('Number of treasures must be greater than 0');
            return;
          }
          created = await apiClient.createCave({ numberOfTreasures: treasures });
          break;
          
        case 'person':
          const height = parseInt(formData.height);
          if (isNaN(height) || height <= 0) {
            alert('Height must be greater than 0');
            return;
          }
          if (!formData.name || !formData.eyeColor) {
            alert('Name and eye color are required');
            return;
          }
          created = await apiClient.createPerson({
            ...formData,
            height: height,
            hairColor: formData.hairColor || null,
            nationality: formData.nationality || null
          });
          break;
          
        case 'head':
          const size = parseInt(formData.size);
          const eyesCount = formData.eyesCount ? parseInt(formData.eyesCount) : null;
          if (isNaN(size) || size <= 0) {
            alert('Size must be greater than 0');
            return;
          }
          created = await apiClient.createHead({ size, eyesCount });
          break;
      }
      
      onEntityCreated(selectedType, created);
      setFormData(getDefaultFormData(selectedType));
      
    } catch (error) {
      console.error(`Failed to create ${selectedType}:`, error);
      alert(`Error creating ${selectedType}`);
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
                className="form-input"
                placeholder="0-696"
                required
              />
            </div>
            <div className="form-group">
              <label className="form-label">y (max:366)</label>
              <input
                type="text"
                name="y"
                value={formData.y || ''}
                onChange={handleChange}
                className="form-input"
                placeholder="0-366"
                required
              />
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
              className="form-input"
              placeholder="> 0"
            />
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
                className="form-input"
                required
              />
            </div>
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">eye color *</label>
                <select
                  name="eyeColor"
                  value={formData.eyeColor || ''}
                  onChange={handleChange}
                  className="form-select"
                  required
                >
                  <option value=""></option>
                  <option value="GREEN">green</option>
                  <option value="BLUE">blue</option>
                  <option value="YELLOW">yellow</option>
                </select>
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
                  className="form-input"
                  placeholder="> 0"
                  required
                />
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
                className="form-input"
                placeholder="> 0"
                required
              />
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