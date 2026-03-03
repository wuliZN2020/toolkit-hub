import request from './request';

/**
 * Health check API
 */
export const healthApi = {
  // Health check
  check: () => request.get('/health'),

  // Test API
  test: () => request.get('/health/test'),
};

/**
 * Book quote API (to be implemented)
 */
export const bookApi = {
  // Search book by Douban ID or URL
  searchBook: (keyword: string) => request.get(`/book/search?keyword=${keyword}`),

  // Get book quotes
  getQuotes: (bookId: number) => request.get(`/book/${bookId}/quotes`),
};

/**
 * Template API (to be implemented)
 */
export const templateApi = {
  // Get all templates
  getList: (platform?: string) => {
    const params = platform ? `?platform=${platform}` : '';
    return request.get(`/template/list${params}`);
  },

  // Get template by ID
  getById: (id: number) => request.get(`/template/${id}`),
};
