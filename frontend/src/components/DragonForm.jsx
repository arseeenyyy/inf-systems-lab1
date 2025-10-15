import React, { useState, useEffect } from 'react';

const DragonForm = ({ dragon, onSubmit, onCancel, coordinates, caves, persons, heads }) => {
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
  const [errors, setErrors] = useState({});

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
      setErrors({});
    } else {
      setFormData({
        name: '', coordinatesId: '', caveId: '', killerId: '', headId: '',
        age: '', weight: '', color: '', character: ''
      });
      setErrors({});
    }
  }, [dragon]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    // очищаем ошибку для этого поля
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const validateAndSubmit = (data) => {
    const newErrors = {};

    // Обязательные поля
    if (!data.name.trim()) newErrors.name = 'required';
    if (!data.coordinatesId) newErrors.coordinatesId = 'required';

    // Числа
    const age = parseInt(data.age);
    const weight = parseFloat(data.weight);
    if (isNaN(age) || age <= 0) newErrors.age = '> 0';
    if (isNaN(weight) || weight <= 0) newErrors.weight = '> 0';

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    e.stopPropagation(); // блокируем HTML5 validation
    
    setErrors({});

    if (!validateAndSubmit(formData)) {
      return;
    }

    try {
      const data = {
        ...formData,
        coordinatesId: parseInt(formData.coordinatesId),
        caveId: formData.caveId ? parseInt(formData.caveId) : null,
        killerId: formData.killerId ? parseInt(formData.killerId) : null,
        headId: formData.headId ? parseInt(formData.headId) : null,
        age: parseInt(formData.age),
        weight: parseFloat(formData.weight)
      };
      await onSubmit(data);
    } catch (error) {
      console.error('Failed to submit dragon:', error);
      setErrors({ submit: `Server error: ${error.message}` });
    }
  };

  return (
    <form onSubmit={handleSubmit} className="form" noValidate>
      <div className="form-group">
        <label className="form-label">name</label>
        <input
          type="text"
          name="name"
          value={formData.name}
          onChange={handleChange}
          className={`form-input ${errors.name ? 'error' : ''}`}
          // убираем required - используем свою валидацию
        />
        {errors.name && <span className="error-text">name: {errors.name}</span>}
      </div>

      <div className="form-group">
        <label className="form-label">coordinates</label>
        <select
          name="coordinatesId"
          value={formData.coordinatesId}
          onChange={handleChange}
          className={`form-select ${errors.coordinatesId ? 'error' : ''}`}
          // убираем required
        >
          <option value=""></option>
          {coordinates.map(coord => (
            <option key={coord.id} value={coord.id}>
              ({coord.x};{coord.y})
            </option>
          ))}
        </select>
        {errors.coordinatesId && <span className="error-text">coordinates: {errors.coordinatesId}</span>}
      </div>

      <div className="form-row">
        <div className="form-group">
          <label className="form-label">age</label>
          <input
            type="text"
            name="age"
            value={formData.age}
            onChange={handleChange}
            className={`form-input ${errors.age ? 'error' : ''}`}
            placeholder="> 0"
            // убираем required
          />
          {errors.age && <span className="error-text">age: {errors.age}</span>}
        </div>

        <div className="form-group">
          <label className="form-label">weight</label>
          <input
            type="text"
            name="weight"
            value={formData.weight}
            onChange={handleChange}
            className={`form-input ${errors.weight ? 'error' : ''}`}
            placeholder="> 0"
            // убираем required
          />
          {errors.weight && <span className="error-text">weight: {errors.weight}</span>}
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
            {caves.map(cave => (
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
            {persons.map(person => (
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
            {heads.map(head => (
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
      
      {errors.submit && (
        <div className="error-text" style={{ marginTop: '5px' }}>
          {errors.submit}
        </div>
      )}
      
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