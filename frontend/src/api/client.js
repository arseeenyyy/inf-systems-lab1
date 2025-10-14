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
  async getCaves() { return this.request('/caves'); }
  async getHeads() { return this.request('/heads'); }
  async getPersons() { return this.request('/persons'); }
}

export const apiClient = new ApiClient();