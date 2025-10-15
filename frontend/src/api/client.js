const BASE_URL = 'http://localhost:8080/infsys/api';

class ApiClient {
  async request(endpoint, options = {}) {
    const response = await fetch(`${BASE_URL}${endpoint}`, {
      headers: { 'Content-Type': 'application/json' },
      ...options
    });
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    return response.status === 204 ? null : response.json();
  }

  async getDragons() { return this.request('/dragons'); }
  async createDragon(data) { return this.request('/dragons', { method: 'POST', body: JSON.stringify(data) }); }
  async updateDragon(id, data) { return this.request(`/dragons/${id}`, { method: 'PUT', body: JSON.stringify(data) }); }
  async deleteDragon(id) { return this.request(`/dragons/${id}`, { method: 'DELETE' }); }

  async getCoordinates() { return this.request('/coordinates'); }
  async createCoordinates(data) { return this.request('/coordinates', { method: 'POST', body: JSON.stringify(data) }); }
  async getCaves() { return this.request('/caves'); }
  async createCave(data) { return this.request('/caves', { method: 'POST', body: JSON.stringify(data) }); }
  async getHeads() { return this.request('/heads'); }
  async createHead(data) { return this.request('/heads', { method: 'POST', body: JSON.stringify(data) }); }
  async getPersons() { return this.request('/persons'); }
  async createPerson(data) { return this.request('/persons', { method: 'POST', body: JSON.stringify(data) }); }

async getTeams() { return this.request('/teams'); }
  async createTeam(data) { 
    return this.request('/teams', { 
      method: 'POST', 
      body: JSON.stringify({ 
        name: data.name, 
        personsIds: data.personsIds 
      }) 
    }); 
  } 
  async addMembersToTeam(teamId, personIds) { 
    return this.request(`/teams/${teamId}/add-members`, { 
      method: 'POST', 
      body: JSON.stringify(personIds) 
    }); 
  }
  async sendTeamToCave(data) { 
    return this.request('/teams/send-to-cave', { 
      method: 'POST', 
      body: JSON.stringify({ 
        teamId: parseInt(data.teamId), 
        caveId: parseInt(data.caveId) 
      }) 
    }); 
  }
}

export const apiClient = new ApiClient();