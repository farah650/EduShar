import { api } from "./axiosConfig";

export const getAllVideos = () => api.get("/api/videos");
export const getVideo = (id) => api.get(`/api/videos/${id}`);
export const uploadVideo = (contributorId, data) =>
  api.post(`/api/videos/upload/${contributorId}`, data);
export const likeVideo = (id) => api.post(`/api/videos/${id}/like`);
export const unlikeVideo = (id) => api.post(`/api/videos/${id}/unlike`);
export const watchVideo = (id) => api.post(`/api/videos/${id}/watch`);
export const deleteVideo = (id) => api.delete(`/api/videos/${id}`);
export const videosByCategory = (cat) =>
  api.get(`/api/videos/category/${encodeURIComponent(cat)}`);
export const recentVideos = () => api.get("/api/videos/recent");
export const popularVideos = () => api.get("/api/videos/popular");
export const freeVideos = () => api.get("/api/videos/free");
export const searchVideos = (q) => api.get(`/api/videos/search?query=${encodeURIComponent(q)}`);
