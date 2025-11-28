import React, { useState, useEffect } from 'react';
import { apiClient } from '../api/client';
import ImportForm from '../components/ImportForm';
import ImportHistory from '../components/ImportHistory';

const ImportPage = () => {
  const [importHistory, setImportHistory] = useState([]);
  const [loading, setLoading] = useState(false);

  const loadImportHistory = async () => {
    setLoading(true);
    try {
      const history = await apiClient.getImportHistory();
      setImportHistory(history);
    } catch (error) {
      console.error('Failed to load import history:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleImportSuccess = (result) => {
    loadImportHistory();
  };

  useEffect(() => {
    loadImportHistory();
  }, []);

  return (
    <div className="layout">
      <div className="sidebar" style={{ width: '400px' }}>
        <ImportForm onImportSuccess={handleImportSuccess} />
      </div>

      <div className="main">
        <ImportHistory history={importHistory} />
      </div>
    </div>
  );
};

export default ImportPage;