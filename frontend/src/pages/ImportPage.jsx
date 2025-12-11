import React, { useState, useEffect } from 'react';
import { apiClient } from '../api/client';
import ImportForm from '../components/ImportForm';
import ImportHistory from '../components/ImportHistory';

const ImportPage = () => {
  const [importHistory, setImportHistory] = useState([]);
  const [loading, setLoading] = useState(false);
  const [cacheLoggingEnabled, setCacheLoggingEnabled] = useState(false);
  const [selectedImportId, setSelectedImportId] = useState('');
  const [downloadLoading, setDownloadLoading] = useState(false);
  const [minioError, setMinioError] = useState('');

  const loadImportHistory = async () => {
    setLoading(true);
    setMinioError('');
    try {
      const history = await apiClient.getImportHistory();
      setImportHistory(history);
    } catch (error) {
      console.error('Failed to load import history:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadCacheStatus = async () => {
    try {
      const status = await apiClient.getCacheStatus();
      setCacheLoggingEnabled(status.statisticsLoggingEnabled);
    } catch (error) {
      console.error('Failed to load cache status:', error);
    }
  };

  const toggleCacheLogging = async () => {
    try {
      if (cacheLoggingEnabled) {
        await apiClient.disableCacheStatistics();
        setCacheLoggingEnabled(false);
      } else {
        await apiClient.enableCacheStatistics();
        setCacheLoggingEnabled(true);
      }
    } catch (error) {
      console.error('Failed to toggle cache logging:', error);
    }
  };

  const handleImportSuccess = (result) => {
    loadImportHistory();
  };

  const handleDownloadFile = async () => {
    if (!selectedImportId) {
      alert('Select an import operation');
      return;
    }

    setDownloadLoading(true);
    setMinioError('');
    try {
      const blob = await apiClient.downloadImportFile(selectedImportId);
      
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `import_${selectedImportId}.json`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
      
    } catch (error) {
      console.error('Failed to download file:', error);
      
      if (error.message.includes('500') || error.message.includes('MinIO') || 
          error.message.includes('503') || error.message.includes('storage unavailable')) {
        setMinioError('MinIO storage is currently unavailable. File download failed.');
      } else if (error.message.includes('404') || error.message.includes('not found')) {
        setMinioError('File not found for this import operation.');
      } else {
        alert('Download failed: ' + error.message);
      }
    } finally {
      setDownloadLoading(false);
    }
  };

  useEffect(() => {
    loadImportHistory();
    loadCacheStatus();
  }, []);

  const importsWithFiles = importHistory.filter(op => op.hasFile || op.fileKey);

  return (
    <div className="layout">
      <div className="sidebar" style={{ width: '400px' }}>
        <ImportForm onImportSuccess={handleImportSuccess} />
        
        <div>
          <h4>Download Import File</h4>
          <select 
            value={selectedImportId} 
            onChange={(e) => setSelectedImportId(e.target.value)}
          >
            <option value="">Select import operation</option>
            {importsWithFiles.map(op => (
              <option key={op.id} value={op.id}>
                #{op.id} - {op.fileName || 'file'} ({new Date(op.createdAt).toLocaleDateString()})
              </option>
            ))}
          </select>
          
          <button 
            onClick={handleDownloadFile} 
            disabled={!selectedImportId || downloadLoading}
          >
            {downloadLoading ? 'Downloading...' : 'Download File'}
          </button>
          
          {minioError && (
            <div>
              {minioError}
            </div>
          )}
        </div>
        
        <div>
          <h4>Cache Control</h4>
          <p>Status: {cacheLoggingEnabled ? 'ENABLED' : 'DISABLED'}</p>
          <button onClick={toggleCacheLogging}>
            {cacheLoggingEnabled ? 'Disable Cache Logging' : 'Enable Cache Logging'}
          </button>
        </div>
      </div>

      <div className="main">
        <ImportHistory history={importHistory} />
      </div>
    </div>
  );
};

export default ImportPage;