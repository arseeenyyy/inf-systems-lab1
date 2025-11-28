import React from 'react';

const ImportHistory = ({ history }) => {
  if (!history || history.length === 0) {
    return (
      <div className="panel">
        <div className="panel-title">[import_history]</div>
        <div className="no-selection">no_imports_found</div>
      </div>
    );
  }

  const formatHistoryData = (history) => {
    return JSON.stringify(history, null, 2);
  };

  return (
    <div className="panel">
      <div className="panel-title">[import_history]</div>
      <div className="json-view">
        {formatHistoryData(history)}
      </div>
    </div>
  );
};

export default ImportHistory;