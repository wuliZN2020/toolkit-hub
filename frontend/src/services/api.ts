import request from './request';

// Health check
export const healthCheck = (): Promise<any> => {
  return request.get('/health');
};

// Book APIs
export const searchBooks = (keyword: string): Promise<any[]> => {
  return request.get('/book/search', { params: { keyword } });
};

export const getBookWithQuotes = (doubanId: string, limit?: number): Promise<any> => {
  return request.get(`/book/${doubanId}`, {
    params: { limit }
  });
};

export const getBookQuotes = (doubanId: string, start?: number, limit?: number): Promise<any[]> => {
  return request.get(`/book/${doubanId}/quotes`, {
    params: { start, limit }
  });
};

// Template APIs
export const getTemplates = (): Promise<any[]> => {
  return request.get('/template/list');
};

export const getTemplatesByPlatform = (platform: string): Promise<any[]> => {
  return request.get(`/template/list/${platform}`);
};

export const getTemplateById = (id: number): Promise<any> => {
  return request.get(`/template/${id}`);
};
