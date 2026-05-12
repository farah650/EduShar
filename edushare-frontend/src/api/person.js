import { api } from "./axiosConfig";

export const getProfile = (id) => api.get(`/api/person/${id}/profile`);
export const updateProfile = (id, data) => api.put(`/api/person/${id}/profile`, data);
export const buyVideo = (personId, videoId) =>
  api.post(`/api/person/${personId}/buy/${videoId}`);
export const uploadPersonVideo = (id, data) => api.post(`/api/person/${id}/upload`, data);
export const withdraw = (id, data) => api.post(`/api/person/${id}/withdraw`, data);
export const addCredits = (id, data) => api.post(`/api/person/${id}/add-credits`, data);
export const myVideos = (id) => api.get(`/api/person/${id}/my-videos`);
export const purchasedVideos = (id) =>
  api.get(`/api/person/${id}/purchased-videos`);
export const balance = (id) => api.get(`/api/person/${id}/balance`);
