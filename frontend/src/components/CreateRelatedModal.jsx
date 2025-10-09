import React, { useState } from 'react';
import { apiClient } from '../api/client';
import '../styles/modal.css';
import '../styles/forms.css';

const CreateRelatedModal = ({ onClose, onCreated }) => {
  const [selectedType, setSelectedType] = useState('');
  const [formData, setFormData] = useState({});
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  const handleTypeChange = (e) => {
    setSelectedType(e.target.value);
    setFormData({});
    setErrors({});
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
    
    if (selectedType === 'Coordinates') {
      if (!formData.x || formData.x === '') {
        newErrors.x = 'X coordinate is required';
      } else if (parseFloat(formData.x) > 696) {
        newErrors.x = 'X coordinate must be ≤ 696';
      }
      
      if (!formData.y || formData.y === '') {
        newErrors.y = 'Y coordinate is required';
      } else if (parseFloat(formData.y) > 366) {
        newErrors.y = 'Y coordinate must be ≤ 366';
      }
    }
    
    if (selectedType === 'Cave') {
      if (!formData.numberOfTreasures || formData.numberOfTreasures === '') {
        newErrors.numberOfTreasures = 'Number of treasures is required';
      } else if (parseInt(formData.numberOfTreasures) < 1) {
        newErrors.numberOfTreasures = 'Number of treasures must be ≥ 1';
      }
    }
    
    if (selectedType === 'Person') {
      if (!formData.name?.trim()) {
        newErrors.name = 'Name is required';
      }
      
      if (!formData.eyeColor) {
        newErrors.eyeColor = 'Eye color is required';
      }
      
      if (!formData.height || formData.height === '') {
        newErrors.height = 'Height is required';
      } else if (parseInt(formData.height) < 1) {
        newErrors.height = 'Height must be ≥ 1';
      }
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
      let dataToSend = { ...formData };
      
      if (selectedType === 'Coordinates') {
        dataToSend.x = parseFloat(formData.x);
        dataToSend.y = parseFloat(formData.y);
      }
      
      if (selectedType === 'Cave') {
        dataToSend.numberOfTreasures = parseInt(formData.numberOfTreasures);
      }
      
      if (selectedType === 'Person') {
        dataToSend.height = parseInt(formData.height);
      }
      
      if (selectedType === 'Head') {
        if (formData.size) dataToSend.size = parseInt(formData.size);
        if (formData.eyesCount) dataToSend.eyesCount = parseInt(formData.eyesCount);
      }

      await apiClient[`create${selectedType}`](dataToSend);
      onCreated();
      onClose();
    } catch (error) {
      console.error(`Failed to create ${selectedType}:`, error);
      setErrors({ submit: `Failed to create ${selectedType}` });
    } finally {
      setLoading(false);
    }
  };

  const renderFormFields = () => {
    switch (selectedType) {
      case 'Coordinates':
        return (
          <>
            <div className="form-group">
              <label className="form-label required">X</label>
              <input
                type="number"
                name="x"
                value={formData.x || ''}
                onChange={handleChange}
                step="0.1"
                max="696"
                className="form-input"
              />
              {errors.x && <div className="form-error">{errors.x}</div>}
            </div>
            
            <div className="form-group">
              <label className="form-label required">Y</label>
              <input
                type="number"
                name="y"
                value={formData.y || ''}
                onChange={handleChange}
                step="0.1"
                max="366"
                className="form-input"
              />
              {errors.y && <div className="form-error">{errors.y}</div>}
            </div>
          </>
        );

      case 'Cave':
        return (
          <div className="form-group">
            <label className="form-label required">Number of Treasures</label>
            <input
              type="number"
              name="numberOfTreasures"
              value={formData.numberOfTreasures || ''}
              onChange={handleChange}
              min="1"
              className="form-input"
            />
            {errors.numberOfTreasures && <div className="form-error">{errors.numberOfTreasures}</div>}
          </div>
        );

      case 'Person':
        return (
          <>
            <div className="form-group">
              <label className="form-label required">Name</label>
              <input
                type="text"
                name="name"
                value={formData.name || ''}
                onChange={handleChange}
                className="form-input"
              />
              {errors.name && <div className="form-error">{errors.name}</div>}
            </div>
            
            <div className="form-group">
              <label className="form-label required">Eye Color</label>
              <select
                name="eyeColor"
                value={formData.eyeColor || ''}
                onChange={handleChange}
                className="form-select"
              >
                <option value="">Select Eye Color</option>
                <option value="GREEN">GREEN</option>
                <option value="BLUE">BLUE</option>
                <option value="YELLOW">YELLOW</option>
              </select>
              {errors.eyeColor && <div className="form-error">{errors.eyeColor}</div>}
            </div>
            
            <div className="form-group">
              <label className="form-label">Hair Color</label>
              <select
                name="hairColor"
                value={formData.hairColor || ''}
                onChange={handleChange}
                className="form-select"
              >
                <option value="">Select Hair Color</option>
                <option value="GREEN">GREEN</option>
                <option value="BLUE">BLUE</option>
                <option value="YELLOW">YELLOW</option>
              </select>
            </div>
            
            <div className="form-group">
              <label className="form-label required">Height</label>
              <input
                type="number"
                name="height"
                value={formData.height || ''}
                onChange={handleChange}
                min="1"
                className="form-input"
              />
              {errors.height && <div className="form-error">{errors.height}</div>}
            </div>
            
            <div className="form-group">
              <label className="form-label">Nationality</label>
              <select
                name="nationality"
                value={formData.nationality || ''}
                onChange={handleChange}
                className="form-select"
              >
                <option value="">Select Nationality</option>
                <option value="SPAIN">SPAIN</option>
                <option value="VATICAN">VATICAN</option>
                <option value="ITALY">ITALY</option>
              </select>
            </div>
          </>
        );

      case 'Head':
        return (
          <>
            <div className="form-group">
              <label className="form-label">Size</label>
              <input
                type="number"
                name="size"
                value={formData.size || ''}
                onChange={handleChange}
                className="form-input"
              />
            </div>
            
            <div className="form-group">
              <label className="form-label">Eyes Count</label>
              <input
                type="number"
                name="eyesCount"
                value={formData.eyesCount || ''}
                onChange={handleChange}
                className="form-input"
              />
            </div>
          </>
        );

      default:
        return null;
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="modal-header">
          <h2 className="modal-title">Create Related Object</h2>
        </div>
        
        {errors.submit && (
          <div style={{ color: 'red', marginBottom: '10px' }}>{errors.submit}</div>
        )}

        <form className="form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Select Type</label>
            <select
              value={selectedType}
              onChange={handleTypeChange}
              className="form-select"
            >
              <option value="">Select Type</option>
              <option value="Coordinates">Coordinates</option>
              <option value="Cave">Cave</option>
              <option value="Person">Person</option>
              <option value="Head">Head</option>
            </select>
          </div>

          {selectedType && renderFormFields()}

          <div className="form-actions">
            <button type="button" onClick={onClose}>
              Cancel
            </button>
            {selectedType && (
              <button type="submit" disabled={loading}>
                {loading ? 'Creating...' : `Create ${selectedType}`}
              </button>
            )}
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateRelatedModal;