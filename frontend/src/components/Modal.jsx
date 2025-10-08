import React from 'react';
import '../styles/Modal.css'

const Modal = ({ children, onClose }) => (
  <div className="modal-overlay" onClick={onClose}>
    <div className="modal-content" onClick={e => e.stopPropagation()}>
      <button className="close-btn" onClick={onClose}>×</button>
      {children}
    </div>
  </div>
);

export default Modal;