import React, { useState } from 'react';
import { apiClient } from '../api/client';

const ImportForm = ({ onImportSuccess }) => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleFileSelect = (e) => {
    const file = e.target.files[0];
    if (file && file.type === 'application/json') {
      setSelectedFile(file);
      setError('');
    } else {
      setError('select_json_file');
      setSelectedFile(null);
    }
  };

  const handleImport = async (e) => {
    e.preventDefault();
    if (!selectedFile) {
      setError('no_file_selected');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const result = await apiClient.importDragons(selectedFile);
      setSelectedFile(null);
      document.getElementById('file-input').value = '';
      onImportSuccess(result);
    } catch (err) {
      setError(`import_failed: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="panel">
      <div className="panel-title">[import_dragons]</div>
      <form className="form" onSubmit={handleImport}>
        <div className="form-group">
          <label className="form-label">[select_json_file]</label>
          <input
            id="file-input"
            type="file"
            accept=".json"
            onChange={handleFileSelect}
            className="form-input"
            disabled={loading}
          />
        </div>

        {selectedFile && (
          <div className="form-info">
            [selected_file]: {selectedFile.name}
          </div>
        )}

        {error && (
          <div className="error-text">
            {error}
          </div>
        )}

        <div className="actions">
          <button 
            type="submit" 
            className="btn btn-primary"
            disabled={!selectedFile || loading}
          >
            {loading ? '[importing...]' : '[import]'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default ImportForm;