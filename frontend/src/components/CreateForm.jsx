import React, { useState } from 'react';
import { apiClient } from '../api/client';
import { Color, DragonCharacter, Country } from '../enums/enums';
import '../styles/CreateForm.css'

const CreateForm = ({ type, onCreated }) => {
  const [formData, setFormData] = useState({});
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await apiClient[`create${type}`](formData);
      onCreated();
      setFormData({});
    } catch (error) {
      alert('Error creating object');
    } finally {
      setLoading(false);
    }
  };

  const renderFields = () => {
    switch (type) {
      case 'Coordinates':
        return (
          <>
            <input name="x" type="number" placeholder="X (max 696)" onChange={handleChange} required />
            <input name="y" type="number" placeholder="Y (max 366)" onChange={handleChange} required />
          </>
        );
      case 'Cave':
        return <input name="numberOfTreasures" type="number" placeholder="Number of Treasures" onChange={handleChange} required />;
      case 'Head':
        return (
          <>
            <input name="size" type="number" placeholder="Size" onChange={handleChange} />
            <input name="eyesCount" type="number" placeholder="Eyes Count" onChange={handleChange} />
          </>
        );
      case 'Person':
        return (
          <>
            <input name="name" placeholder="Name" onChange={handleChange} required />
            <select name="eyeColor" onChange={handleChange} required>
              <option value="">Select Eye Color</option>
              {Object.values(Color).map(color => <option key={color} value={color}>{color}</option>)}
            </select>
            <select name="hairColor" onChange={handleChange}>
              <option value="">Select Hair Color</option>
              {Object.values(Color).map(color => <option key={color} value={color}>{color}</option>)}
            </select>
            <input name="height" type="number" placeholder="Height" onChange={handleChange} required />
            <select name="nationality" onChange={handleChange}>
              <option value="">Select Nationality</option>
              {Object.values(Country).map(country => <option key={country} value={country}>{country}</option>)}
            </select>
          </>
        );
      default:
        return null;
    }
  };

  return (
    <form className="create-form" onSubmit={handleSubmit}>
      <h4>Create {type}</h4>
      {renderFields()}
      <button type="submit" disabled={loading}>{loading ? 'Creating...' : 'Create'}</button>
    </form>
  );
};

export default CreateForm;