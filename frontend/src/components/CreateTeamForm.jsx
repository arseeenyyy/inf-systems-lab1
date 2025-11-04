import React from 'react';

const CreateTeamForm = ({ 
  createFormData, 
  availablePersons, 
  errors, 
  loading, 
  onCreateChange, 
  onCheckboxChange, 
  onSubmit 
}) => {
  return (
    <div className="panel">
      <div className="panel-title">[create_team]</div>
      <form onSubmit={onSubmit} className="form" noValidate>
        <div className="form-group">
          <label className="form-label">name</label>
          <input
            type="text"
            name="name"
            value={createFormData.name}
            onChange={onCreateChange}
            className={`form-input ${errors.name ? 'error' : ''}`}
          />
          {errors.name && <span className="error-text">name: {errors.name}</span>}
        </div>
        
        <div className="form-group">
          <label className="form-label">members</label>
          <div 
            className="scrollable-checkbox-list"
            style={{
              maxHeight: '200px',
              overflowY: 'auto',
              border: '1px solid #333',
              borderRadius: '4px',
              padding: '8px',
              backgroundColor: '#1a1a1a'
            }}
          >
            {availablePersons.length > 0 ? (
              availablePersons.map(person => (
                <div key={person.id} className="checkbox-item">
                  <input
                    type="checkbox"
                    id={`person-${person.id}`}
                    checked={createFormData.personsIds.includes(person.id)}
                    onChange={() => onCheckboxChange(person.id)}
                  />
                  <label htmlFor={`person-${person.id}`} style={{ color: '#fff', marginLeft: '8px' }}>
                    {person.name}
                  </label>
                </div>
              ))
            ) : (
              <div className="no-data" style={{ color: '#999' }}>No available persons</div>
            )}
          </div>
          {errors.personsIds && <span className="error-text">members: {errors.personsIds}</span>}
        </div>
        
        {errors.submit && <div className="error-text">{errors.submit}</div>}
        <div className="actions">
          <button type="submit" className="btn btn-primary" disabled={loading}>
            [{loading ? 'creating...' : 'create'}]
          </button>
        </div>
      </form>
    </div>
  );
};

export default CreateTeamForm;