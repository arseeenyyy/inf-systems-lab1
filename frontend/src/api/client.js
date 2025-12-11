const BASE_URL = 'http://localhost:8080/infsys/api';

class ApiClient {
  constructor() {
    this.token = localStorage.getItem('jwtToken');
  }

  setToken(token) {
    this.token = token;
    if (token) {
      localStorage.setItem('jwtToken', token);
    } else {
      localStorage.removeItem('jwtToken');
    }
  }

  getAuthHeaders() {
    const headers = {
      'Content-Type': 'application/json',
    };
    
    if (this.token) {
      headers['Authorization'] = `Bearer ${this.token}`;
    }
    
    return headers;
  }

  async request(endpoint, options = {}) {
    const config = {
      headers: this.getAuthHeaders(),
      ...options
    };

    if (config.body && typeof config.body === 'object') {
      config.body = JSON.stringify(config.body);
    }

    const response = await fetch(`${BASE_URL}${endpoint}`, config);
    
    if (response.status === 401) {
      this.setToken(null);
      window.location.href = '/login';
      throw new Error('Authentication required');
    }

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }

    return response.status === 204 ? null : response.json();
  }

  async register(userData) {
    const response = await this.request('/users/register', {
      method: 'POST',
      body: {
        username: userData.username,
        password: userData.password,
        role: userData.isAdmin ? 'ADMIN' : 'USER'
      }
    });
    
    if (response.jwt) {
      this.setToken(response.jwt);
    }
    
    return response;
  }

  async login(loginData) {
    const response = await this.request('/users/login', {
      method: 'POST',
      body: {
        username: loginData.username,
        password: loginData.password
      }
    });
    
    if (response.jwt) {
      this.setToken(response.jwt);
    }
    
    return response;
  }

  logout() {
    this.setToken(null);
  }

  isAuthenticated() {
    return !!this.token;
  }

  async getDragons() { 
    return this.request('/dragons'); 
  }

  async createDragon(data) { 
    return this.request('/dragons', { 
      method: 'POST', 
      body: data 
    }); 
  }

  async updateDragon(id, data) { 
    return this.request(`/dragons/${id}`, { 
      method: 'PUT', 
      body: data 
    }); 
  }

  async deleteDragon(id) { 
    return this.request(`/dragons/${id}`, { 
      method: 'DELETE' 
    }); 
  }

  async deleteAllByColor(color) {
    return this.request(`/dragons/color/${color}/all`, { 
      method: 'DELETE' 
    });
  }

  async deleteOneByColor(color) {
    return this.request(`/dragons/color/${color}/one`, { 
      method: 'DELETE' 
    });
  }

  async findByNameStartingWith(substring) {
    return this.request(`/dragons/name-starts-with/${substring}`);
  }

  async getCoordinates() { 
    return this.request('/coordinates'); 
  }

  async createCoordinates(data) { 
    return this.request('/coordinates', { 
      method: 'POST', 
      body: data 
    }); 
  }

  async getCaves() { 
    return this.request('/caves'); 
  }

  async createCave(data) { 
    return this.request('/caves', { 
      method: 'POST', 
      body: data 
    }); 
  }

  async getHeads() { 
    return this.request('/heads'); 
  }

  async createHead(data) { 
    return this.request('/heads', { 
      method: 'POST', 
      body: data 
    }); 
  }

  async getPersons() { 
    return this.request('/persons'); 
  }

  async createPerson(data) { 
    return this.request('/persons', { 
      method: 'POST', 
      body: data 
    }); 
  }

  async getTeams() { 
    return this.request('/teams'); 
  }

  async createTeam(data) { 
    return this.request('/teams', { 
      method: 'POST', 
      body: { 
        name: data.name, 
        personsIds: data.personsIds 
      } 
    }); 
  }

  async addMembersToTeam(teamId, personIds) { 
    return this.request(`/teams/${teamId}/add-members`, { 
      method: 'POST', 
      body: personIds 
    }); 
  }

  async sendTeamToCave(data) { 
    return this.request('/teams/send-to-cave', { 
      method: 'POST', 
      body: { 
        teamId: parseInt(data.teamId), 
        caveId: parseInt(data.caveId) 
      } 
    }); 
  }

  async deleteTeam(id) {
    return this.request(`/teams/${id}`, { 
      method: 'DELETE' 
    });
  }
    async importDragons(file) {
    const formData = new FormData();
    formData.append('file', file);

    const response = await fetch(`${BASE_URL}/import/dragons`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${this.token}`
      },
      body: formData
    });

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }

    return response.json();
  }

  async getImportHistory() {
    return this.request(`/import/history`);
  }
  async getCacheStatus() {
    return this.request('/cache/statistics/status');
  }

  async enableCacheStatistics() {
    return this.request('/cache/statistics/enable', {
      method: 'POST'
    });
  }
  async downloadImportFile(importId) {
    const response = await fetch(`${BASE_URL}/import/${importId}/download`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${this.token}`
      }
    });

    if (!response.ok) {
      try {
        const errorData = await response.json();
        if (errorData.error) {
          throw new Error(errorData.error);
        }
      } catch (e) {
      }
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }

    return response.blob();
  }
}

export const apiClient = new ApiClient();